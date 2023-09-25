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
	
	/**
	 * Retrieves an {@link java.util.Optional} containing a {@link User} entity
	 * based on the provided email ID.
	 *
	 * @param emailId The email ID to search for.
	 * @return An {@code Optional} containing a {@code User} entity if found, or an
	 *         empty {@code Optional} if no matching user is found.
	 */
	Optional<User> findByEmailId(String emailId);
}
