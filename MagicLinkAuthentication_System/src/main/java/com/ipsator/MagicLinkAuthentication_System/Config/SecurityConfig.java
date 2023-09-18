package com.ipsator.MagicLinkAuthentication_System.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ipsator.MagicLinkAuthentication_System.Security.JwtAuthenticationEntryPoint;
import com.ipsator.MagicLinkAuthentication_System.Security.JwtAuthenticationFilter;

/**
 * A configuration class that contains beans of SecurityFilterChain and
 * DaoAuthenticationProvider
 * 
 * @author Roshan
 *
 */
@Configuration
public class SecurityConfig {
	@Autowired
	private JwtAuthenticationEntryPoint point;
	@Autowired
	private JwtAuthenticationFilter filter;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * A bean declaration of SecurityFilterChain
	 * 
	 * @param http object of HttpSecurity
	 * 
	 * @return SecurityFilterChain object
	 * 
	 * @throws Exception
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable()).authorizeHttpRequests(auth -> auth
				.requestMatchers("/ipsator.com/user/pre-final-registration", "/ipsator.com/user/final-registration",
						"/ipsator.com/user/pre-final-login", "/ipsator.com/user/final-login")
				.permitAll().anyRequest().authenticated()).exceptionHandling(ex -> ex.authenticationEntryPoint(point))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * A bean declaration of DaoAuthenticationProvider
	 * 
	 * @param
	 * 
	 * @return DaoAuthenticationProvider object
	 */
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		return daoAuthenticationProvider;
	}
}
