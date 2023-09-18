package com.ipsator.MagicLinkAuthentication_System.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.User;

/**
 * A repository for Users
 * 
 * @author Roshan
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmailId(String emailId);
}
