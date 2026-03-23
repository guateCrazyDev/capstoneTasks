package com.supportTicket.supportTicket.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.exceptions.PasswordException;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.UserResponseRecord;
import com.supportTicket.supportTicket.repository.UserRepo;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FileService fileService;

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepo.findByUsername(username);
	}

	/* ===================== SAVE USER ===================== */

	@Override
	public User saveUser(User user, MultipartFile img) {

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		if (img != null && !img.isEmpty()) {
			String fileName = fileService.uploadSingleImage(img, "users");
			user.setImgPath(fileName);
		}

		return userRepo.save(user);
	}

	/* ===================== UPDATE USER ===================== */

	@Override
	public UserResponseRecord updateUser(
			String userOg,
			String newUser,
			MultipartFile img) {

		User user = userRepo.findByUsername(userOg)
				.orElseThrow(() -> new ElementNotFoundException("User does not exist"));

		user.setUsername(newUser);

		if (img != null && !img.isEmpty()) {
			String fileName = fileService.uploadSingleImage(img, "users");
			user.setImgPath(fileName);
		}

		userRepo.save(user);

		return new UserResponseRecord(
				user.getUsername(),
				user.getRole(),
				user.getImgPath());
	}

	/* ===================== CHANGE PASSWORD ===================== */

	@Override
	public boolean changePassword(
			String username,
			String oldPassword,
			String newPassword) {

		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ElementNotFoundException("User not found"));

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new PasswordException("Incorrect current password");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.save(user);

		return true;
	}

	/* ===================== GET USER INFO ===================== */

	@Override
	public UserResponseRecord getUserInfo(String userName) {

		User user = userRepo.findByUsername(userName)
				.orElseThrow(() -> new ElementNotFoundException("User not found"));

		return new UserResponseRecord(
				user.getUsername(),
				user.getRole(),
				user.getImgPath());
	}
}