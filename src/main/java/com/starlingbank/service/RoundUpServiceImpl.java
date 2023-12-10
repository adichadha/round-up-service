package com.starlingbank.service;

import com.starlingbank.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class RoundUpServiceImpl implements RoundUpService {

    private final AccountsService accountsService;
    private final TransactionService transactionService;
    private final SavingsGoalsService savingsGoalsService;

    @Autowired
    public RoundUpServiceImpl(AccountsService accountsService, TransactionService transactionService, SavingsGoalsService savingsGoalsService) {
        this.accountsService = accountsService;
        this.transactionService = transactionService;
        this.savingsGoalsService = savingsGoalsService;
    }

    @Override
    public RoundUpResponseData performRoundUp(String authorizationHeader, RoundUpRequestData roundupRequestData) throws IOException, InterruptedException {
        UUID accountId = null;
        UUID defaultCategory = null;
        // Call GET All Accounts service.
        var accounts = accountsService.getAccounts(authorizationHeader);
        for (AccountResponseData accountResponseData : accounts.getAccountResponseDataList()) {
            if (AccountTypeEnum.PRIMARY.equals(accountResponseData.getAccountType())){
                accountId = accountResponseData.getAccountUid();
                defaultCategory = accountResponseData.getDefaultCategory();
            }
        }
        // Call GET All Transactions feed for a given period. Also, calculate the round-up.
        long roundUpAmount = getRoundUpAmountFromTransactions(
                authorizationHeader,
                roundupRequestData.getTransactionsStartDate(),
                accountId,
                defaultCategory);

        var savingsGoalsId = roundupRequestData.getSavingsGoalsId();

        if(roundupRequestData.isCreateSavingsGoal()) {
            var createSavingsGoalResponseData = savingsGoalsService.createSavingsGoal(
                    authorizationHeader,
                    accountId,
                    roundupRequestData.getSavingsGoalsName(),
                    roundupRequestData.getSavingsGoalsMinorUnits(),
                    roundupRequestData.getBase64EncodedPhoto());
            savingsGoalsId = createSavingsGoalResponseData.getSavingsGoalUid();
        }

        savingsGoalsService.addMoneyToSavingsGoals(
                authorizationHeader,
                accountId,
                savingsGoalsId,
                roundUpAmount);

        return new RoundUpResponseData(roundUpAmount);
    }

    private long getRoundUpAmountFromTransactions(String authorizationHeader, LocalDate transactionsStartDate, UUID accountId, UUID defaultCategory) throws IOException, InterruptedException {
        var transactionsResponseData = transactionService.getTransactionsForAccountAndCategory(
                authorizationHeader,
                accountId,
                defaultCategory,
                transactionsStartDate);

        var roundUpAmount = transactionsResponseData
                .getTransactionResponseData()
                .stream()
                .filter(transaction -> transaction.getDirection().equals(TransactionDirectionEnum.OUT)
                        && transaction.getStatus().equals(TransactionStatusEnum.SETTLED)
                        && transaction.getSource().equals(TransactionSourceEnum.FASTER_PAYMENTS_OUT))
                .mapToLong(transaction -> 100 - (transaction.getAmountData().getMinorUnits() % 100))
                .sum();
        return roundUpAmount;
    }
}
