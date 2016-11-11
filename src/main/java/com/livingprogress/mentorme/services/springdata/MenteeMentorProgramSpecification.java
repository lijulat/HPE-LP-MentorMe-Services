package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.MenteeMentorProgramSearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query MenteeMentorProgram by criteria.
 */
@AllArgsConstructor
public class MenteeMentorProgramSpecification implements Specification<MenteeMentorProgram> {
    /**
     * The criteria. Final.
     */
    private final MenteeMentorProgramSearchCriteria criteria;


    /**
     * Creates a WHERE clause for a query of the referenced entity
     * in form of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<MenteeMentorProgram> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getMentorId(), pd,
                root.get("mentor").get("id"), cb);
        pd = Helper.buildEqualPredicate(criteria.getMenteeId(), pd,
                root.get("mentee").get("id"), cb);
        pd = Helper.buildEqualPredicate(criteria.getInstitutionalProgramId(), pd,
                root.get("institutionalProgram").get("id"), cb);
        pd = Helper.buildGreaterThanOrEqualToPredicate(criteria.getStartDate(), pd, root.get("startDate"), cb);
        pd = Helper.buildLessThanOrEqualToPredicate(criteria.getEndDate(), pd, root.get("endDate"), cb);
        pd = Helper.buildEqualPredicate(criteria.getCompleted(), pd, root.get("completed"), cb);
        pd = Helper.buildEqualPredicate(criteria.getRequestStatus(), pd, root.get("requestStatus"), cb);
        return pd;
    }
}

