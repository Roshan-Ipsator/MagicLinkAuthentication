package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.LoginKeys;
import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUsers;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.LoginKeysRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.PreFinalUsersRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Security.JwtHelper;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

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
	private PreFinalUsersRepository temporaryUsersRepository;

	@Autowired
	private LoginKeysRepository loginKeysRepository;

	@Autowired
	private LoginEmailServiceImplementation loginEmailServiceImplementation;

	@Autowired
	private SignupEmailServiceImplementation signupEmailServiceImplementation;

	@Autowired
	private AuthenticationManager manager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtHelper helper;

	@Autowired
	private PasswordEncoder passwordEncoder;

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
	public ServiceResponse<PreFinalUsers> preFinalUserRegistration(RegisterUserRecord registerUserRecord)
			throws MessagingException {
		Optional<User> existingUserOpt = userRepository.findByEmailId(registerUserRecord.emailId());
		if (existingUserOpt.isPresent()) {
			ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(false, null,
					"Email Id already exists. Please, directly log in!");
			return response;
		}

		PreFinalUsers newTemporaryUser = new PreFinalUsers();
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
		String url = "http://localhost:8659/ipsator.com/user/final-registration?registrationKey=" + registrationKey;

		signupEmailServiceImplementation.sendEmailWithUrl(to, subject, url);

		PreFinalUsers savedTemporaryUser = temporaryUsersRepository.save(newTemporaryUser);
		ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(true, savedTemporaryUser,
				"Temporarily created the user. Registration verification link has been sent to the email. It will expire after 15 minutes.");
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
	public ServiceResponse<User> finalUserRegistration(String registrationKey) {
		PreFinalUsers existingTemporaryUser = temporaryUsersRepository.findByRegistrationKey(registrationKey);
		if (existingTemporaryUser != null) {
			Optional<User> existingUserOpt = userRepository.findByEmailId(existingTemporaryUser.getEmailId());
			if (existingUserOpt.isPresent()) {
				ServiceResponse<User> response = new ServiceResponse<>(false, null,
						"Email Id already exists. Please, directly log in!");
				return response;
			}
			long noOfMinutes = existingTemporaryUser.getKeyGenerationTime().until(LocalDateTime.now(),
					ChronoUnit.MINUTES);

			if (noOfMinutes > 15) {
				ServiceResponse<User> response = new ServiceResponse<>(false, null,
						"Registration key has expired. Please try again!");
				return response;
			}

			User newUser = new User();
			newUser.setUserId(existingTemporaryUser.getUserId());
			newUser.setFirstName(existingTemporaryUser.getFirstName());
			newUser.setLastName(existingTemporaryUser.getLastName());
			newUser.setEmailId(existingTemporaryUser.getEmailId());
			newUser.setGender(existingTemporaryUser.getGender());
			newUser.setAge(existingTemporaryUser.getAge());
			newUser.setPassKey(passwordEncoder.encode(registrationKey));

			existingTemporaryUser.setUserStatus("Verified");

			temporaryUsersRepository.save(existingTemporaryUser);

			User savedUser = userRepository.save(newUser);
			ServiceResponse<User> response = new ServiceResponse<>(true, savedUser, "User registered successfully.");
			return response;
		}

		ServiceResponse<User> response = new ServiceResponse<>(false, null,
				"Invalid key. Please try with a valid key or try registering once again.");
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
	public ServiceResponse<String> preFinalUserLogin(LoginUserRecord loginUserRecord) throws MessagingException {
		Optional<User> existingUserOpt = userRepository.findByEmailId(loginUserRecord.emailId());
		if (existingUserOpt.isEmpty()) {
			ServiceResponse<String> response = new ServiceResponse<>(false, null,
					"Email Id is not registered. Please, sign up first!");
			return response;
		}

		User existingUser = existingUserOpt.get();
		// Check if it is the first login
		Optional<LoginKeys> loginKeyDetails = loginKeysRepository.findById(existingUser.getUserId());
		if (loginKeyDetails.isPresent()) {
			// not first login
			LoginKeys existingLoginKeyDetails = loginKeyDetails.get();

			// check if user is temporarily locked or not
			// if user is not locked temporarily
			if (existingLoginKeyDetails.getTrackingStartTime().isBefore(LocalDateTime.now())) {
				long currentIntervalInSeconds = ChronoUnit.SECONDS
						.between(existingLoginKeyDetails.getTrackingStartTime(), LocalDateTime.now());

				// if interval is more than 30 minutes --> reset trackingStartTime and no of
				// login attempts
				if (currentIntervalInSeconds > (30 * 60)) {
					existingLoginKeyDetails.setTrackingStartTime(LocalDateTime.now());
					existingLoginKeyDetails.setConsecutiveAttemptCount(1);
					String loginKey = UUID.randomUUID().toString();
					existingLoginKeyDetails.setLoginKey(loginKey);
					existingLoginKeyDetails.setKeyGenerationTime(LocalDateTime.now());
					loginKeysRepository.save(existingLoginKeyDetails);

					String to = loginUserRecord.emailId();
					String subject = "Check out this URL to verify";
					String url = "http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey;

					loginEmailServiceImplementation.sendEmailWithUrl(to, subject, url);

					ServiceResponse<String> response = new ServiceResponse<>(true,
							"Email sent with login verification link. It will expire after 15 minutes. Login Key: "
									+ loginKey,
							"Email sent.");
					return response;
				} else {
					// check the no of login attempts left
					int noOfLoginAttemptsMade = existingLoginKeyDetails.getConsecutiveAttemptCount();
					if (noOfLoginAttemptsMade >= 11115) {
						// lock the user temporarily for next 2 hours
						LocalDateTime lockOutEndTime = LocalDateTime.now().plusHours(2);
						existingLoginKeyDetails.setTrackingStartTime(lockOutEndTime);
						existingLoginKeyDetails.setConsecutiveAttemptCount(0);
						loginKeysRepository.save(existingLoginKeyDetails);

						ServiceResponse<String> response = new ServiceResponse<>(false, null,
								"User is temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
										+ lockOutEndTime);
						return response;
					} else {
						// increase the count
						existingLoginKeyDetails
								.setConsecutiveAttemptCount(existingLoginKeyDetails.getConsecutiveAttemptCount() + 1);
						existingLoginKeyDetails.setKeyGenerationTime(LocalDateTime.now());
						String loginKey = UUID.randomUUID().toString();
						existingLoginKeyDetails.setLoginKey(loginKey);

						loginKeysRepository.save(existingLoginKeyDetails);

						String to = loginUserRecord.emailId();
						String subject = "Check out this URL to verify";
						String url = "http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey;

						loginEmailServiceImplementation.sendEmailWithUrl(to, subject, url);

						ServiceResponse<String> response = new ServiceResponse<>(true,
								"Email sent with login verification link. It will expire after 15 minutes. Login Key: "
										+ loginKey,
								"Email sent.");
						return response;

					}
				}
			} else {
				// user is still temporarily locked
				ServiceResponse<String> response = new ServiceResponse<>(false, null,
						"User is temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
								+ existingLoginKeyDetails.getTrackingStartTime());
				return response;
			}
		} else {
			// first login
			LoginKeys newLoginKey = new LoginKeys();
			String loginKey = UUID.randomUUID().toString();
			newLoginKey.setUserId(existingUser.getUserId());
			newLoginKey.setLoginKey(loginKey);
			newLoginKey.setKeyGenerationTime(LocalDateTime.now());
			newLoginKey.setTrackingStartTime(LocalDateTime.now());
			newLoginKey.setConsecutiveAttemptCount(1);
			loginKeysRepository.save(newLoginKey);

			String to = loginUserRecord.emailId();
			String subject = "Check out this URL to verify";
			String url = "http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey;

			loginEmailServiceImplementation.sendEmailWithUrl(to, subject, url);

			ServiceResponse<String> response = new ServiceResponse<>(true,
					"Email sent with login verification link. It will expire after 15 minutes. Login Key: " + loginKey,
					"Email sent.");
			return response;
		}
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
	public ServiceResponse<String> finalUserLogin(String loginKey) {
		LoginKeys existingLoginKey = loginKeysRepository.findByLoginKey(loginKey);
		if (existingLoginKey != null) {
			long noOfMinutes = existingLoginKey.getKeyGenerationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);

			if (noOfMinutes > 15) {
				ServiceResponse<String> response = new ServiceResponse<>(false, null,
						"Login Key has expired. Please, try again!");
				return response;
			}

			User existingUser = userRepository.findById(existingLoginKey.getUserId()).get();
			System.out.println(existingUser.getEmailId() + " " + existingUser.getPassword());
			boolean passwordMatches = passwordEncoder.matches("b38c6b23-5195-4544-bed3-a2ccc7bf4ae2",
					existingUser.getPassword());
			System.out.println(passwordMatches);
			PreFinalUsers temporaryUser = temporaryUsersRepository.findById(existingUser.getUserId()).get();
			this.doAuthenticate(existingUser.getEmailId(), temporaryUser.getRegistrationKey());
			System.out.println(existingUser.getEmailId() + " " + existingUser.getPassword());
			UserDetails userDetails = userDetailsService.loadUserByUsername(existingUser.getEmailId());
			String token = this.helper.generateToken(userDetails);

			ServiceResponse<String> response = new ServiceResponse<>(true, token, "User logged in successfully.");
			return response;
		}
		ServiceResponse<String> response = new ServiceResponse<>(false, null,
				"Invalid login key. Please try with a valid key or try logging in once again.");
		return response;
	}

	/**
	 * Performs user authentication with the provided email and password.
	 *
	 * @param email    The email address of the user attempting to authenticate.
	 * @param password The password associated with the user's email.
	 * @throws BadCredentialsException If the provided email and password do not
	 *                                 match any valid credentials, or if
	 *                                 authentication fails for any other reason, a
	 *                                 BadCredentialsException is thrown. The
	 *                                 exception message indicates that the username
	 *                                 or password is invalid.
	 */
	private void doAuthenticate(String email, String password) {

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
		try {

			manager.authenticate(authentication);

		} catch (BadCredentialsException e) {
			throw new BadCredentialsException(" Invalid Username or Password  !!");
		}

	}

}
