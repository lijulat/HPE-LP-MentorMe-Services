package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorProgramRequest;
import com.livingprogress.mentorme.entities.MenteeMentorProgramRequestSearchCriteria;
import com.livingprogress.mentorme.entities.MenteeMentorProgramRequestStatus;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorProgramRequestService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;

/**
 * The Spring Data JPA implementation of MenteeMentorGoalService,extends BaseService<MenteeMentorGoal,
 * MenteeMentorGoalSearchCriteria>.
 * Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class MenteeMentorProgramRequestServiceImpl extends BaseService<MenteeMentorProgramRequest, MenteeMentorProgramRequestSearchCriteria>
        implements MenteeMentorProgramRequestService {

    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<MenteeMentorProgramRequest> getSpecification(MenteeMentorProgramRequestSearchCriteria criteria) throws
            MentorMeException {
        return new MenteeMentorProgramRequestSpecification(criteria);
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(MenteeMentorProgramRequest entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        Helper.checkEntity(entity.getMentor(), "entity.mentor");
        Helper.checkEntity(entity.getMentee(), "entity.mentee");
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
    public MenteeMentorProgramRequest create(MenteeMentorProgramRequest entity) throws MentorMeException {
        entity.setRequestTime(new Date());
        return super.create(entity);
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
    public MenteeMentorProgramRequest update(long id, MenteeMentorProgramRequest entity) throws MentorMeException {
        if (entity.getStatus() == MenteeMentorProgramRequestStatus.REJECTED
                || entity.getStatus() == MenteeMentorProgramRequestStatus.APPROVED) {
            entity.setApprovedOrRejectedTime(new Date());
        }
        return super.update(id, entity);
    }
}

