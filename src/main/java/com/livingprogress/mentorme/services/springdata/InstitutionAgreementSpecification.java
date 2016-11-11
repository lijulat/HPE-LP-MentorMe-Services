package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.InstitutionAgreement;
import com.livingprogress.mentorme.entities.InstitutionAgreementSearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query InstitutionAgreement by criteria.
 */
@AllArgsConstructor
public class InstitutionAgreementSpecification implements Specification<InstitutionAgreement> {
    /**
     * The criteria. Final.
     */
    private final InstitutionAgreementSearchCriteria criteria;


    /**
     * Creates a WHERE clause for a query of the referenced entity
     * in form of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<InstitutionAgreement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getInstitutionId(), pd, root.get("institutionId"), cb);
        pd = Helper.buildLikePredicate(criteria.getAgreementName(), pd, root.get("agreementName"), cb);
        if (criteria.getUserRole() != null) {
            pd = cb.and(pd, cb.equal(root.join("userRoles").get("id"),
                    criteria.getUserRole().getId()));
        }
        return pd;
    }
}

