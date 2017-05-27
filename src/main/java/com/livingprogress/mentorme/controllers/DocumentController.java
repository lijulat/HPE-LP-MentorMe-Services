package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.ActivityType;
import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.User;
import com.livingprogress.mentorme.exceptions.AccessDeniedException;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.DocumentService;
import com.livingprogress.mentorme.services.MenteeMentorGoalService;
import com.livingprogress.mentorme.services.MenteeMentorProgramService;
import com.livingprogress.mentorme.services.springdata.ActivityRepository;
import com.livingprogress.mentorme.services.springdata.MenteeMentorProgramRepository;
import com.livingprogress.mentorme.utils.EntityTypes;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import java.io.FileNotFoundException;
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
     * The activity repository to create activity. Should be non-null after injection.
     */
    @Autowired
    private ActivityRepository activityRepository;


    /**
     * The mentee mentor program repository to create activity. Should be non-null after injection.
     */
    @Autowired
    private MenteeMentorProgramRepository menteeMentorProgramRepository;

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
     * The DocumentService instance
     */
    @Autowired
    private DocumentService documentService;
    
    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigNotNull(menteeMentorProgramService, "menteeMentorProgramService");
        Helper.checkConfigNotNull(menteeMentorGoalService, "menteeMentorGoalService");
        Helper.checkConfigNotNull(menteeMentorProgramService, "menteeMentorProgramService");
        Helper.checkConfigNotNull(documentService, "documentService");
        Helper.checkConfigNotNull(menteeMentorProgramRepository, "menteeMentorProgramRepository");
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
        User user = Helper.getAuthUser();

        if (EntityTypes.MENTEE_MENTOR_PROGRAM.equalsIgnoreCase(entityType)) {
            MenteeMentorProgram program = menteeMentorProgramService.get(entityId);

            if (Helper.isMentee() && program.getMentee().getId() != user.getId()) {
                throw new AccessDeniedException("You can not upload files to this program!");
            }

            program.getDocuments().addAll(docs);
            // newly added document
            for (Document doc : docs) {
                Helper.audit(activityRepository, menteeMentorProgramRepository,
                        ActivityType.DOCUMENT_ADDED, doc.getId(),
                        doc.getName(),
                        program.getId(),
                        false);
            }
        } else if (EntityTypes.MENTEE_MENTOR_GOAL.equalsIgnoreCase(entityType)) {
            MenteeMentorGoal goal = menteeMentorGoalService.get(entityId);
            if (Helper.isMentee() && goal.getMenteeMentorProgram().getMentee().getId() != user.getId()) {
                throw new AccessDeniedException("You can not upload files to this goal!");
            }
            goal.getGoal().getDocuments().addAll(docs);
            goal.getDocuments().addAll(docs);
            // newly added document
            for (Document doc : docs) {
                Helper.audit(activityRepository, menteeMentorProgramRepository,
                        ActivityType.DOCUMENT_ADDED, doc.getId(),
                        doc.getName(),
                        Helper.getId(goal.getMenteeMentorProgram()),
                        false);
            }
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
    
    /**
     * Download document
     * 
     * @param documentId the id of the document.
     * @return the binary data of the document.
     * @throws MentorMeException if there are any errors.
     * @throws FileNotFoundException if there are any errors.
     */
    @RequestMapping(value = "download/{documentId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> download(@PathVariable Long documentId) throws MentorMeException, FileNotFoundException {
    	Document document = documentService.get(documentId);
        if (document == null) {
            throw new EntityNotFoundException("Document not found for the documentId: " + documentId);
        }
        return Helper.downloadFile(document.getPath());
    }
}

