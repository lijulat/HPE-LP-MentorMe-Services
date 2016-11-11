package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The mentee mentor goal repository.
 */
public interface MenteeMentorGoalRepository
        extends JpaRepository<MenteeMentorGoal, Long>, JpaSpecificationExecutor<MenteeMentorGoal> {
}

