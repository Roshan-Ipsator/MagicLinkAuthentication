package com.ipsator.MagicLinkAuthentication_System.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;

@RestController
@RequestMapping("/ipsator.com/user")
public class SecureUserController {
	
//	public ResponseEntity<User> getUserDetails() {
//		
//	}
	
	@GetMapping("/resource")
    public ResponseEntity<String> getSecureResource() {
        return ResponseEntity.ok("This is a secure resource!");
    }
}
