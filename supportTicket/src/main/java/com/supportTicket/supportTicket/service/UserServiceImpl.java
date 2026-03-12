package com.supportTicket.supportTicket.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.exceptions.ImageNotFoundException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.UserRecordResponse;
import com.supportTicket.supportTicket.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public boolean updateUser(String userOg, String newUser, MultipartFile img) {
		Optional<User> userUpdate = userRepository.findByUsername(userOg);
		User user = userUpdate.get();
		if (user != null) {
			try {
				user.setUsername(newUser);
				user.setImg(img.getBytes());
				userRepository.save(user);
				return true;
			} catch (Exception e) {
				throw new ImageNotFoundException("It was not possible to process the image");
			}
		} else {
			throw new ElementNotFoundException("This user not exists");
		}
	}

	public UserRecordResponse getUserInfo(String userName) {
		Optional<User> userBase = userRepository.findByUsername(userName);
		User userRes = userBase.get();
		if (userRes != null) {
			UserRecordResponse response = new UserRecordResponse(userName, userRes.getRole(), userRes.getImg());
			return response;
		} else {
			throw new ElementNotFoundException("User not found");
		}
	}
}
