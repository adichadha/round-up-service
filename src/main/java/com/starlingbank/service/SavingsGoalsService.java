package com.starlingbank.service;

import com.starlingbank.client.model.CreateSavingsGoalResponseData;

import java.io.IOException;
import java.util.UUID;

public interface SavingsGoalsService {
    void addMoneyToSavingsGoals(String authorizationHeader,
                                UUID accountId,
                                UUID savingsGoalsId,
                                long roundUpAmount) throws IOException, InterruptedException;

    CreateSavingsGoalResponseData createSavingsGoal(String authorizationHeader,
                                                    UUID accountId,
                                                    String savingsGoalsName,
                                                    Long savingsGoalsMinorUnits,
                                                    String base64EncodedPhoto) throws IOException, InterruptedException;
}
