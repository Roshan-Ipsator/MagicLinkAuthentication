package com.ipsator.MagicLinkAuthentication_System.Service;

import java.util.List;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.SetProfileDetailsRecord;

import jakarta.mail.MessagingException;

/**
 * The interface containing all the abstract method declarations for all
 * functionalities
 * 
 * @author Roshan
 *
 */
public interface UserService {

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
	public ServiceResponse<User> userRegistration(String emailId, String registrationKey);

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
	public ServiceResponse<String> preFinalUserLogin(LoginUserRecord loginUserRecord) throws MessagingException;

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
	public ServiceResponse<String> finalUserLogin(String loginKey);

	/**
	 * Retrieves a list of all users from the service.
	 *
	 * This method sends a request to the service to retrieve a list of all users
	 * currently available in the system. The result is encapsulated within a
	 * {@link ServiceResponse} object, which provides information about the
	 * operation's success or failure and the retrieved user data.
	 *
	 * @return A {@link ServiceResponse} containing a list of users if the operation
	 *         is successful. If the operation fails, the response will indicate the
	 *         error.
	 *
	 * @see ServiceResponse
	 * @see User
	 */
	public ServiceResponse<List<User>> getAllUsers();

	/**
	 * Sets and updates the profile details of a user based on the information
	 * provided in the {@code SetProfileDetailsRecord}.
	 *
	 * @param setProfileDetailsRecord The record containing the updated profile
	 *                                details for the user.
	 * @return A {@code ServiceResponse} representing the result of setting the
	 *         profile details. It encapsulates information about the success or
	 *         failure of the operation, any error messages, and the updated user
	 *         profile if the operation was successful.
	 */
	public ServiceResponse<User> setProfileDetails(SetProfileDetailsRecord setProfileDetailsRecord);

}
