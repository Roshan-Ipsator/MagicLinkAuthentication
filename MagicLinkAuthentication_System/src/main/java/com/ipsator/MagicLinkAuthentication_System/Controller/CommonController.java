package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.SetProfileDetailsRecord;
import com.ipsator.MagicLinkAuthentication_System.Service.UserService;

import jakarta.validation.Valid;

/**
 * A controller class that contains API end point for update user and can be
 * accessible only by the with roles 'USER_ALL_ACCESS', 'USER_UPDATE_ACCESS',
 * 'ADMIN_ALL_ACCESS','ADMIN_UPDATE_ACCESS'
 * 
 * @author Roshan
 *
 */
@RestController
@RequestMapping("/ipsator.com/common")
public class CommonController {
	@Autowired
	private UserService userService;

	/**
	 * API end point for updating a user's details
	 * 
	 * @return ResponseEntity<ApiResponse> where ApiResponse contains the updated
	 *         user details as its data
	 */
	@PutMapping("/update-user")
	@PreAuthorize("hasAnyRole('USER_ALL_ACCESS', 'USER_UPDATE_ACCESS', 'ADMIN_ALL_ACCESS','ADMIN_UPDATE_ACCESS')")
	public ResponseEntity<ApiResponse> setProfileDetails(
			@Valid @RequestBody SetProfileDetailsRecord setProfileDetailsRecord) {
		ResponseEntity<ApiResponse> setProfileDetailsResponse = userService.setProfileDetails(setProfileDetailsRecord)
				.finalResponse();
		return setProfileDetailsResponse;
	}
}
