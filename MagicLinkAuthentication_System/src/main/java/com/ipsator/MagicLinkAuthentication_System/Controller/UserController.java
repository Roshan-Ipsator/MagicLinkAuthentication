package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

@RestController
@RequestMapping("/ipsator.com/user")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping
	public ResponseEntity<User> registerUser(@RequestBody RegisterUserRecord registerUserDto) {
		User savedUser = userService.registerUser(registerUserDto);
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}
}
