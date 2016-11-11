package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The mentee mentor task repository.
 */
public interface MenteeMentorTaskRepository
        extends JpaRepository<MenteeMentorTask, Long>, JpaSpecificationExecutor<MenteeMentorTask> {
}

