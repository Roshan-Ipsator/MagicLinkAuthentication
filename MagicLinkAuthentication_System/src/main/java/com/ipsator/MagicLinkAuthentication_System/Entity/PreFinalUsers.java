package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.time.LocalDateTime;

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
 * An Entity to contain all the details of users before final registration
 * 
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PreFinalUsers {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer userId;
	
	@NotNull(message = "First name can't be null.")
	private String firstName;
	
	@NotNull(message = "Last name can't be null.")
	private String lastName;
	
	@Email(message = "Email must be a valid one.")
	private String emailId;
	
	@NotNull(message = "Gender can't be null.")
	private String gender;
	
	@NotNull(message = "Age can't be null.")
	private Integer age;
	
	private String registrationKey;
	private LocalDateTime keyGenerationTime;
	private String userStatus;
}
