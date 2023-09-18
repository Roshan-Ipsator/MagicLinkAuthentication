package com.ipsator.MagicLinkAuthentication_System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipsator.MagicLinkAuthentication_System.Entity.TempRegdAttemptTracker;

/**
 * A repository for the Temporary-Registration-Attempt-Tracker entity
 * 
 * @author Roshan
 */
@Repository
public interface TempRegdAttemptTrackerRepository extends JpaRepository<TempRegdAttemptTracker, Integer> {
	/**
	 * Finds and retrieves a registration attempt tracker record associated with a
	 * user's email ID.
	 *
	 * @param userEmailId The email ID of the user for whom the registration attempt
	 *                    tracker is to be found.
	 * @return A {@link TempRegdAttemptTracker} object representing the registration
	 *         attempt tracker associated with the user's email ID, or {@code null}
	 *         if no matching record is found.
	 */
	TempRegdAttemptTracker findByUserEmailId(String userEmailId);
}
