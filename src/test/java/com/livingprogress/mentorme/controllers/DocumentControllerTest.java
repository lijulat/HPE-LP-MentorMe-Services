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
import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.utils.EntityTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The test cases for <code>DocumentController</code>
 */
public class DocumentControllerTest extends BaseTest {
    
    /**
     * The demo MenteeMentorProgram entity json.
     */
    private static String demoMenteeMentorProgram;
    
    /**
     * The demo MenteeMentorGoal entity json.
     */
    private static String demoMenteeMentorGoal;
    
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
     * Test create/get/delete of MenteeMentorProgram documents
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void testMenteeMentorProgramDocuments() throws Exception {        
        // create program
        String res = mockMvc.perform(MockMvcRequestBuilders.post("/menteeMentorPrograms")
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .content(demoMenteeMentorProgram))
                            .andExpect(status().isCreated())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
        MenteeMentorProgram program = objectMapper.readValue(res, MenteeMentorProgram.class);
        
        // the program has no documents initially       
        List<Document> docs = getDocuments(EntityTypes.MENTEE_MENTOR_PROGRAM, program.getId());
        assertEquals(0, docs.size());
        
        // upload two files
        mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/documents/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/" + program.getId())
                                                  .file(FILE1)
                                                  .file(FILE2)
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk());
        
        
        // get the documents
        docs = getDocuments(EntityTypes.MENTEE_MENTOR_PROGRAM, program.getId());
        verifyDocuments(docs, 2);
        
        // delete documents
        for (Document doc : docs) {
            mockAuthMvc.perform(MockMvcRequestBuilders.delete("/documents/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/" + program.getId() + "/document/" + doc.getId())
                                                      .header(AUTH_HEADER_NAME, institutionAdminToken))
                       .andExpect(status().isOk());
        }
        
        // get documents again
        docs = getDocuments(EntityTypes.MENTEE_MENTOR_PROGRAM, program.getId());
        assertEquals(0, docs.size());        
    }
    
    /**
     * Test create/get/delete of MenteeMentorGoal documents
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void testMenteeMentorGoalDocuments() throws Exception {       
        // create goal
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.post("/menteeMentorGoals")
                                                               .header(AUTH_HEADER_NAME, mentorToken)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(demoMenteeMentorGoal))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").isNumber())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
        
        MenteeMentorGoal goal = objectMapper.readValue(res, MenteeMentorGoal.class);
        
        // the program has no documents initially        
        // get the documents        
        List<Document> docs = getDocuments(EntityTypes.MENTEE_MENTOR_GOAL, goal.getId());
        assertEquals(0, docs.size());
        
        // upload two files
        mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/documents/" + EntityTypes.MENTEE_MENTOR_GOAL + "/" + goal.getId())
                                                        .file(FILE1)
                                                        .file(FILE2)
                                                        .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                        .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isOk());
        
        
        // get the documents        
        docs = getDocuments(EntityTypes.MENTEE_MENTOR_GOAL, goal.getId());
        verifyDocuments(docs, 2);
        
        // delete documents
        for (Document doc : docs) {
            mockAuthMvc.perform(MockMvcRequestBuilders.delete("/documents/" + EntityTypes.MENTEE_MENTOR_GOAL + "/" + goal.getId() + "/document/" + doc.getId())
                                                      .header(AUTH_HEADER_NAME, institutionAdminToken))
                       .andExpect(status().isOk());
        }
        
        // get documents again        
        docs = getDocuments(EntityTypes.MENTEE_MENTOR_GOAL, goal.getId());
        assertEquals(0, docs.size());        
    }
    
    /**
     * Verify the returned documents
     * 
     * @param docs the documents to verify
     * @param expectedCount the expected number of documents
     */
    private void verifyDocuments(List<Document> docs, int expectedCount) {
        assertEquals(expectedCount, docs.size());
        
        for (Document doc : docs) {
            assertTrue(doc.getName().startsWith("test"));
            assertTrue(doc.getName().endsWith(".txt"));
        }
    }
    
    
    /**
     * Get documents from the api
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the documents
     * @throws Exception if any error occurs
     */
    private List<Document> getDocuments(String entityType, long entityId) throws Exception {
        // get the documents
        String res = mockAuthMvc.perform(MockMvcRequestBuilders.get("/documents/" + entityType + "/" + entityId)
                                                               .header(AUTH_HEADER_NAME, institutionAdminToken))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
        
        return objectMapper.readValue(res, new TypeReference<List<Document>>() {});
    }
    
    /**
     * Failure test for the create operation
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void create() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/documents/unknownType/1")
                                                  .file(FILE1)
                                                  .file(FILE2)
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isInternalServerError());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/documents/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999")
                                                  .file(FILE1)
                                                  .file(FILE2)
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .contentType(MediaType.MULTIPART_FORM_DATA))
                   .andExpect(status().isNotFound());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.fileUpload("/documents/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999")
                                                  .file(FILE1)
                                                  .file(FILE2)
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken)
                                                  .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isNotFound());
        
    }
    
    /**
     * Failure test for the get operation
     * 
     * @throws Exception if any error occurs
     */
    @Test
    public void get() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/documents/unknown/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isInternalServerError());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/documents/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isNotFound());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/documents/" + EntityTypes.MENTEE_MENTOR_GOAL + "/999")
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
        mockAuthMvc.perform(MockMvcRequestBuilders.delete("/documents/unknown/1/document/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isInternalServerError());
        
        mockAuthMvc.perform(MockMvcRequestBuilders.delete("/documents/" + EntityTypes.MENTEE_MENTOR_PROGRAM + "/999/document/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isNotFound());

        
        mockAuthMvc.perform(MockMvcRequestBuilders.delete("/documents/" + EntityTypes.MENTEE_MENTOR_GOAL + "/999/document/1")
                                                  .header(AUTH_HEADER_NAME, institutionAdminToken))
                   .andExpect(status().isNotFound());
    }
        
}
