package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Image;
import com.livingprogress.mentorme.entities.ImageSearchCriteria;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.ImageService;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * The Spring Data JPA implementation of TaskService,
 * extends BaseService<Task,TaskSearchCriteria>. Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class ImageServiceImpl extends BaseService<Image, ImageSearchCriteria> implements ImageService {


    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
    }

    /**
     * Get the specs.
     * @param criteria the criteria
     * @return the spec.
     * @throws MentorMeException if there are any errors.
     */
    @Override
    protected Specification<Image> getSpecification(ImageSearchCriteria criteria) throws MentorMeException {
        return new ImageSpecification(criteria);
    }

}

