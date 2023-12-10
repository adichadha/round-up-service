package com.starlingbank.service;

import com.starlingbank.model.AccountsResponseData;

import java.io.IOException;

public interface AccountsService {
    AccountsResponseData getAccounts(String authorizationHeader) throws IOException, InterruptedException;
}
