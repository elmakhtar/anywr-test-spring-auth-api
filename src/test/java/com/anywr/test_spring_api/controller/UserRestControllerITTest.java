package com.anywr.test_spring_api.controller;

import com.anywr.test_spring_api.dto.LoginDto;
import com.anywr.test_spring_api.dto.RegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserRestControllerITTest {
    @Autowired
    private WebApplicationContext applicationContext;
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();


    private static final String USER_ONE_USERNAME = "test-user-one";
    private static final String USER_ONE_EMAIL = "test-user-one@gmail.com";
    private static final String USER_TWO_USERNAME = "test-user-two";
    private static final String USER_TWO_EMAIL = "test-user-two@gmail.com";


    private static final String USER_THREE_USERNAME = "test-user-three";
    private static final String USER_THREE_EMAIL = "test-user-three@gmail.com";
    private static final String USER_THREE_FIRSTNAME = "jhon";
    private static final String USER_THREE_LASTNAME = "doe";


    @BeforeEach
    public void init(){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext).apply(springSecurity())
                .build();
    }

    @Test
    void login() throws Exception {

        RegisterDto registerDto = RegisterDto.builder()
                .username(USER_ONE_USERNAME)
                .password("test-password")
                .email(USER_ONE_EMAIL)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerDto)))
                .andReturn();

        LoginDto loginDto = LoginDto.builder()
                .username(USER_ONE_USERNAME)
                .password("test-password")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk()).andReturn();

        assertThat(result.getResponse()).isNotNull();
    }

    @Test
    void register() throws Exception {
        RegisterDto registerDto = RegisterDto.builder()
                .username(USER_TWO_USERNAME)
                .password("test-password")
                .email(USER_TWO_EMAIL)
                .build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("Bearer ");
    }

    @Test
    void shouldLoginFail() throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .username(USER_ONE_USERNAME)
                .password("test-wrong-password")
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    void getUserDetail() throws Exception {

        RegisterDto registerDto = RegisterDto.builder()
                .username(USER_THREE_USERNAME)
                .username(USER_THREE_USERNAME)
                .firstName(USER_THREE_FIRSTNAME)
                .lastName(USER_THREE_LASTNAME)
                .password("test-password")
                .email(USER_THREE_EMAIL)
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registerDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        String token = jsonParser.parseMap(result.getResponse().getContentAsString()).get("accessToken").toString();

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/profile")
                        .header("Authorization", "Bearer "+token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(USER_THREE_EMAIL))
                .andExpect(jsonPath("$.username").value(USER_THREE_USERNAME))
                .andExpect(jsonPath("$.firstName").value(USER_THREE_FIRSTNAME))
                .andExpect(jsonPath("$.lastName").value(USER_THREE_LASTNAME))
                .andReturn();
    }

    @Test
    void shouldFailToGetUSerDetailWithoutValidToken() throws Exception {

        //no token provided
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/profile")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        //invalid token used
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/user/profile")
                        .header("Authorization", "Bearer "+"invalid_token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}