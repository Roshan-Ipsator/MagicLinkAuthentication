package com.ipsator.MagicLinkAuthentication_System.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
	private String status;
	private Object data;
	private Error error;
}
