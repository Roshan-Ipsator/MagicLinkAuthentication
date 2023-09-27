package com.ipsator.MagicLinkAuthentication_System.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.Role;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.AddPermissionToRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.CreateRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.UpdateRoleRecord;

public interface RoleService {
	public ServiceResponse<Role> createRole(CreateRoleRecord createRoleRecord);

	public ServiceResponse<Role> addPermissionToRole(AddPermissionToRoleRecord addPermissionToRoleRecord);

	public ServiceResponse<User> updateRole(UpdateRoleRecord updateRoleRecord);
}
