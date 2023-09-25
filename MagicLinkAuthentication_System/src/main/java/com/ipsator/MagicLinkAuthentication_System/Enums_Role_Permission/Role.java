package com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission;

import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.ADMIN_CREATE;
import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.ADMIN_DELETE;
import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.ADMIN_READ;
import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.ADMIN_UPDATE;
import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.USER_CREATE;
import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.USER_DELETE;
import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.USER_READ;
import static com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Permission.USER_UPDATE;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An Enum to contain all available roles
 * 
 * @author Roshan
 */
@RequiredArgsConstructor
public enum Role {
	
	ADMIN_ALL_ACCESS(Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE, USER_READ, USER_UPDATE, USER_DELETE,
			USER_CREATE)),
	ADMIN_READ_ACCESS(Set.of(ADMIN_READ, USER_READ)), ADMIN_UPDATE_ACCESS(Set.of(ADMIN_UPDATE, USER_UPDATE)),
	ADMIN_DELETE_ACCESS(Set.of(ADMIN_DELETE, USER_DELETE)), ADMIN_CREATE_ACCESS(Set.of(ADMIN_CREATE, USER_CREATE)),

	USER_ALL_ACCESS(Set.of(USER_READ, USER_UPDATE, USER_DELETE, USER_CREATE)), USER_READ_ACCESS(Set.of(USER_READ)),
	USER_UPDATE_ACCESS(Set.of(USER_UPDATE)), USER_DELETE_ACCESS(Set.of(USER_DELETE)),
	USER_CREATE_ACCESS(Set.of(USER_CREATE)), USER_DEFAULT_ACCESS(Set.of(USER_READ, USER_CREATE));

	@Getter
	private final Set<Permission> permissions;

	/**
	 * Retrieves the list of authorities (permissions and roles) associated with
	 * this user role.
	 *
	 * This method combines the user's permissions and the role itself to generate a
	 * list of authorities that can be used for authentication and authorization
	 * checks.
	 *
	 * @return A list of
	 *         {@link org.springframework.security.core.authority.SimpleGrantedAuthority}
	 *         objects representing the user's permissions and the role itself.
	 */
	public List<SimpleGrantedAuthority> getAuthorities() {
		var authorities = getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return authorities;
	}
}
