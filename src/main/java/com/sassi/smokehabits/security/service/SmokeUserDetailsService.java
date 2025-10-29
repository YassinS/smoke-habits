package com.sassi.smokehabits.security.service;


import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.UserRepository;
import com.sassi.smokehabits.security.SmokeUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SmokeUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SmokeUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new SmokeUserDetails(user);
    }
}

