package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.InstitutionAgreement;
import com.livingprogress.mentorme.entities.InstitutionAgreementSearchCriteria;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.InstitutionAgreementService;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * The Spring Data JPA implementation of InstitutionAgreementService,
 * extends BaseService<InstitutionAgreement,InstitutionAgreementSearchCriteria>.
 * Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class InstitutionAgreementServiceImpl
        extends BaseService<InstitutionAgreement, InstitutionAgreementSearchCriteria>
        implements InstitutionAgreementService {
    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<InstitutionAgreement> getSpecification(InstitutionAgreementSearchCriteria criteria)
            throws MentorMeException {
        return new InstitutionAgreementSpecification(criteria);
    }
}

