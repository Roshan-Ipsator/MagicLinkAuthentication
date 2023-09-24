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

	public ResponseEntity<ApiResponse> finalResponse() {
		if (this.getSuccess()) {
			ApiResponse apiResponse = new ApiResponse();
			apiResponse.setStatus("success");
			apiResponse.setData(this.data);
			apiResponse.setError(null);

			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

		} else {
			ApiResponse apiResponse = new ApiResponse();
			apiResponse.setStatus("error");
			apiResponse.setData(null);

			Error error = new Error();
			error.setMessage(this.message);

			apiResponse.setError(error);

			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
		}
	}
}
