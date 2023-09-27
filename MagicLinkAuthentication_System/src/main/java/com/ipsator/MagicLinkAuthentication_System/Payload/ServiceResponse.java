package com.ipsator.MagicLinkAuthentication_System.Payload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
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

	public ResponseEntity<ApiResponse> finalResponse() {
		if (this.success) {
			ApiResponse apiResponse = new ApiResponse("success", this.data, null);

			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
		} else {
			Error error = new Error();
			error.setMessage(this.message);

			ApiResponse apiResponse = new ApiResponse("error", null, error);

			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
