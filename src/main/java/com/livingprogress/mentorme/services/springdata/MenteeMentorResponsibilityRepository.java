package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorResponsibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The mentee mentor responsibility repository.
 */
public interface MenteeMentorResponsibilityRepository
        extends JpaRepository<MenteeMentorResponsibility, Long>, JpaSpecificationExecutor<MenteeMentorResponsibility> {
}

