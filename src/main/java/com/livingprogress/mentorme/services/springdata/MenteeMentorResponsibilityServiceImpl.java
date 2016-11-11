package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.MenteeMentorResponsibility;
import com.livingprogress.mentorme.entities.MenteeMentorResponsibilitySearchCriteria;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorResponsibilityService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * The Spring Data JPA implementation of MenteeMentorResponsibilityService,
 * extends BaseService<MenteeMentorResponsibility,MenteeMentorResponsibilitySearchCriteria>.
 * Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class MenteeMentorResponsibilityServiceImpl
        extends BaseService<MenteeMentorResponsibility, MenteeMentorResponsibilitySearchCriteria>
        implements MenteeMentorResponsibilityService {
    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<MenteeMentorResponsibility>
    getSpecification(MenteeMentorResponsibilitySearchCriteria criteria) throws  MentorMeException {
        return new MenteeMentorResponsibilitySpecification(criteria);
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(MenteeMentorResponsibility entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        Helper.checkPositive(entity.getMenteeMentorProgramId(), "entity.menteeMentorProgramId");
        entity.setMenteeMentorProgram(new MenteeMentorProgram());
        entity.getMenteeMentorProgram().setId(entity.getMenteeMentorProgramId());
    }
}

