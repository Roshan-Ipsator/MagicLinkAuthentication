package com.ipsator.MagicLinkAuthentication_System.Payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class to use as a payload for ApiResponses
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
	private String status;
	private Object data;
	private Error error;
}
