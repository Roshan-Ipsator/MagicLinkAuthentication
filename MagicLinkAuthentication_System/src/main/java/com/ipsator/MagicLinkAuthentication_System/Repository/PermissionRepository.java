package com.ipsator.MagicLinkAuthentication_System.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
	
	Optional<Permission> findByName(String name);

}
