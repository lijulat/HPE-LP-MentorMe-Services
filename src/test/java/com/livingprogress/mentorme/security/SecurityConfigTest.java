package com.livingprogress.mentorme.security;

import com.livingprogress.mentorme.BaseTest;
import com.livingprogress.mentorme.entities.NewPassword;
import com.livingprogress.mentorme.entities.User;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Locale;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The test cases for <code>SecurityConfig</code>
 */
public class SecurityConfigTest extends BaseTest {

    /**
     * Test anonymous requests.
     * @throws Exception throws if any error happen
     */
    @Test
    public void anonymousTest() throws Exception {
        // test /lookups/**
        mockAuthMvc.perform(
                MockMvcRequestBuilders.get("/lookups/userRoles")
                                      .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("userRoles.json")));

        // test /users/forgotPassword
        mockAuthMvc.perform(MockMvcRequestBuilders.put("/users/forgotPassword?email=notexist@test.com"))
               .andExpect(status().isNotFound());

        NewPassword entity = new NewPassword();
        entity.setToken("notexist");
        entity.setNewPassword("newPassword");
        // test /users/updatePassword
        mockAuthMvc.perform(MockMvcRequestBuilders.put("/users/updatePassword")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(entity)))
               .andExpect(status().isBadRequest());
    }

    /**
     * Test Unauthorized error without any auth header.
     * @throws Exception throws if any error happen
     */
    @Test
    public void unauthorizedTest1() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
    }

    /**
     * Test Unauthorized error with wrong username.
     * @throws Exception throws if any error happen
     */
    @Test
    public void unauthorizedTest2() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .with(httpBasic("wrong", "password")))
                                              .andExpect(status().isUnauthorized());
    }

    /**
     * Test Unauthorized error with wrong password.
     * @throws Exception throws if any error happen
     */
    @Test
    public void unauthorizedTest3() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .with(httpBasic("test1", "wrong")))
               .andExpect(status().isUnauthorized());
    }

    /**
     * Test Unauthorized error with inactive user.
     * @throws Exception throws if any error happen
     */
    @Test
    public void unauthorizedTest4() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .with(httpBasic("test5", "password")))
               .andExpect(status().isUnauthorized());
    }

    /**
     * Test Unauthorized error with wrong JWT token.
     * @throws Exception throws if any error happen
     */
    @Test
    public void unauthorizedTest5() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, "wrong"))
               .andExpect(status().isUnauthorized());
    }

    /**
     * Test Unauthorized error with expired JWT token.
     * @throws Exception throws if any error happen
     */
    @Test
    public void unauthorizedTest6() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, readFile("expiredToken.txt")))
               .andExpect(status().isUnauthorized());
    }

    /**
     * Test basic auth.
     * @throws Exception throws if any error happen
     */
    @Test
    public void basicTest() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON).with(httpBasic("email1@test.com", "password")))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("user1.json")));
    }

    /**
     * Test system admin JWT token.
     * @throws Exception throws if any error happen
     */
    @Test
    public void sytemAdminTest() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, systemAdminToken))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("user1.json")));
    }

    /**
     * Test institution dmin JWT token.
     * @throws Exception throws if any error happen
     */
    @Test
    public void institutionAdminTest() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/institutions/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, institutionAdminToken))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("institution1.json")));
    }

    /**
     * Test mentor JWT token.
     * @throws Exception throws if any error happen
     */
    @Test
    public void mentorTest() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/mentors/3")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME,  mentorToken))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("mentor3.json")));
    }

    /**
     * Test mentee JWT token.
     * @throws Exception throws if any error happen
     */
    @Test
    public void menteeTest() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/mentees/4")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, menteeToken))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("mentee4.json")));
    }

    /**
     * Test forbidden error with mentor try to access mentee only endpoint.
     * @throws Exception throws if any error happen
     */
    @Test
    public void forbiddenTest1() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.put("/mentees/4")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, mentorToken))
               .andExpect(status().isForbidden());
    }

    /**
     * Test forbidden error with mentor try to access system admin only endpoint.
     * @throws Exception throws if any error happen
     */
    @Test
    public void forbiddenTest2() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, mentorToken))
               .andExpect(status().isForbidden());
    }

    /**
     * Test basic login as system admin.
     * @throws Exception throws if any error happen
     */
    @Test
    public void loginAsSytemAdminTest() throws Exception {
        String json = mockAuthMvc.perform(MockMvcRequestBuilders.post("/login")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .with(httpBasic("email1@test.com", "password")))
               .andExpect(status().isOk())
               .andReturn()
               .getResponse()
               .getContentAsString();
        Map<String, String> token = (Map<String, String>) objectMapper.readValue(json, Map.class);
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/users/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, token.get("token")))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("user1.json")));
    }

    /**
     * Test basic login as institution admin.
     * @throws Exception throws if any error happen
     */
    @Test
    public void loginAsInstitutionAdminTest() throws Exception {
        String json = mockAuthMvc.perform(MockMvcRequestBuilders.post("/login")
                                                            .accept(MediaType.APPLICATION_JSON)
                                                            .with(httpBasic("email2@test.com", "password")))
                             .andExpect(status().isOk())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
        Map<String, String> token = (Map<String, String>) objectMapper.readValue(json, Map.class);
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/institutions/1")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, token.get("token")))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("institution1.json")));
    }

    /**
     * Test basic login as mentor.
     * @throws Exception throws if any error happen
     */
    @Test
    public void loginAsMentorTest() throws Exception {
        String json = mockAuthMvc.perform(MockMvcRequestBuilders.post("/login")
                                                            .accept(MediaType.APPLICATION_JSON)
                                                            .with(httpBasic("email3@test.com", "password")))
                             .andExpect(status().isOk())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
        Map<String, String> token = (Map<String, String>) objectMapper.readValue(json, Map.class);
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/mentors/3")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, token.get("token")))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("mentor3.json")));
    }


    /**
     * Test basic login as mentee.
     * @throws Exception throws if any error happen
     */
    @Test
    public void loginAsMenteeTest() throws Exception {
        String json = mockAuthMvc.perform(MockMvcRequestBuilders.post("/login")
                                                            .accept(MediaType.APPLICATION_JSON)
                                                            .with(httpBasic("email4@test.com", "password")))
                             .andExpect(status().isOk())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();
        Map<String, String> token = (Map<String, String>) objectMapper.readValue(json, Map.class);
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/mentees/4")
                                              .accept(MediaType.APPLICATION_JSON)
                                              .header(AUTH_HEADER_NAME, token.get("token")))
               .andExpect(status().isOk())
               .andExpect(content().json(readFile("mentee4.json")));
    }

    /**
     * Test login as user without roles.
     * @throws Exception throws if any error happen
     */
    @Test
    public void loginWithUserWithoutRolesTest() throws Exception {
        User demoEntity = objectMapper.readValue(readFile("demo-user.json"), User.class);
        demoEntity.setRoles(null);
        demoEntity.setId(1L);
        String newUserName = "newUsername";
        String newPassword = "newPassword";
        demoEntity.setPassword(newPassword);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(demoEntity)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.roles").doesNotExist());
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/mentees/4")
                                                  .accept(MediaType.APPLICATION_JSON)
                                                  .with(httpBasic(newUserName, newPassword)))
                                                  .andExpect(status().isUnauthorized());

    }

    /**
     * Test locale by accept language header.
     * @throws Exception throws if any error happen
     */
    @Test
    public void localeTest() throws Exception {
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/activities/1111")
                                                 .accept(MediaType.APPLICATION_JSON)
                                                 .with(httpBasic("email3@test.com", "password")))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Entity with ID=1111 can not be found"));
        mockAuthMvc.perform(MockMvcRequestBuilders.get("/activities/1111")
                                                                .header("Accept-Language","en_US")
                                                                .locale(Locale.US)
                                                                .accept(MediaType.APPLICATION_JSON)
                                                                .with(httpBasic("email3@test.com", "password")))
                     .andExpect(status().isNotFound())
                     .andExpect(jsonPath("$.message").value("en us Entity with ID=1111 can not be found"));
    }
}
