package com.jackdonahue.investingarena.Controller;

import com.jackdonahue.investingarena.Model.User;
import com.jackdonahue.investingarena.Service.UserService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//Testing the web layer without starting an entire server
@AutoConfigureMockMvc
public class UserControllerTest {
    //Injecting the MockMvc to simulate an HTTP request
    @Autowired
    private MockMvc mockMvc;
    //Mocks the userService, used so that you don't interact with real databases
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Testing the user create method using mock http requests
    @Test
    void testCreateUser_Success() throws Exception {
        String username = "mockUser";
        //Simulate UserService.saveUser returning true
        when(userService.saveUser(any(User.class))).thenReturn(true);

        //Simulate an HTTP POST request, username is the parameter, it expects HTTP 200
        //OK response
        mockMvc.perform(post("/user/create").param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User " + username + " created successfully.")));
    }

    //Testing if the user creation will return a bad response if the user already exists
    @Test
    void testCreateUser_Exists() throws Exception {
        String username = "mockUser";
        //Simulate UserService.saveUser returning true
        when(userService.saveUser(any(User.class))).thenReturn(false);

        //Simulate an HTTP POST request, username is the parameter, it expects HTTP 400
        //BAD response
        mockMvc.perform(post("/user/create").param("username", username))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username " + username + " already exists. Please choose a different one.")));
    }

    //Testing the successful retrieval of a user
    @Test
    void testGetUser_Success() throws Exception {
        
    }
}
