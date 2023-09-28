package com.ipsator.MagicLinkAuthentication_System.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code Permission} class represents a permission entity in the
 * application. Permissions are used to define specific access rights within the
 * application. Each permission can be associated with one or more roles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Permission {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String name;

	/**
	 * The list of roles that have this permission. This establishes a many-to-many
	 * relationship between permissions and roles.
	 */
	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "permissions")
	private List<Role> roles;
}
