package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.InstitutionalProgram;
import com.livingprogress.mentorme.entities.InstitutionalProgramSearchCriteria;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.InstitutionalProgramService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * The Spring Data JPA implementation of InstitutionalProgramService,
 * extends BaseService<InstitutionalProgram,InstitutionalProgramSearchCriteria>.
 * Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class InstitutionalProgramServiceImpl
        extends BaseService<InstitutionalProgram, InstitutionalProgramSearchCriteria>
        implements InstitutionalProgramService {

    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<InstitutionalProgram> getSpecification(InstitutionalProgramSearchCriteria criteria)
            throws MentorMeException {
        return new InstitutionalProgramSpecification(criteria);
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(InstitutionalProgram entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        Helper.checkEntity(entity.getInstitution(), "entity.institution");
        Helper.checkEntity(entity.getProgramCategory(), "entity.programCategory");
        if (entity.getGoals() != null) {
            entity.getGoals()
                  .forEach(g -> {
                      g.setInstitutionalProgram(entity);
                      handleGoalNestedProperties(g);
                  });
        } else {
            entity.setGoals(Collections.emptyList());
        }
        if (entity.getResponsibilities() != null) {
            entity.getResponsibilities()
                  .forEach(g -> g.setInstitutionalProgram(entity));
        } else {
            entity.setResponsibilities(Collections.emptyList());
        }
    }
}

