package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.*;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * The InstitutionalProgram REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/institutionalPrograms")
@NoArgsConstructor
public class InstitutionalProgramController extends BaseUploadController {
    /**
     * The institutional program service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private InstitutionalProgramService institutionalProgramService;
    
    /**
     * The mentee mentor program service used to create mentee mentor program
     */
    @Autowired
    private MenteeMentorProgramService menteeMentorProgramService;

    /**
     * The mentor service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MentorService mentorService;

    /**
     * The mentee service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MenteeService menteeService;

    @Autowired
    private UserService userService;

    /**
     * The lookup service.
     */
    @Autowired
    private LookupService lookupService;

    /**
     * The goal service.
     */
    @Autowired
    private GoalService goalService;

    /**
     * The responsibility service.
     */
    @Autowired
    private ResponsibilityService responsibilityService;


    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkConfigNotNull(institutionalProgramService, "institutionalProgramService");
        Helper.checkConfigNotNull(mentorService, "mentorService");
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
    public InstitutionalProgram get(@PathVariable long id) throws MentorMeException {
        return institutionalProgramService.get(id);
    }

    /**
     * This method is used to create an entity.
     *
     * @param entity the entity to create
     * @param documents the documents to upload
     * @return the created entity
     * @throws IllegalArgumentException if entity is null or not valid
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public InstitutionalProgram create(InstitutionalProgram entity,
            @RequestParam("files") MultipartFile[] documents) throws MentorMeException {
        Helper.checkNull(entity, "entity");
        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        entity.setDocuments(docs);
        entity.setLocale(getLocale());
        return institutionalProgramService.create(entity);
    }

    /**
     * Get the locale.
     * @return the locale
     * @throws MentorMeException if there is any error
     */
    private Locale getLocale() throws MentorMeException {
        List<Locale> locales = lookupService.getLocales();
        if (locales.isEmpty()) {
            throw new IllegalArgumentException("You don't have locales defined.");
        }
        for (java.util.Locale locale : LocaleContext.getCurrentLocales()) {
            for (Locale localeEntity : locales) {
                if (localeEntity.getValue().equals(locale.toString())) {
                    return localeEntity;
                }
            }
        }
        return locales.get(0);
    }

    /**
     * This method is used to update an entity.
     *
     * @param id the id of the entity to update
     * @param entity the entity to update
     * @param documents the documents to upload
     * @return the updated entity
     * @throws IllegalArgumentException if id is not positive or entity is null or id of entity is not positive
     * or id of  entity not match id or entity is invalid
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public InstitutionalProgram update(@PathVariable long id, InstitutionalProgram entity,
            @RequestParam("files") MultipartFile[] documents) throws MentorMeException {
        Helper.checkUpdate(id, entity);
        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        entity.setDocuments(docs);
        entity.setLocale(getLocale());
        InstitutionalProgram original = get(id);
        // clear goals that do not exist anymore
        if (entity.getGoals() != null) {
            for (Goal goal : original.getGoals()) {
                boolean found = false;
                for (Goal goalEntity : entity.getGoals()) {
                    if (goal.getId() == goalEntity.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    goalService.delete(goal.getId());
                }
            }
        }
        // clear responsibilities that do not exist anymore
        if (entity.getResponsibilities() != null) {
            for (Responsibility res : original.getResponsibilities()) {
                boolean found = false;
                for (Responsibility resEntity : entity.getResponsibilities()) {
                    if (res.getId() == resEntity.getId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    responsibilityService.delete(res.getId());
                }
            }
        }
        return institutionalProgramService.update(id, entity);
    }

    /**
     * This method is used to delete an entity.
     *
     * @param id the id of the entity to delete
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@PathVariable long id) throws MentorMeException {
        institutionalProgramService.delete(id);
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
    public SearchResult<InstitutionalProgram> search(@ModelAttribute InstitutionalProgramSearchCriteria criteria,
            @ModelAttribute Paging paging) throws MentorMeException {
        User currentUser = userService.getMe();
        if (currentUser instanceof InstitutionUser) {
            InstitutionUser iUser = (InstitutionUser) currentUser;
            if (iUser.getInstitution() != null) {
                criteria.setInstitutionId(iUser.getInstitution().getId());
            }
        }
        return institutionalProgramService.search(criteria, paging);
    }

    /**
     * This method is used to retrieve program mentees.
     * @param id the id of the entity to retrieve
     * @return the match program mentees
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/mentees", method = RequestMethod.GET)
    public List<Mentee> getProgramMentees(@PathVariable long id) throws MentorMeException {
        // make sure exist valid program
        institutionalProgramService.get(id);
        return menteeService.getProgramMentees(id);
    }

    /**
     * This method is used to retrieve program mentors.
     * @param id the id of the entity to retrieve
     * @return the match program mentors
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/mentors", method = RequestMethod.GET)
    public List<Mentor> getProgramMentors(@PathVariable long id) throws MentorMeException {
        // make sure exist valid program
        institutionalProgramService.get(id);
        return mentorService.getProgramMentors(id);
    }

    /**
     * This method is used to clone program requested by mentor.
     * 
     * @param id the id of the entity to retrieve
     * @param meteeMentorIds the mentee and mentor ids
     * @return the cloned mentee mentor program
     * 
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/clone", method = RequestMethod.POST)
    @Transactional
    public MenteeMentorProgram clone(@PathVariable long id, @RequestBody MenteeMentorIds meteeMentorIds) throws MentorMeException {
        // make sure exist valid program
        InstitutionalProgram instProgram = institutionalProgramService.get(id);
        
        // create MenteeMentorProgram
        LocalDate date = LocalDate.now();
        
        MenteeMentorProgram mmProgram = new MenteeMentorProgram();
        mmProgram.setInstitutionalProgram(instProgram);
        mmProgram.setDocuments(new ArrayList<>(instProgram.getDocuments()));
        mmProgram.setUsefulLinks(new ArrayList<>(instProgram.getUsefulLinks()));
        mmProgram.setStartDate(date.toDate());
        
        Mentee mentee = new Mentee();
        mentee.setId(meteeMentorIds.getMenteeId());
        mmProgram.setMentee(mentee);
        
        Mentor mentor = new Mentor();
        mentor.setId(meteeMentorIds.getMentorId());
        mmProgram.setMentor(mentor);
        
        // clone goals
        if (instProgram.getGoals() != null && !instProgram.getGoals().isEmpty()) {
            List<MenteeMentorGoal> goals = new ArrayList<>();
            for (Goal goal : instProgram.getGoals()) {
                MenteeMentorGoal mmGoal = new MenteeMentorGoal();
                Goal cloneGoal = new Goal();
                cloneGoal.setDescription(goal.getDescription());
                cloneGoal.setSubject(goal.getSubject());
                cloneGoal.setDurationInDays(goal.getDurationInDays());
                cloneGoal.setNumber(goal.getNumber());

                if (goal.getDocuments() != null && !goal.getDocuments().isEmpty()) {
                    List<Document> documents = new ArrayList<>();                   
                    for (Document document : goal.getDocuments()) {
                        Document cloneDocument = new Document();
                        cloneDocument.setName(document.getName());
                        cloneDocument.setPath(document.getPath());  
                        cloneDocument.setCreatedBy(document.getCreatedBy());                   
                        cloneDocument.setCreatedOn(document.getCreatedOn());
                        cloneDocument.setLastModifiedBy(document.getLastModifiedBy());             
                        cloneDocument.setLastModifiedOn(document.getLastModifiedOn());
                        documents.add(cloneDocument);
                    }
                    cloneGoal.setDocuments(documents);
                }
                
                if (goal.getUsefulLinks() != null && !goal.getUsefulLinks().isEmpty()) {
                    List<UsefulLink> usefulLinks = new ArrayList<>();
                    for (UsefulLink usefulLink : goal.getUsefulLinks()) {
                        UsefulLink cloneUsefulLink = new UsefulLink();
                        cloneUsefulLink.setTitle(usefulLink.getTitle());
                        cloneUsefulLink.setAddress(usefulLink.getAddress());
                        cloneUsefulLink.setCreatedOn(usefulLink.getCreatedOn());
                        cloneUsefulLink.setAuthor(usefulLink.getAuthor());
                        usefulLinks.add(cloneUsefulLink);
                     }
                     cloneGoal.setUsefulLinks(usefulLinks);
                }
                
                mmGoal.setGoal(cloneGoal);
                mmGoal.setMenteeMentorProgram(mmProgram);             
                
                if (goal.getTasks() != null && !goal.getTasks().isEmpty()) {
                    List<MenteeMentorTask> tasks = new ArrayList<>();
                    for (Task task : goal.getTasks()) {
                        MenteeMentorTask mmTask = new MenteeMentorTask();
                        Task cloneTask = new Task();
                        cloneTask.setNumber(task.getNumber());
                        cloneTask.setDescription(task.getDescription());
                        cloneTask.setDurationInDays(task.getDurationInDays());
                        cloneTask.setMenteeAssignment(task.getMenteeAssignment());
                        cloneTask.setMentorAssignment(task.getMentorAssignment());
                        cloneTask.setGoal(cloneGoal);
                        mmTask.setTask(cloneTask);
                        mmTask.setMenteeMentorGoal(mmGoal);
                        mmTask.setStartDate(date.toDate());                        
                        date = date.plusDays(task.getDurationInDays());
                        mmTask.setEndDate(date.toDate());
                        
                        tasks.add(mmTask);
                    }
                    mmGoal.setTasks(tasks);
                }
                
                goals.add(mmGoal);
            }
            mmProgram.setEndDate(date.toDate());
            mmProgram.setGoals(goals);            
        }
        
        // clone responsibilities
        if (instProgram.getResponsibilities() != null && !instProgram.getResponsibilities().isEmpty()) {
            List<MenteeMentorResponsibility> responsibilities = new ArrayList<>();
        
            for (Responsibility resp : instProgram.getResponsibilities()) {
                MenteeMentorResponsibility mmResp = new MenteeMentorResponsibility();
                
                mmResp.setDate(resp.getDate());
                mmResp.setMenteeMentorProgram(mmProgram);
                mmResp.setMenteeResponsibility(resp.getMenteeResponsibility());
                mmResp.setMentorResponsibility(resp.getMentorResponsibility());
                mmResp.setNumber(resp.getNumber());
                mmResp.setTitle(resp.getTitle());
                mmResp.setResponsibilityId(resp.getId());
                
                responsibilities.add(mmResp);
            }
            mmProgram.setResponsibilities(responsibilities);
        }
        
        // save to database
        menteeMentorProgramService.create(mmProgram);
        
        return mmProgram;
    }
}

