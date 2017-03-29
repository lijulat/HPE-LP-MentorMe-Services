package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.aop.LogAspect;
import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.remote.services.HODClient;
import com.livingprogress.mentorme.services.LookupService;
import com.livingprogress.mentorme.services.MenteeService;
import com.livingprogress.mentorme.services.MentorService;
import com.livingprogress.mentorme.services.UserService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Mentee REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/mentees")
@NoArgsConstructor
public class MenteeController extends BaseEmailController {
    /**
     * The mentee service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MenteeService menteeService;

    /**
     * The mentor service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MentorService mentorService;

    /**
     * Represents the user service.
     */
    @Autowired
    private UserService userService;

    /**
     * Reprseents the lookup service.
     */
    @Autowired
    private LookupService lookupService;


    /**
     * The hod client used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private HODClient hodClient;

    /**
     * The amount of points if there is a direct matching of the interests.
     */
    @Value("${matchingMentors.directMatchingPoints}")
    private int directMatchingPoints;

    /**
     * The amount of points if there is a parent category matching of the interests.
     */
    @Value("${matchingMentors.parentCategoryMatchingPoints}")
    private int parentCategoryMatchingPoints;

    /**
     * The coefficient for the professional interests score
     * (professional interests are more important than personal ones).
     */
    @Value("${matchingMentors.professionalInterestsCoefficient}")
    private int professionalInterestsCoefficient;

    /**
     * The coefficient for the personal interests.
     */
    @Value("${matchingMentors.personalInterestsCoefficient}")
    private int personalInterestsCoefficient;

    /**
     * the top N best matching mentors that should be returned.
     */
    @Value("${matchingMentors.topMatchingAmount}")
    private int topMatchingAmount;

    /**
     * the minimum goal score. Default value = 0.
     */
    @Value("${matchingMentors.minimumGoalScore}")
    private int minimumGoalScore;


    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkConfigNotNull(menteeService, "menteeService");
        Helper.checkConfigNotNull(mentorService, "mentorService");
        Helper.checkConfigNotNull(hodClient, "hodClient");
        Helper.checkPositive(directMatchingPoints, "directMatchingPoints");
        Helper.checkPositive(parentCategoryMatchingPoints, "parentCategoryMatchingPoints");
        Helper.checkPositive(professionalInterestsCoefficient, "professionalInterestsCoefficient");
        Helper.checkPositive(personalInterestsCoefficient, "personalInterestsCoefficient");
        Helper.checkPositive(topMatchingAmount, "topMatchingAmount");
        Helper.checkConfigState(professionalInterestsCoefficient > personalInterestsCoefficient,
                "professional interests are more important than personal ones");
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
    public Mentee get(@PathVariable long id) throws MentorMeException {
        return menteeService.get(id);
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
    public Mentee create(@RequestBody Mentee entity) throws MentorMeException {
        Helper.checkNull(entity, "entity");
        entity.setStatus(UserStatus.INACTIVE);
        boolean sendEmail = false;
        if (entity.getParentConsent() != null) {
            ParentConsent parentConsent = entity.getParentConsent();
            Helper.checkEmail(parentConsent.getParentEmail(), "parentConsent.parentEmail");
            parentConsent.setToken(UUID.randomUUID().toString());
            sendEmail = true;
        }
        Mentee mentee = menteeService.create(entity);
        if (sendEmail) {
            // will create parent consent and only send email if created successfully
            Map<String, Object> model = new HashMap<>();
            model.put("parentConsent", mentee.getParentConsent());
            sendEmail(mentee.getParentConsent().getParentEmail(), "createMentee", model);
        }
        return mentee;
    }


    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@RequestBody Mentee entity) throws MentorMeException {
        // set the user roles
        List<UserRole> roles = lookupService.getUserRoles();
        for (UserRole role : roles) {
            if ("mentee".equalsIgnoreCase(role.getValue())) {
                entity.setRoles(Arrays.asList(role));
                break;
            }
        }

        // set the status
        entity.setStatus(UserStatus.ACTIVE);

        // get the institution code
        if ((entity.getInstitutionAffiliationCode() == null
                || entity.getInstitutionAffiliationCode().getCode() == null)
                && (entity.getInstitution() == null || entity.getInstitution().getId() <= 0)) {
            throw new IllegalArgumentException("Institution affiliation code or default institution is required.");
        }

        if (entity.getInstitutionAffiliationCode() != null && entity.getInstitutionAffiliationCode().getCode() != null) {
            // set the institution code and institution
            InstitutionAffiliationCode institutionAffiliationCode = menteeService.findInstitutionAffiliationCode(
                    entity.getInstitutionAffiliationCode().getCode());
            if (institutionAffiliationCode == null) {
                throw new IllegalArgumentException(
                        "Code: " + entity.getInstitutionAffiliationCode().getCode() + " Not found");
            }

            entity.setInstitutionAffiliationCode(institutionAffiliationCode);
            Institution institution = new Institution();
            institution.setId(institutionAffiliationCode.getInstitutionId());
            entity.setInstitution(institution);
        }

        // check if the email already exists
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setEmail(entity.getEmail());
        SearchResult<User> result = userService.search(criteria, null);
        if (result.getTotal() > 0) {
            // user already register
            throw new IllegalArgumentException("Email already registered");
        }

        entity.setLastLoginOn(new Date());

        // create the entity
        Mentee mentee = menteeService.create(entity);

        String token = userService.createTokenForUser(mentee);
        Map<String, String> json = new HashMap<>();
        json.put("token", token);
        return json;
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
    public Mentee update(@PathVariable long id, @RequestBody Mentee entity) throws MentorMeException {
        return menteeService.update(id, entity);
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
        menteeService.delete(id);
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
    public SearchResult<Mentee> search(@ModelAttribute MenteeSearchCriteria criteria,
            @ModelAttribute Paging paging) throws MentorMeException {
        return menteeService.search(criteria, paging);
    }

    /**
     * This method is used to get the mentee avg score.
     *
     * @param id the id of the entity to retrieve
     * @return the avg score
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/averageScore", method = RequestMethod.GET)
    public int getAverageScore(@PathVariable long id) throws MentorMeException {
        return menteeService.getAverageMenteeScore(id);
    }

    /**
     * This method is used to get the matching mentors.
     *
     * @param id the id of the entity to retrieve
     * @param matchSearchCriteria the match criteria
     * @return the matching mentors.
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/matchingMentors", method = RequestMethod.GET)
    public List<Mentor> getMatchingMentors(@PathVariable long id,
            @ModelAttribute MatchSearchCriteria matchSearchCriteria) throws MentorMeException {
        Mentee mentee = menteeService.get(id);
        List<Mentor> mentors = Helper.searchMatchEntities(mentee,
                new MentorSearchCriteria(), matchSearchCriteria, mentorService);
        Map<Mentor, Integer> mentorScores = new HashMap<>();
        for (Mentor mentor : mentors) {
            int professionalScore = Helper.getScore(directMatchingPoints, parentCategoryMatchingPoints,
                    new ArrayList<>(mentee.getProfessionalInterests()),
                    new ArrayList<>(mentor.getProfessionalInterests()),
                    WeightedProfessionalInterest::getWeight,
                    WeightedProfessionalInterest::getProfessionalInterest,
                    Helper::getParentCategoryFromWeightedProfessionalInterest);
            int personalScore = Helper.getScore(directMatchingPoints, parentCategoryMatchingPoints,
                    new ArrayList<>(mentee.getPersonalInterests()),
                    new ArrayList<>(mentor.getPersonalInterests()),
                    WeightedPersonalInterest::getWeight,
                    WeightedPersonalInterest::getPersonalInterest,
                    Helper::getParentCategoryFromWeightedPersonalInterest);
            mentorScores.put(mentor,
                    professionalScore * professionalInterestsCoefficient
                            + personalScore * personalInterestsCoefficient);
        }

        // comment below if do not want to show score in log
        mentorScores.entrySet().forEach(k ->
                Helper.logDebugMessage(LogAspect.LOGGER, k.getKey().getId() + "," + k.getValue()));
        // could custom max count to return
        int limit = matchSearchCriteria.getMaxCount() != null
                ? matchSearchCriteria.getMaxCount() : topMatchingAmount;
        // sort the mentorScores by scores and return the top <topMatchingAmount> mentors
        return mentorScores.entrySet().stream()  // reverse means desc order
                .filter(c -> c.getValue() > minimumGoalScore) // must match or weight > minimumGoalScore
                .sorted(Comparator.comparing(Map.Entry<Mentor, Integer>::getValue)
                                  .reversed())
                .map(Map.Entry::getKey).limit(limit).collect(Collectors.toList());
    }

    /**
     * This method is used to confirm the parent consent.
     *
     * @param token the token
     * @return true if found parent consent for inactive mentee
     * @throws IllegalArgumentException if token is null or empty
     * @throws MentorMeException if any other error occurred during operation
     */
    @Transactional
    @RequestMapping(value = "confirmParentConsent", method = RequestMethod.PUT)
    public boolean confirmParentConsent(String token) throws MentorMeException {
        return menteeService.confirmParentConsent(token);
    }

    @Transactional
    @RequestMapping(value = "addParentConsent", method = RequestMethod.PUT)
    public void addParentConsent(@RequestBody ParentConsent parentConsent) throws MentorMeException {
        Mentee mentee = menteeService.get(Helper.getAuthUser().getId());
        if (mentee == null) {
            throw new IllegalArgumentException("Mentee not found");
        }
        Mentee target = new Mentee();
        BeanUtils.copyProperties(mentee, target);
        target.setParentConsent(parentConsent);
        target.setPassword(null);
        menteeService.update(mentee.getId(), target);

    }

    /**
     * This method is used to get the matching mentors using havenondemand api.
     *
     * @param id the id of the entity to retrieve
     * @param criteria the match criteria
     * @return the matching mentors.
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/remoteMatchingMentors")
    public List<Mentor> remoteMatchingMentors(@PathVariable long id,
            @ModelAttribute MatchSearchCriteria criteria) throws MentorMeException {
        List<Long> ids = hodClient.getMatchingMentors(menteeService.get(id), criteria);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            MentorSearchCriteria mentorSearchCriteria = new MentorSearchCriteria();
            mentorSearchCriteria.setIds(ids);
            return mentorService.search(mentorSearchCriteria, null).getEntities();
        }
    }
}

