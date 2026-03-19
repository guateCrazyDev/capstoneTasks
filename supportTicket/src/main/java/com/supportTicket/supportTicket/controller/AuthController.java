package com.supportTicket.supportTicket.controller;

import java.util.Optional;

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

/*
 * 
 * */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserService userService;

	public AuthController(AuthenticationManager authenticationManager,
			JwtService jwtService,
			UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userService = userService;
	}

	/**
	 * LOGIN
	 * --------------------------------
	 * - Authenticates user credentials
	 * - Generates JWT
	 * - Returns user info + token
	 */
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody UserRequestRecord user) {

		try {
			var authToken = new UsernamePasswordAuthenticationToken(
					user.username(),
					user.password());

			var authentication = authenticationManager.authenticate(authToken);

			String jwt = jwtService.generateToken(authentication.getName());

			User userRes = userService.findByUsername(user.username())
					.orElseThrow();

			JwtResponse response = new JwtResponse();
			response.setJwt(jwt);
			response.setRole(userRes.getRole());
			response.setUsername(userRes.getUsername());
			response.setImg(userRes.getImgPath());

			return ResponseEntity.ok(response);

		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Invalid credentials");
		}
	}

	/**
	 * REGISTER
	 * --------------------------------
	 * - Validates password rules
	 * - Creates new user
	 * - Assigns default role
	 * - Returns JWT
	 */
	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody UserRequestRecord req) {

		if (userService.findByUsername(req.username()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("User already exists");
		}

		validatePassword(req.password());

		User user = new User();
		user.setUsername(req.username());
		user.setPassword(req.password());

		// Always enforce ROLE_ prefix
		String role = (req.role() == null || req.role().isBlank())
				? "ROLE_USER"
				: normalizeRole(req.role());

		user.setRole(role);

		userService.saveUser(user);

		String jwt = jwtService.generateToken(user.getUsername());

		JwtResponse response = new JwtResponse();
		response.setJwt(jwt);
		response.setRole(user.getRole());
		response.setUsername(user.getUsername());
		response.setImg(user.getImgPath());

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * UPDATE USER
	 * --------------------------------
	 * - Updates username and optional image
	 * - Generates new JWT
	 */
	@PutMapping("/update")
	public ResponseEntity<?> updateUser(
			@RequestParam String originalUsername,
			@RequestParam String newUsername,
			@RequestParam(required = false) MultipartFile img) {

		Optional<User> existingUser = userService.findByUsername(newUsername);

		if (existingUser.isPresent() && !newUsername.equals(originalUsername)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Username already exists");
		}

		UserRecordResponse updated = userService.updateUser(originalUsername, newUsername, img);

		String newToken = jwtService.generateToken(updated.username());

		JwtResponse response = new JwtResponse();
		response.setJwt(newToken);
		response.setUsername(updated.username());
		response.setImg(updated.imgUrl());

		return ResponseEntity.ok(response);
	}

	/**
	 * GET USER INFO
	 */
	@GetMapping("/user/{username}")
	public ResponseEntity<UserRecordResponse> getUserInfo(@PathVariable String username) {
		return ResponseEntity.ok(userService.getUserInfo(username));
	}

	/**
	 * CHANGE PASSWORD
	 */
	@PutMapping("/change-password")
	public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordRecord req) {
		userService.changePassword(req.username(), req.oldPassword(), req.newPassword());
		return ResponseEntity.ok(true);
	}

	/**
	 * PASSWORD VALIDATION
	 * --------------------------------
	 * Enforces strong password rules
	 */
	private void validatePassword(String password) {

		if (password.length() < 8) {
			throw new PasswordException("Password must be at least 8 characters");
		}

		if (!password.matches(".*[A-Z].*")) {
			throw new PasswordException("Password must contain at least one uppercase letter");
		}

		if (!password.matches(".*\\d.*")) {
			throw new PasswordException("Password must contain at least one number");
		}

		if (!password.matches(".*[^A-Za-z0-9].*")) {
			throw new PasswordException("Password must contain at least one special character");
		}
	}

	/**
	 * ROLE NORMALIZATION
	 * --------------------------------
	 * Ensures roles always follow Spring Security format: ROLE_XXX
	 */
	private String normalizeRole(String role) {
		return role.startsWith("ROLE_") ? role : "ROLE_" + role;
	}
}