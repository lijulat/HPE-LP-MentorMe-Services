package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.InstitutionalProgram;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.MenteeMentorProgramSearchCriteria;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorProgramService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Collections;

/**
 * The Spring Data JPA implementation of MenteeMentorProgramService, extends BaseService<MenteeMentorProgram,
 * MenteeMentorProgramSearchCriteria>. Effectively thread safe after configuration.
 */
@Service
@NoArgsConstructor
public class MenteeMentorProgramServiceImpl extends BaseService<MenteeMentorProgram,
        MenteeMentorProgramSearchCriteria> implements MenteeMentorProgramService {

    /**
     * The default institutional program id.
     */
    @Value("${menteeMentorProgram.defaultInstitutionalProgramId}")
    private long defaultInstitutionalProgramId;

    /**
     * This method is used to get the specification.
     *
     * @param criteria the search criteria
     * @return the specification
     * @throws MentorMeException if any other error occurred during operation
     */
    protected Specification<MenteeMentorProgram> getSpecification(MenteeMentorProgramSearchCriteria criteria) throws
            MentorMeException {
        return new MenteeMentorProgramSpecification(criteria);
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkPositive(defaultInstitutionalProgramId, "defaultInstitutionalProgramId");
    }

    /**
     * This method is used to handle nested properties.
     *
     * @param entity the entity
     * @throws IllegalArgumentException if entity is invalid
     * @throws MentorMeException if any error occurred during operation
     */
    @Override
    protected void handleNestedProperties(MenteeMentorProgram entity) throws MentorMeException {
        super.handleNestedProperties(entity);
        Helper.checkEntity(entity.getMentor(), "entity.mentor");
        Helper.checkEntity(entity.getMentee(), "entity.mentee");
        if (entity.getResponsibilities() != null) {
            entity.getResponsibilities()
                  .forEach(c -> c.setMenteeMentorProgram(entity));
        } else {
            entity.setResponsibilities(Collections.emptyList());
        }
        if (entity.getGoals() != null) {
            entity.getGoals()
                  .forEach(c -> {
                      c.setMenteeMentorProgram(entity);
                      if (c.getTasks() != null) {
                          c.getTasks()
                           .forEach(t -> {
                               t.setMenteeMentorGoal(c);
                               if (t.getTask() != null) {
                                   t.getTask().setGoal(c.getGoal());
                               }
                           });
                      }
                      if (c.getGoal() != null && c.getGoal().getTasks() != null) {
                          c.getGoal().getTasks().forEach(t -> t.setGoal(c.getGoal()));
                      }
                  });
        } else {
            entity.setGoals(Collections.emptyList());
        }
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
    public MenteeMentorProgram create(MenteeMentorProgram entity) throws MentorMeException {
        Helper.checkNull(entity, "entity");
        if (entity.getInstitutionalProgram() == null) {
            entity.setInstitutionalProgram(new InstitutionalProgram());
            entity.getInstitutionalProgram()
                  .setId(defaultInstitutionalProgramId);
        } else {
            Helper.checkEntity(entity.getInstitutionalProgram(), "entity.institutionalProgram");
        }
        return super.create(entity);
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
    public MenteeMentorProgram update(long id, MenteeMentorProgram entity) throws MentorMeException {
        super.checkUpdate(id, entity);
        // ensure institutionalProgram exists for update method
        Helper.checkEntity(entity.getInstitutionalProgram(), "entity.institutionalProgram");
        return super.update(id, entity);
    }
}

