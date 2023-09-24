package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

/**
 * A controller class that contains API end point for Get All Users and can be
 * accessible only by the Admins with admin:read authority
 * 
 * @author Roshan
 *
 */
@RestController
@RequestMapping("/ipsator.com/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private UserService userService;

	/**
	 * API end point for Getting All Users
	 * 
	 * @return List of users
	 */
	@GetMapping("/get-all-users")
	@PreAuthorize("hasAuthority('admin:read')")
	public ResponseEntity<ApiResponse> getAllUsers() {
		ResponseEntity<ApiResponse> getAllUsersResponseResponse = userService.getAllUsers().finalResponse();
		return getAllUsersResponseResponse;
	}
}
