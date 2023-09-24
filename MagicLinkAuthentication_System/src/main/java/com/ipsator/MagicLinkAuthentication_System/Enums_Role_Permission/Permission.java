package com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An Enum to contain all available permissions
 * 
 * @author Roshan
 */
@RequiredArgsConstructor
public enum Permission {
	ADMIN_READ("admin:read"), ADMIN_UPDATE("admin:update"), ADMIN_CREATE("admin:create"), ADMIN_DELETE("admin:delete"),
	USER_READ("user:read"), USER_UPDATE("user:update"), USER_CREATE("user:create"), USER_DELETE("user:delete");

	@Getter
	private final String permission;
}
