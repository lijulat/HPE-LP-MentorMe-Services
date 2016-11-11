package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.InstitutionalProgram;
import com.livingprogress.mentorme.entities.Responsibility;
import com.livingprogress.mentorme.entities.ResponsibilitySearchCriteria;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.ResponsibilityService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * The Spring Data JPA implementation of ResponsibilityService,
 * extends BaseService<Responsibility,ResponsibilitySearchCriteria>. Effectively
 * thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class ResponsibilityServiceImpl
        extends BaseService<Responsibility, ResponsibilitySearchCriteria> implements ResponsibilityService {
    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<Responsibility>
    getSpecification(ResponsibilitySearchCriteria criteria) throws MentorMeException {
        return new ResponsibilitySpecification(criteria);
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(Responsibility entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        Helper.checkPositive(entity.getInstitutionalProgramId(), "entity.institutionalProgramId");
        entity.setInstitutionalProgram(new InstitutionalProgram());
        entity.getInstitutionalProgram().setId(entity.getInstitutionalProgramId());
    }
}

