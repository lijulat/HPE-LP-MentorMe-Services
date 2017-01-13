package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.MenteeMentorProgramRequest;
import com.livingprogress.mentorme.entities.MenteeMentorProgramRequestSearchCriteria;
import com.livingprogress.mentorme.entities.Paging;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorProgramRequestService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * The mentee  mentor goal REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/menteeMentorProgramRequests")
@NoArgsConstructor
public class MenteeMentorProgramRequestController {
    /**
     * The mentee mentor goal service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MenteeMentorProgramRequestService menteeMentorProgramRequestService;

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigNotNull(menteeMentorProgramRequestService, "menteeMentorProgramRequestService");
    }


    /**
     * This method is used to retrieve an entity.
     *
     * @param id the id of the entity to retrieve
     * @return the match entity
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public MenteeMentorProgramRequest get(@PathVariable long id) throws MentorMeException {
        return menteeMentorProgramRequestService.get(id);
    }

    /**
     * This method is used to create an entity.
     *
     * @param entity the entity to create
     * @return the created entity
     * @throws IllegalArgumentException if entity is null or not valid
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public MenteeMentorProgramRequest create(@RequestBody MenteeMentorProgramRequest entity) throws MentorMeException {
        return menteeMentorProgramRequestService.create(entity);
    }

    /**
     * This method is used to update an entity.
     *
     * @param id the id of the entity to update
     * @param entity the entity to update
     * @return the updated entity
     * @throws IllegalArgumentException if id is not positive or entity is null or id of entity is not positive
     * or id of  entity not match id or entity is invalid
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public MenteeMentorProgramRequest update(@PathVariable long id, @RequestBody MenteeMentorProgramRequest entity) throws
            MentorMeException {
        return menteeMentorProgramRequestService.update(id, entity);
    }

    /**
     * This method is used to delete an entity.
     *
     * @param id the id of the entity to delete
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable long id) throws MentorMeException {
        menteeMentorProgramRequestService.delete(id);
    }

    /**
     * This method is used to search for entities by criteria and paging params.
     *
     * @param criteria the search criteria
     * @param paging the paging data
     * @return the search result
     * @throws IllegalArgumentException if pageSize is not positive or pageNumber is negative
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(method = RequestMethod.GET)
    public SearchResult<MenteeMentorProgramRequest> search(@ModelAttribute MenteeMentorProgramRequestSearchCriteria criteria,
                                                           @ModelAttribute Paging paging) throws MentorMeException {
        return menteeMentorProgramRequestService.search(criteria, paging);
    }
}

