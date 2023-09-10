package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/ipsator.com/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	// Testing Purpose
	@GetMapping
	public String hello() {
		return "Welcome to my application! :)";
	}
	
	@GetMapping("/welcome")
	public String hello2() {
		return userService.sendHello();
	}

	@PostMapping
	public ResponseEntity<User> registerUser(@RequestBody RegisterUserRecord registerUserRecord) {
		User savedUser = userService.registerUser(registerUserRecord);
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}
	
	@GetMapping("/send-verify-email")
	public ResponseEntity<String> sendVerifyEmail(@RequestBody LoginUserRecord loginUserRecord) throws UserException, MessagingException{
		return new ResponseEntity<String>(userService.sendVerifyEmail(loginUserRecord), HttpStatus.OK);
	}
	
}
