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

/**
 * The {@code RoleController} class handles HTTP requests related to roles and
 * permissions within the application. It provides endpoints for creating,
 * updating, and managing roles and their associated permissions.
 */
@RestController
@RequestMapping("/role")
@PreAuthorize("hasRole('ADMIN_ALL_ACCESS')")
public class RoleController {

	@Autowired
	private RoleService roleService;

	/**
	 * Creates a new role in the system based on the provided
	 * {@code CreateRoleRecord}.
	 *
	 * @param createRoleRecord The details of the role to be created.
	 * @return A {@code ResponseEntity} containing the result of the role creation
	 *         operation wrapped in an {@code ApiResponse}.
	 */
	@PostMapping
	public ResponseEntity<ApiResponse> createRole(@RequestBody CreateRoleRecord createRoleRecord) {
		return roleService.createRole(createRoleRecord).finalResponse();
	}

	/**
	 * Adds a permission to an existing role in the system.
	 *
	 * @param addPermissionToRoleRecord The details of the permission to be added to
	 *                                  the role.
	 * @return A {@code ResponseEntity} containing the result of the permission
	 *         addition operation wrapped in an {@code ApiResponse}.
	 */
	@PostMapping("/add-permission")
	public ResponseEntity<ApiResponse> addPermissionToRole(
			@RequestBody AddPermissionToRoleRecord addPermissionToRoleRecord) {
		return roleService.addPermissionToRole(addPermissionToRoleRecord).finalResponse();
	}

	/**
	 * Updates an existing role in the system based on the provided
	 * {@code UpdateRoleRecord}.
	 *
	 * @param updateRoleRecord The details of the role to be updated.
	 * @return A {@code ResponseEntity} containing the result of the role update
	 *         operation wrapped in an {@code ApiResponse}.
	 */
	@PutMapping("/update-role")
	public ResponseEntity<ApiResponse> updateRole(@RequestBody UpdateRoleRecord updateRoleRecord) {
		return roleService.updateRole(updateRoleRecord).finalResponse();
	}
}
