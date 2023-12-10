package com.starlingbank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.client.ServiceClient;
import com.starlingbank.client.model.*;
import com.starlingbank.exception.BadRequestException;
import com.starlingbank.exception.InternalServerErrorException;
import com.starlingbank.model.CurrencyEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SavingsGoalsServiceImpl implements SavingsGoalsService {

    private final ServiceClient serviceClient;
    @Value("${com.starlingbank.round-up.domain:}")
    private String domain;

    public SavingsGoalsServiceImpl(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @Override
    public void addMoneyToSavingsGoals(String authorizationHeader,
                                       UUID accountId,
                                       UUID savingsGoalsId,
                                       long roundUpAmount) throws IOException, InterruptedException {

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("accountUid", String.valueOf(accountId));
        pathParams.put("savingsGoalUid", String.valueOf(savingsGoalsId));
        pathParams.put("transferUid", UUID.randomUUID().toString());

        var addMoneyToSavingsGoalsRequestData = new AddMoneyToSavingsGoalsRequestData();
        var addMoneyToSavingsGoalsAmount = new AddMoneyToSavingsGoalsAmount(CurrencyEnum.GBP, roundUpAmount);
        addMoneyToSavingsGoalsRequestData.setAddMoneyToSavingsGoalsAmount(addMoneyToSavingsGoalsAmount);

        var objectMapper = new ObjectMapper();
        var jsonBody = objectMapper.writeValueAsString(addMoneyToSavingsGoalsRequestData);

        serviceClient
                .put(domain + "/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}",
                        new HashMap<>(),
                        pathParams,
                        jsonBody,
                        "Authorization", authorizationHeader,
                        "Content-Type","application/json"
                );

    }

    @Override
    public CreateSavingsGoalResponseData createSavingsGoal(String authorizationHeader,
                                                           UUID accountId,
                                                           String savingsGoalsName,
                                                           Long savingsGoalsMinorUnits,
                                                           String base64EncodedPhoto) throws IOException, InterruptedException {

        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("accountUid", String.valueOf(accountId));

        var createSavingsGoalRequestData = new CreateSavingsGoalRequestData();
        createSavingsGoalRequestData.setName(savingsGoalsName);
        createSavingsGoalRequestData.setCurrency(CurrencyEnum.GBP);
        createSavingsGoalRequestData.setBase64EncodedPhoto("String");
        var createSavingsGoalTargetData = new CreateSavingsGoalTargetData(savingsGoalsMinorUnits,CurrencyEnum.GBP);
        createSavingsGoalRequestData.setTargetData(createSavingsGoalTargetData);

        var objectMapper = new ObjectMapper();
        var jsonBody = objectMapper.writeValueAsString(createSavingsGoalRequestData);

        var response = serviceClient
                .put(domain + "/api/v2/account/{accountUid}/savings-goals",
                        new HashMap<>(),
                        pathParams,
                        jsonBody,
                        "Authorization", authorizationHeader,
                        "Content-Type","application/json"
                );
        if (response.statusCode() == 200) {
            // Map the JSON response to a Java object using Jackson ObjectMapper
            return objectMapper.readValue(response.body(), CreateSavingsGoalResponseData.class);
        } else if(response.statusCode() >=400 && response.statusCode() < 500){
            throw objectMapper.readValue(response.body(), BadRequestException.class);
        } else {
            throw new InternalServerErrorException("Error occurred when fetching the Transaction feed");
        }
    }
}
