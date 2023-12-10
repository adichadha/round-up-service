package com.starlingbank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.client.ServiceClient;
import com.starlingbank.exception.BadRequestException;
import com.starlingbank.exception.InternalServerErrorException;
import com.starlingbank.model.TransactionsResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    @Mock
    private ServiceClient serviceClient;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(transactionService, "domain", "http://test.com");
    }

    @Test
    void testGetTransactionsForAccountAndCategory_SuccessfulResponse() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID accountId = UUID.randomUUID();
        UUID defaultCategory = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        TransactionsResponseData expectedResponse = new TransactionsResponseData(/* your data */);
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.get(anyString(), any(), any(), eq("Authorization"), eq(authorizationHeader)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(new ObjectMapper().writeValueAsString(expectedResponse));

        // Act
        TransactionsResponseData actualResponse = transactionService.getTransactionsForAccountAndCategory(
                authorizationHeader, accountId, defaultCategory, date);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetTransactionsForAccountAndCategory_BadRequestException() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID accountId = UUID.randomUUID();
        UUID defaultCategory = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.get(anyString(), any(), any(), eq("Authorization"), eq(authorizationHeader)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"error\":\"invalid_request\",\"error_description\":\"Bad Request\"}");

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                transactionService.getTransactionsForAccountAndCategory(authorizationHeader, accountId, defaultCategory, date));
    }

    @Test
    void testGetTransactionsForAccountAndCategory_InternalServerErrorException() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID accountId = UUID.randomUUID();
        UUID defaultCategory = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.get(anyString(), any(), any(), eq("Authorization"), eq(authorizationHeader)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);

        // Act & Assert
        assertThrows(InternalServerErrorException.class, () ->
                transactionService.getTransactionsForAccountAndCategory(authorizationHeader, accountId, defaultCategory, date));
    }
}

