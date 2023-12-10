package com.starlingbank.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.starlingbank.model.CurrencyEnum;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateSavingsGoalRequestData {
    private String name;
    private CurrencyEnum currency;
    @JsonProperty("target")
    private CreateSavingsGoalTargetData targetData;
    private String base64EncodedPhoto;
}
