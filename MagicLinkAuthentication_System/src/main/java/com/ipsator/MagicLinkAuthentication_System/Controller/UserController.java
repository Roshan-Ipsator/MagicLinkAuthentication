package com.ipsator.MagicLinkAuthentication_System.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUsers;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Payload.Error;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

/**
 * A controller class that contains API end points for Pre-final User
 * Registration, Final User Registration, Pre-final User Login, Final User
 * Login, Get All Users
 * 
 * @author Roshan
 *
 */
@RestController
@RequestMapping("/ipsator.com/user")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	/**
	 * API end point for Pre-final User Registration
	 * 
	 * @param registerUserRecord object of RegisterUserRecord
	 * @return ResponseEntity object
	 * @throws MessagingException
	 */
	@PostMapping("/pre-final-registration")
	public ResponseEntity<ApiResponse> preFinalUserRegistration(@Valid @RequestBody RegisterUserRecord registerUserRecord)
			throws MessagingException {
		ServiceResponse<PreFinalUsers> savedTemporaryUser = userService.preFinalUserRegistration(registerUserRecord);
		if (savedTemporaryUser.getSuccess()) {
			return new ResponseEntity<>(new ApiResponse("success", savedTemporaryUser.getData(), null),
					HttpStatus.CREATED);
		}
		return new ResponseEntity<>(new ApiResponse("error", null, new Error(savedTemporaryUser.getMessage())),
				HttpStatus.BAD_REQUEST);
	}

	/**
	 * API end point for Final User Registration
	 * 
	 * @param registrationKey a String
	 * @return ResponseEntity object
	 */
	@GetMapping("/final-registration")
	public ResponseEntity<ApiResponse> finalUserRegistration(@RequestParam String registrationKey) {
		ServiceResponse<User> savedUser = userService.finalUserRegistration(registrationKey);
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

	/**
	 * API end point for Getting All Users
	 * 
	 * @return List of users
	 */
	@GetMapping("/get-all-users")
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

}
