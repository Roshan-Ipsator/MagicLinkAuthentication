package com.ipsator.MagicLinkAuthentication_System.Security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Payload.Error;

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
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setStatus("error");
		apiResponse.setData(null);
		Error error = new Error();
		error.setMessage("Access Denied !! " + authException.getMessage());
		apiResponse.setError(error);

		// Serialize the JSON response
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonResponse = objectMapper.writeValueAsString(apiResponse);

		// Set the response content type to JSON
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		PrintWriter writer = response.getWriter();
		writer.println(jsonResponse);

	}

}
