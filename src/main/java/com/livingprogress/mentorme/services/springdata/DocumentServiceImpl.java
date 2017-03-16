package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.DocumentSearchCriteria;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.DocumentService;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * The Spring Data JPA implementation of DocumentService,
 * extends BaseService<Document,DocumentSearchCriteria>. Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class DocumentServiceImpl extends BaseService<Document, DocumentSearchCriteria> implements DocumentService {


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
    protected Specification<Document> getSpecification(DocumentSearchCriteria criteria) throws MentorMeException {
        return new DocumentSpecification(criteria);
    }

}

