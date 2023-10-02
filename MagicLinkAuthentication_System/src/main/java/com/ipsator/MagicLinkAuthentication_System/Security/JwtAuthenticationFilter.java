package com.ipsator.MagicLinkAuthentication_System.Security;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A custom filter class that gets executed once for each incoming HTTP request
 * 
 * @author Roshan
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

	@Autowired
	private JwtHelper jwtHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * A method to check the token that comes in the header for each incoming HTTP
	 * request
	 * 
	 * @param request     a HttpServletRequest object
	 * @param response    a HttpServletResponse object
	 * @param filterChain a FilterChain object
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestHeader = request.getHeader("Authorization");

		// Generating a unique trace ID
		String traceId = generateTraceId();

		// Adding trace ID to log
		logger.info("Trace ID: {} - Header: {}", traceId, requestHeader);

		// Adding trace ID to request headers
		request.setAttribute("TraceId", traceId);
		
		// After generating the trace ID, setting it in the response headers
		response.setHeader("Trace-ID", traceId);

		logger.info(" Header :  {}", requestHeader);
		String username = null;
		String token = null;
		if (requestHeader != null && requestHeader.startsWith("Bearer")) {
			// looking good
			token = requestHeader.substring(7);
			try {
				username = this.jwtHelper.getUsernameFromToken(token);
			} catch (IllegalArgumentException e) {
				logger.info("Trace ID: {} - Illegal Argument while fetching the username !!", traceId);
				e.printStackTrace();
			} catch (ExpiredJwtException e) {
				logger.info("Trace ID: {} - Given jwt token has expired !!", traceId);
				e.printStackTrace();
			} catch (MalformedJwtException e) {
				logger.info("Trace ID: {} - Some changes have been done in token !! Invalid Token", traceId);
				e.printStackTrace();
			} catch (Exception e) {
				logger.info("Trace ID: {} - An exception occurred while processing the token", traceId);
				e.printStackTrace();
			}
		} else {
			logger.info("Trace ID: {} - Invalid Header Value !!", traceId);
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// fetching user details from username
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
			if (validateToken) {
				// setting the authentication
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				logger.info("Trace ID: {} - Validation fails !!", traceId);
			}
		}
		filterChain.doFilter(request, response);
	}

	// Helper method to generate a unique trace ID
	private String generateTraceId() {
		return UUID.randomUUID().toString();
	}
}