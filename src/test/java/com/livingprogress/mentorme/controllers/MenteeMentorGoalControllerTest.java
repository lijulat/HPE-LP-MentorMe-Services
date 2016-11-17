package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.ActivityType;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * The test cases for <code>MenteeMentorGoalController</code>
 */
public class MenteeMentorGoalControllerTest extends BaseTest {
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
        sample = readFile("menteeMentorGoal1.json");
        demo = readFile("demo-menteeMentorGoal.json");
        entities = readFile("menteeMentorGoals.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorGoals/1")
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
        MenteeMentorGoal demoEntity = objectMapper.readValue(demo, MenteeMentorGoal.class);
        checkEntities(demoEntity.getTasks());
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.post("/menteeMentorGoals")
                                                               .header(AUTH_HEADER_NAME, mentorToken)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(demo))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorGoal result = objectMapper.readValue(res, MenteeMentorGoal.class);
        verifyEntities(demoEntity.getTasks(), result.getTasks());
        long newId = result.getId();
        demoEntity.setId(newId);
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        MenteeMentorProgram program = entityManager.find(MenteeMentorProgram.class, result.getMenteeMentorProgramId());
        verifyActivity(ActivityType.GOAL_CREATED, newId,
                CustomMessageSource.getMessage("menteeMentorGoal.created.description"),
                program.getInstitutionalProgram().getId(),
                program.getMentee().getId(),
                3L,
                false);
        // test null fields with basic auth
        demoEntity.setId(0);
        demoEntity.setGoal(null);
        demoEntity.setTasks(null);
        long mentorId = 6L;
        res = mockAuthMvc.perform(MockMvcRequestBuilders.post("/menteeMentorGoals")
                                                  .with(httpBasic("test" + mentorId, "password"))
                                                          .contentType(MediaType.APPLICATION_JSON)
                                                          .content(objectMapper.writeValueAsString(demoEntity)))
                                                  .andExpect(status().isCreated())
                                                  .andExpect(jsonPath("$.id").isNumber())
                                                  .andExpect(jsonPath("$.goal").doesNotExist())
                                                   .andExpect(jsonPath("$.tasks", Matchers.hasSize(0)))
                         .andReturn()
                         .getResponse()
                         .getContentAsString();
        result = objectMapper.readValue(res, MenteeMentorGoal.class);
        newId = result.getId();
        assertNotEquals(mentorId, program.getMentor().getId());
        verifyActivity(ActivityType.GOAL_CREATED, newId,
                CustomMessageSource.getMessage("menteeMentorGoal.created.description"),
                program.getInstitutionalProgram().getId(),
                program.getMentee().getId(),
                mentorId,
                false);
    }

    /**
     * Test update method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void update() throws Exception {
        MenteeMentorGoal demoEntity = objectMapper.readValue(demo, MenteeMentorGoal.class);
        demoEntity.setId(1);
        checkEntities(demoEntity.getTasks());
        demoEntity.getTasks()
                  .get(0)
                  .setId(1L);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.put("/menteeMentorGoals/1")
                                                               .header(AUTH_HEADER_NAME, mentorToken)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                                .content(json))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.tasks", Matchers.hasSize(1)))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorGoal result = objectMapper.readValue(res, MenteeMentorGoal.class);
        // same id entity just updates
        assertEquals(1, result.getTasks()
                              .get(0)
                              .getId());
        verifyEntities(demoEntity.getTasks(), result.getTasks());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        MenteeMentorProgram program = entityManager.find(MenteeMentorProgram.class, result.getMenteeMentorProgramId());
        verifyActivity(ActivityType.GOAL_UPDATED, 1,
                CustomMessageSource.getMessage("menteeMentorGoal.updated.description"),
                program.getInstitutionalProgram().getId(),
                program.getMentee().getId(),
                3L,
                false);
        int id = 3;
        demoEntity.setMenteeMentorProgramId(id);
        mockAuthMvc.perform(MockMvcRequestBuilders.put("/menteeMentorGoals/1")
                                                  .header(AUTH_HEADER_NAME, mentorToken)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.menteeMentorProgramId").value(id));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorGoals/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorGoals/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorGoals/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<MenteeMentorGoal> result = readSearchResult(entities, MenteeMentorGoal.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorGoals?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities));
        SearchResult<MenteeMentorGoal> result1 = getSearchResult
                ("/menteeMentorGoals?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", MenteeMentorGoal
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorGoal::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(MenteeMentorGoal::getId)
                       .toArray());
        SearchResult<MenteeMentorGoal> result2 = getSearchResult
                ("/menteeMentorGoals?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", MenteeMentorGoal
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorGoal::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(MenteeMentorGoal::getId)
                       .toArray());

        SearchResult<MenteeMentorGoal> result3 = getSearchResult
                ("/menteeMentorGoals?pageNumber=2&pageSize=2&sortColumn=menteeMentorProgramId&sortOrder=DESC",
                        MenteeMentorGoal.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(MenteeMentorGoal::getMenteeMentorProgramId)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(MenteeMentorGoal::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(MenteeMentorGoal::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorGoals?menteeMentorProgramId=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorGoals?menteeId=11")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorGoals?completed=true")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/menteeMentorGoals?pageNumber=0&pageSize=2&sortColumn=menteeMentorProgramId&sortOrder=DESC&menteeMentorProgramId=1" +
                        "&menteeId=4&completed=true")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }
}
