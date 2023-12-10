package com.starlingbank.controller;

import com.starlingbank.model.RoundUpRequestData;
import com.starlingbank.model.RoundUpResponseData;
import com.starlingbank.service.RoundUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/round-up-service/api/v1")
public class RoundUpController {


    private final RoundUpService roundUpService;

    @Autowired
    public RoundUpController(RoundUpService roundUpService) {
        this.roundUpService = roundUpService;
    }

    @RequestMapping(value = "/perform", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<RoundUpResponseData> performRoundUp(@RequestHeader("Authorization") String authorizationHeader,
                                                              @RequestBody RoundUpRequestData roundupRequestData) throws IOException, InterruptedException {
        return new ResponseEntity(roundUpService.performRoundUp(authorizationHeader, roundupRequestData), HttpStatus.OK);

    }
}