package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.MenteeFeedback;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.MenteeMentorProgramSearchCriteria;
import com.livingprogress.mentorme.entities.MentorFeedback;
import com.livingprogress.mentorme.entities.Paging;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorProgramService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * The MenteeMentorProgram REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/menteeMentorPrograms")
@NoArgsConstructor
public class MenteeMentorProgramController {
    /**
     * The mentee mentor program service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MenteeMentorProgramService menteeMentorProgramService;


    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigNotNull(menteeMentorProgramService, "menteeMentorProgramService");
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
    public MenteeMentorProgram get(@PathVariable long id) throws MentorMeException {
        return menteeMentorProgramService.get(id);
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
    public MenteeMentorProgram create(@RequestBody MenteeMentorProgram entity) throws MentorMeException {
        return menteeMentorProgramService.create(entity);
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
    public MenteeMentorProgram update(@PathVariable long id, @RequestBody MenteeMentorProgram entity) throws
            MentorMeException {
        return menteeMentorProgramService.update(id, entity);
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
        menteeMentorProgramService.delete(id);
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
    public SearchResult<MenteeMentorProgram> search(@ModelAttribute MenteeMentorProgramSearchCriteria criteria,
            @ModelAttribute Paging paging) throws MentorMeException {
        return menteeMentorProgramService.search(criteria, paging);
    }

    /**
     * This method is used to submit the mentee feedback.
     *
     * @param id the id of mentee mentor program
     * @param feedback the mentee feedback
     * @throws IllegalArgumentException if id is not positive or feedback is null
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    @RequestMapping(value = "{id}/menteeFeedback", method = RequestMethod.PUT)
    public void submitMenteeFeedback(@PathVariable long id, @RequestBody MenteeFeedback feedback) throws
            MentorMeException {
        Helper.checkNull(feedback, "feedback");
        MenteeMentorProgram program = menteeMentorProgramService.get(id);
        program.setMenteeFeedback(feedback);
        menteeMentorProgramService.update(id, program);
    }

    /**
     * This method is used to submit the mentor feedback.
     *
     * @param id the id of mentee mentor program
     * @param feedback the mentor feedback
     * @throws IllegalArgumentException if id is not positive or feedback is null
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    @RequestMapping(value = "{id}/mentorFeedback", method = RequestMethod.PUT)
    public void submitMentorFeedback(@PathVariable long id, @RequestBody MentorFeedback feedback) throws
            MentorMeException {
        Helper.checkNull(feedback, "feedback");
        MenteeMentorProgram program = menteeMentorProgramService.get(id);
        program.setMentorFeedback(feedback);
        menteeMentorProgramService.update(id, program);
    }
}

