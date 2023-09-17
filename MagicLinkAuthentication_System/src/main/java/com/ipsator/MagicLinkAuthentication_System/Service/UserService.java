package com.ipsator.MagicLinkAuthentication_System.Service;

import java.util.Map;

import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUserRegistration;
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
	public ServiceResponse<PreFinalUserRegistration> registerUserInit(RegisterUserRecord registerUserRecord) throws MessagingException;

	public ServiceResponse<User> registerUserFinal(String registrationKey);

	public ServiceResponse<String> sendVerifyEmail(LoginUserRecord loginUserRecord) throws MessagingException;

	public ServiceResponse<String> userLoginFinal(String loginKey);

}
