package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorTask;
import com.livingprogress.mentorme.entities.MenteeMentorTaskSearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query mentee mentor task by criteria.
 */
@AllArgsConstructor
public class MenteeMentorTaskSpecification implements Specification<MenteeMentorTask> {
    /**
     * The criteria. Final.
     */
    private final MenteeMentorTaskSearchCriteria criteria;

     /**
     * Creates a WHERE clause for a query of the referenced entity
      * in form of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<MenteeMentorTask> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getMenteeMentorProgramId(), pd,
                root.get("menteeMentorGoal").get("menteeMentorProgramId"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMenteeId(), pd,
                root.get("menteeMentorGoal").get("menteeMentorProgram").get("mentee").get("id"), cb);
        pd = Helper.buildEqualPredicate(criteria.getCompleted(), pd, root.get("completed"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMentorAssignment(), pd,
                root.get("task").get("mentorAssignment"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMenteeAssignment(), pd,
                root.get("task").get("menteeAssignment"), cb);
        pd = Helper.buildEqualPredicate(criteria.getCompletedOn(), pd,
                root.get("completedOn"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMenteeMentorGoalId(), pd,
                root.get("menteeMentorGoalId"), cb);
        pd = Helper.buildGreaterThanOrEqualToPredicate(criteria.getStartDate(), pd,
                root.get("startDate"), cb);
        pd = Helper.buildLessThanOrEqualToPredicate(criteria.getEndDate(), pd,
                root.get("endDate"), cb);
        return pd;
    }
}

