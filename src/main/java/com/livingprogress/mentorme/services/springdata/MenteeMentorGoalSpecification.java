package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorGoalSearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query mentee mentor goal by criteria.
 */
@AllArgsConstructor
public class MenteeMentorGoalSpecification implements Specification<MenteeMentorGoal> {
    /**
     * The criteria. Final.
     */
    private final MenteeMentorGoalSearchCriteria criteria;


    /**
     * Creates a WHERE clause for a query of the referenced entity
     * in form of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<MenteeMentorGoal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getMenteeMentorProgramId(), pd,
                root.get("menteeMentorProgramId"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMenteeId(), pd,
                root.get("menteeMentorProgram").get("mentee").get("id"), cb);
        pd = Helper.buildEqualPredicate(criteria.getCompleted(), pd, root.get("completed"), cb);
        return pd;
    }
}

