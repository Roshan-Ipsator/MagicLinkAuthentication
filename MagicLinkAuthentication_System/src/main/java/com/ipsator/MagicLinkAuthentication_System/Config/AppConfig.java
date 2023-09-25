package com.ipsator.MagicLinkAuthentication_System.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A configuration class that contains beans of PassowrdEncoder and
 * AuthenticationManager
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
}
