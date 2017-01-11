package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorTaskService;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * The Spring Data JPA implementation of MenteeMentorTaskService,
 * extends BaseService<MenteeMentorTask,MenteeMentorTaskSearchCriteria>.
 * Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class MenteeMentorTaskServiceImpl extends BaseService<MenteeMentorTask, MenteeMentorTaskSearchCriteria>
        implements MenteeMentorTaskService {
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
     * The mentee mentor goal repository to create activity. Should be non-null after injection.
     */
    @Autowired
    private MenteeMentorGoalRepository menteeMentorGoalRepository;

    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<MenteeMentorTask> getSpecification(MenteeMentorTaskSearchCriteria criteria) throws
            MentorMeException {
        return new MenteeMentorTaskSpecification(criteria);
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
        Helper.checkConfigNotNull(menteeMentorGoalRepository, "menteeMentorGoalRepository");
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(MenteeMentorTask entity) throws MentorMeException {
        super.handleNestedProperties(entity);

        Helper.checkPositive(entity.getMenteeMentorGoalId(), "entity.menteeMentorGoalId");
        entity.setMenteeMentorGoal(new MenteeMentorGoal());
        entity.getMenteeMentorGoal().setId(entity.getMenteeMentorGoalId());
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
    public MenteeMentorTask create(MenteeMentorTask entity) throws MentorMeException {
        if (entity.getTask().getGoal() == null) {
            entity.getTask().setGoal(new Goal());
        }
        entity.getTask().getGoal().setId(entity.getTask().getGoalId());
        MenteeMentorTask created = super.create(entity);
        MenteeMentorGoal goal = menteeMentorGoalRepository.findOne(created.getMenteeMentorGoalId());
        Helper.audit(activityRepository, menteeMentorProgramRepository,
                ActivityType.TASK_CREATED, created.getId(),
                CustomMessageSource.getMessage("menteeMentorTask.created.description"),
                goal.getMenteeMentorProgramId(), false);
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
    public MenteeMentorTask update(long id, MenteeMentorTask entity) throws MentorMeException {
        if (entity.getTask().getGoal() == null) {
            entity.getTask().setGoal(new Goal());
        }
        entity.getTask().getGoal().setId(entity.getTask().getGoalId());
        if (entity.getTask() != null && entity.getTask().getCustomData() != null) {
            entity.getTask().getCustomData().setTask(entity.getTask());
            entity.getTask().getCustomData().setTaskId(entity.getTask().getId());
        }
        MenteeMentorTask updated = super.update(id, entity);
        MenteeMentorGoal goal = menteeMentorGoalRepository.findOne(updated.getMenteeMentorGoalId());
        Helper.audit(activityRepository, menteeMentorProgramRepository, ActivityType.TASK_UPDATED,
                updated.getId(),
                CustomMessageSource.getMessage("menteeMentorTask.updated.description"),
                goal.getMenteeMentorProgramId(), false);
        return updated;
    }
}

