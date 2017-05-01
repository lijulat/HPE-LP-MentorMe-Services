package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.Goal;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.SearchResult;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The test cases for <code>GoalController</code>
 */
public class GoalControllerTest extends BaseTest {
    /**
     * The sample entity json.
     */
    private static String sample;

    /**
     * The demo entity json.
     */
    private static String demo;

    /**
     * The demo entity json.
     */
    private static String demoCreate;

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
        sample = readFile("goal1.json");
        demo = readFile("demo-goal.json");
        demoCreate = readFile("demo-goal-create.json");
        entities = readFile("goals.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/goals/1")
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
        Goal demoEntity = objectMapper.readValue(demoCreate, Goal.class);
        checkEntities(demoEntity.getUsefulLinks());
        checkEntities(demoEntity.getTasks());
        // create without documents
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/goals")
                                                           .params(getGoalParams(demoEntity))
                                                           .contentType(MediaType.MULTIPART_FORM_DATA))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andExpect(jsonPath("$.documents", Matchers.hasSize(0)))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        final Goal result = objectMapper.readValue(res, Goal.class);
        demoEntity.setId(result.getId());
        verifyEntities(demoEntity.getUsefulLinks(), result.getUsefulLinks());
        verifyEntities(demoEntity.getTasks(), result.getTasks());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        verifyDocuments(0);
        // upload file
        demoEntity.setId(0);
        demoEntity.setDocuments(null);
        res = mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/goals")
                                                    .file(FILE1)
                                                    .file(FILE2)
                                                    .header(AUTH_HEADER_NAME, mentorToken)
                                                    .params(getGoalParams(demoEntity))
                                                    .contentType(MediaType.MULTIPART_FORM_DATA))
                     .andExpect(status().isCreated())
                     .andExpect(jsonPath("$.id").isNumber())
                     .andExpect(jsonPath("$.documents", Matchers.hasSize(2)))
                     .andReturn()
                     .getResponse()
                     .getContentAsString();
        final Goal result2 = objectMapper.readValue(res, Goal.class);
        verifyDocuments(2);
        mockMvc.perform(MockMvcRequestBuilders.get("/goals/" + result2.getId())
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.documents", Matchers.hasSize(2)));
        // test null nested properties
        demoEntity.setId(0);
        demoEntity.setTasks(null);
        demoEntity.setDocuments(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/goals")
                                              .params(getGoalParams(demoEntity))
                                              .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.documents", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.tasks", Matchers.hasSize(0)));
    }

    /**
     * Test update method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void update() throws Exception {
        Goal demoEntity = objectMapper.readValue(demo, Goal.class);
        checkEntities(demoEntity.getUsefulLinks());
        checkEntities(demoEntity.getTasks());
        demoEntity.setId(1);
        // update without documents
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/goals/1")
                                                           .params(getGoalParams(demoEntity))
                                                           .contentType(MediaType.MULTIPART_FORM_DATA))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        Goal result = objectMapper.readValue(res, Goal.class);
        verifyEntities(demoEntity.getUsefulLinks(), result.getUsefulLinks());
        verifyEntities(demoEntity.getTasks(), result.getTasks());
        demoEntity.setTasks(result.getTasks());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        // upload file
        mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/goals/1")
                                                  .file(FILE1)
                                                  .file(FILE2)
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                              .params(getGoalParams(demoEntity))
                                              .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.documents", Matchers.hasSize(2)));
        verifyDocuments(2);
        mockMvc.perform(MockMvcRequestBuilders.get("/goals/1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.documents", Matchers.hasSize(2)));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/goals/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/goals/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/goals/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<Goal> result = readSearchResult(entities, Goal.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/goals?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities));
        SearchResult<Goal> result1 = getSearchResult
                ("/goals?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", Goal
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(Goal::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(Goal::getId)
                       .toArray());
        SearchResult<Goal> result2 = getSearchResult
                ("/goals?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", Goal
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(Goal::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(Goal::getId)
                       .toArray());

        SearchResult<Goal> result3 = getSearchResult
                ("/goals?pageNumber=2&pageSize=2&sortColumn=subject&sortOrder=DESC",
                        Goal.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(Goal::getSubject)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(Goal::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(Goal::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/goals?institutionalProgramId=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/goals?description=description3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/goals?subject=subject3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/goals?pageNumber=0&pageSize=2&sortColumn=subject&sortOrder=DESC&institutionalProgramId=1" +
                        "&description=description1&subject=subject1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }
}
