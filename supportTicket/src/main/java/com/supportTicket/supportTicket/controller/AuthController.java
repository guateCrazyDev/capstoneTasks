package com.supportTicket.supportTicket.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.CategoryRecord;
import com.supportTicket.supportTicket.records.UserRecord;
import com.supportTicket.supportTicket.records.UserRecordResponse;
import com.supportTicket.supportTicket.records.UserRequestRecord;
import com.supportTicket.supportTicket.config.JwtService;
import com.supportTicket.supportTicket.service.UserService;
import com.supportTicket.supportTicket.wrappers.JwtResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserService userService;

	@Autowired
	public AuthController(AuthenticationManager authenticationManager, JwtService jwtTokenUtil,
			UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtTokenUtil;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody UserRequestRecord user) {
		try {
			var authenticationToken = new UsernamePasswordAuthenticationToken(user.username(), user.password());
			var authentication = authenticationManager.authenticate(authenticationToken);
			var jwt = jwtService.generateToken(authentication.getName());
			Optional<User> useResOp = userService.findByUsername(user.username());
			User useRes = useResOp.get();
			JwtResponse response = new JwtResponse();
			response.setJwt(jwt);
			response.setRole(useRes.getRole());
			response.setUsername(useRes.getUsername());
			response.setImg(useRes.getPath());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (AuthenticationException e) {
			return new ResponseEntity<>("Invalid credentials", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/register")
	public String register(@RequestBody UserRequestRecord req) {
		if (req.username() == null || req.username().isBlank() ||
				req.password() == null || req.password().isBlank()) {
			return "username and password are required";
		}

		try {
			Optional<User> user = userService.findByUsername(req.username());
			User us = user.get();
		} catch (Exception e) {
			User u = new User();
			u.setUsername(req.username());
			u.setPassword(req.password());
			u.setRole((req.role() == null || req.role().isBlank()) ? "USER" : req.role());

			userService.saveUser(u);
			return "user created";
		}

		return "User already exist";
	}

	@PutMapping("/update")
	public ResponseEntity<Object> updateUser(@RequestParam("originalUser") String originalUser,
			@RequestParam("newUser") String newUser, @RequestParam(value="img", required=false) MultipartFile img) {
		userService.updateUser(originalUser, newUser, img);
		var jwt = jwtService.generateToken(newUser);
		Optional<User> useResOp = userService.findByUsername(newUser);
		User useRes = useResOp.get();
		JwtResponse response = new JwtResponse();
		response.setRole(useRes.getRole());
		response.setUsername(useRes.getUsername());
		response.setImg(useRes.getPath());
		response.setJwt(jwt);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/user/{userName}")
	public ResponseEntity<UserRecordResponse> getUserInfo(@PathVariable String userName) {
		UserRecordResponse response = userService.getUserInfo(userName);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}