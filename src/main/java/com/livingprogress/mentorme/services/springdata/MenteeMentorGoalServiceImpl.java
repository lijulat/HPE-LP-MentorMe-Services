package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.ActivityType;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorGoalSearchCriteria;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorGoalService;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The Spring Data JPA implementation of MenteeMentorGoalService,extends BaseService<MenteeMentorGoal,
 * MenteeMentorGoalSearchCriteria>.
 * Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class MenteeMentorGoalServiceImpl extends BaseService<MenteeMentorGoal, MenteeMentorGoalSearchCriteria>
        implements MenteeMentorGoalService {
    /**
     * The activity repository to create activity. Should be non-null after injection.
     */
    @Autowired
    private ActivityRepository activityRepository;

    /**
     * The mentee mentor program repository to create activity. Should be non-null after injection.
     */
    @Autowired
    private MenteeMentorProgramRepository menteeMentorProgramRepository;

    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<MenteeMentorGoal> getSpecification(MenteeMentorGoalSearchCriteria criteria) throws
            MentorMeException {
        return new MenteeMentorGoalSpecification(criteria);
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkConfigNotNull(activityRepository, "activityRepository");
        Helper.checkConfigNotNull(menteeMentorProgramRepository, "menteeMentorProgramRepository");
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(MenteeMentorGoal entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        Helper.checkPositive(entity.getMenteeMentorProgramId(), "entity.menteeMentorProgramId");
        entity.setMenteeMentorProgram(new MenteeMentorProgram());
        entity.getMenteeMentorProgram()
              .setId(entity.getMenteeMentorProgramId());
        if (entity.getTasks() != null) {
            entity.getTasks()
                  .forEach(t -> {
                      t.setMenteeMentorGoal(entity);
                      if (t.getTask() != null) {
                          t.getTask().setGoal(entity.getGoal());
                      }
                  });
        } else {
            entity.setTasks(Collections.emptyList());
        }
    }

    /**
     * This method is used to create an entity.
     *
     * @param entity the entity to create
     * @return the created entity
     * @throws IllegalArgumentException if entity is null or not valid
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    public MenteeMentorGoal create(MenteeMentorGoal entity) throws MentorMeException {
        MenteeMentorGoal created = super.create(entity);
        Helper.audit(activityRepository,
                menteeMentorProgramRepository, ActivityType.GOAL_CREATED, created.getId(),
                CustomMessageSource.getMessage("menteeMentorGoal.created.description"),
                Helper.getId(created.getMenteeMentorProgram()), false);
        return created;
    }

    /**
     * This method is used to update an entity.
     *
     * @param id the id of the entity to update
     * @param entity the entity to update
     * @return the updated entity
     * @throws IllegalArgumentException if id is not positive or entity is null or id of entity is not positive
     * or id of  entity not match id or entity is invalid
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    public MenteeMentorGoal update(long id, MenteeMentorGoal entity) throws MentorMeException {
        MenteeMentorGoal updated = super.update(id, entity);
        Helper.audit(activityRepository,
                menteeMentorProgramRepository, ActivityType.GOAL_UPDATED, updated.getId(),
                CustomMessageSource.getMessage("menteeMentorGoal.updated.description"),
                Helper.getId(updated.getMenteeMentorProgram()), false);
        checkMenteeMentorProgram(updated.getMenteeMentorProgram());
        return updated;
    }

    /**
     * Check if all goals in mentee/mentor program are completed and updated program.
     * 
     * @param menteeMentorProgram the program to check and update
     */
    private void checkMenteeMentorProgram(MenteeMentorProgram menteeMentorProgram) {
		List<MenteeMentorGoal> goals = menteeMentorProgram.getGoals();
		boolean completed = goals.stream().allMatch(t -> t.isCompleted());
		if (completed != menteeMentorProgram.isCompleted()) {
			menteeMentorProgram.setCompleted(completed);
			menteeMentorProgram.setCompletedOn(completed ? new Date() : null);
			menteeMentorProgramRepository.saveAndFlush(menteeMentorProgram);
		}
	}

}

