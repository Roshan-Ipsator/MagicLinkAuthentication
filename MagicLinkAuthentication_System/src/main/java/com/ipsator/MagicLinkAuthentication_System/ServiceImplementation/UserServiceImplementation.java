package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import jakarta.mail.MessagingException;

@Service
public class UserServiceImplementation implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailServiceImplementation emailServiceImplementation;

	@Override
	public User registerUser(RegisterUserRecord registerUserRecord) throws UserException {
		User existingUser = userRepository.findByEmailId(registerUserRecord.emailId());
		if (existingUser != null) {
			throw new UserException("Email Id already exists. Please, directly log in!");
		}

		User newUser = new User();
		newUser.setFirstName(registerUserRecord.firstName());
		newUser.setLastName(registerUserRecord.lastName());
		newUser.setEmailId(registerUserRecord.emailId());
		newUser.setGender(registerUserRecord.gender());
		newUser.setAge(registerUserRecord.age());

		return userRepository.save(newUser);
	}
	
	@Override
	public String sendVerifyEmail(LoginUserRecord loginUserRecord) throws UserException, MessagingException {
		User existingUser = userRepository.findByEmailId(loginUserRecord.emailId());
		if (existingUser == null) {
			throw new UserException("Email Id is not registered. Please, sign up first!");
		}
		String to = loginUserRecord.emailId();
		String subject = "Check out this URL to verify";
		String url = "https://drive.google.com/file/d/1cVdOHxChQefgr70Nm69S0axmJTSW3THA/view?usp=sharing";

		emailServiceImplementation.sendEmailWithUrl(to, subject, url);

		return "email-sent";
	}
}
