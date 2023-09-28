package com.ipsator.MagicLinkAuthentication_System.Payload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

	/**
	 * Generates a standardized response based on the success status of the
	 * operation.
	 *
	 * @return A ResponseEntity containing an ApiResponse with either a success
	 *         message and data or an error message.
	 */
	public ResponseEntity<ApiResponse> finalResponse() {
		if (this.success) {
			// If the operation was successful, create a response with success status, data,
			// and HTTP status OK (200).
			ApiResponse apiResponse = new ApiResponse("success", this.data, null);

			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
		} else {
			// If the operation failed, create a response with error status, error message,
			// and HTTP status BAD REQUEST (400).
			Error error = new Error();
			error.setMessage(this.message);

			ApiResponse apiResponse = new ApiResponse("error", null, error);

			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
