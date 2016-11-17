package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.ActivityType;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.MenteeMentorTask;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>MenteeMentorTaskController</code>
 */
public class MenteeMentorTaskControllerTest extends BaseTest {
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
        sample = readFile("menteeMentorTask1.json");
        demo = readFile("demo-menteeMentorTask.json");
        entities = readFile("menteeMentorTasks.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks/1")
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
        long mentorId = 6L;
        MenteeMentorTask demoEntity = objectMapper.readValue(demo, MenteeMentorTask.class);
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.post("/menteeMentorTasks")
                                                               .with(httpBasic("test" + mentorId, "password"))
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(demo))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").isNumber())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
        MenteeMentorTask result = objectMapper.readValue(res, MenteeMentorTask.class);
        long newId = result.getId();
        demoEntity.setId(newId);
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        MenteeMentorGoal goal = entityManager.find(MenteeMentorGoal.class, result.getMenteeMentorGoalId());
        MenteeMentorProgram program = goal.getMenteeMentorProgram();
        verifyActivity(ActivityType.TASK_CREATED, newId,
                CustomMessageSource.getMessage("menteeMentorTask.created.description"),
                program.getInstitutionalProgram()
                       .getId(),
                program.getMentee()
                       .getId(),
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
        long mentorId = 6L;
        MenteeMentorTask demoEntity = objectMapper.readValue(demo, MenteeMentorTask.class);
        demoEntity.setId(1);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.put("/menteeMentorTasks/1")
                                                               .with(httpBasic("test" + mentorId, "password"))
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(json))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
        MenteeMentorTask result = objectMapper.readValue(res, MenteeMentorTask.class);
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        MenteeMentorGoal goal = entityManager.find(MenteeMentorGoal.class, result.getMenteeMentorGoalId());
        MenteeMentorProgram program = goal.getMenteeMentorProgram();
        verifyActivity(ActivityType.TASK_UPDATED, 1,
                CustomMessageSource.getMessage("menteeMentorTask.updated.description"),
                program.getInstitutionalProgram()
                       .getId(),
                program.getMentee()
                       .getId(),
                mentorId,
                false);
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorTasks/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorTasks/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<MenteeMentorTask> result = readSearchResult(entities, MenteeMentorTask.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities));
        SearchResult<MenteeMentorTask> result1 = getSearchResult
                ("/menteeMentorTasks?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", MenteeMentorTask
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorTask::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(MenteeMentorTask::getId)
                       .toArray());
        SearchResult<MenteeMentorTask> result2 = getSearchResult
                ("/menteeMentorTasks?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", MenteeMentorTask
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorTask::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(MenteeMentorTask::getId)
                       .toArray());

        SearchResult<MenteeMentorTask> result3 = getSearchResult
                ("/menteeMentorTasks?pageNumber=2&pageSize=2&sortColumn=menteeMentorGoalId&sortOrder=DESC",
                        MenteeMentorTask.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(MenteeMentorTask::getMenteeMentorGoalId)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(MenteeMentorTask::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(MenteeMentorTask::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?menteeMentorProgramId=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?menteeId=11")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?completed=true")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?mentorAssignment=false")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?menteeAssignment=false")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(3))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(3)));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?menteeMentorGoalId=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?startDate=2016/10/06")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(6));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?endDate=2016/10/26")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(6));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorTasks?completedOn=2016/10/31")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/menteeMentorTasks?pageNumber=0&pageSize=2&sortColumn=completedOn&sortOrder=DESC" +
                        "&menteeMentorProgramId=1&menteeId=4&completed=true" +
                        "&mentorAssignment=true&menteeAssignment=true&menteeMentorGoalId=1" +
                        "&startDate=2016/10/01&endDate=2016/10/31&completedOn=2016/10/31")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }
}
