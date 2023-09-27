package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.KeyDetails;
import com.ipsator.MagicLinkAuthentication_System.Entity.Role;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.RoleEnum;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.SetProfileDetailsRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.KeyDetailsRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.RoleRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Security.JwtHelper;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

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
	private KeyDetailsRepository keyDetailsRepository;

	@Autowired
	private LoginEmailServiceImplementation loginEmailServiceImplementation;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtHelper helper;

	@Autowired
	private Environment environment;

	@Autowired
	private RoleRepository roleRepository;

	/**
	 * 
	 * The method to send a verification email for the final registration/ final
	 * login
	 * 
	 * @param loginUserRecord object of LoginUserRecord contains the user's email id
	 * 
	 * @return loginKey a string to verify the user for final registration/ final
	 *         login
	 * 
	 * @throws UserException, MessagingException
	 * 
	 */
	@Override
	public ServiceResponse<String> preFinalUserLogin(LoginUserRecord loginUserRecord) throws MessagingException {

		// Check if it is the first login
		Optional<KeyDetails> keyDetailsOptional = keyDetailsRepository.findByEmailId(loginUserRecord.emailId());

		if (keyDetailsOptional.isPresent()) {
			KeyDetails keyDetails = keyDetailsOptional.get();
			// check if user is temporarily locked or not
			// if user is not locked temporarily
			if (keyDetails.getTrackingStartTime().isBefore(LocalDateTime.now())) {
				long currentIntervalInSeconds = ChronoUnit.SECONDS.between(keyDetails.getTrackingStartTime(),
						LocalDateTime.now());

				// if interval is more than 30 minutes --> reset trackingStartTime and no of
				// login attempts
				if (currentIntervalInSeconds > environment.getProperty("time.bound.duration.seconds", Long.class)) {
					keyDetails.setTrackingStartTime(LocalDateTime.now());
					keyDetails.setConsecutiveAttemptCount(1);
					String loginKey = UUID.randomUUID().toString();
					keyDetails.setLogInKey(loginKey);
					keyDetails.setKeyGenerationTime(LocalDateTime.now());
					keyDetailsRepository.save(keyDetails);

					// sending email for login
					loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
							"Check out this URL to verify",
							"http://localhost:8659/open/user/final-login?loginKey=" + loginKey);

					ServiceResponse<String> response = new ServiceResponse<>(true,
							"Email sent with login verification link to the email id: " + loginUserRecord.emailId()
									+ ". It will expire after 15 minutes!",
							"Email sent.");
					return response;
				} else {
					// check the no of login attempts left
					int noOfLoginAttemptsMade = keyDetails.getConsecutiveAttemptCount();
					if (noOfLoginAttemptsMade >= environment.getProperty("max.consecutive.attempts", Long.class)) {
						// lock the user temporarily for next 2 hours
						LocalDateTime lockOutEndTime = LocalDateTime.now()
								.plusHours(environment.getProperty("lockout.time.duration.hours", Long.class));
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
						keyDetails.setLogInKey(loginKey);

						keyDetailsRepository.save(keyDetails);

						// sending email for login
						loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(),
								"Check out this URL to verify",
								"http://localhost:8659/open/user/final-login?loginKey=" + loginKey);

						ServiceResponse<String> response = new ServiceResponse<>(
								true, "Email sent with login verification link to the email id: "
										+ loginUserRecord.emailId() + ". It will expire after 15 minutes!",
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
		}

		// first login attempt and not a registered user
		KeyDetails newKeyDetails = new KeyDetails();
		newKeyDetails.setEmailId(loginUserRecord.emailId());
		String logInKey = UUID.randomUUID().toString();
		newKeyDetails.setLogInKey(logInKey);
		newKeyDetails.setKeyGenerationTime(LocalDateTime.now());
		newKeyDetails.setTrackingStartTime(LocalDateTime.now());
		newKeyDetails.setConsecutiveAttemptCount(1);
		keyDetailsRepository.save(newKeyDetails);

		// sending email for login
		loginEmailServiceImplementation.sendEmailWithUrl(loginUserRecord.emailId(), "Check out this URL to verify",
				"http://localhost:8659/open/user/final-login?loginKey=" + logInKey);

		ServiceResponse<String> response = new ServiceResponse<>(true,
				"Email sent with login verification link to the email id: " + loginUserRecord.emailId()
						+ ". It will expire after 15 minutes!",
				"Email sent.");
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
	public ServiceResponse<String> finalUserLogin(String loginKey) {
		KeyDetails existingKeyDetails = keyDetailsRepository.findByLogInKey(loginKey);
		if (existingKeyDetails != null) {
			long noOfMinutes = existingKeyDetails.getKeyGenerationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);

			if (noOfMinutes > environment.getProperty("magic.link.expiration.time.minutes", Long.class)) {
				ServiceResponse<String> response = new ServiceResponse<>(false, null,
						"Login Key has expired. Please, try again!");
				return response;
			}

			Optional<User> existingUserOptional = userRepository.findById(existingKeyDetails.getId());

			// check if the user is registered or not
			if (existingUserOptional.isPresent()) {
				// user is a registered user
				User existingUser = existingUserOptional.get();
				UserDetails userDetails = userDetailsService.loadUserByUsername(existingUser.getEmailId());
				String token = this.helper.generateToken(userDetails);

				System.out.println("Token: " + token);

				// resetting the relevant details in KeyDetails table for the first login of the
				// user
				existingKeyDetails.setConsecutiveAttemptCount(0);
				existingKeyDetails.setTrackingStartTime(LocalDateTime.now());

				keyDetailsRepository.save(existingKeyDetails);

				ServiceResponse<String> response = new ServiceResponse<>(true, "Login Successful!",
						"User logged in successfully.");
				return response;
			}

			// if user is not a registered user
			// first save the user before generating a jwt token
			User newUser = new User();
			newUser.setId(existingKeyDetails.getId());
			newUser.setEmailId(existingKeyDetails.getEmailId());
			newUser.setUserCreationTime(LocalDateTime.now());

			// set the user role
			Optional<Role> roleOptional = roleRepository.findByName("ADMIN_WRITE_ACCESS");
			if (roleOptional.isPresent()) {
				Role role = roleOptional.get();
				newUser.setRole(role);
			} else {
				Role newRole = new Role();
				newRole.setName("ADMIN_WRITE_ACCESS");
				Role savedRole = roleRepository.save(newRole);

				newUser.setRole(savedRole);
			}

			User savedUser = userRepository.save(newUser);

			UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmailId());
			String token = this.helper.generateToken(userDetails);

			System.out.println("Token: " + token);

			// resetting the relevant details in KeyDetails table for the first login of the
			// user
			existingKeyDetails.setConsecutiveAttemptCount(0);
			existingKeyDetails.setTrackingStartTime(LocalDateTime.now());

			keyDetailsRepository.save(existingKeyDetails);

			ServiceResponse<String> response = new ServiceResponse<>(true, "Login Successful!",
					"User logged in successfully.");
			return response;
		}
		ServiceResponse<String> response = new ServiceResponse<>(false, null,
				"Invalid login key. Please try with a valid key or try logging in once again.");
		return response;
	}

	/**
	 * Retrieves a list of all users from the database.
	 *
	 * @return A {@link ServiceResponse} containing the list of users if found, or
	 *         an empty list if no users are found. The response status and message
	 *         indicate the success or failure of the operation.
	 */
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

	/**
	 * Updates the profile details of a user based on the provided
	 * {@code setProfileDetailsRecord}.
	 *
	 * @param setProfileDetailsRecord The record containing the new profile details
	 *                                to be set.
	 * @return A {@code ServiceResponse} containing the updated user information if
	 *         the operation is successful; otherwise, an appropriate error message.
	 */
	@Override
	public ServiceResponse<User> setProfileDetails(SetProfileDetailsRecord setProfileDetailsRecord) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			// The current user is authenticated
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String username = userDetails.getUsername();

			Optional<User> existingUserOptional = userRepository.findByEmailId(setProfileDetailsRecord.emailId());

			if (existingUserOptional.isPresent()) {
				User existingUser = existingUserOptional.get();
				if (((existingUser.getRole().getName().equals(RoleEnum.USER_ALL_ACCESS.name())
						|| existingUser.getRole().getName().equals(RoleEnum.USER_DEFAULT_ACCESS.name()))
						&& existingUser.getEmailId().equals(username))
						|| (existingUser.getRole().getName().equals(RoleEnum.ADMIN_ALL_ACCESS.name())
								|| existingUser.getRole().getName().equals(RoleEnum.ADMIN_DEFAULT_ACCESS.name()))) {
					existingUser.setFirstName(setProfileDetailsRecord.firstName());
					existingUser.setLastName(setProfileDetailsRecord.lastName());
					existingUser.setAge(setProfileDetailsRecord.age());
					existingUser.setGender(setProfileDetailsRecord.gender());

					existingUser.setUserUpdationTime(LocalDateTime.now());

					User updatedUser = userRepository.save(existingUser);

					ServiceResponse<User> response = new ServiceResponse<>(true, updatedUser,
							"Current authenticated user successfully updated.");

					return response;
				}
				ServiceResponse<User> response = new ServiceResponse<>(false, null,
						"Only admins with all and update access and the owner of the provided email id with all and update access can update user details.");

				return response;
			}
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"No user found with this email id: " + setProfileDetailsRecord.emailId());

			return response;
		} else {
			// No user is authenticated
			ServiceResponse<User> response = new ServiceResponse<>(false, null,
					"Currently no user is authenticated. Please, login first!");
			return response;
		}

	}

}
