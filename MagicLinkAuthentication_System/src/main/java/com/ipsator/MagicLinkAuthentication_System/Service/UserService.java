package com.ipsator.MagicLinkAuthentication_System.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.TemporaryUsers;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
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
	public TemporaryUsers registerUserInit(RegisterUserRecord registerUserRecord)
			throws UserException, MessagingException;

	public User registerUserFinal(String registrationKey) throws UserException;

	public String sendVerifyEmail(LoginUserRecord loginUserRecord) throws UserException, MessagingException;
	
	public User userLoginFinal(String loginKey) throws UserException; 

	public String sendHello();
}
