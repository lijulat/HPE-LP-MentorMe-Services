package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Task;
import com.livingprogress.mentorme.entities.TaskSearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query Task by criteria.
 */
@AllArgsConstructor
public class TaskSpecification implements Specification<Task> {
    /**
     * The criteria. Final.
     */
    private final TaskSearchCriteria criteria;

    /**
     * Creates a WHERE clause for a query of the referenced entity
     * in form of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getGoalId(), pd, root.get("goalId"), cb);
        pd = Helper.buildLikePredicate(criteria.getDescription(), pd, root.get("description"), cb);
        pd = Helper.buildEqualPredicate(criteria.getCustom(), pd, root.get("custom"), cb);
        return pd;
    }
}

