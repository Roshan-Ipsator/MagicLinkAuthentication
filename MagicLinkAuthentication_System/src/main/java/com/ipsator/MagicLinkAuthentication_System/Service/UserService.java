package com.ipsator.MagicLinkAuthentication_System.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUserRegistration;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;

import jakarta.mail.MessagingException;

/**
 * The interface containing all the abstract method declarations for all functionalities 
 * 
 * @author Roshan
 *
 */
public interface UserService {
	public ServiceResponse<Object> registerUserInit(RegisterUserRecord registerUserRecord)
			throws MessagingException;

	public ServiceResponse<Object> registerUserFinal(String registrationKey);

	public String sendVerifyEmail(LoginUserRecord loginUserRecord) throws UserException, MessagingException;
	
	public User userLoginFinal(String loginKey) throws UserException; 

	public String sendHello();
}
