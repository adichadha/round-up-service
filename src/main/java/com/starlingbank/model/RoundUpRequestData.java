package com.starlingbank.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoundUpRequestData {
    private boolean createSavingsGoal;
    private UUID savingsGoalsId;
    private String savingsGoalsName;
    private Long savingsGoalsMinorUnits;
    private String base64EncodedPhoto;
    @NotBlank(message = "weekCommencing cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Use YYYY-MM-DD")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionsStartDate;

}
