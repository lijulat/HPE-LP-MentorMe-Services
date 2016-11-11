package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.InstitutionAgreement;
import com.livingprogress.mentorme.entities.SearchResult;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>InstitutionAgreementController</code>
 */
public class InstitutionAgreementControllerTest extends BaseTest {
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
     * Read related json.
     *
     * @throws Exception throws if any error happens.
     */
    @BeforeClass
    public static void setupClass() throws Exception {
        sample = readFile("institutionAgreement1.json");
        demo = readFile("demo-institutionAgreement.json");
        entities = readFile("institutionAgreements.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionAgreements/1")
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
        InstitutionAgreement demoEntity = objectMapper.readValue(demo, InstitutionAgreement.class);
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/institutionAgreements")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demo))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        InstitutionAgreement result = objectMapper.readValue(res, InstitutionAgreement.class);
        demoEntity.setId(result.getId());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
    }

    /**
     * Test update method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void update() throws Exception {
        InstitutionAgreement demoEntity = objectMapper.readValue(demo, InstitutionAgreement.class);
        demoEntity.setId(1);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockMvc.perform(MockMvcRequestBuilders.put("/institutionAgreements/1")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(json))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        InstitutionAgreement result = objectMapper.readValue(res, InstitutionAgreement.class);
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/institutionAgreements/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionAgreements/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/institutionAgreements/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<InstitutionAgreement> result = readSearchResult(entities, InstitutionAgreement.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionAgreements?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities, true));
        SearchResult<InstitutionAgreement> result1 = getSearchResult
                ("/institutionAgreements?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", InstitutionAgreement
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(InstitutionAgreement::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(InstitutionAgreement::getId)
                       .toArray());
        SearchResult<InstitutionAgreement> result2 = getSearchResult
                ("/institutionAgreements?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", InstitutionAgreement
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(InstitutionAgreement::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(InstitutionAgreement::getId)
                       .toArray());

        SearchResult<InstitutionAgreement> result3 = getSearchResult
                ("/institutionAgreements?pageNumber=2&pageSize=2&sortColumn=agreementName&sortOrder=DESC",
                        InstitutionAgreement.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(InstitutionAgreement::getAgreementName)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(InstitutionAgreement::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(InstitutionAgreement::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionAgreements?agreementName=agreementName5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionAgreements?institutionId=3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionAgreements?userRole.id=2")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/institutionAgreements?pageNumber=0&pageSize=2&sortColumn=agreementName&sortOrder=DESC&agreementName" +
                        "=agreementName1&institutionId=1&userRole.id=1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }
}
