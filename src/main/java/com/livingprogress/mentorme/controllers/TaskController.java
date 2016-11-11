package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.Paging;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.entities.Task;
import com.livingprogress.mentorme.entities.TaskSearchCriteria;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.TaskService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

/**
 * The Task REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/tasks")
@NoArgsConstructor
public class TaskController extends BaseUploadController {
    /**
     * The service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private TaskService taskService;

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkConfigNotNull(taskService, "taskService");
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
    public Task get(@PathVariable long id) throws MentorMeException {
        return taskService.get(id);
    }

    /**
     * This method is used to create an entity.
     *
     * @param entity the entity to create
     * @param documents the documents
     * @return the created entity
     * @throws IllegalArgumentException if entity is null or not valid
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Task create(Task entity, @RequestParam("files") MultipartFile[] documents) throws MentorMeException  {
        Helper.checkNull(entity, "entity");
        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        entity.setDocuments(docs);
        return taskService.create(entity);
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
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    public Task update(@PathVariable long id, Task entity,
            @RequestParam("files") MultipartFile[] documents) throws MentorMeException  {
        Helper.checkUpdate(id, entity);
        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        entity.setDocuments(docs);
        return taskService.update(id, entity);
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
    public void delete(@PathVariable long id) throws MentorMeException  {
        taskService.delete(id);
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
    public SearchResult<Task> search(@ModelAttribute TaskSearchCriteria criteria,
            @ModelAttribute Paging paging) throws MentorMeException  {
        return taskService.search(criteria, paging);
    }
}

