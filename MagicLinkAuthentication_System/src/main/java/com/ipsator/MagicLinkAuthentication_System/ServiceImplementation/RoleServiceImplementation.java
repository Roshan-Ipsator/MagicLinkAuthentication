package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipsator.MagicLinkAuthentication_System.Entity.Permission;
import com.ipsator.MagicLinkAuthentication_System.Entity.Role;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;
import com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.PermissionEnum;
import com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.RoleEnum;
import com.ipsator.MagicLinkAuthentication_System.Payload.ServiceResponse;
import com.ipsator.MagicLinkAuthentication_System.Record.AddPermissionToRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.CreateRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Record.UpdateRoleRecord;
import com.ipsator.MagicLinkAuthentication_System.Repository.PermissionRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.RoleRepository;
import com.ipsator.MagicLinkAuthentication_System.Repository.UserRepository;
import com.ipsator.MagicLinkAuthentication_System.Service.RoleService;

@Service
public class RoleServiceImplementation implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public ServiceResponse<Role> createRole(CreateRoleRecord createRoleRecord) {

		try {
			RoleEnum.valueOf(createRoleRecord.roleName().toUpperCase());
		} catch (IllegalArgumentException e) {
			ServiceResponse<Role> response = new ServiceResponse<>(false, null,
					"Invalid role name passed. Please, try again!");
			return response;
		}

		// check if the provided role is already existing or not
		Optional<Role> roleOptional = roleRepository.findByName(createRoleRecord.roleName());

		// if role is already present
		if (roleOptional.isPresent()) {
			ServiceResponse<Role> response = new ServiceResponse<>(false, null, "Role already exists!");
			return response;
		}

		// if role is not already present
		// create a new role
		Role newRole = new Role();
		newRole.setName(createRoleRecord.roleName().toUpperCase());

		Role savedRole = roleRepository.save(newRole);

		ServiceResponse<Role> response = new ServiceResponse<>(true, savedRole, "Role created successfully!");
		return response;
	}

	@Override
	public ServiceResponse<Role> addPermissionToRole(AddPermissionToRoleRecord addPermissionToRoleRecord) {

		Optional<Role> roleOptional = roleRepository.findByName(addPermissionToRoleRecord.roleName());

		if (roleOptional.isPresent()) {
			try {
				PermissionEnum.valueOf(addPermissionToRoleRecord.permissionName().toUpperCase());
			} catch (IllegalArgumentException e) {
				ServiceResponse<Role> response = new ServiceResponse<>(false, null,
						"Invalid permission name passed. Please, try again!");
				return response;
			}
			Role existingRole = roleOptional.get();

			// check if the provided permission already exists or not
			Optional<Permission> permissionOptional = permissionRepository.findByName(addPermissionToRoleRecord.permissionName());

			// if provided permission already exists
			if (permissionOptional.isPresent()) {
				Permission existingPermission = permissionOptional.get();

				// check if the role already has the permission
				List<Permission> currentPermissions = existingRole.getPermissions();

				if (currentPermissions.contains(existingPermission)) {
					ServiceResponse<Role> response = new ServiceResponse<>(false, null,
							"The provided role already has the requested permission.");
					return response;
				}

				// associate the permission with role
				List<Permission> permissions = existingRole.getPermissions();
				permissions.add(existingPermission);
				existingRole.setPermissions(permissions);

				// associate the role with permission
				List<Role> roles = existingPermission.getRoles();
				roles.add(existingRole);
				existingPermission.setRoles(roles);

				Role savedRole = roleRepository.save(existingRole);

				ServiceResponse<Role> response = new ServiceResponse<>(true, savedRole,
						"Permission added to the Role.");
				return response;
			}

			// if provided permission doesn't already exist
			// create a new permission
			Permission newPermission = new Permission();
			newPermission.setName(addPermissionToRoleRecord.permissionName().toUpperCase());

			Permission savedPermission = permissionRepository.save(newPermission);

			List<Role> roles = savedPermission.getRoles();
			if (roles == null) {
				List<Role> roleList = new ArrayList<>();
				roleList.add(existingRole);
				savedPermission.setRoles(roleList);
			} else {
				roles.add(existingRole);
				savedPermission.setRoles(roles);
			}

			List<Permission> permissions = existingRole.getPermissions();
			if (permissions == null) {
				List<Permission> permissionList = new ArrayList<>();
				permissionList.add(savedPermission);
				existingRole.setPermissions(permissionList);
			} else {
				permissions.add(savedPermission);
				existingRole.setPermissions(permissions);
			}

			Role savedRole = roleRepository.save(existingRole);

			ServiceResponse<Role> response = new ServiceResponse<>(true, savedRole, "Permission added to the Role.");
			return response;

		}
		ServiceResponse<Role> response = new ServiceResponse<>(false, null,
				"Invalid role name passed. Please, try again!");
		return response;

	}

	@Override
	public ServiceResponse<User> updateRole(UpdateRoleRecord updateRoleRecord) {
		// find user by email id
		Optional<User> userOptional = userRepository.findByEmailId(updateRoleRecord.userEmailId());

		if (userOptional.isPresent()) {
			// check if the requested role is valid or not
			try {
				RoleEnum.valueOf(updateRoleRecord.newRole().toUpperCase());
			} catch (IllegalArgumentException e) {
				ServiceResponse<User> response = new ServiceResponse<>(false, null,
						"Invalid role name passed. Please, try again!");
				return response;
			}

			User existingUser = userOptional.get();

			Role currentRole = existingUser.getRole();

			if (currentRole != null && currentRole.getName().equals(updateRoleRecord.newRole().toUpperCase())) {
				ServiceResponse<User> response = new ServiceResponse<>(false, null,
						"The user already has the requested role. No need to update with the same!");
				return response;
			}

			Optional<Role> roleOptional = roleRepository.findByName(updateRoleRecord.newRole().toUpperCase());

			// check if the requested new role is already existing or not
			// if the role doesn't already exist in database
			if (roleOptional.isEmpty()) {
				Role newRole = new Role();
				newRole.setName(updateRoleRecord.newRole());

				Role savedRole = roleRepository.save(newRole);

				existingUser.setRole(savedRole);
			} else {
				Role existingRole = roleOptional.get();

				existingUser.setRole(existingRole);
			}

			User savedUser = userRepository.save(existingUser);

			ServiceResponse<User> response = new ServiceResponse<>(true, savedUser,
					"User's role updated successfully.");
			return response;
		}
		ServiceResponse<User> response = new ServiceResponse<>(false, null,
				"Invalid email id passed. Please, try again!");
		return response;
	}

}
