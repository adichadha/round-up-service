package com.starlingbank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.client.ServiceClient;
import com.starlingbank.exception.BadRequestException;
import com.starlingbank.exception.InternalServerErrorException;
import com.starlingbank.model.AccountsResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountsServiceTest {

    @Mock
    private ServiceClient serviceClient;

    @InjectMocks
    private AccountsServiceImpl accountsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(accountsService, "domain", "http://test.com");
    }

    @Test
    void testGetAccounts_SuccessfulResponse() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        AccountsResponseData expectedResponse = new AccountsResponseData();
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.get(anyString(), any(), any(), eq("Authorization"), eq(authorizationHeader)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(new ObjectMapper().writeValueAsString(expectedResponse));

        // Act
        AccountsResponseData actualResponse = accountsService.getAccounts(authorizationHeader);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetAccounts_BadRequestException() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.get(anyString(), any(), any(), eq("Authorization"), eq(authorizationHeader)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"error\":\"invalid_token\",\"error_description\":\"Access token has expired\"}");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> accountsService.getAccounts(authorizationHeader));
    }

    @Test
    void testGetAccounts_InternalServerErrorException() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(serviceClient.get(anyString(), any(), any(), eq("Authorization"), eq(authorizationHeader)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);

        // Act & Assert
        assertThrows(InternalServerErrorException.class, () -> accountsService.getAccounts(authorizationHeader));
    }
}