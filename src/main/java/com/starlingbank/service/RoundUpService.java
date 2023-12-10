package com.starlingbank.service;

import com.starlingbank.model.RoundUpRequestData;
import com.starlingbank.model.RoundUpResponseData;

import java.io.IOException;

public interface RoundUpService {
    RoundUpResponseData performRoundUp(String authorizationHeader, RoundUpRequestData roundupRequestData) throws IOException, InterruptedException;
}
