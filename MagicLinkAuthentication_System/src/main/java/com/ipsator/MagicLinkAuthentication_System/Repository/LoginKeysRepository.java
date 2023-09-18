package com.ipsator.MagicLinkAuthentication_System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.LoginKeys;

/**
 * A repository for LoginKeys
 * 
 * @author Roshan
 */
@Repository
public interface LoginKeysRepository extends JpaRepository<LoginKeys, Integer> {
	/**
	 * Finds a login key in the database and returns the corresponding
	 * {@link LoginKeys} entity, if it exists.
	 *
	 * @param loginKey The login key to search for.
	 * @return A {@link LoginKeys} entity representing the login key, or
	 *         {@code null} if the key is not found.
	 */
	LoginKeys findByLoginKey(String loginKey);
}
