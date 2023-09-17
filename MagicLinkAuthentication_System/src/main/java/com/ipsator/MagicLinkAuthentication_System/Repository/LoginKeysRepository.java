package com.ipsator.MagicLinkAuthentication_System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.LoginKeys;
import com.ipsator.MagicLinkAuthentication_System.Entity.PreFinalUsers;
import com.ipsator.MagicLinkAuthentication_System.Entity.User;

/**
 * A repository for LoginKeys
 * @author Roshan
 */
@Repository
public interface LoginKeysRepository extends JpaRepository<LoginKeys, Integer> {
	LoginKeys findByLoginKey(String loginKey);
}
