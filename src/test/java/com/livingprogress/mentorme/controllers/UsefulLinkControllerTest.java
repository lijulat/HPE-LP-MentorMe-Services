package com.livingprogress.mentorme.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.UsefulLink;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.utils.EntityTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The test cases for <code>UsefulLinkController</code>
 */
public class UsefulLinkControllerTest extends BaseTest {
    
    /**
     * The demo MenteeMentorProgram entity json.
     */
    private static String demoMenteeMentorProgram;
    
    /**
     * The demo MenteeMentorGoal entity json.
     */
    private static String demoMenteeMentorGoal;
    
    /**
     * Useful link template
     */
    private static String LINK_TEMPLATE = "{ \"title\": \"test-title%s\", \"address\": \"test-address%s\" }";
    
    /**
     * Read related json.
     *
     * @throws Exception throws if any error happens.
     */
    @BeforeClass
    public static void setupClass() throws Exception {
        demoMenteeMentorProgram = readFile("demo-menteeMentorProgram.json");
        demoMenteeMentorGoal = readFile("demo-menteeMentorGoal.json");
    }

    /**
     * Test create/get/delete of MenteeMentorProgram usefulLinks
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void testMenteeMentorProgramUsefulLinks() throws Exception {        
        // create program
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/menteeMentorPrograms")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demoMenteeMentorProgram))
                            .andExpect(status().isCreated())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorProgram program = objectMapper.readValue(res, MenteeMentorProgram.class);
        
        // the program has no usefulLinks initially       
        List<UsefulLink> links = getUsefulLinks(EntityTypes.MENTEE_MENTOR_PROGRAM, program.getId());
        assertEquals(0, links.size());
        
        // add two links
        for (int i = 0; i < 2; ++i) {
            mockAuthMvc.perform(MockMvcRequestBuilders.post("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/" + program.getId())                                                  
                                                      .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .content(String.format(LINK_TEMPLATE, i + 1, i + 1)))
                        .andExpect(status().isOk());
        }
        
        
        // get the usefulLinks
        links = getUsefulLinks(EntityTypes.MENTEE_MENTOR_PROGRAM, program.getId());
        verifyUsefulLinks(links, 2);
        
        // delete usefulLinks
        for (UsefulLink link : links) {
            mockAuthMvc.perform(MockMvcRequestBuilders.delete("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/" + program.getId() + "/link/" + link.getId())
                                                      .header(AUTH_HEADER_NAME, institutionAdminToken))
                       .andExpect(status().isOk());
        }
        
        // get usefulLinks again
        links = getUsefulLinks(EntityTypes.MENTEE_MENTOR_PROGRAM, program.getId());
        assertEquals(0, links.size());        
    }
    
    /**
     * Test create/get/delete of MenteeMentorGoal usefulLinks
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void testMenteeMentorGoalUsefulLinks() throws Exception {       
        // create goal
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.post("/menteeMentorGoals")
                                                               .header(AUTH_HEADER_NAME, mentorToken)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(demoMenteeMentorGoal))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
        
        MenteeMentorGoal goal = objectMapper.readValue(res, MenteeMentorGoal.class);
        
        // the program has no usefulLinks initially        
        // get the usefulLinks        
        List<UsefulLink> links = getUsefulLinks(EntityTypes.MENTEE_MENTOR_GOAL, goal.getId());
        assertEquals(0, links.size());
        
        // add two links
        for (int i = 0; i < 2; ++i) {
            mockAuthMvc.perform(MockMvcRequestBuilders.post("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_GOAL + "/" + goal.getId())                                                  
                                                      .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .content(String.format(LINK_TEMPLATE, i + 1, i + 1)))
                        .andExpect(status().isOk());
        }
        
        
        // get the usefulLinks        
        links = getUsefulLinks(EntityTypes.MENTEE_MENTOR_GOAL, goal.getId());        
        verifyUsefulLinks(links, 2);
        
        // delete usefulLinks
        for (UsefulLink link : links) {
            mockAuthMvc.perform(MockMvcRequestBuilders.delete("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_GOAL + "/" + goal.getId() + "/link/" + link.getId())
                                                      .header(AUTH_HEADER_NAME, institutionAdminToken))
                       .andExpect(status().isOk());
        }
        
        // get usefulLinks again        
        links = getUsefulLinks(EntityTypes.MENTEE_MENTOR_GOAL, goal.getId());
        assertEquals(0, links.size());        
    }
    
    /**
     * Verify the links are correct
     * 
     * @param links the links
     * @param expectedCount the expected number of links
     */
    private void verifyUsefulLinks(List<UsefulLink> links, int expectedCount) {
        assertEquals(expectedCount, links.size());
        for (UsefulLink link : links) {
            assertTrue(link.getTitle().startsWith("test-title"));
            assertTrue(link.getAddress().startsWith("test-address"));
        }
    }
    
    /**
     * Get usefulLinks from the api
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the usefulLinks
     * @throws Exception if any error occurs
     */
    private List<UsefulLink> getUsefulLinks(String entityType, long entityId) throws Exception {
        // get the usefulLinks
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.get("/usefulLinks/" + entityType + "/" + entityId)
                                                               .header(AUTH_HEADER_NAME, institutionAdminToken))
                         .andExpect(status().isOk())
                         .andReturn()
                         .getResponse()
                         .getContentAsString();
        
        return objectMapper.readValue(res, new TypeReference<List<UsefulLink>>() {});
    }
    
    /**
     * Failure test for the create operation
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void create() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.post("/usefulLinks/unknownType/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(String.format(LINK_TEMPLATE, 1, 1)))
                   .andExpect(status().isInternalServerError());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.post("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(String.format(LINK_TEMPLATE, 1, 1)))
                   .andExpect(status().isNotFound());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.post("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(String.format(LINK_TEMPLATE, 1, 1)))
                    .andExpect(status().isNotFound());
        
    }
    
    /**
     * Failure test for the get operation
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void get() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/usefulLinks/unknown/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isInternalServerError());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isNotFound());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_GOAL + "/999")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isNotFound());
    }
    
    /**
     * Failure test for the delete operation
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void delete() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.delete("/usefulLinks/unknown/1/link/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isInternalServerError());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.delete("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999/link/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isNotFound());

        
        mockAuthMvc.perform(MockMvcRequestBuilders.delete("/usefulLinks/" + EntityTypes.MENTEE_MENTOR_GOAL + "/999/link/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isNotFound());
    }
        
}
