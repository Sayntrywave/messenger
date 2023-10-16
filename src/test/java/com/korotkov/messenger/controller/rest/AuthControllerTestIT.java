package com.korotkov.messenger.controller.rest;

import com.korotkov.messenger.model.Token;
import com.korotkov.messenger.service.JWTService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class AuthControllerTestIT {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> selfPostgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTService jwtService;

    String token;


    @BeforeEach
    void setUp() throws Exception {
        TimeUnit.SECONDS.sleep(1); //because jwt tokens are the same
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "login": "nikitos",
                            "password": "qwerty"
                        }
                        """);


        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk()
                ).andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(json);

        token = jsonObject.getString("token");
    }


    @Test
    void login() {
        String login = jwtService.validateTokenAndRetrieveClaim(token);

        assertEquals("nikitos", login);
    }

    @Test
    void logout() throws Exception {
        assertDoesNotThrow(() -> jwtService.getToken(token));
        Token tokenBefore = jwtService.getToken(token);

        assertFalse(tokenBefore.getIsExpired());


        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/logout").header("Authorization", token);

        this.mockMvc.perform(builder)
                .andExpectAll(status().isOk());

        assertDoesNotThrow(() -> jwtService.getToken(token));
        Token tokenAfter = jwtService.getToken(token);

        assertTrue(tokenAfter.getIsExpired());

    }
}