package com.ipsator.MagicLinkAuthentication_System.Controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller class that contains API end points for get current user's
 * username
 * 
 * @author Roshan
 *
 */
@RestController
@RequestMapping("/ipsator.com/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

	/**
	 * API end point for getting username of current authenticated user
	 * 
	 * @param principal a Principal obejct
	 * @return ResponseEntity object
	 */
	@GetMapping("/username")
	@PreAuthorize("hasAuthority('user:read')")
	public ResponseEntity<String> getUserName(Principal principal) {
		return new ResponseEntity<>("Currently authenticated user's username: " + principal.getName(), HttpStatus.OK);
	}

}
