package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Activity;
import com.livingprogress.mentorme.entities.ActivitySearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query Activity by criteria.
 */
@AllArgsConstructor
public class ActivitySpecification  implements Specification<Activity> {
    /**
     * The criteria. Final.
     */
    private final ActivitySearchCriteria criteria;

    /**
     * Creates a WHERE clause for a query of the referenced entity in form
     * of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getInstitutionalProgramId(),
                pd, root.get("institutionalProgramId"), cb);
        if (criteria.getActivityTypes() != null) {
            pd = cb.and(pd, root.get("activityType").in(criteria.getActivityTypes()));
        }
        pd = Helper.buildLikePredicate(criteria.getDescription(), pd, root.get("description"), cb);
        pd = Helper.buildGreaterThanOrEqualToPredicate(criteria.getStartDate(), pd, root.get("createdOn"), cb);
        pd = Helper.buildLessThanOrEqualToPredicate(criteria.getEndDate(), pd, root.get("createdOn"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMenteeId(), pd, root.get("menteeId"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMentorId(), pd, root.get("mentorId"), cb);
        pd = Helper.buildEqualPredicate(criteria.getGlobal(), pd, root.get("global"), cb);
        return pd;
    }
}

