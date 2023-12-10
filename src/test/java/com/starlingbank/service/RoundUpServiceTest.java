package com.starlingbank.service;

import com.starlingbank.client.model.CreateSavingsGoalResponseData;
import com.starlingbank.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RoundUpServiceTest {

    @Mock
    private AccountsService accountsService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SavingsGoalsService savingsGoalsService;

    @InjectMocks
    private RoundUpServiceImpl roundUpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPerformRoundUp_CreateSavingsGoal_Successful() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        RoundUpRequestData roundUpRequestData = new RoundUpRequestData(
                true, UUID.randomUUID(),"Savings Goal", 100L, "Base64EncodedPhoto",
                LocalDate.now());
        AccountsResponseData accountsResponseData = new AccountsResponseData(Collections.singletonList(
                new AccountResponseData(UUID.randomUUID(), AccountTypeEnum.PRIMARY, UUID.randomUUID(), CurrencyEnum.GBP)
        ));
        TransactionsResponseData transactionsResponseData = new TransactionsResponseData(Collections.singletonList(
                new TransactionResponseData(TransactionDirectionEnum.OUT,
                        TransactionStatusEnum.SETTLED, TransactionSourceEnum.FASTER_PAYMENTS_OUT,
                        new TransactionAmountData(CurrencyEnum.GBP,1500))
        ));
        CreateSavingsGoalResponseData createSavingsGoalResponseData = new CreateSavingsGoalResponseData(UUID.randomUUID());

        when(accountsService.getAccounts(eq(authorizationHeader))).thenReturn(accountsResponseData);
        when(transactionService.getTransactionsForAccountAndCategory(eq(authorizationHeader), any(), any(), any()))
                .thenReturn(transactionsResponseData);
        when(savingsGoalsService.createSavingsGoal(eq(authorizationHeader), any(), any(), any(), any())).thenReturn(createSavingsGoalResponseData);

        // Act
        RoundUpResponseData roundUpResponseData = roundUpService.performRoundUp(authorizationHeader, roundUpRequestData);

        // Assert
        assertNotNull(roundUpResponseData);
        assertEquals(100, roundUpResponseData.getRoundUpAmount());

        // Verify interactions
        verify(savingsGoalsService, times(1)).addMoneyToSavingsGoals(
                eq(authorizationHeader),
                any(),
                eq(createSavingsGoalResponseData.getSavingsGoalUid()),
                eq(100L));
    }

    @Test
    void testPerformRoundUp_UseExistingSavingsGoal_Successful() throws IOException, InterruptedException {
        // Arrange
        String authorizationHeader = "Bearer TOKEN";
        UUID savingsGoalsId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID defaultCategory = UUID.randomUUID();
        RoundUpRequestData roundUpRequestData = new RoundUpRequestData(
                false, savingsGoalsId,"Savings Goal", 1000L, "Base64EncodedPhoto",
                LocalDate.now());
        AccountsResponseData accountsResponseData = new AccountsResponseData(Collections.singletonList(
                new AccountResponseData(accountId, AccountTypeEnum.PRIMARY, defaultCategory, CurrencyEnum.GBP)
        ));
        TransactionsResponseData transactionsResponseData = new TransactionsResponseData(Collections.singletonList(
                new TransactionResponseData(TransactionDirectionEnum.OUT,
                        TransactionStatusEnum.SETTLED, TransactionSourceEnum.FASTER_PAYMENTS_OUT,
                        new TransactionAmountData(CurrencyEnum.GBP,1500))
        ));

        when(accountsService.getAccounts(eq(authorizationHeader))).thenReturn(accountsResponseData);
        when(transactionService.getTransactionsForAccountAndCategory(eq(authorizationHeader), any(), any(), any()))
                .thenReturn(transactionsResponseData);

        // Act
        RoundUpResponseData roundUpResponseData = roundUpService.performRoundUp(authorizationHeader, roundUpRequestData);

        // Assert
        assertNotNull(roundUpResponseData);
        assertEquals(100, roundUpResponseData.getRoundUpAmount());

        // Verify interactions
        verify(savingsGoalsService, times(1)).addMoneyToSavingsGoals(
                eq(authorizationHeader),
                any(),
                eq(savingsGoalsId),
                eq(100L));
        verify(savingsGoalsService, never()).createSavingsGoal(any(), any(), any(), any(), any());
    }
}
