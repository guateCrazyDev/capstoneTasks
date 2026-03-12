package com.supportTicket.supportTicket.service;

import java.util.Optional;

import com.supportTicket.supportTicket.model.User;

public interface UserService {
	Optional<User> findByUsername(String username);
    User saveUser(User user);
}
