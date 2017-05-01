package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.LocaleContext;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Locale;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>LookupController</code>
 */
public class LookupControllerTest extends BaseTest {

    /**
     * Test getUserRoles method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getUserRoles() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/userRoles")
                                      .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("userRoles.json")));
    }

    /**
     * Test getCountries method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getCountries() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/countries")
                                      .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("countries.json")));
    }

    /**
     * Test getCountries by locale.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getCountriesByLocale() throws Exception {
        LocaleContext.setCurrentLocales(Arrays.asList(new Locale("es")));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/countries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("countries-es.json")));
    }

    /**
     * Test getStates method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getStates() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/states")
                                      .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("states.json")));
    }

    /**
     * Test getStates by locale.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getStatesByLocale() throws Exception {
        LocaleContext.setCurrentLocales(Arrays.asList(new Locale("es")));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/states")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("states-es.json")));
    }

    /**
     * Test getProfessionalConsultantAreas method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getProfessionalConsultantAreas() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/professionalConsultantAreas")
                                      .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("professionalConsultantAreas.json")));
    }

    /**
     * Test getProfessionalConsultantAreas by locale.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getProfessionalConsultantAreasByLocale() throws Exception {
        LocaleContext.setCurrentLocales(Arrays.asList(new Locale("es")));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/professionalConsultantAreas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("professionalConsultantAreas-es.json")));
    }

    /**
     * Test getPersonalInterests method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getPersonalInterests() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/personalInterests")
                                      .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("personalInterests.json")));
    }

    /**
     * Test getPersonalInterests by locale.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getPersonalInterestsByLocale() throws Exception {
        LocaleContext.setCurrentLocales(Arrays.asList(new Locale("es")));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/personalInterests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("personalInterests-es.json")));
    }

    /**
     * Test getProfessionalInterests method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getProfessionalInterests() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/professionalInterests")
                                      .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("professionalInterests.json")));
    }

    /**
     * Test getProfessionalInterests by locale.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getProfessionalInterestsByLocale() throws Exception {
        LocaleContext.setCurrentLocales(Arrays.asList(new Locale("es")));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/professionalInterests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("professionalInterests-es.json")));
    }

    /**
     * Test getSkills.
     *
     * @throws Exception throws if any error happens.
     */
    public void getSkills() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/skills")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("skills.json")));
    }

    /**
     * Test getSkills by locale.
     *
     * @throws Exception throws if any error happens.
     */
    public void getSkillsByLocale() throws Exception {
        LocaleContext.setCurrentLocales(Arrays.asList(new Locale("es")));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/lookups/skills")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readFile("skills-es.json")));
    }
}
