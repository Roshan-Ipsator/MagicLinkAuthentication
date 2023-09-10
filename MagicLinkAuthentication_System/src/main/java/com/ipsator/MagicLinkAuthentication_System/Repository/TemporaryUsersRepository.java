package com.ipsator.MagicLinkAuthentication_System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.TemporaryUsers;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;

@Repository
public interface TemporaryUsersRepository extends JpaRepository<TemporaryUsers, Integer> {
	TemporaryUsers findByRegistrationKey(String registrationKey);
}
