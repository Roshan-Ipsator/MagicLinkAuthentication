package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.LoginKeys;
import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUserRegistration;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.LoginKeysRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.TemporaryUsersRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

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
	public ServiceResponse<PreFinalUserRegistration> registerUserInit(RegisterUserRecord registerUserRecord)
			throws MessagingException {
		User existingUser = userRepository.findByEmailId(registerUserRecord.emailId());
		if (existingUser != null) {
			ServiceResponse<PreFinalUserRegistration> response = new ServiceResponse<>(false, null, "Email Id already exists. Please, directly log in!");
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
		
		PreFinalUserRegistration savedTemporaryUser = temporaryUsersRepository.save(newTemporaryUser);
		ServiceResponse<PreFinalUserRegistration> response = new ServiceResponse<>(true, savedTemporaryUser, "Temporarily created the user. Registration verification link has been sent to the email. It will expire after 15 minutes.");
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
	public ServiceResponse<User> registerUserFinal(String registrationKey) {
		PreFinalUserRegistration existingTemporaryUser = temporaryUsersRepository
				.findByRegistrationKey(registrationKey);
		if (existingTemporaryUser != null) {
			long noOfMinutes = existingTemporaryUser.getKeyGenerationTime().until(LocalDateTime.now(),
					ChronoUnit.MINUTES);

			if (noOfMinutes > 15) {
				ServiceResponse<User> response = new ServiceResponse<>(false, null, "Registration key has expired. Please try again!");
				return response;
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
			
			User savedUser = userRepository.save(newUser);
			ServiceResponse<User> response = new ServiceResponse<>(true, savedUser, "User registered successfully.");
			return response;
		}
		
		ServiceResponse<User> response = new ServiceResponse<>(false, null, "Invalid key. Please try with a valid key or try registering once again.");
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
	public ServiceResponse<String> sendVerifyEmail(LoginUserRecord loginUserRecord) throws MessagingException {
		User existingUser = userRepository.findByEmailId(loginUserRecord.emailId());
		if (existingUser == null) {
			ServiceResponse<String> response = new ServiceResponse<>(false, null, "Email Id is not registered. Please, sign up first!");
			return response;
		}

		// String token = JwtUtil.generateToken(loginUserRecord.emailId());

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

		ServiceResponse<String> response = new ServiceResponse<>(true, "Email sent with login verification link. It will expire after 15 minutes. Login Key: " + loginKey, "Email sent.");
		return response;
		
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
	public ServiceResponse<User> userLoginFinal(String loginKey) {
		LoginKeys existingLoginKey = loginKeysRepository.findByLoginKey(loginKey);
		if (existingLoginKey != null) {
			long noOfMinutes = existingLoginKey.getKeyGenerationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);

			if (noOfMinutes > 15) {
				ServiceResponse<User> response = new ServiceResponse<>(false, null, "Login Key has expired. Please, try again!");
				return response;
			}

			User existingUser = userRepository.findById(existingLoginKey.getUserId()).get();

			ServiceResponse<User> response = new ServiceResponse<>(true, existingUser, "User logged in successfully.");
			return response;
		}
		ServiceResponse<User> response = new ServiceResponse<>(false, null, "Invalid login key. Please try with a valid key or try logging in once again.");
		return response;
	}

}
