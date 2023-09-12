package com.ipsator.MagicLinkAuthentication_System.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse<T> {
	private String status;
	private T data;
	private String message;
	private String code;
}
