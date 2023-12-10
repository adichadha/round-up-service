package com.starlingbank.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponseData {
    private TransactionDirectionEnum direction;
    private TransactionStatusEnum status;
    private TransactionSourceEnum source;
    @JsonProperty("amount")
    private TransactionAmountData amountData;
}
