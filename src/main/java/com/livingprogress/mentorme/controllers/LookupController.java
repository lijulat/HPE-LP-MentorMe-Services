package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.LookupService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The Lookup REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/lookups")
@NoArgsConstructor
public class LookupController {
    /**
     * The lookup service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private LookupService lookupService;


    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigNotNull(lookupService, "lookupService");
    }

    /**
     * This method is used to get user role lookups.
     *
     * @return the lookups for user role.
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "/userRoles", method = RequestMethod.GET)
    public List<UserRole> getUserRoles() throws MentorMeException {
        return lookupService.getUserRoles();
    }

    /**
     * This method is used to get country lookups.
     *
     * @return the lookups for country.
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    public List<Country> getCountries() throws MentorMeException {
        return lookupService.getCountries();
    }

    /**
     * This method is used to get state lookups.
     *
     * @return the lookups for state.
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "/states", method = RequestMethod.GET)
    public List<State> getStates(@RequestParam(required = false) Long countryId) throws MentorMeException {
        return lookupService.getStates(countryId);
    }

    /**
     * This method is used to get professional consultant area lookups.
     *
     * @return the lookups for professional consultant area.
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "/professionalConsultantAreas", method = RequestMethod.GET)
    public List<ProfessionalConsultantArea> getProfessionalConsultantAreas() throws MentorMeException {
        return lookupService.getProfessionalConsultantAreas();
    }

    /**
     * This method is used to personal interest lookups.
     *
     * @return the lookups for personal interest
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "/personalInterests", method = RequestMethod.GET)
    public List<PersonalInterest> getPersonalInterests() throws MentorMeException {
        return lookupService.getPersonalInterests();
    }

    /**
     * This method is used to professional interest lookups.
     *
     * @return the lookups for professional interest
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "/professionalInterests", method = RequestMethod.GET)
    public List<ProfessionalInterest> getProfessionalInterests() throws MentorMeException {
        return lookupService.getProfessionalInterests();
    }

    /**
     * This method is used to skill lookups.
     *
     * @return the lookups for skill
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "/skills", method = RequestMethod.GET)
    public List<Skill> getSkills() throws MentorMeException {
        return lookupService.getSkills();
    }
}

