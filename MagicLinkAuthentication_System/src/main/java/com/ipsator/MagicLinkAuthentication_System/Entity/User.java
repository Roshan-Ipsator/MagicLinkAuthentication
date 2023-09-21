package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer userId;
	
//	@NotNull(message = "First name can't be null.")
	private String firstName;
	
//	@NotNull(message = "Last name can't be null.")
	private String lastName;
	
//	@Email(message = "Email must be a valid one.")
	private String emailId;
	
//	@NotNull(message = "Gender can't be null.")
	private String gender;
	
//	@NotNull(message = "Age can't be null.")
	private Integer age;
	
	private LocalDateTime userRegistrationTime;
	
	private LocalDateTime userUpdationTime;
	
	private String registrationKey;
	
	private LocalDateTime regdKeyGenerationTime;
	
	private String userStatus;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
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
