package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The InventoryTransferRequest repository.
 */
public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findByCountryId(long countryId);
}

