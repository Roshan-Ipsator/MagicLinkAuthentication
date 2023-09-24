package com.ipsator.MagicLinkAuthentication_System.Role_Permission;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.ADMIN_READ;
import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.ADMIN_UPDATE;
import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.ADMIN_DELETE;
import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.ADMIN_CREATE;

import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.USER_READ;
import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.USER_UPDATE;
import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.USER_DELETE;
import static com.ipsator.MagicLinkAuthentication_System.Role_Permission.Permission.USER_CREATE;

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

	public List<SimpleGrantedAuthority> getAuthorities() {
		var authorities = getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
		return authorities;
	}
}
