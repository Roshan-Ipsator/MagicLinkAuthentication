package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import jakarta.mail.MessagingException;

/**
 * A controller class that contains API end points for user registration,
 * pre-final user login, and final user login
 * 
 * @author Roshan
 *
 */
@RestController
@RequestMapping("/open/user")
public class OpenController {
	@Autowired
	private UserService userService;

	/**
	 * Creates a new admin user with full access and privileges.
	 *
	 * This endpoint allows the creation of an administrator user account with
	 * all-access privileges. An administrator with full access typically has the
	 * highest level of control and permissions within the application.
	 *
	 * @return A ResponseEntity containing an ApiResponse with the result of the
	 *         creation operation. The ApiResponse includes information about the
	 *         success or failure of the operation, along with any relevant status
	 *         codes and messages.
	 *
	 */
	@PostMapping
	public ResponseEntity<ApiResponse> createAdminWithAllAccess() {
		return userService.createAdminWithAllAccess().finalResponse();
	}

	/**
	 * API end point for Pre-final User Login
	 * 
	 * @param loginUserRecord object of LoginUserRecord
	 * @return ResponseEntity object
	 * @throws MessagingException
	 */
	@PostMapping("/pre-final-login")
	public ResponseEntity<ApiResponse> preFinalUserLogin(@RequestBody LoginUserRecord loginUserRecord)
			throws MessagingException {
		ResponseEntity<ApiResponse> loginKeyConfirmationResponse = userService.preFinalUserLogin(loginUserRecord)
				.finalResponse();
		return loginKeyConfirmationResponse;
	}

	/**
	 * API end point for Final User Login
	 * 
	 * @param loginKey a String
	 * @return ResponseEntity object
	 */
	@GetMapping("/final-login")
	public ResponseEntity<ApiResponse> userLoginFinal(@RequestParam String loginKey) {
		ResponseEntity<ApiResponse> loggedInUserResponse = userService.finalUserLogin(loginKey).finalResponse();
		return loggedInUserResponse;
	}
}
