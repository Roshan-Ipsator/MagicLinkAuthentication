package com.ipsator.MagicLinkAuthentication_System.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class to use as a payload for ServiceResponses
 * 
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse<T> {
	private Boolean success;
	private T data;
	private String message;
}
