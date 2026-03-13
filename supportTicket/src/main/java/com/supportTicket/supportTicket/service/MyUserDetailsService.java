package com.supportTicket.supportTicket.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws ElementNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ElementNotFoundException("User not found: " + username));
    }
}