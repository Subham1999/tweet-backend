package com.tweetapp.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.backend.dto.auth.AuthRequest;
import com.tweetapp.backend.dto.auth.AuthResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RestUserControllerTest {

    private static final String TESTER_PASSWORD = "abcd";
    private static final String TESTER_MAIL = "subham4@gmail.com";

    @Autowired
    private MockMvc mockMvc;

    private String authToken;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
	objectMapper = new ObjectMapper();
	AuthRequest authRequest = AuthRequest.builder().email(TESTER_MAIL).password(TESTER_PASSWORD).build();
	String authRequestBody = objectMapper.writeValueAsString(authRequest);

	MockHttpServletResponse mockHttpServletResponse = mockMvc
		.perform(post("/auth/generate_token").contentType("application/json").content(authRequestBody))
		.andReturn().getResponse();

	if (mockHttpServletResponse.getStatus() == 200) {
	    String contentAsString = mockHttpServletResponse.getContentAsString();
	    AuthResponse authResponse = objectMapper.readValue(contentAsString.getBytes(), AuthResponse.class);
	    authToken = authResponse.getJwtToken();
	} else {
	    authToken = "<no_token>";
	}
    }

    @AfterEach
    void tearDown() throws Exception {
	authToken = null;
	objectMapper = null;
    }

    HttpHeaders headers() {
	final HttpHeaders httpHeaders = new HttpHeaders();
	httpHeaders.add("Authorization", "Bearer " + authToken);
	return httpHeaders;
    }

    @Test
    void testHealthCheck() throws Exception {

	mockMvc.perform(get("/users/health").headers(headers())).andExpect(status().isOk())
		.andExpect(jsonPath("serverStatus").value("SERVER IS UP AND RUNNING"));
    }

    @Test
    void testViewUser() throws Exception {

	mockMvc.perform(get("/users/{email}", TESTER_MAIL).headers(headers()).contentType("application/json"))
		.andExpect(status().isOk()).andExpect(jsonPath("firstName").value("Subham"))
		.andExpect(jsonPath("lastName").value("Santra")).andExpect(jsonPath("email").value(TESTER_MAIL));
    }

    @Test
    void testViewUser_whenEmailIsNotPresent() throws Exception {

	mockMvc.perform(get("/users/{email}", "no_such_mail@gmail.com").headers(headers())).andExpect(status().is(452));
    }

    @Test
    void testSearchUsers() throws Exception {
	mockMvc.perform(get("/users/search").param("key", "subham").headers(headers()).contentType("application/json"))
		.andExpect(status().isOk());
    }

    @Test
    void testCreateUser() throws Exception {
	mockMvc.perform(get("/users/{email}", "no_such_mail@gmail.com").headers(headers())).andExpect(status().is(452));
    }

    @Test
    void testUpdateUser() throws Exception {
	mockMvc.perform(get("/users/{email}", "subham4@gmail.com").headers(headers())).andExpect(status().is(200));
    }

//    @Test
//    void testStoreSecrets() {
//	fail("Not yet implemented");
//    }
//
//    @Test
//    void testGetAllUsers() {
//	fail("Not yet implemented");
//    }

}
