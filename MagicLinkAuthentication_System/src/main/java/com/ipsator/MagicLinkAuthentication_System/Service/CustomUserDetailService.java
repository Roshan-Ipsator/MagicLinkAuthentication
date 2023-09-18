package com.ipsator.MagicLinkAuthentication_System.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;

/**
 * CustomUserDetailService is a service class that implements the Spring
 * Security UserDetailsService interface. It is responsible for loading user
 * details from the application's database based on the provided username during
 * authentication.
 *
 * @author Roshan
 *
 * @see UserDetailsService
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see UserRepository
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Loads user details by username for authentication and authorization.
	 *
	 * @param username The username (typically an email or username) provided during
	 *                 authentication.
	 * @return A UserDetails object containing the user's information.
	 * @throws UsernameNotFoundException If no user with the provided username is
	 *                                   found.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailId(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found!!"));
		return user;
	}

}
