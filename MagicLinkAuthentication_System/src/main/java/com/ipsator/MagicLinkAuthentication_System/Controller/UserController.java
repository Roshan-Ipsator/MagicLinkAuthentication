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

import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUserRegistration;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Exception.UserException;
import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Payload.Error;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.LoginUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.RegisterUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.ValidateUserRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/ipsator.com/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	// Testing Purpose
	@GetMapping
	public String hello() {
		return "Welcome to my application! :)";
	}

	@PostMapping
	public ResponseEntity<ApiResponse> registerUserInit(@RequestBody RegisterUserRecord registerUserRecord) throws MessagingException {
		ServiceResponse<Object> savedTemporaryUser = userService.registerUserInit(registerUserRecord);
		if(savedTemporaryUser.getSuccess()) {
			return new ResponseEntity<>(new ApiResponse("success",savedTemporaryUser.getData(),null), HttpStatus.CREATED);
		}
		return new ResponseEntity<>(new ApiResponse("error",null,new Error(savedTemporaryUser.getMessage())), HttpStatus.BAD_REQUEST);
	}
	
	@GetMapping("/finalRegistration")
	public ResponseEntity<ApiResponse> registerUserFinal(@RequestParam String registrationKey) {
		ServiceResponse<Object> savedUser = userService.registerUserFinal(registrationKey);
		if(savedUser.getSuccess()) {
			return new ResponseEntity<>(new ApiResponse("success",savedUser.getData(),null), HttpStatus.CREATED);
		}
		return new ResponseEntity<>(new ApiResponse("error",null,new Error(savedUser.getMessage())), HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/send-verify-email")
	public ResponseEntity<String> sendVerifyEmail(@RequestBody LoginUserRecord loginUserRecord) throws UserException, MessagingException{
		return new ResponseEntity<String>(userService.sendVerifyEmail(loginUserRecord), HttpStatus.OK);
	}
	
	@GetMapping("/finalLogin")
	public ResponseEntity<User> userLoginFinal(@RequestParam String loginKey) {
		return new ResponseEntity<User>(userService.userLoginFinal(loginKey), HttpStatus.OK);
	}
	
	
	
	
	// Currently not in use
	@GetMapping("/validateUser")
	public ResponseEntity<User> validateUserUsingToken(@RequestBody ValidateUserRecord validateUserRecord){
		try {
            // Parse the JWT token without verifying the signature
//            @SuppressWarnings("deprecation")
//			Claims claims = Jwts.parser()
//                    .parseClaimsJwt(validateUserRecord.jwt())
//                    .getBody();
            
            Claims claims = (Claims) Jwts.parserBuilder().setSigningKey(Keys.secretKeyFor(SignatureAlgorithm.HS256)).build().parse(validateUserRecord.jwt()).getBody();
            
            User user = userRepository.findByEmailId(claims.getSubject());
            if(user!=null) {
            	return new ResponseEntity<>(user,HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
}
