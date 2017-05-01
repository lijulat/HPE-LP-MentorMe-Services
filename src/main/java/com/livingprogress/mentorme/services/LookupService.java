package com.livingprogress.mentorme.services;

import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.exceptions.MentorMeException;

import java.util.List;

/**
 * The lookup service.Implementation should be effectively thread-safe.
 */
public interface LookupService {
    /**
     * This method is used to get user role lookups.
     *
     * @return the lookups for user role.
     * @throws MentorMeException if any other error occurred during operation
     */
   List<UserRole> getUserRoles() throws MentorMeException;

    /**
     * This method is used to get country lookups.
     *
     * @return the lookups for country.
     * @throws MentorMeException if any other error occurred during operation
     */
    List<Country> getCountries() throws MentorMeException;

    /**
     * This method is used to get state lookups.
     *
     * @return the lookups for state.
     * @throws MentorMeException if any other error occurred during operation
     */
    List<State> getStates(Long countryId) throws MentorMeException;

    /**
     * This method is used to get professional consultant area lookups.
     *
     * @return the lookups for professional consultant area.
     * @throws MentorMeException if any other error occurred during operation
     */
    List<ProfessionalConsultantArea> getProfessionalConsultantAreas() throws MentorMeException;

    /**
     * This method is used to personal interest lookups.
     *
     * @return the lookups for personal interest
     * @throws MentorMeException if any other error occurred during operation
     */
    List<PersonalInterest> getPersonalInterests() throws MentorMeException;

    /**
     * This method is used to professional interest lookups.
     *
     * @return the lookups for professional interest
     * @throws MentorMeException if any other error occurred during operation
     */
    List<ProfessionalInterest> getProfessionalInterests() throws MentorMeException;

    /**
     * Gets all the skills.
     *
     * @return the skills.
     * @throws MentorMeException if there are any errors.
     */
    List<Skill> getSkills() throws MentorMeException;

    /**
     * Get all the locales.
     *
     * @return the locales
     * @throws MentorMeException if there are any errors.
     */
    List<Locale> getLocales() throws MentorMeException;


}

