package com.ipsator.MagicLinkAuthentication_System.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('USER')")
public class UserController {

	@GetMapping("/username")
	@PreAuthorize("hasAuthority('user:read')")
	public ResponseEntity<String> getUserName(Principal principal) {
		return new ResponseEntity<>("Currently authenticated user's username: " + principal.getName(), HttpStatus.OK);
	}

}
