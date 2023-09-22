package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.LoginKeys;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.LoginKeysRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Security.JwtHelper;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

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
	private LoginKeysRepository loginKeysRepository;

	@Autowired
	private SignupEmailServiceImplementation signupEmailServiceImplementation;

	@Autowired
	private LoginEmailServiceImplementation loginEmailServiceImplementation;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtHelper helper;

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
	public ServiceResponse<User> userRegistration(String emailId, String registrationKey) {
		Optional<User> userOptional = userRepository.findByEmailId(emailId);

		// if userOptional is present and user status is "Verified"
		if (userOptional.isPresent() && userOptional.get().getUserStatus().equals("Verified")) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Email Id already exists. Please, directly login!");
			return response;
		}

		// else check for the user details from the database using registration key
		User existingTemporaryUser = userRepository.findByRegistrationKey(registrationKey);

		// if no user found
		if (existingTemporaryUser == null) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Invalid Registration key. Please try again!");
			return response;
		}

		// else if user found using the registration key, check if email id is matching
		// if email id doesn't match
		if (existingTemporaryUser.getEmailId().equals(emailId) == false) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Registration key doesn't match with the provided email id. Please try again!");
			return response;
		}

		// else if email id matches with the registration key
		// check for the expiration time of the registration key
		long noOfMinutes = existingTemporaryUser.getRegdKeyGenerationTime().until(LocalDateTime.now(),
				ChronoUnit.MINUTES);

		// if registration key has expired
		if (noOfMinutes > 15) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Registration key has expired. Please try again!");
			return response;
		}

		// else if registration key has not expired
		// Register the user
		User newUser = userOptional.get();
		newUser.setEmailId(emailId);
		newUser.setUserRegistrationTime(LocalDateTime.now());
		newUser.setUserStatus("Verified");

		User savedUser = userRepository.save(newUser);

		ServiceResponse<User> response = new ServiceResponse<>(true, savedUser, "User registered successfully.");
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

		// if no user found using the provided email id
		if (existingUserOpt.isEmpty()) {
			// first login attempt and not a registered user
			User newTempUser = new User();
			newTempUser.setEmailId(loginUserRecord.emailId());
			newTempUser.setUserStatus("Not Verified");
			String registrationKey = UUID.randomUUID().toString();
			newTempUser.setRegistrationKey(registrationKey);
			newTempUser.setRegdKeyGenerationTime(LocalDateTime.now());
			newTempUser.setTrackingStartTime(LocalDateTime.now());
			newTempUser.setNoOfAttempts(1);
			userRepository.save(newTempUser);

			// sending email for registration
			signupEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(), "Check out this URL to verify",
					"http://localhost:8659/ipsator.com/user/registration?emailId=" + loginUserRecord.emailId()
							+ "&registrationKey=" + registrationKey);

			ServiceResponse<String> response = new ServiceResponse<>(false, null,
					"Email Id is not registered. Please, sign up first by clicking on the link sent to your email id: "
							+ loginUserRecord.emailId() + "! Link will expire after 15 minutes!");
			return response;
		}

		// else if user found using the provided email id
		// check if the user is verified or not
		// if the user is not verified yet
		if (existingUserOpt.isPresent() && existingUserOpt.get().getUserStatus().equals("Not Verified")) {
			// check if the user is locked or not
			User existingTempUser = existingUserOpt.get();

			if (existingTempUser.getTrackingStartTime().isAfter(LocalDateTime.now())) {
				// user is still locked
				ServiceResponse<String> response = new ServiceResponse<>(false, null,
						"User is temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
								+ existingTempUser.getTrackingStartTime());
				return response;
			}

			// else if the user is not temporarily locked
			// calculate the interval between the tracking start time and the present time
			long intervalInSeconds = ChronoUnit.SECONDS.between(existingTempUser.getTrackingStartTime(),
					LocalDateTime.now());

			// if the interval is more than 30 minutes
			if (intervalInSeconds > (30 * 60)) {
				// reset the values of tracking start time and no of attempts
				existingTempUser.setTrackingStartTime(LocalDateTime.now());
				existingTempUser.setNoOfAttempts(1);
				String registrationKey = UUID.randomUUID().toString();
				existingTempUser.setRegistrationKey(registrationKey);
				existingTempUser.setRegdKeyGenerationTime(LocalDateTime.now());
				userRepository.save(existingTempUser);

				// sending email for registration
				signupEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
						"Check out this URL to verify", "http://localhost:8659/ipsator.com/user/registration?emailId="
								+ loginUserRecord.emailId() + "&registrationKey=" + registrationKey);

				ServiceResponse<String> response = new ServiceResponse<>(true,
						"Email Id is not registered. Please, sign up first by clicking on the link sent to your email id: "
								+ loginUserRecord.emailId() + "! Link will expire after 15 minutes!",
						"Email sent.");
				return response;
			}

			// else if the interval is less than 30 minutes
			else {
				// check the no of temporary registration attempts left
				int noOfSignUpAttemptsMade = existingTempUser.getNoOfAttempts();

				// if the user has already reached the maximum limit
				if (noOfSignUpAttemptsMade >= 5) {
					// lock the user temporarily for next 2 hours
//					LocalDateTime lockOutEndTime = LocalDateTime.now().plusHours(2);
					LocalDateTime lockOutEndTime = LocalDateTime.now().plusMinutes(2);
					existingTempUser.setTrackingStartTime(lockOutEndTime);
					existingTempUser.setNoOfAttempts(0);
					userRepository.save(existingTempUser);

					ServiceResponse<String> response = new ServiceResponse<>(false, null,
							"User is temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
									+ lockOutEndTime);
					return response;
				}

				// else if the user has attempts left for the current 30 minutes
				else {
					// increase the count
					existingTempUser.setNoOfAttempts(existingTempUser.getNoOfAttempts() + 1);
					existingTempUser.setRegdKeyGenerationTime(LocalDateTime.now());
					String registrationKey = UUID.randomUUID().toString();
					existingTempUser.setRegistrationKey(registrationKey);

					userRepository.save(existingTempUser);

					// sending email for registration
					signupEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
							"Check out this URL to verify",
							"http://localhost:8659/ipsator.com/user/registration?emailId=" + loginUserRecord.emailId()
									+ "&registrationKey=" + registrationKey);

					ServiceResponse<String> response = new ServiceResponse<>(true,
							"Email Id is not registered. Please, sign up first by clicking on the link sent to your email id: "
									+ loginUserRecord.emailId() + "! Link will expire after 15 minutes!",
							"Email sent.");
					return response;

				}
			}
		}

		// if the user is eligible for login
		// send the login verification email
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

					// sending email for login
					loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
							"Check out this URL to verify",
							"http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey);

					ServiceResponse<String> response = new ServiceResponse<>(true,
							"Email sent with login verification link to the email id: "+loginUserRecord.emailId()+". It will expire after 15 minutes!",
							"Email sent.");
					return response;
				} else {
					// check the no of login attempts left
					int noOfLoginAttemptsMade = existingLoginKeyDetails.getConsecutiveAttemptCount();
					if (noOfLoginAttemptsMade >= 5) {
						// lock the user temporarily for next 2 hours
//						LocalDateTime lockOutEndTime = LocalDateTime.now().plusHours(2);
						LocalDateTime lockOutEndTime = LocalDateTime.now().plusMinutes(2);
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

						// sending email for login
						loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
								"Check out this URL to verify",
								"http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey);

						ServiceResponse<String> response = new ServiceResponse<>(true,
								"Email sent with login verification link to the email id: "+loginUserRecord.emailId()+". It will expire after 15 minutes!",
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

			// sending email for login
			loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(), "Check out this URL to verify",
					"http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey);

			ServiceResponse<String> response = new ServiceResponse<>(true,
					"Email sent with login verification link to the email id: "+loginUserRecord.emailId()+". It will expire after 15 minutes!",
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

			UserDetails userDetails = userDetailsService.loadUserByUsername(existingUser.getEmailId());
			String token = this.helper.generateToken(userDetails);

			ServiceResponse<String> response = new ServiceResponse<>(true, token, "User logged in successfully.");
			return response;
		}
		ServiceResponse<String> response = new ServiceResponse<>(false, null,
				"Invalid login key. Please try with a valid key or try logging in once again.");
		return response;
	}

	@Override
	public ServiceResponse<List<User>> getAllUsers() {
		List<User> allUsers = userRepository.findAll();
		if(allUsers.size()>0) {
			ServiceResponse<List<User>> response = new ServiceResponse<>(true, allUsers,
					"All users fetched.");
			return response;
		}
		ServiceResponse<List<User>> response = new ServiceResponse<>(false, null,
				"No user found in database.");
		return response;
	}

}
