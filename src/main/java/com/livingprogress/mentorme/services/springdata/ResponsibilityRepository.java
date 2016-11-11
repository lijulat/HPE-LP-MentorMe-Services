package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Responsibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The Responsibility repository.
 */
public interface ResponsibilityRepository
        extends JpaRepository<Responsibility, Long>, JpaSpecificationExecutor<Responsibility> {
}

