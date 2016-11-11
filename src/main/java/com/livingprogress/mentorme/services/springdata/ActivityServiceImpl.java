package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Activity;
import com.livingprogress.mentorme.entities.ActivitySearchCriteria;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.ActivityService;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * The Spring Data JPA implementation of ActivityService,
 * extends BaseService<Activity,ActivitySearchCriteria>. Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class ActivityServiceImpl extends BaseService<Activity, ActivitySearchCriteria> implements ActivityService {

    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<Activity> getSpecification(ActivitySearchCriteria criteria) throws MentorMeException {
        return new ActivitySpecification(criteria);
    }
}

