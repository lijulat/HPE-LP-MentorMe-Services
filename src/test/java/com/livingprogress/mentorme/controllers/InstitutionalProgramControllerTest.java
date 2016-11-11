package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.Goal;
import com.livingprogress.mentorme.entities.IdentifiableEntity;
import com.livingprogress.mentorme.entities.InstitutionalProgram;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test cases for <code>InstitutionalProgramController</code>
 */
public class InstitutionalProgramControllerTest extends BaseTest {
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
        sample = readFile("institutionalProgram1.json");
        demo = readFile("demo-institutionalProgram.json");
        entities = readFile("institutionalPrograms.json");
    }

    /**
     * Test get method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void get() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/1")
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
        InstitutionalProgram demoEntity = objectMapper.readValue(demo, InstitutionalProgram.class);
        assertNull(demoEntity.getCreatedOn());
        checkEntities(demoEntity.getUsefulLinks());
        checkEntities(demoEntity.getResponsibilities());
        checkEntities(demoEntity.getGoals());
        demoEntity.getGoals().forEach(g->{
            checkEntities(g.getTasks());
            checkEntities(g.getUsefulLinks());
            checkEntity(g.getCustomData());
            g.getTasks().forEach(t->checkEntity(t.getCustomData()));
        });
        // create without documents
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/institutionalPrograms")
                                                           .params(getInstitutionalProgramParams(demoEntity))
                                                           .contentType(MediaType.MULTIPART_FORM_DATA))
                            .andDo(print())
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.id").isNumber())
                            .andExpect(jsonPath("$.createdOn").exists())
                            .andExpect(jsonPath("$.documents", Matchers.hasSize(0)))
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        final InstitutionalProgram result = objectMapper.readValue(res, InstitutionalProgram.class);
        demoEntity.setId(result.getId());
        demoEntity.setCreatedOn(result.getCreatedOn());
        verifyEntities(demoEntity.getUsefulLinks(), result.getUsefulLinks());
        verifyEntities(demoEntity.getResponsibilities(), result.getResponsibilities());
        verifyEntities(demoEntity.getGoals(), result.getGoals());
        IntStream.range(0, demoEntity.getGoals()
                                     .size()).forEach(idx -> {
            Goal goal1 = demoEntity.getGoals().get(idx);
            Goal goal2 = result.getGoals().get(idx);
            verifyEntity(goal1.getCustomData(), goal2.getCustomData());
            verifyEntities(goal1.getTasks(), goal2.getTasks());
            verifyEntities(goal1.getUsefulLinks(), goal2.getUsefulLinks());
            IntStream.range(0, goal1.getTasks().size()).forEach(idy -> {
                verifyEntity(goal1.getTasks().get(idy).getCustomData(), goal2.getTasks().get(idy).getCustomData());
            });
        });
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        verifyDocuments(0);
        // upload file
        demoEntity.setId(0);
        demoEntity.setCreatedOn(null);
        demoEntity.setDocuments(null);
        res = mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/institutionalPrograms")
                                                    .file(FILE1)
                                                    .file(FILE2)
                                                    .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                    .params(getInstitutionalProgramParams(demoEntity))
                                                    .contentType(MediaType.MULTIPART_FORM_DATA))
                     .andExpect(status().isCreated())
                     .andExpect(jsonPath("$.id").isNumber())
                     .andExpect(jsonPath("$.createdOn").exists())
                     .andExpect(jsonPath("$.documents", Matchers.hasSize(2)))
                     .andReturn()
                     .getResponse()
                     .getContentAsString();
        final InstitutionalProgram result2 = objectMapper.readValue(res, InstitutionalProgram.class);
        verifyDocuments(2);
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/" + result2.getId())
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.documents", Matchers.hasSize(2)));
        // test null nested properties
        demoEntity.setId(0);
        demoEntity.setDocuments(null);
        demoEntity.setGoals(null);
        demoEntity.setResponsibilities(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/institutionalPrograms")
                                              .params(getInstitutionalProgramParams(demoEntity))
                                              .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.createdOn").exists())
               .andExpect(jsonPath("$.documents", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.goals", Matchers.hasSize(0)))
               .andExpect(jsonPath("$.responsibilities", Matchers.hasSize(0)));
    }

    /**
     * Test update method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void update() throws Exception {
        InstitutionalProgram demoEntity = objectMapper.readValue(demo, InstitutionalProgram.class);
        checkEntities(demoEntity.getUsefulLinks());
        checkEntities(demoEntity.getResponsibilities());
        checkEntities(demoEntity.getGoals());
        demoEntity.getGoals().forEach(g->{
            checkEntities(g.getTasks());
            checkEntities(g.getUsefulLinks());
            checkEntity(g.getCustomData());
            g.getTasks().forEach(t->checkEntity(t.getCustomData()));
        });
        // try to update created on
        demoEntity.setCreatedOn(sampleFutureDate);
        demoEntity.setId(1);
        // update without documents
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/institutionalPrograms/1")
                                                           .params(getInstitutionalProgramParams(demoEntity))
                                                           .contentType(MediaType.MULTIPART_FORM_DATA))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        InstitutionalProgram result = objectMapper.readValue(res, InstitutionalProgram.class);
        // will not update created on during updating
        assertNotEquals(sampleFutureDate, result.getCreatedOn());
        demoEntity.setCreatedOn(result.getCreatedOn());
        verifyEntities(demoEntity.getUsefulLinks(), result.getUsefulLinks());
        verifyEntities(demoEntity.getResponsibilities(), result.getResponsibilities());
        verifyEntities(demoEntity.getGoals(), result.getGoals());
        IntStream.range(0, demoEntity.getGoals()
                                 .size()).forEach(idx -> {
            Goal goal1 = demoEntity.getGoals().get(idx);
            Goal goal2 = result.getGoals().get(idx);
            verifyEntity(goal1.getCustomData(), goal2.getCustomData());
            verifyEntities(goal1.getTasks(), goal2.getTasks());
            verifyEntities(goal1.getUsefulLinks(), goal2.getUsefulLinks());
            IntStream.range(0, goal1.getTasks().size()).forEach(idy -> {
                verifyEntity(goal1.getTasks()
                                  .get(idy)
                                  .getCustomData(), goal2.getTasks()
                                                         .get(idy)
                                                         .getCustomData());
            });
        });
        demoEntity.setInstitution(result.getInstitution());
        demoEntity.setGoals(result.getGoals());
        assertEquals(objectMapper.writeValueAsString(demoEntity), objectMapper.writeValueAsString(result));
        // upload file
        mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/institutionalPrograms/1")
                                                  .file(FILE1)
                                              .file(FILE2)
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .params(getInstitutionalProgramParams(demoEntity))
                                              .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").isNumber())
               .andExpect(jsonPath("$.createdOn").exists())
               .andExpect(jsonPath("$.documents", Matchers.hasSize(2)));
        verifyDocuments(2);
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/1")
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
        mockMvc.perform(MockMvcRequestBuilders.delete("/institutionalPrograms/1"))
               .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/1"))
               .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/institutionalPrograms/1"))
               .andExpect(status().isNotFound());
    }

    /**
     * Test search method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void search() throws Exception {
        SearchResult<InstitutionalProgram> result = readSearchResult(entities, InstitutionalProgram.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms?sortColumn=id&sortOrder=ASC")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(entities, true));
        SearchResult<InstitutionalProgram> result1 = getSearchResult
                ("/institutionalPrograms?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=ASC", InstitutionalProgram
                        .class);
        assertEquals(result.getTotal(), result1.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result1.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .skip(2)
                                .limit(2)
                                .map(InstitutionalProgram::getId)
                                .toArray(),
                result1.getEntities()
                       .stream()
                       .map(InstitutionalProgram::getId)
                       .toArray());
        SearchResult<InstitutionalProgram> result2 = getSearchResult
                ("/institutionalPrograms?pageNumber=1&pageSize=2&sortColumn=id&sortOrder=DESC", InstitutionalProgram
                        .class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(IdentifiableEntity::getId)
                                                  .reversed())
                                .skip(2)
                                .limit(2)
                                .map(InstitutionalProgram::getId)
                                .toArray(),
                result2.getEntities()
                       .stream()
                       .map(InstitutionalProgram::getId)
                       .toArray());

        SearchResult<InstitutionalProgram> result3 = getSearchResult
                ("/institutionalPrograms?pageNumber=2&pageSize=2&sortColumn=programName&sortOrder=DESC",
                        InstitutionalProgram.class);
        assertEquals(result.getTotal(), result2.getTotal());
        assertEquals(getTotalPages(result.getTotal(), 2), result2.getTotalPages());
        assertArrayEquals(result.getEntities()
                                .stream()
                                .sorted(Comparator.comparing(InstitutionalProgram::getProgramName)
                                                  .reversed())
                                .skip(4)
                                .limit(2)
                                .map(InstitutionalProgram::getId)
                                .toArray(),
                result3.getEntities()
                       .stream()
                       .map(InstitutionalProgram::getId)
                       .toArray());
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms?programName=programName5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(5));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms?institutionId=3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(3));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms?programCategory.id=3")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(2))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(2)));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms?minDurationInDays=20")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms?maxDurationInDays=5")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(6));
        mockMvc.perform(MockMvcRequestBuilders.get
                ("/institutionalPrograms?pageNumber=0&pageSize=2&sortColumn=programName&sortOrder=DESC&programName" +
                        "=programName1&institutionId=1&programCategory.id=1&minDurationInDays=1&maxDurationInDays=100")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.total").value(1))
               .andExpect(jsonPath("$.totalPages").value(1))
               .andExpect(jsonPath("$.entities", Matchers.hasSize(1)))
               .andExpect(jsonPath("$.entities[0].id").value(1));
    }

    /**
     * Test getProgramMentees method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getProgramMentees() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/1/mentees")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("getProgramMentees.json")));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/999/mentees")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    /**
     * Test getProgramMentors method.
     *
     * @throws Exception throws if any error happens.
     */
    @Test
    public void getProgramMentors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/1/mentors")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("getProgramMentors.json")));
        mockMvc.perform(MockMvcRequestBuilders.get("/institutionalPrograms/999/mentors")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
}
