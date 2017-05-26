package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorProgramService;
import com.livingprogress.mentorme.services.MenteeService;
import com.livingprogress.mentorme.services.MentorService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;

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

    @Autowired
    private MentorService mentorService;

    @Autowired
    private MenteeService menteeService;

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
        if (Helper.isMentee()) {
            criteria.setMenteeId(Helper.getAuthUser().getId());
        } else if (Helper.isMentor()) {
            criteria.setMentorId(Helper.getAuthUser().getId());
        }
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
        feedback.setCreatedOn(new Date());
        program.setMenteeFeedback(feedback);
        menteeMentorProgramService.update(id, program);

        // re-calculate the avg rating for that mentee
        MenteeMentorProgramSearchCriteria critera = new MenteeMentorProgramSearchCriteria();
        critera.setMenteeId(program.getMentee().getId());
        SearchResult<MenteeMentorProgram> programs = menteeMentorProgramService.search(critera, null);
        int c = 0;
        double sum = 0;
        for (int i = 0; i < programs.getEntities().size(); i++) {
            MenteeMentorProgram p = programs.getEntities().get(i);
            if (p.getMenteeFeedback() != null && p.getMenteeFeedback().getMentorScore() != null) {
                c++;
                sum += p.getMenteeFeedback().getMentorScore();
            }
        }
        Mentee mentee = menteeService.get(program.getMentee().getId());
        Mentee cloneMentee = new Mentee();
        BeanUtils.copyProperties(mentee, cloneMentee);
        cloneMentee.setAveragePerformanceScore((int) (sum / c + 0.5));
        cloneMentee.setPassword(null);
        menteeService.update(mentee.getId(), cloneMentee);

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
        feedback.setCreatedOn(new Date());
        program.setMentorFeedback(feedback);
        menteeMentorProgramService.update(id, program);
        // re-calculate the avg rating for that mentor
        MenteeMentorProgramSearchCriteria critera = new MenteeMentorProgramSearchCriteria();
        critera.setMentorId(program.getMentor().getId());
        SearchResult<MenteeMentorProgram> programs = menteeMentorProgramService.search(critera, null);
        int c = 0;
        double sum = 0;
        for (int i = 0; i < programs.getEntities().size(); i++) {
            MenteeMentorProgram p = programs.getEntities().get(i);
            if (p.getMentorFeedback() != null && p.getMentorFeedback().getMenteeScore() != null) {
                c++;
                sum += p.getMentorFeedback().getMenteeScore();
            }
        }
        Mentor mentor = mentorService.get(program.getMentor().getId());
        Mentor cloneMentor =  new Mentor();
        BeanUtils.copyProperties(mentor, cloneMentor);
        cloneMentor.setAveragePerformanceScore((int) (sum / c + 0.5));
        cloneMentor.setPassword(null);        
        mentorService.update(mentor.getId(), cloneMentor);
    }
}

