package com.supportTicket.supportTicket.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.config.JwtService;
import com.supportTicket.supportTicket.exceptions.PasswordException;
import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.ChangePasswordRecord;
import com.supportTicket.supportTicket.records.UserRecordResponse;
import com.supportTicket.supportTicket.records.UserRequestRecord;
import com.supportTicket.supportTicket.service.UserService;
import com.supportTicket.supportTicket.wrappers.JwtResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody UserRequestRecord user) {

		try {

			var authenticationToken = new UsernamePasswordAuthenticationToken(user.username(), user.password());

			var authentication = authenticationManager.authenticate(authenticationToken);

			var jwt = jwtService.generateToken(authentication.getName());

			Optional<User> userOp = userService.findByUsername(user.username());

			User userRes = userOp.get();

			JwtResponse response = new JwtResponse();

			response.setJwt(jwt);
			response.setRole(userRes.getRole());
			response.setUsername(userRes.getUsername());
			response.setImg(userRes.getImgPath());

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (AuthenticationException e) {
			return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody UserRequestRecord req) {

		Optional<User> user = userService.findByUsername(req.username());

		if (user.isPresent()) {
			return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
		}
		
		if (req.password().length() < 8) {
			throw new PasswordException("Password length needs to be more large");
		}
		
		if(!req.password().matches("(?=.*[A-Z])")) {
			throw new PasswordException("Password must have at least one capital letter");
		}
		
		if(!req.password().matches("(?=.*\\d)")) {
			throw new PasswordException("Password must have at least one number");
		}
		
		if(!req.password().matches("(?=.*[^A-Za-z0-9])")) {
			throw new PasswordException("Password must have at least one special character");
		}

		User u = new User();
		u.setUsername(req.username());
		u.setPassword(req.password());
		u.setRole((req.role() == null || req.role().isBlank()) ? "USER" : req.role());

		userService.saveUser(u);

		var jwt = jwtService.generateToken(u.getUsername());

		JwtResponse response = new JwtResponse();
		response.setJwt(jwt);
		response.setRole(u.getRole());
		response.setUsername(u.getUsername());
		response.setImg(u.getImgPath());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateUser(
			@RequestParam String originalUsername,
			@RequestParam String newUsername,
			@RequestParam(required = false) MultipartFile img) {

		Optional<User> existingUser = userService.findByUsername(newUsername);

		if (existingUser.isPresent() && !newUsername.equals(originalUsername)) {
			return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
		}

		UserRecordResponse userUpdated = userService.updateUser(originalUsername, newUsername, img);

		String newToken = jwtService.generateToken(userUpdated.username());

		JwtResponse response = new JwtResponse();
		response.setJwt(newToken);
		response.setUsername(userUpdated.username());
		response.setImg(userUpdated.imgUrl());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{userName}")
	public ResponseEntity<UserRecordResponse> getUserInfo(@PathVariable String userName) {

		UserRecordResponse response = userService.getUserInfo(userName);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/change-password")
	public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordRecord req) {
		userService.changePassword(req.username(), req.oldPassword(), req.newPassword());
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
}