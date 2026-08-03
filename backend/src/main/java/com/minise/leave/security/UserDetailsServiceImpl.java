package com.minise.leave.security;

import com.minise.leave.repository.UserRepository;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Cho Spring Security biet lay thong tin nguoi dung o dau. Dung luon builder co san
 * thay vi tu viet class UserDetails rieng, do them mot class cho bon field thi khong bo.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        builder.password(user.getPasswordHash());
        builder.roles(user.getRole().name());
        return builder.build();
    }
}
