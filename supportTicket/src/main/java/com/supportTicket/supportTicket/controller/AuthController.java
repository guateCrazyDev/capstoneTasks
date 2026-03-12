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

import com.supportTicket.supportTicket.model.User;
import com.supportTicket.supportTicket.records.UserRequestRecord;
import com.supportTicket.supportTicket.security.JwtTokenUtil;
import com.supportTicket.supportTicket.service.UserService;
import com.supportTicket.supportTicket.wrappers.JwtResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:5173/")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserRequestRecord user) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(user.username(), user.password());
            var authentication = authenticationManager.authenticate(authenticationToken);
            var jwt = jwtTokenUtil.generateToken(authentication.getName());
            JwtResponse response = new JwtResponse();
            Optional<User> useResOp = userService.findByUsername(user.username());
            User useRes = useResOp.get();
            response.setJwt(jwt);
            response.setRole(useRes.getRole());
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (AuthenticationException e) {
        	return new ResponseEntity<>("Invalid credentials",HttpStatus.NOT_FOUND);
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
	    }catch(Exception e) {
	        User u = new User();
	        u.setUsername(req.username());
	        u.setPassword(req.password()); 
	        u.setRole((req.role() == null || req.role().isBlank()) ? "USER" : req.role());
	
	        userService.saveUser(u);
	        return "user created";
	    }
	
		return "User already exist";
	}

}