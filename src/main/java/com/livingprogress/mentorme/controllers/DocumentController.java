package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorGoalService;
import com.livingprogress.mentorme.services.MenteeMentorProgramService;
import com.livingprogress.mentorme.utils.EntityTypes;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;

/**
 * The Document REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/documents")
@NoArgsConstructor
public class DocumentController extends BaseUploadController {
    
    /**
     * The mentee mentor program service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MenteeMentorProgramService menteeMentorProgramService;
    
    /**
     * The MenteeMentorGoalService instance
     */
    @Autowired
    private MenteeMentorGoalService menteeMentorGoalService;
    
    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigNotNull(menteeMentorProgramService, "menteeMentorProgramService");
        Helper.checkConfigNotNull(menteeMentorGoalService, "menteeMentorGoalService");
    }

    /**
     * Add documents to the specified entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @param documents the documents to add
     * @return the added documents info
     * @throws MentorMeException if any error occurs
     */
    @RequestMapping(value = "{entityType}/{entityId}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public List<Document> create(@PathVariable String entityType, @PathVariable long entityId,
            @RequestParam("files") MultipartFile[] documents) throws MentorMeException {

        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        
        if (EntityTypes.MENTEE_MENTOR_PROGRAM.equalsIgnoreCase(entityType)) {
            MenteeMentorProgram program = menteeMentorProgramService.get(entityId);
            program.getDocuments().addAll(docs);
        } else if (EntityTypes.MENTEE_MENTOR_GOAL.equalsIgnoreCase(entityType)) {
            MenteeMentorGoal goal = menteeMentorGoalService.get(entityId);
            goal.getDocuments().addAll(docs);
        } else {
            throw new MentorMeException("The provided entityType is unknown.");
        }
        
        return docs;
    }
    
    /**
     * Delete the document from the specified entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @param documentId the document id
     * @throws MentorMeException if any error occurs
     */
    @RequestMapping(value = "{entityType}/{entityId}/document/{documentId}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@PathVariable String entityType, @PathVariable long entityId, @PathVariable long documentId) throws MentorMeException {
        List<Document> docs;
        
        if (EntityTypes.MENTEE_MENTOR_PROGRAM.equalsIgnoreCase(entityType)) {
            docs = menteeMentorProgramService.get(entityId).getDocuments();
        } else if (EntityTypes.MENTEE_MENTOR_GOAL.equalsIgnoreCase(entityType)) {
            docs = menteeMentorGoalService.get(entityId).getDocuments();
        } else {
            throw new MentorMeException("The provided entityType is unknown.");
        }

        boolean removed = false;
        Iterator<Document> it = docs.iterator();
        while (it.hasNext()) {
            Document doc = it.next();
            if (doc.getId() == documentId) {
                it.remove();
                removed = true;
                break;
            }
        }
        
        if (!removed) {
            throw new EntityNotFoundException("The document is not in the entity");
        }
    }
    
    /**
     * Get all documents of the specified entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @return all the associated documents
     * @throws MentorMeException if any error occurs
     */
    @RequestMapping(value = "{entityType}/{entityId}", method = RequestMethod.GET)
    public List<Document> get(@PathVariable String entityType, @PathVariable long entityId) throws MentorMeException {
        if (EntityTypes.MENTEE_MENTOR_PROGRAM.equalsIgnoreCase(entityType)) {
            return menteeMentorProgramService.get(entityId).getDocuments();
        } else if (EntityTypes.MENTEE_MENTOR_GOAL.equalsIgnoreCase(entityType)) {
            return menteeMentorGoalService.get(entityId).getDocuments();
        } else {
            throw new MentorMeException("The provided entityType is unknown.");
        }
    }
}
