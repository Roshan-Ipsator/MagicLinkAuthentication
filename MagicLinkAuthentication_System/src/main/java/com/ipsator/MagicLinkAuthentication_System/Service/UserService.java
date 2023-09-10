package com.ipsator.MagicLinkAuthentication_System.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;

import jakarta.mail.MessagingException;

public interface UserService {
	public User registerUser(RegisterUserRecord registerUserRecord) throws UserException;
	
	public String sendVerifyEmail(LoginUserRecord loginUserRecord) throws UserException, MessagingException;
	
	public String sendHello();
}
