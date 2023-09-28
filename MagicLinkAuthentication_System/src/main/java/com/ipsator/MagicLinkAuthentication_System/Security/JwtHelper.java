package com.ipsator.MagicLinkAuthentication_System.Security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * A class that contains methods related to operation on JWT token line
 * generateToken, validateToken, etc.
 * 
 * @author Roshan
 */
@Component
public class JwtHelper {

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

	/**
	 * A method to retrieve username from a jwt token
	 * 
	 * @param token a String
	 * @return Subject from token in form of String
	 */
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 * A method to retrieve expiration date from a jwt token
	 * 
	 * @param token a String
	 * @return Expiration from token as an object of Date
	 */
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	/**
	 * Extracts a specific claim from a JWT token using the provided claims resolver
	 * function.
	 * 
	 * @param token          a String
	 * @param claimsResolver a Function object <T> The type of claim to extract
	 * 
	 * @return The extracted claim of type T
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Parses a JWT token and retrieves all the claims (payload) from it.
	 *
	 * @param token The JWT token to parse and extract claims from.
	 * 
	 * @return A Claims object containing all the claims (payload) from the JWT
	 *         token.
	 * 
	 * @throws JwtException If the token cannot be parsed or if it has an invalid
	 *                      signature.
	 */
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	/**
	 * Checks whether a JWT token has expired.
	 *
	 * @param token The JWT token to check for expiration.
	 * @return {@code true} if the token has expired, {@code false} if it is still
	 *         valid.
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	/**
	 * Generates a JSON Web Token (JWT) for the provided user details.
	 *
	 * @param userDetails The user details for whom the JWT is generated.
	 * @return A JWT string representing the user's authentication.
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}

	/**
	 * Generates a JSON Web Token (JWT) with the specified claims and subject.
	 *
	 * @param claims  A map containing the claims to include in the JWT.
	 * @param subject The subject of the JWT, typically representing the token's
	 *                owner or context.
	 * @return The generated JWT as a string.
	 */
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	/**
	 * Validates a JWT token by comparing the username extracted from the token with
	 * the username from the provided user details and checking if the token has
	 * expired.
	 *
	 * @param token       The JWT token to be validated.
	 * @param userDetails The user details representing the user attempting to
	 *                    validate the token.
	 * @return {@code true} if the token is valid (username matches and not
	 *         expired), otherwise {@code false}.
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
