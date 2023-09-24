package com.ipsator.MagicLinkAuthentication_System.Record;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public record SetProfileDetailsRecord(String emailId,
		@NotBlank(message = "First name can't be null or blank.") String firstName,
		@NotBlank(message = "Last name can't be null or blank.") String lastName,
		@NotBlank(message = "Gender can't be null or blank.") String gender,
		@NotNull(message = "Age can't be null.") @Min(value = 1, message = "Age must be at least 1") Integer age) {
//	private String emailId;
//
//	@NotBlank(message = "First name can't be null or blank.")
//	private String firstName;
//
//	@NotBlank(message = "Last name can't be null or blank.")
//	private String lastName;
//
//	@NotBlank(message = "Gender can't be null or blank.")
//	private String gender;
//
//	@NotNull(message = "Age can't be null.")
//	@Min(value = 1, message = "Age must be at least 1")
//	private Integer age;
}
