package com.absabanking.service;

import com.absabanking.dto.InternalTransactionDto;
import com.absabanking.enums.EAccountType;
import com.absabanking.enums.ETranType;
import com.absabanking.exception.SavingsAccountException;
import com.absabanking.model.Account;
import com.absabanking.model.Transaction;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.BankRepository;
import com.absabanking.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(JUnit4.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        return transaction;
    }

    /*
       public void postInternalTransfer(InternalTransactionDto internalTransactionDto) throws Exception {
        internalTransactionDto.setETranType(ETranType.INTERNAL_TRANSFER);
        Account senderAccount = accountService.findAccountByAccountNumber(internalTransactionDto.getSenderAccount());
        if (senderAccount.getAccountType().equalsIgnoreCase(EAccountType.SAVINGS.toString())) {
            transactionServiceLogger.error("Savings account cant transfer money", internalTransactionDto.getSenderAccount());
            throw new SavingsAccountException("Savings account cant transfer money");
        }
        Account receiverAccount = accountService.findAccountByAccountNumber(internalTransactionDto.getReceiverAccount());
        if (receiverAccount != null && senderAccount != null) {
            if (senderAccount.getAccountBalance().compareTo(internalTransactionDto.getTransactionAmount()) == -1) {
                transactionServiceLogger.info("amount  being transferred is less than your current balance please  review/recharge: {}", senderAccount.getAccountNumber());
                throw new Exception("amount  being transferred is less than your current balance please  review ");
            }
            BigDecimal transactionChargesAmount = internalTransactionDto.getTransactionAmount().multiply(bankTransactionCharges);
            BigDecimal newAccountBalance = senderAccount.getAccountBalance().subtract(transactionChargesAmount.add(internalTransactionDto.getTransactionAmount()));
            senderAccount.setAccountBalance(newAccountBalance);

            if (receiverAccount.getAccountType().equalsIgnoreCase(EAccountType.SAVINGS.toString())) {
                BigDecimal savingsAccountNewAccountBalance = receiverAccount.getAccountBalance().add(receiverAccount.getAccountBalance().multiply(bankInterest)).add(internalTransactionDto.getTransactionAmount());
                receiverAccount.setAccountBalance(savingsAccountNewAccountBalance);
                accountService.updateAccount(receiverAccount);
            }
        }
        accountService.updateAccount(senderAccount);
        postInternalTransactions(InternalTransactionDto.getInstance(internalTransactionDto));
    }
     */

    private Account createAccount(EAccountType accountType) {
        Account account = new Account();
        account.setAccountBalance(BigDecimal.valueOf(500));
        account.setAccountNumber(200000L);
        account.setAccountType(accountType.toString());
        return account;
    }

    @Test
    public void transferMoneyFromSavingsAccountSHouldThrowSavingsAccountException() throws Exception {

        Account account = createAccount(EAccountType.SAVINGS);

        Mockito.when(accountService.findAccountByAccountNumber(Mockito.anyLong())).thenReturn(account);

        assertThrows(SavingsAccountException.class,
                () -> transactionService.postInternalTransfer(createInternalTransactionDTO()));
    }

    @Test
    void handleDeposit() {
    }

    @Test
    void processTransactionsForOtherBanks() {
    }

    @Test
    void postInternalTransfer() {
    }

    private InternalTransactionDto createInternalTransactionDTO() {

        return new InternalTransactionDto(200000L, 2000L,
                "Internal transfer", BigDecimal.valueOf(300), ETranType.INTERNAL_TRANSFER);
    }
}