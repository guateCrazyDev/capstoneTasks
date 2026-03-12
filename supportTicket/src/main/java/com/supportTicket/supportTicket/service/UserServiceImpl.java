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
import com.supportTicket.supportTicket.repository.UserRepo;

@Service
public class UserServiceImpl implements UserService{

	private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepository, PasswordEncoder passwordEncoder) {
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
    
    public boolean updateUser(String userOg,String newUser,MultipartFile img) {
		Optional<User> userUpdate = userRepository.findByUsername(userOg);
		User user = userUpdate.get();
		if(user != null) {
    	try {
    		user.setUsername(newUser);
			user.setImg(img.getBytes());
			userRepository.save(user);
			return true;
		}catch(Exception e) {
			throw new ImageNotFoundException("It was not possible to process the image");
		}
    	}else {
    		throw new ElementNotFoundException("This user not exists");
    	}
    }
}
