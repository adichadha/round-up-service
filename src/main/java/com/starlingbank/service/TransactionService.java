package com.starlingbank.service;

import com.starlingbank.model.TransactionsResponseData;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public interface TransactionService {
    TransactionsResponseData getTransactionsForAccountAndCategory(String authorizationHeader,
                                                                  UUID accountId,
                                                                  UUID defaultCategory, LocalDate weekCommencing) throws IOException, InterruptedException;
}
