package com.ipsator.MagicLinkAuthentication_System.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;

public interface UserService {
	public User registerUser(RegisterUserRecord registerUserRecord) throws UserException;
}
