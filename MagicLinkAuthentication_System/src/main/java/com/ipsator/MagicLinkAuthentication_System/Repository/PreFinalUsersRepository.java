package com.ipsator.MagicLinkAuthentication_System.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUsers;

/**
 * A repository for Pre Final Users
 * 
 * @author Roshan
 */
@Repository
public interface PreFinalUsersRepository extends JpaRepository<PreFinalUsers, Integer> {
	/**
	 * Retrieves a {@link PreFinalUsers} entity from the database based on the
	 * provided registration key.
	 *
	 * @param registrationKey The unique registration key associated with the user.
	 * @return A {@link PreFinalUsers} entity representing the user with the given
	 *         registration key, or {@code null} if no user is found with the
	 *         specified registration key.
	 */
	PreFinalUsers findByRegistrationKey(String registrationKey);

	/**
	 * Retrieves a list of {@link PreFinalUsers} entities by their email ID.
	 *
	 * @param emailId The email ID to search for.
	 * @return A list of {@link PreFinalUsers} entities matching the specified email
	 *         ID.
	 */
	List<PreFinalUsers> findByEmailId(String emailId);
}
