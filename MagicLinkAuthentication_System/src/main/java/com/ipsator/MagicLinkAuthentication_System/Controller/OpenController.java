package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Payload.Error;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/ipsator.com/open/user")
public class OpenController {

	@Autowired
	private UserService userService;

	/**
	 * API end point for User Registration
	 * 
	 * @param registrationKey a String
	 * @return ResponseEntity object
	 */
	@GetMapping("/registration")
	public ResponseEntity<ApiResponse> finalUserRegistration(@RequestParam String emailId,
			@RequestParam String registrationKey) {
		ServiceResponse<User> savedUser = userService.userRegistration(emailId, registrationKey);
		if (savedUser.getSuccess()) {
			return new ResponseEntity<>(new ApiResponse("success", savedUser.getData(), null), HttpStatus.CREATED);
		}
		return new ResponseEntity<>(new ApiResponse("error", null, new Error(savedUser.getMessage())),
				HttpStatus.BAD_REQUEST);
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
		ServiceResponse<String> loginKeyConfirmation = userService.preFinalUserLogin(loginUserRecord);
		if (loginKeyConfirmation.getSuccess()) {
			return new ResponseEntity<>(new ApiResponse("success", loginKeyConfirmation.getData(), null),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse("error", null, new Error(loginKeyConfirmation.getMessage())),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * API end point for Final User Login
	 * 
	 * @param loginKey a String
	 * @return ResponseEntity object
	 */
	@GetMapping("/final-login")
	public ResponseEntity<ApiResponse> userLoginFinal(@RequestParam String loginKey) {
		ServiceResponse<String> loggedInUser = userService.finalUserLogin(loginKey);
		if (loggedInUser.getSuccess()) {
			return new ResponseEntity<>(new ApiResponse("success", loggedInUser.getData(), null), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse("error", null, new Error(loggedInUser.getMessage())),
				HttpStatus.BAD_REQUEST);
	}
}
