package com.ipsator.MagicLinkAuthentication_System.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.Role;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.AddPermissionToRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.CreateRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.UpdateRoleRecord;

/**
 * The RoleService interface defines methods for managing roles and permissions.
 * It provides functionality to create roles, add permissions to roles, and
 * update role details.
 */
public interface RoleService {

	/**
	 * Creates a new role based on the provided CreateRoleRecord.
	 *
	 * @param createRoleRecord The record containing information to create the role.
	 * @return A ServiceResponse containing the created Role if successful, along
	 *         with a success message, or an error message if the operation fails.
	 */
	public ServiceResponse<Role> createRole(CreateRoleRecord createRoleRecord);

	/**
	 * Adds permissions to an existing role based on the provided
	 * AddPermissionToRoleRecord.
	 *
	 * @param addPermissionToRoleRecord The record containing information to add
	 *                                  permissions to the role.
	 * @return A ServiceResponse containing the updated Role with added permissions
	 *         if successful, along with a success message, or an error message if
	 *         the operation fails.
	 */
	public ServiceResponse<Role> addPermissionToRole(AddPermissionToRoleRecord addPermissionToRoleRecord);

	/**
	 * Updates the details of an existing role based on the provided
	 * UpdateRoleRecord.
	 *
	 * @param updateRoleRecord The record containing information to update the role.
	 * @return A ServiceResponse containing the updated Role if successful, along
	 *         with a success message, or an error message if the operation fails.
	 */
	public ServiceResponse<User> updateRole(UpdateRoleRecord updateRoleRecord);
}
