package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.DocumentSearchCriteria;
import com.livingprogress.mentorme.utils.Helper;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The specification used to query documents by criteria.
 */
@AllArgsConstructor
public class DocumentSpecification implements Specification<Document> {
    /**
     * The criteria. Final.
     */
    private final DocumentSearchCriteria criteria;


    /**
     * Creates a WHERE clause for a query of the referenced entity
     * in form of a Predicate for the given Root and CriteriaQuery.
     * @param root the root
     * @param query the criteria query
     * @param cb the query builder
     * @return the predicate
     */
    public Predicate toPredicate(Root<Document> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate pd = cb.and();
        pd = Helper.buildEqualPredicate(criteria.getPath(), pd,
                root.get("path"), cb);
        return pd;
    }
}

