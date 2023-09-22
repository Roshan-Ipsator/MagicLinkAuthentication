package com.ipsator.MagicLinkAuthentication_System.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Payload.Error;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

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
		ServiceResponse<List<User>> allUsersResponse = userService.getAllUsers();
		if (allUsersResponse.getSuccess()) {
			return new ResponseEntity<>(new ApiResponse("success", allUsersResponse.getData(), null), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse("error", null, new Error(allUsersResponse.getMessage())),
				HttpStatus.BAD_REQUEST);
	}
}
