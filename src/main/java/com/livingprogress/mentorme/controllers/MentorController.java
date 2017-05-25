package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.aop.LogAspect;
import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.entities.Locale;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Mentor REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/mentors")
@NoArgsConstructor
public class MentorController {
    /**
     * The mentor service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MentorService mentorService;

    @Autowired
    private UserService userService;

    /**
     * The mentee service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MenteeService menteeService;

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
    @Value("${matchingMentees.directMatchingPoints}")
    private int directMatchingPoints;

    /**
     * The amount of points if there is a parent category matching of the interests.
     */
    @Value("${matchingMentees.parentCategoryMatchingPoints}")
    private int parentCategoryMatchingPoints;

    /**
     * The coefficient for the professional interests score
     * (professional interests are more important than personal ones).
     */
    @Value("${matchingMentees.professionalInterestsCoefficient}")
    private int professionalInterestsCoefficient;

    /**
     * The coefficient for the personal interests.
     */
    @Value("${matchingMentees.personalInterestsCoefficient}")
    private int personalInterestsCoefficient;

    /**
     * the top N best matching mentors that should be returned.
     */
    @Value("${matchingMentees.topMatchingAmount}")
    private int topMatchingAmount;

    /**
     * the minimum goal score. Default value = 0.
     */
    @Value("${matchingMentees.minimumGoalScore}")
    private int minimumGoalScore;

    /**
     * Spring social linked in api.
     */
    private LinkedIn linkedIn;

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
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
        //TODO uncomment below if implemented linkedin
        // Helper.checkConfigNotNull(linkedIn, "linkedIn");
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
    public Mentor get(@PathVariable long id) throws MentorMeException {
        return mentorService.get(id);
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
    public Mentor create(@RequestBody Mentor entity) throws MentorMeException {
        return mentorService.create(entity);
    }

    /**
     * Registers a mentor.
     * @param entity the entity.
     * @return the token.
     * @throws MentorMeException if there are any errors.
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@RequestBody Mentor entity) throws MentorMeException {
        // set the user roles
        List<UserRole> roles = lookupService.getUserRoles();
        for (UserRole role : roles) {
            if ("mentor".equalsIgnoreCase(role.getValue())) {
                entity.setRoles(Arrays.asList(role));
                break;
            }
        }

	// set the locale object
	Locale localeObj = entity.getLocale();
	if(localeObj!=null) {
	   String localeValue = localeObj.getValue();
	   Locale localeObjTemp = mentorService.findLocale(localeValue); // fetch the Locale Instance
	   entity.setLocale(localeObjTemp);                              // Set in the entity 
	}
	else {
	   Locale localeObjTemp = mentorService.findLocale("en");        // get locale Instance having "en" by default
	   entity.setLocale(localeObjTemp);                              // Set in the entity by default english language
	}

        // set the status
        entity.setStatus(UserStatus.ACTIVE);

        // check if the email already exists
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setEmail(entity.getEmail());
        SearchResult<User> result = userService.search(criteria, null);
        if (result.getTotal() > 0) {
            // user already register
            throw new IllegalArgumentException("This email already registered");
        }

        entity.setLastLoginOn(new Date());

        // create the entity
        Mentor mentor = mentorService.create(entity);

        String token = userService.createTokenForUser(mentor);
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
    public Mentor update(@PathVariable long id, @RequestBody Mentor entity) throws MentorMeException {
        return mentorService.update(id, entity);
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
        mentorService.delete(id);
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
    public SearchResult<Mentor> search(@ModelAttribute MentorSearchCriteria criteria,
            @ModelAttribute Paging paging) throws MentorMeException {
        return mentorService.search(criteria, paging);
    }

    /**
     * This method is used to get the mentor avg score.
     *
     * @param id the id of the entity to retrieve
     * @return the avg score
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/averageScore", method = RequestMethod.GET)
    public int getAverageScore(@PathVariable long id) throws MentorMeException {
        return mentorService.getAverageMentorScore(id);
    }

    /**
     * This method is used to get the matching mentees.
     *
     * @param id the id of the entity to retrieve
     * @param matchSearchCriteria the match criteria
     * @return the matching mentees.
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/matchingMentees", method = RequestMethod.GET)
    public List<Mentee> getMatchingMentees(@PathVariable long id,
            @ModelAttribute MatchSearchCriteria matchSearchCriteria) throws MentorMeException {
        Mentor mentor = mentorService.get(id);
        List<Mentee> mentees = Helper.searchMatchEntities(mentor,
                new MenteeSearchCriteria(), matchSearchCriteria, menteeService);
        Map<Mentee, Integer> menteeScores = new HashMap<>();
        for (Mentee mentee : mentees) {
            int professionalScore = Helper.getScore(directMatchingPoints, parentCategoryMatchingPoints,
                    new ArrayList<>(mentor.getProfessionalInterests()),
                    new ArrayList<>(mentee.getProfessionalInterests()),
                    WeightedProfessionalInterest::getWeight,
                    WeightedProfessionalInterest::getProfessionalInterest,
                    Helper::getParentCategoryFromWeightedProfessionalInterest);
            int personalScore = Helper.getScore(directMatchingPoints,
                    parentCategoryMatchingPoints,
                    new ArrayList<>(mentor.getPersonalInterests()),
                    new ArrayList<>(mentee.getPersonalInterests()),
                    WeightedPersonalInterest::getWeight,
                    WeightedPersonalInterest::getPersonalInterest,
                    Helper::getParentCategoryFromWeightedPersonalInterest);
            menteeScores.put(mentee, professionalScore * professionalInterestsCoefficient
                    + personalScore * personalInterestsCoefficient);
        }

        // comment below if do not want to show score in log
        menteeScores.entrySet().forEach(k ->
                Helper.logDebugMessage(LogAspect.LOGGER, k.getKey().getId() + "," + k.getValue()));
        // could custom max count to return
        int limit = matchSearchCriteria.getMaxCount() != null
                ? matchSearchCriteria.getMaxCount() : topMatchingAmount;
        // sort the mentorScores by scores and return the top <topMatchingAmount> mentees;
        return menteeScores.entrySet().stream() // reverse means desc order
                .filter(c -> c.getValue() > minimumGoalScore) // must match or weight > minimumGoalScore
                .sorted(Comparator.comparing(Map.Entry<Mentee, Integer>::getValue)
                                  .reversed())
                .map(Map.Entry::getKey).limit(limit).collect(Collectors.toList());
    }

    /**
     * This method is used to retrieve the linked in experience data.
     *
     * @return the professional experience data
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "linkedInExperience", method = RequestMethod.GET)
    public List<ProfessionalExperienceData> getLinkedInProfessionalExperienceData() throws MentorMeException {
        //TODO fix this if implemented linkedin
        throw new MentorMeException("Not implemented!");
    }

    /**
     * This method is used to get the matching mentees using havenondemand api.
     *
     * @param id the id of the entity to retrieve
     * @param criteria the remote criteria
     * @return the matching mentees.
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/remoteMatchingMentees")
    public List<Mentee> remoteMatchingMentees(@PathVariable long id,
            @ModelAttribute MatchSearchCriteria criteria) throws MentorMeException {
        List<Long> ids = hodClient.getMatchingMentees(mentorService.get(id), criteria);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        } else {
            MenteeSearchCriteria menteeSearchCriteria = new MenteeSearchCriteria();
            menteeSearchCriteria.setIds(ids);
            return menteeService.search(menteeSearchCriteria, null).getEntities();
        }
    }
}

