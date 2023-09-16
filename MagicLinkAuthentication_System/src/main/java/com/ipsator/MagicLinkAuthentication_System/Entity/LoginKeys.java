package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LoginKeys {
	@Id
	private Integer userId;
	private String loginKey;
	private LocalDateTime keyGenerationTime;
	private Integer consecutiveAttemptCount;
	private LocalDateTime trackingStartTime;
}
