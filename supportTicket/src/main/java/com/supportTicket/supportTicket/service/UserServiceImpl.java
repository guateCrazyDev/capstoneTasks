package com.supportTicket.supportTicket.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.exceptions.ElementNotFoundException;
import com.supportTicket.supportTicket.exceptions.ImageNotFoundException;
import com.supportTicket.supportTicket.model.Category;
import com.supportTicket.supportTicket.model.Comments;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.UserRecordResponse;
import com.supportTicket.supportTicket.repository.CommentsRepo;
import com.supportTicket.supportTicket.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final Path fileStorageLocation;
	

	public UserServiceImpl(@Value("${file.upload-dir}") String uploadDir,UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de carga.", ex);
        }
	}
	

    private String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try { 
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "C:/uploads/images/"+fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Error al guardar el archivo " + fileName, ex);
        } 
    }

    @Autowired
	public CommentsRepo commentsRepo;
    
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
				String path = "";
				user.setUsername(newUser);
				if(img != null) {
					path = storeFile(img);
					user.setPath(path);
				}else {
					path = user.getPath();
					user.setPath(path);
				}
				user.setPassword(user.getPassword());
				for(Comments comment : commentsRepo.findByUserNameComent(userOg)) {
					comment.setUserNameComent(newUser);
					comment.setUserImgPath(path);
					commentsRepo.save(comment);
				}
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
			UserRecordResponse response = new UserRecordResponse(userName, userRes.getRole(), userRes.getPath());
			return response;
		} else { 
			throw new ElementNotFoundException("User not found");
		}
	}
}
