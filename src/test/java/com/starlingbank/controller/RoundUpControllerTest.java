package com.starlingbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.starlingbank.model.RoundUpRequestData;
import com.starlingbank.model.RoundUpResponseData;
import com.starlingbank.service.RoundUpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RoundUpController.class)
public class RoundUpControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    RoundUpService roundUpService;

    @Test
    public void testPerformRoundUp_SuccessfulResponse() throws Exception {
        // Create a user object for testing
        RoundUpRequestData requestData = new RoundUpRequestData();
        requestData.setCreateSavingsGoal(false);
        requestData.setSavingsGoalsId(UUID.fromString("9c60087a-416c-4266-9d94-aaa08f2bb0a2"));
        requestData.setCreateSavingsGoal(false);
        requestData.setTransactionsStartDate(LocalDate.of(2023,12,06));
        // Convert user object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String requestDataJson = objectMapper.writeValueAsString(requestData);
        var roundUpResponseData = new RoundUpResponseData(4L);
        when(roundUpService.performRoundUp(anyString(), any(RoundUpRequestData.class))).thenReturn(roundUpResponseData);

        // Perform the POST request and assert the response
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/round-up-service/api/v1/perform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDataJson)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roundUpAmount").value(4));
        // Verify that the userService.saveUser method was called with the correct user
        verify(roundUpService, times(1)).performRoundUp("token", requestData);
    }
}
