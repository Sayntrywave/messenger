package com.korotkov.messenger.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korotkov.messenger.service.JWTService;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class UserControllerTestIT {

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
    void getMessages() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.
                get("/user/messages").
                queryParam("nick", "n").
                header("Authorization", token);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk()
                ).andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        int size = mapper.readValue(json, ArrayList.class).size();
        Assertions.assertEquals(0, size);
    }


    @Test
    void getFriends() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.
                get("/user/friends").
                queryParam("nick", "n").
                header("Authorization", token);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk()
                ).andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        int size = mapper.readValue(json, ArrayList.class).size();
        Assertions.assertEquals(0, size);


        MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders.
                get("/user/friends").
                header("Authorization", token);

        MvcResult mvcResult2 = this.mockMvc.perform(requestBuilder2)
                .andExpectAll(
                        status().isOk()
                ).andReturn();

        json = mvcResult2.getResponse().getContentAsString();
        mapper = new ObjectMapper();
        size = mapper.readValue(json, ArrayList.class).size();
        Assertions.assertEquals(0, size);
    }
}