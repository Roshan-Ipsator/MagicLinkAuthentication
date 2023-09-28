package com.ipsator.MagicLinkAuthentication_System.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.Permission;

/**
 * Repository interface for managing {@link Permission} entities in the
 * database.
 * <p>
 * This interface extends {@link JpaRepository} and provides methods to perform
 * CRUD (Create, Read, Update, Delete) operations on {@link Permission}
 * entities, as well as custom query methods.
 * </p>
 *
 * @see Permission
 * @see JpaRepository
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Optional<Permission> findByName(String name);

}
