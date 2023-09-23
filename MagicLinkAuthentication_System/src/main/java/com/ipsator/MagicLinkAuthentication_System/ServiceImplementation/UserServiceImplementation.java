package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.KeyDetails;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.SetProfileDetailsRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.KeyDetailsRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Role_Permission.Role;
import com.ipsator.MagicLinkAuthentication_System.Security.JwtHelper;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;

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
	private KeyDetailsRepository keyDetailsRepository;

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

		// if userOptional is present
		if (userOptional.isPresent()) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Email Id already exists. Please, directly login!");
			return response;
		}

		// else check for the user details from the database using registration key
		KeyDetails existingKeyDetails = keyDetailsRepository.findBySignUpLogInKey(registrationKey);

		// if no details found
		if (existingKeyDetails == null) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Invalid Registration key. Please try again!");
			return response;
		}

		// else if details found using the registration key, check if email id is
		// matching
		// if email id doesn't match
		if (existingKeyDetails.getEmailId().equals(emailId) == false) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Registration key doesn't match with the provided email id. Please try again!");
			return response;
		}

		// else if email id matches with the registration key
		// check for the expiration time of the registration key
		long noOfMinutes = existingKeyDetails.getKeyGenerationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);

		// if registration key has expired
		if (noOfMinutes > 15) {
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Registration key has expired. Please try again!");
			return response;
		}

		// else if registration key has not expired
		// Register the user
		User newUser = new User();
		newUser.setUserId(existingKeyDetails.getUserId());
		newUser.setEmailId(emailId);
		newUser.setUserRegistrationTime(LocalDateTime.now());
		newUser.setRole(Role.USER_UPDATE_ACCESS);

		User savedUser = userRepository.save(newUser);

		// resetting the relevant details in KeyDetails table for the first login of the
		// user
		existingKeyDetails.setConsecutiveAttemptCount(0);
		existingKeyDetails.setTrackingStartTime(LocalDateTime.now());

		keyDetailsRepository.save(existingKeyDetails);

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
		KeyDetails existingKeyDetails = keyDetailsRepository.findByEmailId(loginUserRecord.emailId());

		// if no user found using the provided email id in user table and key details
		// table
		if (existingUserOpt.isEmpty() && existingKeyDetails == null) {
			// first login attempt and not a registered user
			KeyDetails newKeyDetails = new KeyDetails();
			newKeyDetails.setEmailId(loginUserRecord.emailId());
			String registrationKey = UUID.randomUUID().toString();
			newKeyDetails.setSignUpLogInKey(registrationKey);
			newKeyDetails.setKeyGenerationTime(LocalDateTime.now());
			newKeyDetails.setTrackingStartTime(LocalDateTime.now());
			newKeyDetails.setConsecutiveAttemptCount(1);
			keyDetailsRepository.save(newKeyDetails);

			// sending email for registration
			signupEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(), "Check out this URL to verify",
					"http://localhost:8659/ipsator.com/open/user/registration?emailId=" + loginUserRecord.emailId()
							+ "&registrationKey=" + registrationKey);

			ServiceResponse<String> response = new ServiceResponse<>(false, null,
					"Email Id is not registered. Please, sign up first by clicking on the link sent to your email id: "
							+ loginUserRecord.emailId() + "! Link will expire after 15 minutes!");
			return response;
		}

		// else if user found using the provided email id at least in the KeyDetails
		// table
		// check if the user is locked or not

		if (existingUserOpt.isEmpty() && existingKeyDetails != null) {
			if (existingKeyDetails.getTrackingStartTime().isAfter(LocalDateTime.now())) {
				// user is still locked
				ServiceResponse<String> response = new ServiceResponse<>(false, null,
						"User is temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
								+ existingKeyDetails.getTrackingStartTime());
				return response;
			}

			// else if the user is not temporarily locked
			// calculate the interval between the tracking start time and the present time
			long intervalInSeconds = ChronoUnit.SECONDS.between(existingKeyDetails.getTrackingStartTime(),
					LocalDateTime.now());

			// if the interval is more than 30 minutes
			if (intervalInSeconds > (30 * 60)) {
				// reset the values of tracking start time and no of attempts
				existingKeyDetails.setTrackingStartTime(LocalDateTime.now());
				existingKeyDetails.setConsecutiveAttemptCount(1);
				String registrationKey = UUID.randomUUID().toString();
				existingKeyDetails.setSignUpLogInKey(registrationKey);
				existingKeyDetails.setKeyGenerationTime(LocalDateTime.now());
				keyDetailsRepository.save(existingKeyDetails);

				// sending email for registration
				signupEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
						"Check out this URL to verify",
						"http://localhost:8659/ipsator.com/open/user/registration?emailId=" + loginUserRecord.emailId()
								+ "&registrationKey=" + registrationKey);

				ServiceResponse<String> response = new ServiceResponse<>(true,
						"Email Id is not registered. Please, sign up first by clicking on the link sent to your email id: "
								+ loginUserRecord.emailId() + "! Link will expire after 15 minutes!",
						"Email sent.");
				return response;
			}

			// else if the interval is less than 30 minutes
			if (intervalInSeconds <= (30 * 60)) {
				// check the no of temporary registration attempts left
				int noOfSignUpAttemptsMade = existingKeyDetails.getConsecutiveAttemptCount();

				// if the user has already reached the maximum limit
				if (noOfSignUpAttemptsMade >= 5) {
					// lock the user temporarily for next 2 hours
//						LocalDateTime lockOutEndTime = LocalDateTime.now().plusHours(2);
					LocalDateTime lockOutEndTime = LocalDateTime.now().plusMinutes(2);
					existingKeyDetails.setTrackingStartTime(lockOutEndTime);
					existingKeyDetails.setConsecutiveAttemptCount(0);
					keyDetailsRepository.save(existingKeyDetails);

					ServiceResponse<String> response = new ServiceResponse<>(false, null,
							"User got temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
									+ lockOutEndTime);
					return response;
				}

				// else if the user has attempts left for the current 30 minutes
				else {
					// increase the count
					existingKeyDetails.setConsecutiveAttemptCount(existingKeyDetails.getConsecutiveAttemptCount() + 1);
					existingKeyDetails.setKeyGenerationTime(LocalDateTime.now());
					String registrationKey = UUID.randomUUID().toString();
					existingKeyDetails.setSignUpLogInKey(registrationKey);

					keyDetailsRepository.save(existingKeyDetails);

					// sending email for registration
					signupEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
							"Check out this URL to verify",
							"http://localhost:8659/ipsator.com/open/user/registration?emailId="
									+ loginUserRecord.emailId() + "&registrationKey=" + registrationKey);

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

//		User existingUser = existingUserOpt.get();
		// Check if it is the first login
		KeyDetails keyDetails = keyDetailsRepository.findByEmailId(loginUserRecord.emailId());
//		if (loginKeyDetails.isPresent()) {
		// not first login
//			KeyDetails existingLoginKeyDetails = loginKeyDetails.get();
//
		// check if user is temporarily locked or not
		// if user is not locked temporarily
		if (keyDetails.getTrackingStartTime().isBefore(LocalDateTime.now())) {
			long currentIntervalInSeconds = ChronoUnit.SECONDS.between(keyDetails.getTrackingStartTime(),
					LocalDateTime.now());

			// if interval is more than 30 minutes --> reset trackingStartTime and no of
			// login attempts
			if (currentIntervalInSeconds > (30 * 60)) {
				keyDetails.setTrackingStartTime(LocalDateTime.now());
				keyDetails.setConsecutiveAttemptCount(1);
				String loginKey = UUID.randomUUID().toString();
				keyDetails.setSignUpLogInKey(loginKey);
				keyDetails.setKeyGenerationTime(LocalDateTime.now());
				keyDetailsRepository.save(keyDetails);

				// sending email for login
				loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
						"Check out this URL to verify",
						"http://localhost:8659/ipsator.com/open/user/final-login?loginKey=" + loginKey);

				ServiceResponse<String> response = new ServiceResponse<>(true,
						"Email sent with login verification link to the email id: " + loginUserRecord.emailId()
								+ ". It will expire after 15 minutes!",
						"Email sent.");
				return response;
			} else {
				// check the no of login attempts left
				int noOfLoginAttemptsMade = keyDetails.getConsecutiveAttemptCount();
				if (noOfLoginAttemptsMade >= 5) {
					// lock the user temporarily for next 2 hours
//						LocalDateTime lockOutEndTime = LocalDateTime.now().plusHours(2);
					LocalDateTime lockOutEndTime = LocalDateTime.now().plusMinutes(2);
					keyDetails.setTrackingStartTime(lockOutEndTime);
					keyDetails.setConsecutiveAttemptCount(0);
					keyDetailsRepository.save(keyDetails);

					ServiceResponse<String> response = new ServiceResponse<>(false, null,
							"User got temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
									+ lockOutEndTime);
					return response;
				} else {
					// increase the count
					keyDetails.setConsecutiveAttemptCount(keyDetails.getConsecutiveAttemptCount() + 1);
					keyDetails.setKeyGenerationTime(LocalDateTime.now());
					String loginKey = UUID.randomUUID().toString();
					keyDetails.setSignUpLogInKey(loginKey);

					keyDetailsRepository.save(keyDetails);

					// sending email for login
					loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
							"Check out this URL to verify",
							"http://localhost:8659/ipsator.com/open/user/final-login?loginKey=" + loginKey);

					ServiceResponse<String> response = new ServiceResponse<>(true,
							"Email sent with login verification link to the email id: " + loginUserRecord.emailId()
									+ ". It will expire after 15 minutes!",
							"Email sent.");
					return response;

				}
			}
		} else {
			// user is still temporarily locked
			ServiceResponse<String> response = new ServiceResponse<>(false, null,
					"User is temporarily locked due to maximum login attempt exceeded. Please, try logging in after "
							+ keyDetails.getTrackingStartTime());
			return response;
		}
//		} else {
//			// first login
//			KeyDetails newLoginKey = new KeyDetails();
//			String loginKey = UUID.randomUUID().toString();
//			newLoginKey.setUserId(existingUser.getUserId());
//			newLoginKey.setLoginKey(loginKey);
//			newLoginKey.setKeyGenerationTime(LocalDateTime.now());
//			newLoginKey.setTrackingStartTime(LocalDateTime.now());
//			newLoginKey.setConsecutiveAttemptCount(1);
//			loginKeysRepository.save(newLoginKey);
//
//			// sending email for login
//			loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(), "Check out this URL to verify",
//					"http://localhost:8659/ipsator.com/open/user/final-login?loginKey=" + loginKey);
//
//			ServiceResponse<String> response = new ServiceResponse<>(true,
//					"Email sent with login verification link to the email id: "+loginUserRecord.emailId()+". It will expire after 15 minutes!",
//					"Email sent.");
//			return response;
//		}
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
		KeyDetails existingKeyDetails = keyDetailsRepository.findBySignUpLogInKey(loginKey);
		if (existingKeyDetails != null) {
			long noOfMinutes = existingKeyDetails.getKeyGenerationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);

			if (noOfMinutes > 15) {
				ServiceResponse<String> response = new ServiceResponse<>(false, null,
						"Login Key has expired. Please, try again!");
				return response;
			}

			User existingUser = userRepository.findById(existingKeyDetails.getUserId()).get();

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
		if (allUsers.size() > 0) {
			ServiceResponse<List<User>> response = new ServiceResponse<>(true, allUsers, "All users fetched.");
			return response;
		}
		ServiceResponse<List<User>> response = new ServiceResponse<>(false, null, "No user found in database.");
		return response;
	}

	@Override
	public ServiceResponse<User> setProfileDetails(SetProfileDetailsRecord setProfileDetailsRecord) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			// The current user is authenticated
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String username = userDetails.getUsername();

			Optional<User> existingUserOptional = userRepository.findByEmailId(setProfileDetailsRecord.getEmailId());

			if (existingUserOptional.isPresent()) {
				User existingUser = existingUserOptional.get();
//				if (((existingUser.getRole().equals(Role.USER_ALL_ACCESS)
//						|| existingUser.getRole().equals(Role.USER_UPDATE_ACCESS))
//						&& existingUser.getEmailId().equals(username))
//						|| (existingUser.getRole().equals(Role.ADMIN_ALL_ACCESS)
//								|| existingUser.getRole().equals(Role.ADMIN_UPDATE_ACCESS))) {
//				if (existingUser.getRole().equals("ADMIN_ALL_ACCESS")) {
					existingUser.setFirstName(setProfileDetailsRecord.getFirstName());
					existingUser.setLastName(setProfileDetailsRecord.getLastName());
					existingUser.setAge(setProfileDetailsRecord.getAge());
					existingUser.setGender(setProfileDetailsRecord.getGender());

					existingUser.setUserUpdationTime(LocalDateTime.now());

					User updatedUser = userRepository.save(existingUser);

					ServiceResponse<User> response = new ServiceResponse<>(true, updatedUser,
							"Current authenticated user successfully updated.");

					return response;
//				}
//				ServiceResponse<User> response = new ServiceResponse<>(false, null,
//						"Only admins with all and update access and the owner of the provided email id with all and update access can update user details.");
//
//				return response;
			}
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"No user found with this email id: " + setProfileDetailsRecord.getEmailId());

			return response;
		} else {
			// No user is authenticated
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Currently no user is authenticated. Please, login first!");
			return response;
		}

	}

}
