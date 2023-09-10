package com.ipsator.MagicLinkAuthentication_System.Utility;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private static final long expirationMs = 3600000; // 1 hour

	public static String generateToken(String username) {
		Date expiration = new Date(System.currentTimeMillis() + expirationMs);
		return Jwts.builder().setSubject(username).setExpiration(expiration).signWith(key).compact();
	}

	public static boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
