package com.silvermaiden.mywaifu.services.implementations;

import com.silvermaiden.mywaifu.models.entities.CustomUserDetails;
import com.silvermaiden.mywaifu.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.silvermaiden.mywaifu.common.constants.ErrorMessage.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class UserDetailsImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }
}
