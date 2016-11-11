package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Responsibility;
import com.livingprogress.mentorme.entities.ResponsibilitySearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query responsibility by criteria.
 */
@AllArgsConstructor
public class ResponsibilitySpecification implements Specification<Responsibility> {
    /**
     * The criteria. Final.
     */
    private final ResponsibilitySearchCriteria criteria;


    /**
     * Creates a WHERE clause for a query of the referenced entity
     * in form of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<Responsibility> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getInstitutionalProgramId(),
                pd, root.get("institutionalProgramId"), cb);
        return pd;
    }
}

