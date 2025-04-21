package com.silvermaiden.mywaifu.services.implementations;

import com.silvermaiden.mywaifu.configurations.exceptions.custom.CustomAuthenticationException;
import com.silvermaiden.mywaifu.configurations.security.jwt.JwtService;
import com.silvermaiden.mywaifu.models.dtos.auth.AuthRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.auth.AuthResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.meta.PagedResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserDTO;
import com.silvermaiden.mywaifu.models.entities.CustomUserDetails;
import com.silvermaiden.mywaifu.models.entities.User;
import com.silvermaiden.mywaifu.models.mappers.UserMapper;
import com.silvermaiden.mywaifu.repositories.UserRepository;
import com.silvermaiden.mywaifu.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.silvermaiden.mywaifu.common.constants.ErrorMessage.*;
import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Auth related
    @Override
    @Transactional
    public AuthResponseDTO register(UserRequestDTO req) {
        User savedUser = this.createUser(req);

        JwtService.TokenResponse accessTokenResponse = this.jwtService.generateAccessToken(
                savedUser.getUsername(),
                savedUser.getRoles());
        String refreshToken = this.jwtService.generateRefreshToken(savedUser.getUsername());
        return new AuthResponseDTO(accessTokenResponse.token(), refreshToken, accessTokenResponse.expiresAt());
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            JwtService.TokenResponse accessTokenResponse = this.jwtService.generateAccessToken(
                    user.getUsername(),
                    user.getRoles()
            );
            String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());

            return new AuthResponseDTO(accessTokenResponse.token(), refreshToken, accessTokenResponse.expiresAt());

        } catch (BadCredentialsException ex) {
            log.error("login: {}", USER_PASSWORD_WRONG);
            throw new CustomAuthenticationException(USER_PASSWORD_WRONG);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!this.jwtService.isTokenValid(refreshToken)) {
            log.error("refreshToken: {}", INVALID_TOKEN_MESSAGE);
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        String username = this.jwtService.extractUsername(refreshToken);
        return this.userRepository.findByUsername(username)
                .map(user -> {
                    JwtService.TokenResponse accessTokenResponse = this.jwtService.generateAccessToken(
                            user.getUsername(),
                            user.getRoles()
                    );

                    String newRefreshToken = this.jwtService.generateRefreshToken(user.getUsername());

                    return new AuthResponseDTO(
                            accessTokenResponse.token(),
                            newRefreshToken,
                            accessTokenResponse.expiresAt()
                    );
                })
                .orElseThrow(() -> {
                    log.error("refreshToken: {}", USER_NOT_FOUND);
                    return new EntityNotFoundException(USER_NOT_FOUND);
                });
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    // Normal user management
    @Override
    @Transactional
    public Long create(UserRequestDTO req) {
        return this.createUser(req).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        return this.userMapper.toDtoList(this.userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        return this.userMapper.toDto(this.userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("getById: {}", USER_NOT_FOUND);
                    return new EntityNotFoundException(USER_NOT_FOUND);
                }));
    }

    @Override
    @Transactional
    public Long update(Long id, UserRequestDTO req) {
        return this.userRepository.findById(id)
                .map(user -> {
                    Optional.ofNullable(req.username())
                            .filter(username -> !username.equals(user.getUsername()))
                            .ifPresent(username -> {
                                if (this.userRepository.existsByUsername(username)) {
                                    log.error("update: {}", USERNAME_EXISTS);
                                    throw new IllegalArgumentException(USERNAME_EXISTS);
                                }
                                user.setUsername(username);
                            });

                    Optional.ofNullable(req.name())
                            .ifPresent(user::setName);

                    Optional.ofNullable(req.password())
                            .ifPresent(password -> user.setPassword(this.passwordEncoder.encode(password)));

                    Optional.ofNullable(req.email())
                            .ifPresent(email -> {
                                if (this.userRepository.existsByEmail(email)) {
                                    log.error("update: {}", EMAIL_EXISTS);
                                    throw new IllegalArgumentException(EMAIL_EXISTS);
                                }
                                user.setEmail(email);
                            });

                    return this.userRepository.save(user).getId();
                })
                .orElseThrow(() -> {
                   log.error("update: {}", USER_NOT_FOUND);
                   return new EntityNotFoundException(USER_NOT_FOUND);
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!this.userRepository.existsById(id)) {
            log.error("delete: {}", USER_NOT_FOUND);
            throw new EntityNotFoundException(USER_NOT_FOUND);
        }

        this.userRepository.deleteById(id);
    }

    // Paged result
    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<UserDTO> getPaged(int page, int size) {
        if (page < 0 || size <= 0) {
            log.error("getPaged: {}", INVALID_SIZE_OR_PAGE);
            throw new IllegalArgumentException(INVALID_SIZE_OR_PAGE);
        }

        Pageable pageable = PageRequest.of(page, size);

        return this.userMapper.toPagedResponse(userRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<UserDTO> getPagedSorted(int page, int size, String sortBy, String sortDirection) {
        if (page < 0 || size <= 0) {
            log.error("getPagedSorted: {}", INVALID_SIZE_OR_PAGE);
            throw new IllegalArgumentException(INVALID_SIZE_OR_PAGE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return this.userMapper.toPagedResponse(userRepository.findAll(pageable));
    }

    // Public
    // Get current user
    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userDetails.getUser();
        return this.userMapper.toDto(user);
    }

    // Update current user
    @Override
    @Transactional
    public Long updateCurrentUser(UserRequestDTO req) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userDetails.getUser();

        Optional.ofNullable(req.username())
                .filter(username -> !username.equals(user.getUsername()))
                .ifPresent(username -> {
                    if (this.userRepository.existsByUsername(username)) {
                        log.error("updateCurrentUser: {}", USERNAME_EXISTS);
                        throw new IllegalArgumentException(USERNAME_EXISTS);
                    }
                    user.setUsername(username);
                });

        Optional.ofNullable(req.name())
                .ifPresent(user::setName);

        Optional.ofNullable(req.password())
                .ifPresent(password -> user.setPassword(this.passwordEncoder.encode(password)));

        Optional.ofNullable(req.email())
                .filter(email -> !email.equals(user.getEmail()))
                .ifPresent(email -> {
                    if (this.userRepository.existsByEmail(email)) {
                        log.error("updateCurrentUser: {}", EMAIL_EXISTS);
                        throw new IllegalArgumentException(EMAIL_EXISTS);
                    }
                    user.setEmail(email);
                });

        User updatedUser = this.userRepository.save(user);
        return updatedUser.getId();
    }

    // Create User wrapper
    @Transactional
    private User createUser(UserRequestDTO req) {
        if (this.userRepository.existsByUsername(req.username())) {
            log.error("createUser: {}", USER_EXISTS);
            throw new IllegalArgumentException(USER_EXISTS);
        }

        if (this.userRepository.existsByEmail(req.email())) {
            log.error("createUser: {}", EMAIL_EXISTS);
            throw new IllegalArgumentException(EMAIL_EXISTS);
        }

        User user = new User();
        user.setUsername(req.username());
        user.setPassword(this.passwordEncoder.encode(req.password()));
        user.setName(req.name());
        user.setEmail(req.email());
        user.setRoles(DEFAULT_ROLE);
        return this.userRepository.save(user);
    }
}
