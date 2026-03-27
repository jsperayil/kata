package com.kata.trafficlight;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrafficLightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void shouldReturnRedAsDefaultState() throws Exception {
        mockMvc.perform(get("/light"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("RED"));
    }

    @Test
    @Order(2)
    void shouldChangeStateToGreen() throws Exception {
        mockMvc.perform(put("/light")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\": \"GREEN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("GREEN"));
    }

    @Test
    @Order(3)
    void shouldChangeStateToYellow() throws Exception {
        mockMvc.perform(put("/light")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\": \"YELLOW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("YELLOW"));
    }
}
