package com.starlingbank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.client.ServiceClient;
import com.starlingbank.client.model.CreateSavingsGoalResponseData;
import com.starlingbank.exception.BadRequestException;
import com.starlingbank.exception.InternalServerErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SavingsGoalsServiceTest {

    @Mock
    private ServiceClient serviceClient;

    @InjectMocks
    private SavingsGoalsServiceImpl savingsGoalsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(savingsGoalsService, "domain", "http://test.com");
    }

    @Test
    void testAddMoneyToSavingsGoals_SuccessfulRequest() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID accountId = UUID.randomUUID();
        UUID savingsGoalsId = UUID.randomUUID();
        long roundUpAmount = 100L;
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.put(anyString(), any(), any(), anyString(), eq("Authorization"), eq(authorizationHeader), eq("Content-Type"), eq("application/json")))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);

        // Act
        assertDoesNotThrow(() -> savingsGoalsService.addMoneyToSavingsGoals(authorizationHeader, accountId, savingsGoalsId, roundUpAmount));
    }

    @Test
    void testCreateSavingsGoal_SuccessfulResponse() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID accountId = UUID.randomUUID();
        String savingsGoalsName = "My Savings Goal";
        Long savingsGoalsMinorUnits = 5000L;
        String base64EncodedPhoto = "Base64EncodedString";
        CreateSavingsGoalResponseData expectedResponse = new CreateSavingsGoalResponseData(/* your data */);
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.put(anyString(), any(), any(), anyString(), eq("Authorization"), eq(authorizationHeader), eq("Content-Type"), eq("application/json")))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(new ObjectMapper().writeValueAsString(expectedResponse));

        // Act
        CreateSavingsGoalResponseData actualResponse = savingsGoalsService.createSavingsGoal(
                authorizationHeader, accountId, savingsGoalsName, savingsGoalsMinorUnits, base64EncodedPhoto);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testCreateSavingsGoal_BadRequestException() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID accountId = UUID.randomUUID();
        String savingsGoalsName = "My Savings Goal";
        Long savingsGoalsMinorUnits = 5000L;
        String base64EncodedPhoto = "Base64EncodedString";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.put(anyString(), any(), any(), anyString(), eq("Authorization"), eq(authorizationHeader), eq("Content-Type"), eq("application/json")))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"error\":\"invalid_request\",\"error_description\":\"Bad Request\"}");

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                savingsGoalsService.createSavingsGoal(authorizationHeader, accountId, savingsGoalsName, savingsGoalsMinorUnits, base64EncodedPhoto));
    }

    @Test
    void testCreateSavingsGoal_InternalServerErrorException() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID accountId = UUID.randomUUID();
        String savingsGoalsName = "My Savings Goal";
        Long savingsGoalsMinorUnits = 5000L;
        String base64EncodedPhoto = "Base64EncodedString";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.put(anyString(), any(), any(), anyString(), eq("Authorization"), eq(authorizationHeader), eq("Content-Type"), eq("application/json")))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);

        // Act & Assert
        assertThrows(InternalServerErrorException.class, () ->
                savingsGoalsService.createSavingsGoal(authorizationHeader, accountId, savingsGoalsName, savingsGoalsMinorUnits, base64EncodedPhoto));
    }
}

