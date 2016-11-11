package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.ActivityType;
import com.livingprogress.mentorme.entities.Goal;
import com.livingprogress.mentorme.entities.GoalSearchCriteria;
import com.livingprogress.mentorme.entities.InstitutionalProgram;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.GoalService;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * The Spring Data JPA implementation of GoalService, extends BaseService<Goal,GoalSearchCriteria>. Effectively
 * thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class GoalServiceImpl extends BaseService<Goal, GoalSearchCriteria> implements GoalService {
    /**
     * The activity repository for CRUD operations. Should be non-null after injection.
     */
    @Autowired
    private ActivityRepository activityRepository;


    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<Goal> getSpecification(GoalSearchCriteria criteria) throws MentorMeException {
        return new GoalSpecification(criteria);
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
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(Goal entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        handleGoalNestedProperties(entity);
        Helper.checkPositive(entity.getInstitutionalProgramId(), "entity.institutionalProgramId");
        entity.setInstitutionalProgram(new InstitutionalProgram());
        entity.getInstitutionalProgram().setId(entity.getInstitutionalProgramId());
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
    public Goal create(Goal entity) throws MentorMeException {
        Goal created = super.create(entity);
        Helper.audit(activityRepository, ActivityType.GOAL_CREATED, created.getId(),
                CustomMessageSource.getMessage("goal.created.description"),
                created.getInstitutionalProgramId(), Helper.getMenteeId(), Helper.getMentorId(), false);
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
    public Goal update(long id, Goal entity) throws MentorMeException {
        Goal updated = super.update(id, entity);
        Helper.audit(activityRepository, ActivityType.GOAL_UPDATED, updated.getId(),
                CustomMessageSource.getMessage("goal.updated.description"),
                updated.getInstitutionalProgramId(), Helper.getMenteeId(), Helper.getMentorId(), false);
        return updated;
    }
}

