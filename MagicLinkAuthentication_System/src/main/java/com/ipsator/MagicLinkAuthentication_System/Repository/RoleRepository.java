package com.ipsator.MagicLinkAuthentication_System.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.Role;

/**
 * The {@code RoleRepository} interface provides methods for accessing and
 * managing roles in the application's data store. It extends the Spring Data
 * JPA {@code JpaRepository}, which provides standard CRUD (Create, Read,
 * Update, Delete) operations for entities.
 *
 * @see Role
 * @see JpaRepository
 * @see org.springframework.stereotype.Repository
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(String name);
}
