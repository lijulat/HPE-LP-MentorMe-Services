package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.ActivityType;
import com.livingprogress.mentorme.entities.Goal;
import com.livingprogress.mentorme.entities.Task;
import com.livingprogress.mentorme.entities.TaskSearchCriteria;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.TaskService;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * The Spring Data JPA implementation of TaskService,
 * extends BaseService<Task,TaskSearchCriteria>. Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class TaskServiceImpl extends BaseService<Task, TaskSearchCriteria> implements TaskService {
    /**
     * The activity repository for CRUD operations. Should be non-null after injection.
     */
    @Autowired
    private ActivityRepository activityRepository;

    /**
     * The goal repository for CRUD operations. Should be non-null after injection.
     */
    @Autowired
    private GoalRepository goalRepository;

    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<Task> getSpecification(TaskSearchCriteria criteria) throws
            MentorMeException {
        return new TaskSpecification(criteria);
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
        Helper.checkConfigNotNull(goalRepository, "goalRepository");
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(Task entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        if (entity.getCustomData() != null) {
            entity.getCustomData().setTask(entity);
        }
        Helper.checkPositive(entity.getNumber(), "entity.number");
        Helper.checkPositive(entity.getGoalId(), "entity.goalId");
        entity.setGoal(new Goal());
        System.out.println("########### Adding goal id: " + entity.getGoalId());
        entity.getGoal().setId(entity.getGoalId());
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
    public Task create(Task entity) throws MentorMeException {
        Task created = super.create(entity);
        Goal goal = goalRepository.findOne(created.getGoalId());
        Helper.audit(activityRepository, ActivityType.TASK_CREATED, created.getId(),
                CustomMessageSource.getMessage("task.created.description"),
                goal.getInstitutionalProgramId(), Helper.getMenteeId(), Helper.getMentorId(), false);
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
    public Task update(long id, Task entity) throws MentorMeException {
        Task updated = super.update(id, entity);
        Goal goal = goalRepository.findOne(updated.getGoalId());
        Helper.audit(activityRepository, ActivityType.TASK_UPDATED, updated.getId(),
                CustomMessageSource.getMessage("task.updated.description"),
                goal.getInstitutionalProgramId(), Helper.getMenteeId(), Helper.getMentorId(), false);
        return updated;
    }
}

