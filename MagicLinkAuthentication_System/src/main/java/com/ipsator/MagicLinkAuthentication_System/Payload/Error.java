package com.ipsator.MagicLinkAuthentication_System.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class to use as a payload for errors in ApiResponses
 * 
 * @author Roshan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {
	
	private String message;
}
