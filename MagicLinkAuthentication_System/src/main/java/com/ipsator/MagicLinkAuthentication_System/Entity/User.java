package com.ipsator.MagicLinkAuthentication_System.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
	@Id
	private Integer id;
	private String firstName;
	private String lastName;
	private String emailId;
	private String gender;
	private Integer age;
}
