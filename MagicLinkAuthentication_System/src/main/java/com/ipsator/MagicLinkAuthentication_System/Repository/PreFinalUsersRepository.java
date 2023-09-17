package com.ipsator.MagicLinkAuthentication_System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUsers;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;

/**
 * A repository for Pre Final Users
 * @author Roshan
 */
@Repository
public interface PreFinalUsersRepository extends JpaRepository<PreFinalUsers, Integer> {
	PreFinalUsers findByRegistrationKey(String registrationKey);
}
