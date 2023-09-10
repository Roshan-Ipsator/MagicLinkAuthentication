package com.ipsator.MagicLinkAuthentication_System.Config;

import org.springframework.web.servlet.HandlerInterceptor;

import com.ipsator.MagicLinkAuthentication_System.Utility.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
            if (JwtUtil.validateToken(token)) {
                return true; // Token is valid
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false; // Token is invalid or missing
    }
}
