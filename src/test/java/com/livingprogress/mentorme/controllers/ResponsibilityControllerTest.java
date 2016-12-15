package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.Responsibility;
import com.livingprogress.mentorme.entities.SearchResult;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>ResponsibilityController</code>
 */
public class ResponsibilityControllerTest extends BaseTest {
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
        sample = readFile("responsibility1.json");
        demo = readFile("demo-responsibility.json");
        entities = readFile("responsibilities.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/responsibilities/1")
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
        Responsibility demoEntity = objectMapper.readValue(demo, Responsibility.class);
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/responsibilities")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demo))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        Responsibility result = objectMapper.readValue(res, Responsibility.class);
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
        Responsibility demoEntity = objectMapper.readValue(demo, Responsibility.class);
        demoEntity.setId(1);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockMvc.perform(MockMvcRequestBuilders.put("/responsibilities/1")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(json))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        Responsibility result = objectMapper.readValue(res, Responsibility.class);
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/responsibilities/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/responsibilities/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/responsibilities/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<Responsibility> result = readSearchResult(entities, Responsibility.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/responsibilities?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities, true));
        SearchResult<Responsibility> result1 = getSearchResult
                ("/responsibilities?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", Responsibility
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(Responsibility::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(Responsibility::getId)
                       .toArray());
        SearchResult<Responsibility> result2 = getSearchResult
                ("/responsibilities?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", Responsibility
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(Responsibility::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(Responsibility::getId)
                       .toArray());

        SearchResult<Responsibility> result3 = getSearchResult
                ("/responsibilities?pageNumber=2&pageSize=2&sortColumn=title&sortOrder=DESC",
                        Responsibility.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(Responsibility::getTitle)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(Responsibility::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(Responsibility::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/responsibilities?institutionalProgramId=3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/responsibilities?pageNumber=0&pageSize=2&sortColumn=title&sortOrder=DESC&institutionalProgramId=1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }
}
