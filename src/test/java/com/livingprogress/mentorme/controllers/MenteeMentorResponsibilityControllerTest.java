package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.MenteeMentorResponsibility;
import com.livingprogress.mentorme.entities.SearchResult;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The test cases for <code>MenteeMentorResponsibilityController</code>
 */
public class MenteeMentorResponsibilityControllerTest extends BaseTest {
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
        sample = readFile("menteeMentorResponsibility1.json");
        demo = readFile("demo-menteeMentorResponsibility.json");
        entities = readFile("menteeMentorResponsibilities.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorResponsibilities/1")
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
        MenteeMentorResponsibility demoEntity = objectMapper.readValue(demo, MenteeMentorResponsibility.class);
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/menteeMentorResponsibilities")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demo))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorResponsibility result = objectMapper.readValue(res, MenteeMentorResponsibility.class);
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
        MenteeMentorResponsibility demoEntity = objectMapper.readValue(demo, MenteeMentorResponsibility.class);
        demoEntity.setId(1);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorResponsibilities/1")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(json))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorResponsibility result = objectMapper.readValue(res, MenteeMentorResponsibility.class);
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorResponsibilities/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorResponsibilities/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorResponsibilities/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<MenteeMentorResponsibility> result = readSearchResult(entities, MenteeMentorResponsibility.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorResponsibilities?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities, true));
        SearchResult<MenteeMentorResponsibility> result1 = getSearchResult
                ("/menteeMentorResponsibilities?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", MenteeMentorResponsibility
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorResponsibility::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(MenteeMentorResponsibility::getId)
                       .toArray());
        SearchResult<MenteeMentorResponsibility> result2 = getSearchResult
                ("/menteeMentorResponsibilities?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", MenteeMentorResponsibility
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorResponsibility::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(MenteeMentorResponsibility::getId)
                       .toArray());

        SearchResult<MenteeMentorResponsibility> result3 = getSearchResult
                ("/menteeMentorResponsibilities?pageNumber=2&pageSize=2&sortColumn=title&sortOrder=DESC",
                        MenteeMentorResponsibility.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(MenteeMentorResponsibility::getTitle)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(MenteeMentorResponsibility::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(MenteeMentorResponsibility::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorResponsibilities?menteeMentorProgramId=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorResponsibilities?responsibilityId=3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/menteeMentorResponsibilities?pageNumber=0&pageSize=2&sortColumn=title&sortOrder=DESC" +
                        "&menteeMentorProgramId=1&responsibilityId=1")
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }
}
