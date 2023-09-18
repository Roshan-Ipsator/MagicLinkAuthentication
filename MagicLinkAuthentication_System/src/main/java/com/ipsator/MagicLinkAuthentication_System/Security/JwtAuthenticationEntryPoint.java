package com.ipsator.MagicLinkAuthentication_System.Security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AuthenicationEntryPoint to handle the beginning of the authentication process
 * 
 * @author Roshan
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/**
	 * method for initiating the authentication process when an unauthenticated user
	 * attempts to access a protected resource
	 * 
	 * @param request       a HttpServletRequest object
	 * @param response      a HttpServletResponse object
	 * @param authException an AuthenticationException object
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter writer = response.getWriter();
		writer.println("Access Denied !! " + authException.getMessage());

	}

}
