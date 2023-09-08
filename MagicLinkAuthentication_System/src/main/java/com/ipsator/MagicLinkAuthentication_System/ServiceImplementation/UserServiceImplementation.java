package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

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
}
