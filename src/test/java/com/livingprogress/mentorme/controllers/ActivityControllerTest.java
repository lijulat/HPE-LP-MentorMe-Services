package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.Activity;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.SearchResult;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>ActivityController</code>
 */
public class ActivityControllerTest extends BaseTest {
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
        sample = readFile("activity1.json");
        demo = readFile("demo-activity.json");
        entities = readFile("activities.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/activities/1")
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
        Activity demoEntity = objectMapper.readValue(demo, Activity.class);
        assertNull(demoEntity.getCreatedOn());
        assertNull(demoEntity.getLastModifiedOn());
        assertEquals(0, demoEntity.getCreatedBy());
        assertEquals(0, demoEntity.getLastModifiedBy());
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.post("/activities")
                                                               .header(AUTH_HEADER_NAME, systemAdminToken)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(demo))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").isNumber())
                                .andExpect(jsonPath("$.createdOn").exists())
                                .andExpect(jsonPath("$.createdBy").isNumber())
                                .andExpect(jsonPath("$.lastModifiedOn").exists())
                                .andExpect(jsonPath("$.lastModifiedBy").isNumber())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
        Activity result = objectMapper.readValue(res, Activity.class);
        assertNotNull(result.getCreatedOn());
        assertEquals(1, result.getCreatedBy());
        assertEquals(1, result.getLastModifiedBy());
        demoEntity.setId(result.getId());
        demoEntity.setCreatedBy(result.getCreatedBy());
        demoEntity.setCreatedOn(result.getCreatedOn());
        demoEntity.setLastModifiedBy(result.getLastModifiedBy());
        demoEntity.setLastModifiedOn(result.getLastModifiedOn());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
    }

    /**
     * Test update method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void update() throws Exception {
        Activity demoEntity = objectMapper.readValue(demo, Activity.class);
        demoEntity.setId(1);
        // try to update created on/by
        demoEntity.setCreatedOn(sampleFutureDate);
        demoEntity.setLastModifiedOn(sampleFutureDate);
        demoEntity.setCreatedBy(6);
        demoEntity.setLastModifiedBy(6);
        String json = objectMapper.writeValueAsString(demoEntity);
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.put("/activities/1")
                                                            .header(AUTH_HEADER_NAME, systemAdminToken)
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .content(json))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        Activity result = objectMapper.readValue(res, Activity.class);
        // will not update created on/by during updating
        assertNotEquals(sampleFutureDate, result.getCreatedOn());
        demoEntity.setCreatedOn(result.getCreatedOn());
        assertNotEquals(sampleFutureDate, result.getLastModifiedOn());
        demoEntity.setLastModifiedOn(result.getLastModifiedOn());
        assertNotEquals(6, result.getCreatedBy());
        demoEntity.setCreatedBy(result.getCreatedBy());
        assertNotEquals(6, result.getLastModifiedBy());
        demoEntity.setLastModifiedBy(result.getLastModifiedBy());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
    }

    /**
     * Test delete method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/activities/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/activities/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/activities/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<Activity> result = readSearchResult(entities, Activity.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities));
        // default to use id as sort column
        SearchResult<Activity> result1 = getSearchResult
                ("/activities?pageNumber=1&pageSize=2&sortOrder=ASC", Activity
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(Activity::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(Activity::getId)
                       .toArray());
        SearchResult<Activity> result2 = getSearchResult
                ("/activities?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", Activity
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(Activity::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(Activity::getId)
                       .toArray());

        SearchResult<Activity> result3 = getSearchResult
                ("/activities?pageNumber=2&pageSize=2&sortColumn=description&sortOrder=DESC",
                        Activity.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(Activity::getDescription)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(Activity::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(Activity::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?institutionalProgramId=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?description=description3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?startDate=2016/12/06")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(6));
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?endDate=2016/12/01")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?menteeId=10")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(2));
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?mentorId=6")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/activities?global=true")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(3))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(3)));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/activities?activityTypes[0]=TASK_UPDATED&activityTypes[1]=TASK_CREATED")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(2))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(2)));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/activities?pageNumber=0&pageSize=2&sortColumn=description&sortOrder=DESC&institutionalProgramId=3" +
                        "&description=description3&startDate=2016/12/03&endDate=2016/12/31&menteeId=11&mentorId=6" +
                        "&global=true&activityTypes[0]=TASK_UPDATED&activityTypes[1]=TASK_CREATED")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
    }
}
