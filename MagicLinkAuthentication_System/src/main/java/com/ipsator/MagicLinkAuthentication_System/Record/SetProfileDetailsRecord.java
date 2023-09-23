package com.ipsator.MagicLinkAuthentication_System.Record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetProfileDetailsRecord {
	private String emailId;
	private String firstName;
	private String lastName;
	private String gender;
	private Integer age;
}
