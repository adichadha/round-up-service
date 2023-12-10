package com.starlingbank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.client.ServiceClient;
import com.starlingbank.exception.BadRequestException;
import com.starlingbank.exception.InternalServerErrorException;
import com.starlingbank.model.TransactionsResponseData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final ServiceClient serviceClient;
    public TransactionServiceImpl(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @Value("${com.starlingbank.round-up.domain:}")
    private String domain;

    @Value("${com.starlingbank.round-up.transactions-days}")
    private long transactionDays;
    @Override
    public TransactionsResponseData getTransactionsForAccountAndCategory(String authorizationHeader,
                                                                         UUID accountId,
                                                                         UUID defaultCategory, LocalDate date) throws IOException, InterruptedException {

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("accountUid", String.valueOf(accountId));
        pathParams.put("defaultCategory", String.valueOf(defaultCategory));

        var queryParams = getQueryParams(date);

        var response = serviceClient
                .get(domain + "/api/v2/feed/account/{accountUid}/category/{defaultCategory}/transactions-between",
                queryParams,
                pathParams,
                "Authorization", authorizationHeader);

        var objectMapper = new ObjectMapper();
        if (response.statusCode() == 200) {
            // Map the JSON response to a Java object using Jackson ObjectMapper
            return objectMapper.readValue(response.body(), TransactionsResponseData.class);
        } else if(response.statusCode() >=400 && response.statusCode() < 500){
            throw objectMapper.readValue(response.body(), BadRequestException.class);
        } else {
            throw new InternalServerErrorException("Error occurred when fetching the Transaction feed");
        }
    }

    private Map<String, String> getQueryParams(LocalDate weekCommencing) {
        Map<String, String> queryParams = new HashMap<>();
        // Combine LocalDate and LocalTime to create LocalDateTime
        // Here, we assume midnight (00:00:00) for the time components
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalDateTime combinedDateTime = weekCommencing.atTime(time);
        // Define the desired output format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        // Format LocalDateTime to the desired string format
        String minTransactionTimestamp = combinedDateTime.format(formatter);
        String maxTransactionTimestamp = combinedDateTime.plusDays(transactionDays).format(formatter);
        queryParams.put("minTransactionTimestamp",minTransactionTimestamp);
        queryParams.put("maxTransactionTimestamp",maxTransactionTimestamp);
        return queryParams;
    }
}
