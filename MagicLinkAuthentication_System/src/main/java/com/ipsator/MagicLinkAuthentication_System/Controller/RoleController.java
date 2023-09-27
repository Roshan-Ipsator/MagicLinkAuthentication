package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Payload.ApiResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.AddPermissionToRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.CreateRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.UpdateRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Service.RoleService;

@RestController
@RequestMapping("/role")
@PreAuthorize("hasRole('ADMIN_ALL_ACCESS')")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@PostMapping
	public ResponseEntity<ApiResponse> createRole(@RequestBody CreateRoleRecord createRoleRecord) {
		return roleService.createRole(createRoleRecord).finalResponse();
	}

	@PostMapping("/add-permission")
	public ResponseEntity<ApiResponse> addPermissionToRole(
			@RequestBody AddPermissionToRoleRecord addPermissionToRoleRecord) {
		return roleService.addPermissionToRole(addPermissionToRoleRecord).finalResponse();
	}

	@PutMapping("/update-role")
	public ResponseEntity<ApiResponse> updateRole(@RequestBody UpdateRoleRecord updateRoleRecord) {
		return roleService.updateRole(updateRoleRecord).finalResponse();
	}
}
