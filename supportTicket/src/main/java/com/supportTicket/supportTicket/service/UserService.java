package com.supportTicket.supportTicket.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.UserRecordResponse;

public interface UserService {

	Optional<User> findByUsername(String username);

	User saveUser(User user);

	UserRecordResponse updateUser(String userOg, String newUser, MultipartFile img);

	boolean changePassword(String username, String oldPassword, String newPassword);

	UserRecordResponse getUserInfo(String userName);
}