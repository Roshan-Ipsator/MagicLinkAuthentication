package com.ipsator.MagicLinkAuthentication_System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.KeyDetails;

/**
 * A repository for LoginKeys
 * 
 * @author Roshan
 */
@Repository
public interface KeyDetailsRepository extends JpaRepository<KeyDetails, Integer> {
	KeyDetails findBySignUpLogInKey(String key);

	KeyDetails findByEmailId(String emailId);
}
