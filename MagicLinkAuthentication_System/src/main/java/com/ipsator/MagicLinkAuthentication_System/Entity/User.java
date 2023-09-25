package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ipsator.MagicLinkAuthentication_System.Enums_Role_Permission.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An Entity to contain all details of users
 * 
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements UserDetails {
	
	@Id
	private Integer userId;

	private String firstName;

	private String lastName;

	private String emailId;

	private String gender;

	private Integer age;

	@Enumerated(EnumType.STRING)
	private Role role;

	private LocalDateTime userRegistrationTime;

	private LocalDateTime userUpdationTime;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.getAuthorities();
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.emailId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
