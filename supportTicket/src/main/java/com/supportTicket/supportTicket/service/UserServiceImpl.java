package com.supportTicket.supportTicket.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.exceptions.PasswordException;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.UserRecordResponse;
import com.supportTicket.supportTicket.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FileService fileService;

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public UserRecordResponse updateUser(String userOg, String newUser, MultipartFile img) {

		Optional<User> userUpdate = userRepository.findByUsername(userOg);

		if (userUpdate.isEmpty()) {
			throw new ElementNotFoundException("User does not exists");
		}

		User user = userUpdate.get();
		user.setUsername(newUser);

		if (img != null && !img.isEmpty()) {
			String fileName = fileService.saveImage(img);
			user.setImgPath(fileName);
		}

		userRepository.save(user);

		return new UserRecordResponse(
				user.getUsername(),
				user.getRole(),
				user.getImgPath());
	}

	@Override
	public boolean changePassword(String username, String oldPassword, String newPassword) {

		Optional<User> userOp = userRepository.findByUsername(username);

		if (userOp.isEmpty()) {
			throw new ElementNotFoundException("User not found");
		}

		User user = userOp.get();

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new PasswordException("Incorrect current password");
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		userRepository.save(user);

		return true;
	}

	@Override
	public UserRecordResponse getUserInfo(String userName) {

		Optional<User> userBase = userRepository.findByUsername(userName);

		if (userBase.isEmpty()) {
			throw new ElementNotFoundException("User not found");
		}

		User user = userBase.get();

		return new UserRecordResponse(
				user.getUsername(),
				user.getRole(),
				user.getImgPath());
	}
}