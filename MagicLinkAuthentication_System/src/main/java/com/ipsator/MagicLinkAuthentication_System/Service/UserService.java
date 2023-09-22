package com.ipsator.MagicLinkAuthentication_System.Service;

import java.util.List;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;

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
	
	public ServiceResponse<List<User>> getAllUsers();

}
