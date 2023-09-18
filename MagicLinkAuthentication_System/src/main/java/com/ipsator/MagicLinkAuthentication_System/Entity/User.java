package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An Entity to contain all details of users after final registration
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
	private String passKey;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
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

	@Override
	public String getPassword() {
		return this.passKey;
	}
}
