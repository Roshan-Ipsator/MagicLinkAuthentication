package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An Entity to contain login keys with relevant details for different users
 * 
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class KeyDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String emailId;
	private String logInKey;
	private LocalDateTime keyGenerationTime;
	private Integer consecutiveAttemptCount;
	private LocalDateTime trackingStartTime;

	public KeyDetails(String emailId, String logInKey, LocalDateTime keyGenerationTime, Integer consecutiveAttemptCount,
			LocalDateTime trackingStartTime) {
		super();
		this.emailId = emailId;
		this.logInKey = logInKey;
		this.keyGenerationTime = keyGenerationTime;
		this.consecutiveAttemptCount = consecutiveAttemptCount;
		this.trackingStartTime = trackingStartTime;
	}

}
