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

	@Override
	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public UserResponseRecord updateUser(String userOg, String newUser, MultipartFile img) {

		Optional<User> userUpdate = userRepo.findByUsername(userOg);

		if (userUpdate.isEmpty()) {
			throw new ElementNotFoundException("User does not exists");
		}

		User user = userUpdate.get();
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

	@Override
	public boolean changePassword(String username, String oldPassword, String newPassword) {

		Optional<User> userOp = userRepo.findByUsername(username);

		if (userOp.isEmpty()) {
			throw new ElementNotFoundException("User not found");
		}

		User user = userOp.get();

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new PasswordException("Incorrect current password");
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		userRepo.save(user);

		return true;
	}

	@Override
	public UserResponseRecord getUserInfo(String userName) {

		Optional<User> userBase = userRepo.findByUsername(userName);

		if (userBase.isEmpty()) {
			throw new ElementNotFoundException("User not found");
		}

		User user = userBase.get();

		return new UserResponseRecord(
				user.getUsername(),
				user.getRole(),
				user.getImgPath());
	}
}