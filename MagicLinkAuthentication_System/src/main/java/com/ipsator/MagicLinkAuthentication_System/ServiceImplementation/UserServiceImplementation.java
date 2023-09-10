package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.LoginKeys;
import com.ipsator.MagicLinkAuthentication_System.Entity.TemporaryUsers;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.LoginKeysRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.TemporaryUsersRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;
import com.ipsator.MagicLinkAuthentication_System.Utility.JwtUtil;

import jakarta.mail.MessagingException;

@Service
public class UserServiceImplementation implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TemporaryUsersRepository temporaryUsersRepository;

	@Autowired
	private LoginKeysRepository loginKeysRepository;

	@Autowired
	private EmailServiceImplementation emailServiceImplementation;

	@Override
	public TemporaryUsers registerUserInit(RegisterUserRecord registerUserRecord)
			throws UserException, MessagingException {
		User existingUser = userRepository.findByEmailId(registerUserRecord.emailId());
		if (existingUser != null) {
			throw new UserException("Email Id already exists. Please, directly log in!");
		}

		TemporaryUsers newTemporaryUser = new TemporaryUsers();
		newTemporaryUser.setFirstName(registerUserRecord.firstName());
		newTemporaryUser.setLastName(registerUserRecord.lastName());
		newTemporaryUser.setEmailId(registerUserRecord.emailId());
		newTemporaryUser.setGender(registerUserRecord.gender());
		newTemporaryUser.setAge(registerUserRecord.age());
		String registrationKey = UUID.randomUUID().toString();
		newTemporaryUser.setRegistrationKey(registrationKey);

		String to = registerUserRecord.emailId();
		String subject = "Check out this URL to complete your registration.";
		String url = "http://localhost:8659/ipsator.com/user/finalRegistration?registrationKey="+registrationKey;

		emailServiceImplementation.sendEmailWithUrl(to, subject, url);

		return temporaryUsersRepository.save(newTemporaryUser);
	}

	@Override
	public User registerUserFinal(String registrationKey) throws UserException {
		TemporaryUsers existingTemporaryUser = temporaryUsersRepository.findByRegistrationKey(registrationKey);
		if (existingTemporaryUser != null) {
			User newUser = new User();
			newUser.setId(existingTemporaryUser.getId());
			newUser.setFirstName(existingTemporaryUser.getFirstName());
			newUser.setLastName(existingTemporaryUser.getLastName());
			newUser.setEmailId(existingTemporaryUser.getEmailId());
			newUser.setGender(existingTemporaryUser.getGender());
			newUser.setAge(existingTemporaryUser.getAge());
			
			temporaryUsersRepository.delete(existingTemporaryUser);

			return userRepository.save(newUser);
		}

		throw new UserException("Invalid key. Please try with a valid key or try registring once again.");
	}

	@Override
	public String sendVerifyEmail(LoginUserRecord loginUserRecord) throws UserException, MessagingException {
		User existingUser = userRepository.findByEmailId(loginUserRecord.emailId());
		if (existingUser == null) {
			throw new UserException("Email Id is not registered. Please, sign up first!");
		}

//		String token = JwtUtil.generateToken(loginUserRecord.emailId());

		LoginKeys newLoginKey = new LoginKeys();
		String loginKey = UUID.randomUUID().toString();
		newLoginKey.setUserId(existingUser.getId());
		newLoginKey.setLoginKey(loginKey);
		newLoginKey.setKeyGenerationTime(LocalDateTime.now());
		loginKeysRepository.save(newLoginKey);

		String to = loginUserRecord.emailId();
		String subject = "Check out this URL to verify";
		String url = "http://localhost:8659/ipsator.com/user/finalLogin?loginKey="+loginKey;

		emailServiceImplementation.sendEmailWithUrl(to, subject, url);

		return "email-sent. Login Key: "+loginKey ;
//		return token;
	}

	@Override
	public User userLoginFinal(String loginKey) throws UserException {
		LoginKeys existingLoginKey = loginKeysRepository.findByLoginKey(loginKey);
		if(existingLoginKey!=null) {
			long noOfMinutes = existingLoginKey.getKeyGenerationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);

			if(noOfMinutes>15) {
				throw new UserException("Login Key expired. Please, try again!");
			}
			
			User existingUser = userRepository.findById(existingLoginKey.getUserId()).get();
			
			return existingUser;
		}
		throw new UserException("Invalid login key. Please, try again!");
	}

	@Override
	public String sendHello() {
		return "hi";
	}

}
