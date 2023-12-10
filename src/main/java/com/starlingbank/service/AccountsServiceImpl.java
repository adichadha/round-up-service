package com.starlingbank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.client.ServiceClient;
import com.starlingbank.exception.BadRequestException;
import com.starlingbank.exception.InternalServerErrorException;
import com.starlingbank.model.AccountsResponseData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class AccountsServiceImpl implements AccountsService {

    private final ServiceClient serviceClient;
    @Value("${com.starlingbank.round-up.domain:}")
    private String domain;

    public AccountsServiceImpl(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @Override
    public AccountsResponseData getAccounts(String authorizationHeader) throws IOException, InterruptedException {
        //GET All Accounts
        var response = serviceClient.get(domain+"/api/v2/accounts",
                new HashMap<>(),
                new HashMap<>(),
                "Authorization", authorizationHeader);
        var objectMapper = new ObjectMapper();
        if (response.statusCode() == 200) {
            // Map the JSON response to a Java object using Jackson ObjectMapper
             return objectMapper.readValue(response.body(), AccountsResponseData.class);
        } else if(response.statusCode() >=400 && response.statusCode() < 500){
            throw objectMapper.readValue(response.body(), BadRequestException.class);
        } else {
            throw new InternalServerErrorException("Error occurred when fetching the Accounts");
        }
    }
}
