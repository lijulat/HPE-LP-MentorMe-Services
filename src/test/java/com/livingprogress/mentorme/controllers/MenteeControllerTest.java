package com.livingprogress.mentorme.controllers;


import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.Mentee;
import com.livingprogress.mentorme.entities.ParentConsent;
import com.livingprogress.mentorme.entities.PersonalInterest;
import com.livingprogress.mentorme.entities.ProfessionalInterest;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.entities.UserStatus;
import com.livingprogress.mentorme.entities.WeightedPersonalInterest;
import com.livingprogress.mentorme.entities.WeightedProfessionalInterest;
import com.livingprogress.mentorme.remote.services.HODClient;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>MenteeController</code>
 */
public class MenteeControllerTest extends BaseTest {
    /**
     * The sample entity json.
     */
    private static String sample;

    /**
     * The demo entity json.
     */
    private static String demo;

    /**
     * All entities json.
     */
    private static String entities;

    /**
     * The mock HOD client.
     */
    @Autowired
    private HODClient hodClient;

    /**
     * Read related json.
     *
     * @throws Exception throws if any error happens.
     */
    @BeforeClass
    public static void setupClass() throws Exception {
        sample = readFile("mentee4.json");
        demo = readFile("demo-mentee.json");
        entities = readFile("mentees.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(sample));
    }

    /**
     * Test create method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void create() throws Exception {
        Mentee demoEntity = objectMapper.readValue(demo, Mentee.class);
        assertNotNull(demoEntity.getPassword());
        assertNull(demoEntity.getCreatedOn());
        assertNull(demoEntity.getLastModifiedOn());
        demoEntity.setPassword(null);
        // will create mentee as inactive
        demoEntity.setStatus(UserStatus.ACTIVE);
        checkEntities(demoEntity.getPersonalInterests());
        checkEntities(demoEntity.getProfessionalInterests());
        checkEntity(demoEntity.getParentConsent());
        checkEntity(demoEntity.getInstitutionAffiliationCode());
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/mentees")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demo))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.password").doesNotExist())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andExpect(jsonPath("$.createdOn").exists())
                            .andExpect(jsonPath("$.lastModifiedOn").exists())
                            .andExpect(jsonPath("$.status").value("INACTIVE"))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        Mentee result = objectMapper.readValue(res, Mentee.class);
        demoEntity.setId(result.getId());
        demoEntity.setCreatedOn(result.getCreatedOn());
        demoEntity.setLastModifiedOn(result.getLastModifiedOn());
        demoEntity.setStatus(UserStatus.INACTIVE);
        // will use random value
        assertNotEquals(demoEntity.getParentConsent()
                                  .getToken(), result.getParentConsent()
                                                     .getToken());
        demoEntity.getParentConsent()
                  .setToken(result.getParentConsent()
                                  .getToken());
        verifyEntities(demoEntity.getPersonalInterests(), result.getPersonalInterests());
        verifyEntities(demoEntity.getProfessionalInterests(), result.getProfessionalInterests());
        verifyEntity(demoEntity.getParentConsent(), result.getParentConsent());
        verifyEntity(demoEntity.getInstitutionAffiliationCode(), result.getInstitutionAffiliationCode());
        result.setInstitution(demoEntity.getInstitution());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        ParentConsent parentConsent = result.getParentConsent();
        verifyEmail("New Mentee", parentConsent.getToken(), parentConsent.getParentEmail());
    }

    /**
     * Test update method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void update() throws Exception {
        // no updates
        mockMvc.perform(MockMvcRequestBuilders.put("/mentees/4")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(sample))
               .andExpect(status().isOk())
               .andExpect(content().json(sample));
        Mentee demoEntity = objectMapper.readValue(demo, Mentee.class);
        checkEntities(demoEntity.getPersonalInterests());
        checkEntities(demoEntity.getProfessionalInterests());
        checkEntity(demoEntity.getParentConsent());
        demoEntity.getPersonalInterests()
                  .get(0)
                  .setId(1L);
        demoEntity.getProfessionalInterests()
                  .get(0)
                  .setId(1L);
        demoEntity.getParentConsent()
                  .setId(1L);
        // try to update created on
        demoEntity.setCreatedOn(sampleFutureDate);
        demoEntity.setId(4);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockMvc.perform(MockMvcRequestBuilders.put("/mentees/4")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(json))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.password").doesNotExist())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andExpect(jsonPath("$.createdOn").exists())
                            .andExpect(jsonPath("$.lastModifiedOn").exists())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        Mentee result = objectMapper.readValue(res, Mentee.class);

        // same id entity just updates
        assertEquals(1, result.getPersonalInterests()
                              .get(0)
                              .getId());
        assertEquals(1, result.getProfessionalInterests()
                              .get(0)
                              .getId());
        assertEquals(1, result.getParentConsent()
                              .getId());

        // will not update created on during updating
        assertNotEquals(sampleFutureDate, result.getCreatedOn());
        demoEntity.setCreatedOn(result.getCreatedOn());
        demoEntity.setLastModifiedOn(result.getLastModifiedOn());
        verifyEntities(demoEntity.getPersonalInterests(), result.getPersonalInterests());
        verifyEntities(demoEntity.getProfessionalInterests(), result.getProfessionalInterests());
        verifyEntity(demoEntity.getParentConsent(), result.getParentConsent());
        verifyEntity(demoEntity.getInstitutionAffiliationCode(), result.getInstitutionAffiliationCode());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        // test nested properties
        demoEntity.setParentConsent(null);
        demoEntity.setPersonalInterests(null);
        demoEntity.setProfessionalInterests(null);
        mockMvc.perform(MockMvcRequestBuilders.put("/mentees/4")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.password").doesNotExist())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.createdOn").exists())
               .andExpect(jsonPath("$.parentConsent").doesNotExist())
               .andExpect(jsonPath("$.personalInterests", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.professionalInterests", Matchers.hasSize(0)));
        // second null updates
        mockMvc.perform(MockMvcRequestBuilders.put("/mentees/4")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.password").doesNotExist())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.createdOn").exists())
               .andExpect(jsonPath("$.parentConsent").doesNotExist())
               .andExpect(jsonPath("$.personalInterests", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.professionalInterests", Matchers.hasSize(0)));
        // update with new nested values
        List<PersonalInterest> personalInterests = lookupService.getPersonalInterests();
        assertTrue(personalInterests.size() > 0);
        demoEntity.setPersonalInterests(personalInterests.stream()
                                                         .map(p -> {
                                                             WeightedPersonalInterest weightedPersonalInterest = new
                                                                     WeightedPersonalInterest();
                                                             weightedPersonalInterest.setPersonalInterest(p);
                                                             weightedPersonalInterest.setWeight(1);
                                                             return weightedPersonalInterest;
                                                         })
                                                         .collect(Collectors.toList()));
        List<ProfessionalInterest> professionalInterests = lookupService.getProfessionalInterests();
        assertTrue(professionalInterests.size() > 0);
        demoEntity.setProfessionalInterests(professionalInterests.stream()
                                                                 .map(p -> {
                                                                     WeightedProfessionalInterest
                                                                            weightedProfessionalInterest = new
                                                                             WeightedProfessionalInterest();
                                                                     weightedProfessionalInterest.setProfessionalInterest(p);
                                                                     weightedProfessionalInterest.setWeight(1);
                                                                     return weightedProfessionalInterest;
                                                                 })
                                                                 .collect(Collectors.toList()));
        mockMvc.perform(MockMvcRequestBuilders.put("/mentees/4")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.password").doesNotExist())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.createdOn").exists())
               .andExpect(jsonPath("$.parentConsent").doesNotExist())
               .andExpect(jsonPath("$.personalInterests", Matchers.hasSize(personalInterests.size())))
               .andExpect(jsonPath("$.professionalInterests", Matchers.hasSize(professionalInterests.size())));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/mentees/4"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/mentees/4"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<Mentee> result = readSearchResult(entities, Mentee.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities));
        SearchResult<Mentee> result1 = getSearchResult
                ("/mentees?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", Mentee.class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(Mentee::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(Mentee::getId)
                       .toArray());
        SearchResult<Mentee> result2 = getSearchResult
                ("/mentees?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", Mentee.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(Mentee::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(Mentee::getId)
                       .toArray());

        SearchResult<Mentee> result3 = getSearchResult
                ("/mentees?pageNumber=1&pageSize=2&sortColumn=email&sortOrder=DESC", Mentee.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(Mentee::getEmail)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(Mentee::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(Mentee::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?institutionId=2")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(10));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?status=INACTIVE")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(11));
        ;
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?minAveragePerformanceScore=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(14));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?maxAveragePerformanceScore=0")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(4));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?name=firstname13")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(13));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?name=lastname14")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(14));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?schoolName=school4")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(12));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?personalInterests[0].id=1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(4));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?professionalInterests[0].id=1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(4));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/mentees?pageNumber=0&pageSize=2&sortColumn=email&sortOrder=DESC&institutionId=1&status=ACTIVE" +
                        "&minAveragePerformanceScore=0&maxAveragePerformanceScore=0&name=firstname4&schoolName" +
                "=school1&personalInterests[0].id=1&professionalInterests[0].id=1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(4));

        // check distance
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?distance=1&longitude=-73.9095494&latitude=40.7131197")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(4))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(4)));

        mockMvc.perform(MockMvcRequestBuilders.get("/mentees?distance=0.4&longitude=-73.9095494&latitude=40.7131197")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(3))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(3)));
    }

    /**
     * Test getAverageScore method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getAverageScore() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/averageScore"))
               .andExpect(status().isOk())
               .andExpect(content().string(readFile("mentee4AverageScore.txt")));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/999/averageScore"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test getMatchingMentors method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getMatchingMentors() throws Exception {
        // assigned
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/matchingMentors"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(2)))
               .andExpect(content().json(readFile("mentee4MatchingMentors.json")));
        // not assigned
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/10/matchingMentors"))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("mentee10MatchingMentors.json")));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/999/matchingMentors"))
               .andExpect(status().isNotFound());

        // test match criteria
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/matchingMentors?distance=10")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(2)));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/matchingMentors?distance=2")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(1)));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/matchingMentors?maxCount=1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(1)));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/matchingMentors?personalInterests[0].id=1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(1)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/mentees/4/matchingMentors?professionalInterests[0].id=1")
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(1)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/mentees/4/matchingMentors?distance=30&maxCount=100"
                        + "&personalInterests[0].id=2"
                        + "&professionalInterests[0].id=1")
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    /**
     * Test confirmParentConsent method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void confirmParentConsent() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/mentees")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demo))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.password").doesNotExist())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andExpect(jsonPath("$.createdOn").exists())
                            .andExpect(jsonPath("$.status").value("INACTIVE"))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        Mentee result = objectMapper.readValue(res, Mentee.class);
        String token = result.getParentConsent()
                             .getToken();
        mockMvc.perform(MockMvcRequestBuilders.put("/mentees/confirmParentConsent?token=" + token))
               .andExpect(status().isOk())
               .andExpect(content().string("true"));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/" + result.getId())
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("ACTIVE"));
        mockMvc.perform(MockMvcRequestBuilders.put("/mentees/confirmParentConsent?token=" + token))
               .andExpect(status().isOk())
               .andExpect(content().string("false"));
    }

    /**
     * Test remoteMatchingMentors method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void remoteMatchingMentors() throws Exception {
        when(hodClient.getMatchingMentors(any(), any()))
                .thenReturn(Collections.emptyList());
        // empty ids
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/remoteMatchingMentors"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(0)));
        // fake two ids
        when(hodClient.getMatchingMentors(any(), any()))
                .thenReturn(Arrays.asList(3L, 5L));
        mockMvc.perform(MockMvcRequestBuilders.get("/mentees/4/remoteMatchingMentors"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", Matchers.hasSize(2)))
               .andExpect(jsonPath("$[0].id").value(3))
               .andExpect(jsonPath("$[1].id").value(5));
    }
}
