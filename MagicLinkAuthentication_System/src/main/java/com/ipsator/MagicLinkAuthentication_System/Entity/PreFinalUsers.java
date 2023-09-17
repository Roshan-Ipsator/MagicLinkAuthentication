package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An Entity to contain all the details of users before final registration
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
	private String firstName;
	private String lastName;
	private String emailId;
	private String gender;
	private Integer age;
	private String registrationKey;
	private LocalDateTime keyGenerationTime;
	private String userStatus;
}
