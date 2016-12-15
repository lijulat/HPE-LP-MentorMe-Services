package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.Goal;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.MenteeFeedback;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.MenteeMentorResponsibility;
import com.livingprogress.mentorme.entities.MentorFeedback;
import com.livingprogress.mentorme.entities.SearchResult;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>MenteeMentorProgramController</code>
 */
public class MenteeMentorProgramControllerTest extends BaseTest {
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
     * The default institutional program id
     */
    @Value("${menteeMentorProgram.defaultInstitutionalProgramId}")
    private long defaultInstitutionalProgramId;

    /**
     * Read related json.
     *
     * @throws Exception throws if any error happens.
     */
    @BeforeClass
    public static void setupClass() throws Exception {
        sample = readFile("menteeMentorProgram1.json");
        demo = readFile("demo-menteeMentorProgram.json");
        entities = readFile("menteeMentorPrograms.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms/1")
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
        MenteeMentorProgram demoEntity = objectMapper.readValue(demo, MenteeMentorProgram.class);
        checkEntities(demoEntity.getResponsibilities());
        checkEntities(demoEntity.getGoals());
        demoEntity.getGoals().forEach(g->checkEntities(g.getTasks()));
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/menteeMentorPrograms")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demo))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorProgram result = objectMapper.readValue(res, MenteeMentorProgram.class);
        demoEntity.setId(result.getId());
        verifyEntities(demoEntity.getResponsibilities(), result.getResponsibilities());
        verifyEntities(demoEntity.getGoals(), result.getGoals());
        IntStream.range(0, demoEntity.getGoals().size()).forEach(idx -> {
            verifyEntities(demoEntity.getGoals()
                                     .get(idx)
                                     .getTasks(), result.getGoals()
                                                        .get(idx).getTasks());
        });
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        // test null institutional program
        demoEntity.setId(0);
        demoEntity.setInstitutionalProgram(null);
        demoEntity.setGoals(null);
        demoEntity.setResponsibilities(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/menteeMentorPrograms")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.goals", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.responsibilities", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.institutionalProgram.id").value((int)defaultInstitutionalProgramId));

    }

    /**
     * Test update method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void update() throws Exception {
        MenteeMentorProgram demoEntity = objectMapper.readValue(demo, MenteeMentorProgram.class);
        checkEntities(demoEntity.getResponsibilities());
        checkEntities(demoEntity.getGoals());
        demoEntity.getResponsibilities()
                  .get(0)
                  .setId(1L);
        demoEntity.getGoals()
                  .get(0)
                  .setId(1L);
        demoEntity.setId(1);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/1")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(json))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorProgram result = objectMapper.readValue(res, MenteeMentorProgram.class);
        // same id entity just updates
        assertEquals(1, result.getResponsibilities()
                              .get(0)
                              .getId());
        assertEquals(1, result.getGoals()
                              .get(0)
                              .getId());
        verifyEntities(demoEntity.getResponsibilities(), result.getResponsibilities());
        verifyEntities(demoEntity.getGoals(), result.getGoals());
        demoEntity.setInstitutionalProgram(result.getInstitutionalProgram());
        demoEntity.setGoals(result.getGoals());
        demoEntity.setUsefulLinks(result.getUsefulLinks());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        // test nested properties
        demoEntity.setResponsibilities(null);
        demoEntity.setGoals(null);
        mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/1")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.responsibilities", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.goals", Matchers.hasSize(0)));
        // second null updates
        mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/1")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.responsibilities", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.goals", Matchers.hasSize(0)));
        // update with new nested values
        int count = 3;
        demoEntity.setGoals(new ArrayList<>());
        demoEntity.setResponsibilities(new ArrayList<>());
        IntStream.range(0, count)
                 .forEach(idx -> {
                     MenteeMentorResponsibility data = new MenteeMentorResponsibility();
                     data.setTitle("title" + idx);
                     data.setDate(new Date());
                     data.setMenteeResponsibility(idx % 2 == 0);
                     data.setMentorResponsibility(idx % 3 == 0);
                     data.setNumber(idx);
                     data.setResponsibilityId(idx + 1);
                     demoEntity.getResponsibilities()
                               .add(data);
                     MenteeMentorGoal goal = new MenteeMentorGoal();
                     goal.setGoal(new Goal());
                     goal.getGoal().setId(idx + 1);
                     goal.setCompleted(idx % 2 == 0);
                     demoEntity.getGoals().add(goal);
                 });
        mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/1")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.responsibilities", Matchers.hasSize(count)))
               .andExpect(jsonPath("$.goals", Matchers.hasSize(count)));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorPrograms/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/menteeMentorPrograms/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<MenteeMentorProgram> result = readSearchResult(entities, MenteeMentorProgram.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities));
        SearchResult<MenteeMentorProgram> result1 = getSearchResult
                ("/menteeMentorPrograms?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", MenteeMentorProgram
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorProgram::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(MenteeMentorProgram::getId)
                       .toArray());
        SearchResult<MenteeMentorProgram> result2 = getSearchResult
                ("/menteeMentorPrograms?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", MenteeMentorProgram
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorProgram::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(MenteeMentorProgram::getId)
                       .toArray());

        SearchResult<MenteeMentorProgram> result3 = getSearchResult
                ("/menteeMentorPrograms?pageNumber=1&pageSize=2&sortColumn=requestStatus&sortOrder=DESC",
                        MenteeMentorProgram.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(MenteeMentorProgram::getRequestStatus)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(MenteeMentorProgram::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(MenteeMentorProgram::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?mentorId=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(2));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?menteeId=11")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?institutionalProgramId=4")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(4));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?startDate=2016/12/06")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(6));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?endDate=2016/12/26")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(6));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?completed=true")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms?requestStatus=REJECTED")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(2));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/menteeMentorPrograms?pageNumber=0&pageSize=2&sortColumn=startDate&sortOrder=DESC&mentorId=3" +
                        "&menteeId=4&institutionalProgramId=1&startDate=2016/12/01&endDate=2016/12/31&completed=true" +
                        "&requestStatus=APPROVED")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }

    /**
     * Test submitMenteeFeedback method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void submitMenteeFeedback() throws Exception {
        MenteeFeedback data = new MenteeFeedback();
        data.setCreatedOn(new Date());
        data.setMentorScore(3);
        data.setComment("test mentee feedback");
        mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/6/menteeFeedback")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(data)))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms/6"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.menteeFeedback.comment").value(data.getComment()))
               .andExpect(jsonPath("$.menteeFeedback.mentorScore").value(data.getMentorScore()));
        //test null score
        data.setMentorScore(null);
        mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/6/menteeFeedback")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(data)))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms/6"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.menteeFeedback.comment").value(data.getComment()))
               .andExpect(jsonPath("$.menteeFeedback.mentorScore").doesNotExist());
    }

    /**
     * Test submitMentorFeedback method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void submitMentorFeedback() throws Exception {
        MentorFeedback data = new MentorFeedback();
        data.setMenteeScore(2);
        data.setCreatedOn(new Date());
        data.setComment("test mentor feedback");
        mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/6/mentorFeedback")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(data)))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms/6"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mentorFeedback.comment").value(data.getComment()))
               .andExpect(jsonPath("$.mentorFeedback.menteeScore").value(data.getMenteeScore()));
        // check null score
        data.setMenteeScore(null);
        mockMvc.perform(MockMvcRequestBuilders.put("/menteeMentorPrograms/6/mentorFeedback")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(data)))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/menteeMentorPrograms/6"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.mentorFeedback.comment").value(data.getComment()))
               .andExpect(jsonPath("$.mentorFeedback.menteeScore").doesNotExist());
    }
}
