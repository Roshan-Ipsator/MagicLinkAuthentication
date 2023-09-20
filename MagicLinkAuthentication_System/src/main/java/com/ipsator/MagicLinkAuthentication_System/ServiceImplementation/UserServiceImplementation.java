package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
	 * The method to temporarily register a user before final verification
	 * 
	 * @param registerUserRecord object of RegisterUserRecord class
	 * 
	 * @return temporaryUser object
	 * 
	 * @throws UserException, MessagingException
	 * 
	 */
//	@Override
//	public ServiceResponse<PreFinalUsers> preFinalUserRegistration(RegisterUserRecord registerUserRecord)
//			throws MessagingException {
//		Optional<User> existingUserOpt = userRepository.findByEmailId(registerUserRecord.emailId());
//
//		// if user is already registered after verification
//		if (existingUserOpt.isPresent()) {
//			ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(false, null,
//					"Email Id already exists. Please, directly log in!");
//			return response;
//		}
//
//		// else
//		TempRegdAttemptTracker tempRegdAttemptTracker = tempRegdAttemptTrackerRepository
//				.findByUserEmailId(registerUserRecord.emailId());
//
//		// if this is user's first registration attempt
//		if (tempRegdAttemptTracker == null) {
//			// this is the first registration attempt for the user
//
//			PreFinalUsers newTemporaryUser = createNewPreFinalUser(registerUserRecord.firstName(),
//					registerUserRecord.lastName(), registerUserRecord.emailId(), registerUserRecord.gender(),
//					registerUserRecord.age());
//
//			signupEmailServiceImplementation.sendEmailWithUrl(registerUserRecord.emailId(),
//					"Check out this URL to complete your registration.",
//					"http://localhost:8659/ipsator.com/user/final-registration?registrationKey="
//							+ newTemporaryUser.getRegistrationKey());
//
//			PreFinalUsers savedTemporaryUser = preFinalUsersRepository.save(newTemporaryUser);
//
//			TempRegdAttemptTracker newTempRegdAttemptTracker = new TempRegdAttemptTracker();
//			newTempRegdAttemptTracker.setUserEmailId(savedTemporaryUser.getEmailId());
//			newTempRegdAttemptTracker.setTrackingStartTime(LocalDateTime.now());
//			tempRegdAttemptTrackerRepository.save(newTempRegdAttemptTracker);
//
//			ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(true, savedTemporaryUser,
//					"Temporarily created the user. Registration verification link has been sent to the email. It will expire after 15 minutes.");
//			return response;
//		}
//
//		// check if user is locked or not
//		if (LocalDateTime.now().isBefore(tempRegdAttemptTracker.getTrackingStartTime())) {
//			// user is still locked
//			ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(false, null,
//					"User is temporarily locked due to maximum registration attempt exceeded. Please, try after "
//							+ tempRegdAttemptTracker.getTrackingStartTime());
//			return response;
//		}
//
//		// if user is not locked
//		Duration duration = Duration.between(tempRegdAttemptTracker.getTrackingStartTime(), LocalDateTime.now());
//		long totalSeconds = duration.toSeconds();
//
//		// check if this attempt is within the 30 minutes from the current Tracking
//		// Start Time
//		if (totalSeconds > (30 * 60)) {
//			// it is not
//			// reset the Tracking Start Time for the same user
//
//			PreFinalUsers newTemporaryUser = createNewPreFinalUser(registerUserRecord.firstName(),
//					registerUserRecord.lastName(), registerUserRecord.emailId(), registerUserRecord.gender(),
//					registerUserRecord.age());
//
//			signupEmailServiceImplementation.sendEmailWithUrl(registerUserRecord.emailId(),
//					"Check out this URL to complete your registration.",
//					"http://localhost:8659/ipsator.com/user/final-registration?registrationKey="
//							+ newTemporaryUser.getRegistrationKey());
//
//			PreFinalUsers savedTemporaryUser = preFinalUsersRepository.save(newTemporaryUser);
//
//			tempRegdAttemptTracker.setTrackingStartTime(LocalDateTime.now());
//			tempRegdAttemptTrackerRepository.save(tempRegdAttemptTracker);
//
//			ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(true, savedTemporaryUser,
//					"Temporarily created the user. Registration verification link has been sent to the email. It will expire after 15 minutes.");
//			return response;
//		}
//
//		// else if it is within that 30 minutes
//		List<PreFinalUsers> preFinalUsers = preFinalUsersRepository
//				.findByEmailId(tempRegdAttemptTracker.getUserEmailId());
//		long listSize = 0;
//
//		for (PreFinalUsers preFinalUser : preFinalUsers) {
//			if (preFinalUser.getKeyGenerationTime().isAfter(tempRegdAttemptTracker.getTrackingStartTime())
//					&& preFinalUser.getKeyGenerationTime().isBefore(LocalDateTime.now())) {
//				listSize++;
//			}
//		}
//
//		// if with in that 30 minutes, the attempt count is more than or equal to 5
//		// Temporarily lock the user for next 2 hours
//		if (listSize >= 5) {
//			LocalDateTime lockOutEndTime = LocalDateTime.now().plusHours(2);
//			tempRegdAttemptTracker.setTrackingStartTime(lockOutEndTime);
//			tempRegdAttemptTrackerRepository.save(tempRegdAttemptTracker);
//
//			ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(false, null,
//					"User got temporarily locked due to maximum registration attempt exceeded. Please, try after "
//							+ lockOutEndTime);
//			return response;
//		}
//
//		// else just save the user as a pre-final user
//
//		PreFinalUsers newTemporaryUser = createNewPreFinalUser(registerUserRecord.firstName(),
//				registerUserRecord.lastName(), registerUserRecord.emailId(), registerUserRecord.gender(),
//				registerUserRecord.age());
//
//		signupEmailServiceImplementation.sendEmailWithUrl(registerUserRecord.emailId(),
//				"Check out this URL to complete your registration.",
//				"http://localhost:8659/ipsator.com/user/final-registration?registrationKey="
//						+ newTemporaryUser.getRegistrationKey());
//
//		PreFinalUsers savedTemporaryUser = preFinalUsersRepository.save(newTemporaryUser);
//
//		ServiceResponse<PreFinalUsers> response = new ServiceResponse<>(true, savedTemporaryUser,
//				"Temporarily created the user. Registration verification link has been sent to the email. It will expire after 15 minutes.");
//		return response;
//	}

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
	public ServiceResponse<User> userRegistration(String emailId) {
		Optional<User> userOptional = userRepository.findByEmailId(emailId);

		if (userOptional.isPresent()) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Email Id already exists. Please, directly login!");
			return response;
		}

		User newUser = new User();
		newUser.setEmailId(emailId);
		newUser.setUserRegistrationTime(LocalDateTime.now());

		User savedUser = userRepository.save(newUser);

		ServiceResponse<User> response = new ServiceResponse<>(true, savedUser, "User registered successfully.");
		return response;

//		if (existingTemporaryUser != null) {
//			Optional<User> existingUserOpt = userRepository.findByEmailId(existingTemporaryUser.getEmailId());
//			if (existingUserOpt.isPresent()) {
//				ServiceResponse<User> response = new ServiceResponse<>(false, null,
//						"Email Id already exists. Please, directly log in!");
//				return response;
//			}
//			long noOfMinutes = existingTemporaryUser.getKeyGenerationTime().until(LocalDateTime.now(),
//					ChronoUnit.MINUTES);
//
//			if (noOfMinutes > 15) {
//				ServiceResponse<User> response = new ServiceResponse<>(false, null,
//						"Registration key has expired. Please try again!");
//				return response;
//			}
//
//			User newUser = new User();
//			newUser.setUserId(existingTemporaryUser.getUserId());
//			newUser.setFirstName(existingTemporaryUser.getFirstName());
//			newUser.setLastName(existingTemporaryUser.getLastName());
//			newUser.setEmailId(existingTemporaryUser.getEmailId());
//			newUser.setGender(existingTemporaryUser.getGender());
//			newUser.setAge(existingTemporaryUser.getAge());
//			newUser.setPassKey(passwordEncoder.encode(registrationKey));
//
//			existingTemporaryUser.setUserStatus("Verified");
//
//			preFinalUsersRepository.save(existingTemporaryUser);
//
//			User savedUser = userRepository.save(newUser);
//			ServiceResponse<User> response = new ServiceResponse<>(true, savedUser, "User registered successfully.");
//			return response;
//		}
//
//		ServiceResponse<User> response = new ServiceResponse<>(false, null,
//				"Invalid key. Please try with a valid key or try registering once again.");
//		return response;
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
			signupEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(), "Check out this URL to verify",
					"http://localhost:8659/ipsator.com/user/registration?emailId=" + loginUserRecord.emailId());
			ServiceResponse<String> response = new ServiceResponse<>(false, null,
					"Email Id is not registered. Please, sign up first by clicking on the link sent to your email id: "+loginUserRecord.emailId()+"!");
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

					loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
							"Check out this URL to verify",
							"http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey);

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

						loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
								"Check out this URL to verify",
								"http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey);

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

			loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(), "Check out this URL to verify",
					"http://localhost:8659/ipsator.com/user/final-login?loginKey=" + loginKey);

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
	 * Creates a new PreFinalUsers object with the provided user information.
	 *
	 * This method creates a new PreFinalUsers object and populates it with the
	 * provided user information, including first name, last name, email, gender,
	 * age, and generates a unique registration key. The user's status is set to
	 * "Verification Pending" and the key generation time is set to the current date
	 * and time.
	 *
	 * @param firstName The first name of the user.
	 * @param lastName  The last name of the user.
	 * @param emailId   The email address of the user.
	 * @param gender    The gender of the user.
	 * @param age       The age of the user.
	 * @return A newly created PreFinalUsers object with the provided information.
	 */
//	private PreFinalUsers createNewPreFinalUser(String firstName, String lastName, String emailId, String gender,
//			Integer age) {
//
//		PreFinalUsers newTemporaryUser = new PreFinalUsers();
//		newTemporaryUser.setFirstName(firstName);
//		newTemporaryUser.setLastName(lastName);
//		newTemporaryUser.setEmailId(emailId);
//		newTemporaryUser.setGender(gender);
//		newTemporaryUser.setAge(age);
//		String registrationKey = UUID.randomUUID().toString();
//		newTemporaryUser.setRegistrationKey(registrationKey);
//		newTemporaryUser.setKeyGenerationTime(LocalDateTime.now());
//		newTemporaryUser.setUserStatus("Verification Pending");
//
//		return newTemporaryUser;
//	}

}
