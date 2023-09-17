package com.ipsator.MagicLinkAuthentication_System.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A configuration class that contains beans of PassowrdEncoder and AuthenticationManager
 * 
 * @author Roshan
 *
 */
@Configuration
public class AppConfig {

	/**
	 * A bean declaration of PasswordEncoder
	 * 
	 * @return PasswordEncoder object
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * A bean declaration of AuthenticationManager
	 * 
	 * @param builder object of AuthenticationConfiguration
	 * 
	 * @return AuthenticationManager object
	 * 
	 * @throws Exception
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
		return builder.getAuthenticationManager();
	}
}
