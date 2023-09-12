package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//import javax.security.auth.login.LoginException;
//
//import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.LoginKeys;
import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUserRegistration;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.LoginKeysRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.TemporaryUsersRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;
//import com.ipsator.MagicLinkAuthentication_System.Utility.JwtUtil;

import jakarta.mail.MessagingException;

/**
 * The implementation class of UserService interface
 * 
 * @author Roshan
 *
 */
@Service
public class UserServiceImplementation implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TemporaryUsersRepository temporaryUsersRepository;

	@Autowired
	private LoginKeysRepository loginKeysRepository;

	@Autowired
	private LoginEmailServiceImplementation loginEmailServiceImplementation;

	@Autowired
	private SignupEmailServiceImplementation signupEmailServiceImplementation;

	/**
	 * 
	 * The method to temporarily register a user before final verification
	 * 
	 * @param registerUserRecord object of RegisterUserRecord class
	 * 
	 * @return temporaryUser object
	 * 
	 * @throws UserException, MessagingException
	 * 
	 */
	@Override
	public ServiceResponse<Object> registerUserInit(RegisterUserRecord registerUserRecord)
			throws MessagingException {
		User existingUser = userRepository.findByEmailId(registerUserRecord.emailId());
		if (existingUser != null) {
			// throw new UserException("Email Id already exists. Please, directly log in!");
			ServiceResponse<Object> response = new ServiceResponse<>(false, null, "Email Id already exists. Please, directly log in!");
			return response;
		}

		PreFinalUserRegistration newTemporaryUser = new PreFinalUserRegistration();
		newTemporaryUser.setFirstName(registerUserRecord.firstName());
		newTemporaryUser.setLastName(registerUserRecord.lastName());
		newTemporaryUser.setEmailId(registerUserRecord.emailId());
		newTemporaryUser.setGender(registerUserRecord.gender());
		newTemporaryUser.setAge(registerUserRecord.age());
		String registrationKey = UUID.randomUUID().toString();
		newTemporaryUser.setRegistrationKey(registrationKey);
		newTemporaryUser.setKeyGenerationTime(LocalDateTime.now());
		newTemporaryUser.setUserStatus("Verification Pending");

		String to = registerUserRecord.emailId();
		String subject = "Check out this URL to complete your registration.";
		String url = "http://localhost:8659/ipsator.com/user/finalRegistration?registrationKey=" + registrationKey;

		signupEmailServiceImplementation.sendEmailWithUrl(to, subject, url);

		// return temporaryUsersRepository.save(newTemporaryUser);
		
		PreFinalUserRegistration savedTemporaryUser = temporaryUsersRepository.save(newTemporaryUser);
		Map data = new HashMap();
		data.put("user", savedTemporaryUser);
		ServiceResponse<Object> response = new ServiceResponse<>(true, data, "Temporarily created the user.");
		return response;
	}

	/**
	 * 
	 * The method to finally register a user after final verification
	 * 
	 * @param registrationKey a string to verify the user for complete registration
	 * 
	 * @return User object
	 * 
	 * @throws UserException
	 * 
	 */
	@Override
	public ServiceResponse<Object> registerUserFinal(String registrationKey) {
		PreFinalUserRegistration existingTemporaryUser = temporaryUsersRepository
				.findByRegistrationKey(registrationKey);
		if (existingTemporaryUser != null) {
			long noOfMinutes = existingTemporaryUser.getKeyGenerationTime().until(LocalDateTime.now(),
					ChronoUnit.MINUTES);

			if (noOfMinutes > 15) {
				throw new UserException("Registration key has expired. Please try again!");
			}

			User newUser = new User();
			newUser.setUserId(existingTemporaryUser.getUserId());
			newUser.setFirstName(existingTemporaryUser.getFirstName());
			newUser.setLastName(existingTemporaryUser.getLastName());
			newUser.setEmailId(existingTemporaryUser.getEmailId());
			newUser.setGender(existingTemporaryUser.getGender());
			newUser.setAge(existingTemporaryUser.getAge());

			existingTemporaryUser.setUserStatus("Verified");

			temporaryUsersRepository.save(existingTemporaryUser);

			// return userRepository.save(newUser);
			
			User savedUser = userRepository.save(newUser);
			Map data = new HashMap();
			data.put("user", savedUser);
			ServiceResponse<Object> response = new ServiceResponse<>(true, data, "User registered successfully.");
			return response;
		}

		// throw new UserException("Invalid key. Please try with a valid key or try registering once again.");
		
		ServiceResponse<Object> response = new ServiceResponse<>(false, null, "Invalid key. Please try with a valid key or try registering once again.");
		return response;
	}

	/**
	 * 
	 * The method to send a verification email for the final login
	 * 
	 * @param loginUserRecord object of LoginUserRecord contains the user's email id
	 * 
	 * @return loginKey a string to verify the user for final login
	 * 
	 * @throws UserException, MessagingException
	 * 
	 */
	@Override
	public String sendVerifyEmail(LoginUserRecord loginUserRecord) throws UserException, MessagingException {
		User existingUser = userRepository.findByEmailId(loginUserRecord.emailId());
		if (existingUser == null) {
			throw new UserException("Email Id is not registered. Please, sign up first!");
		}

//		String token = JwtUtil.generateToken(loginUserRecord.emailId());

		LoginKeys newLoginKey = new LoginKeys();
		String loginKey = UUID.randomUUID().toString();
		newLoginKey.setUserId(existingUser.getUserId());
		newLoginKey.setLoginKey(loginKey);
		newLoginKey.setKeyGenerationTime(LocalDateTime.now());
		loginKeysRepository.save(newLoginKey);

		String to = loginUserRecord.emailId();
		String subject = "Check out this URL to verify";
		String url = "http://localhost:8659/ipsator.com/user/finalLogin?loginKey=" + loginKey;

		loginEmailServiceImplementation.sendEmailWithUrl(to, subject, url);

		return "email-sent. Login Key: " + loginKey;
//		return token;
	}

	/**
	 * 
	 * The method for the final login after final login verification
	 * 
	 * @param loginKey a string to verify the user for final login
	 * 
	 * @return User object
	 * 
	 * @throws UserException
	 * 
	 */
	@Override
	public User userLoginFinal(String loginKey) throws UserException {
		LoginKeys existingLoginKey = loginKeysRepository.findByLoginKey(loginKey);
		if (existingLoginKey != null) {
			long noOfMinutes = existingLoginKey.getKeyGenerationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);

			if (noOfMinutes > 15) {
				throw new UserException("Login Key expired. Please, try again!");
			}

			User existingUser = userRepository.findById(existingLoginKey.getUserId()).get();

			return existingUser;
		}
		throw new UserException("Invalid login key. Please, try again!");
	}

	/**
	 * 
	 * The method to use in the testing API that returns a string for confirmation
	 * 
	 * @return hi --> a String
	 * 
	 */
	@Override
	public String sendHello() {
		return "hi";
	}

}
