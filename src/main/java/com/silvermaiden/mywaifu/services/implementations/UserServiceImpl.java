package com.silvermaiden.mywaifu.services.implementations;

import com.silvermaiden.mywaifu.configurations.exceptions.custom.CustomAuthenticationException;
import com.silvermaiden.mywaifu.configurations.security.jwt.JwtService;
import com.silvermaiden.mywaifu.models.dtos.auth.AuthRequestDTO;
import com.silvermaiden.mywaifu.models.dtos.auth.AuthResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.meta.PagedResponseDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserCreateDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserDTO;
import com.silvermaiden.mywaifu.models.dtos.user.UserUpdateDTO;
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
    @Transactional
    private User createUser(UserCreateDTO req) {
        if (userRepository.existsByUsername(req.username())) {
            log.error("createUser: {}", USER_EXISTS);
            throw new IllegalArgumentException(USER_EXISTS);
        }

        if (userRepository.existsByEmail(req.email())) {
            log.error("createUser: {}", EMAIL_EXISTS);
            throw new IllegalArgumentException(EMAIL_EXISTS);
        }

        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setName(req.name());
        user.setEmail(req.email());
        user.setRoles(DEFAULT_ROLE);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponseDTO register(UserCreateDTO req) {
        User savedUser = createUser(req);

        JwtService.TokenResponse accessTokenResponse = jwtService.generateAccessToken(
                savedUser.getUsername(),
                savedUser.getRoles());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getUsername());
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

            JwtService.TokenResponse accessTokenResponse = jwtService.generateAccessToken(
                    user.getUsername(),
                    user.getRoles()
            );
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            return new AuthResponseDTO(accessTokenResponse.token(), refreshToken, accessTokenResponse.expiresAt());

        } catch (BadCredentialsException ex) {
            log.error("login: {}", USER_PASSWORD_WRONG);
            throw new CustomAuthenticationException(USER_PASSWORD_WRONG);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            log.error("refreshToken: {}", INVALID_TOKEN_MESSAGE);
            throw new IllegalArgumentException(INVALID_TOKEN_MESSAGE);
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("refreshToken: {}", USER_NOT_FOUND);
                    return new EntityNotFoundException(USER_NOT_FOUND);
                });
        JwtService.TokenResponse accessTokenResponse = jwtService.generateAccessToken(
                user.getUsername(),
                user.getRoles());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());
        return new AuthResponseDTO(accessTokenResponse.token(), newRefreshToken, accessTokenResponse.expiresAt());
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    // Normal user management
    @Override
    @Transactional
    public Long create(UserCreateDTO req) {
        return createUser(req).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("getById: {}", USER_NOT_FOUND);
                    return new EntityNotFoundException(USER_NOT_FOUND);
                }));
    }

    @Override
    @Transactional
    public Long update(Long id, UserUpdateDTO req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("update: {}", USER_NOT_FOUND);
                    return new EntityNotFoundException(USER_NOT_FOUND);
                });

        if (req.username() != null && !req.username().equals(user.getUsername())) {
           if (userRepository.existsByUsername(req.username())) {
               log.error("update: {}", USERNAME_EXISTS);
               throw new IllegalArgumentException(USERNAME_EXISTS);
           }
           user.setUsername(req.username());
        }
        if (req.name() != null) user.setName(req.name());
        if (req.password() != null) {
            user.setPassword(passwordEncoder.encode(req.password()));
        }
        if (req.email() != null && !req.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.email())) {
                log.error("update: {}", EMAIL_EXISTS);
                throw new IllegalArgumentException(EMAIL_EXISTS);
            }
            user.setEmail(req.email());
        }
        User updatedUser = userRepository.save(user);

        return updatedUser.getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            log.error("delete: {}", USER_NOT_FOUND);
           throw new EntityNotFoundException(USER_NOT_FOUND);
        }

        userRepository.deleteById(id);
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

        return userMapper.toPagedResponse(userRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<UserDTO> getPagedSorted(int page, int size, String sortBy, String sortDirection) {
        if (page < 0 || size <= 0) {
            log.error("getPagedSorted: {}", INVALID_SIZE_OR_PAGE);
            throw new IllegalArgumentException(INVALID_SIZE_OR_PAGE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return userMapper.toPagedResponse(userRepository.findAll(pageable));
    }

    // Public
    // Get current user
    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userDetails.getUser();
        return userMapper.toDto(user);
    }

    // Update current user
    @Override
    @Transactional
    public Long updateCurrentUser(UserUpdateDTO req) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userDetails.getUser();
        if (req.username() != null && !req.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(req.username())) {
                log.error("updateCurrentUser: {}", USERNAME_EXISTS);
                throw new IllegalArgumentException(USERNAME_EXISTS);
            }
            user.setUsername(req.username());
        }
        if (req.name() != null) {
            user.setName(req.name());
        }
        if (req.password() != null) {
            user.setPassword(passwordEncoder.encode(req.password()));
        }
        if (req.email() != null && !req.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.email())) {
                log.error("updateCurrentUser: {}", EMAIL_EXISTS);
                throw new IllegalArgumentException(EMAIL_EXISTS);
            }
            user.setEmail(req.email());
        }
        User updatedUser = userRepository.save(user);
        return updatedUser.getId();
    }
}
