package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorProgramRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The MenteeMentorProgram repository.
 */
public interface MenteeMentorProgramRequestRepository
        extends JpaRepository<MenteeMentorProgramRequest, Long>, JpaSpecificationExecutor<MenteeMentorProgramRequest> {

}

