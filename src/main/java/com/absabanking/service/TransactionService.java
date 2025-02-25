package com.absabanking.service;

import com.absabanking.dto.*;
import com.absabanking.enums.EAccountType;
import com.absabanking.enums.EPostingType;
import com.absabanking.enums.ETranType;
import com.absabanking.exception.InsufficientFundsException;
import com.absabanking.exception.InterbankTransactionException;
import com.absabanking.exception.SavingsAccountException;
import com.absabanking.model.Account;
import com.absabanking.model.Bank;
import com.absabanking.model.Transaction;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.TransactionRepository;
import com.absabanking.util.TransactionReferenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    public TransactionService(ApplicationEventPublisher applicationEventPublisher, TransactionRepository transactionRepository, AccountService accountService, BankService bankService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.bankService = bankService;
    }
    private static final org.slf4j.Logger transactionServiceLogger = org.slf4j.LoggerFactory.getLogger(TransactionService.class);
    @Value("${bank.charges.payment_charges_amount}")
    private BigDecimal bankTransactionCharges;
    @Value("${bank.charges.credit_percentage}")
    private BigDecimal bankInterest;

    @Value("${bank.charges.payment_charges_for_deposit}")
    private BigDecimal bankCharges;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final BankService bankService;



    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }



    public List<Transaction> findAllTransactionsByBankCode(String bankCode, ETranType eTranType) {
        return transactionRepository.findTransactionByAcquiringInstitutionAndTranType(bankCode, eTranType);
    }


    /**
     * @param withDrawRequestDto
     * @return
     */
    @Transactional
    public WithDrawResponseDto handleWithdrawal(WithDrawRequestDto withDrawRequestDto) {
        Account account = accountService.findAccountByAccountNumber(withDrawRequestDto.getAccountNumber());
        String transactionReference = TransactionReferenceGenerator.getAlphaNumericString(32);

        validateSufficientFunds(account, withDrawRequestDto.getAmount());
        executeWithdrawalTransaction(account, withDrawRequestDto, transactionReference);
        WithDrawResponseDto withDrawResponseDto = WithDrawResponseDto.builder().
                account(account.getAccountNumber()).
                newAccountBalance(account.getAccountBalance()).
                amountWithDrawn(withDrawRequestDto.getAmount()).
                message("Successfully withdrew !!").
                build();
        return withDrawResponseDto;


    }
    /**
     *
     * @param receiverAccount
     * @param depositDto
     * @param transactionReference
     */
    private void executeCashDepositTransaction(Account receiverAccount, DepositDto depositDto, String transactionReference) {
        transactionServiceLogger.info("---------------Start executing a cash Deposit --------------------");
        Transaction cashDepositTransaction = new Transaction();
        cashDepositTransaction.setTranType(ETranType.CASH_DEPOSIT);
        cashDepositTransaction.setTransactionAmount(depositDto.getAmount());
        cashDepositTransaction.setNarrative("Cash Deposit of amount " + depositDto.getAmount());
        cashDepositTransaction.setReference(transactionReference);
        cashDepositTransaction.setAccountNumber(receiverAccount.getAccountNumber());
        cashDepositTransaction.setComms("cash deposit detailed message");
        cashDepositTransaction.setTransactionAmount(depositDto.getAmount());
        cashDepositTransaction.setSenderAccount(depositDto.getSenderAccountNumber());
        cashDepositTransaction.setReceiverAccount(depositDto.getReceiverAccountNumber());

        transactionRepository.save(cashDepositTransaction);

        BigDecimal accountNewBalance = receiverAccount.getAccountBalance()
                .add(depositDto.getAmount());

        receiverAccount.setAccountBalance(accountNewBalance);
        accountService.updateAccount(receiverAccount);

        applicationEventPublisher.publishEvent(cashDepositTransaction);

        transactionServiceLogger.info("---------------End  executed successfully for with draw--------------------");
    }


    /**
     * Used    for  deposit transactions
     *
     * @param depositDto the deposit object
     */
    @Transactional
    public void handleCashDeposit(DepositDto depositDto) {
        Account receiverAccount = accountService.findAccountByAccountNumber(depositDto.getReceiverAccountNumber());
        String transactionReference = TransactionReferenceGenerator.getAlphaNumericString(32);

        if (receiverAccount == null) {
            throw  new ResourceNotFoundException("account not found " + depositDto.getReceiverAccountNumber());
        }

        if (receiverAccount != null) {
            executeCashDepositTransaction(receiverAccount, depositDto, transactionReference);
        }

    }

    /**
     *
     * @param senderAccount
     * @param amount
     */
    private void validateSufficientFunds(Account senderAccount, BigDecimal amount) {
        if (senderAccount.getAccountBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("You cannot perform this operation: Insufficient funds.");
        }
    }

    /**
     *
     * @param receiverAccount
     * @param senderAccount
     * @param depositDto
     * @param transactionReference
     */
    private void executeTransactionForReceiver(Account receiverAccount, Account senderAccount, DepositDto depositDto, String transactionReference) {
        transactionServiceLogger.info("---------------executing a deposit for receiver--------------------");

        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setTranType(ETranType.DEPOSIT);
        receiverTransaction.setTransactionAmount(depositDto.getAmount());
        receiverTransaction.setNarrative("Receiving amount " + depositDto.getAmount() + " from " + senderAccount.getAccountNumber());
        receiverTransaction.setReference(transactionReference);
        receiverTransaction.setAccountNumber(receiverAccount.getAccountNumber());
        receiverTransaction.setComms("receiver detailed message");

        transactionRepository.save(receiverTransaction);

        BigDecimal newReceiverBalance = receiverAccount.getAccountBalance()
                .add(receiverAccount.getAccountBalance().multiply(bankInterest))
                .add(depositDto.getAmount());

        receiverAccount.setAccountBalance(newReceiverBalance);
        accountService.updateAccount(receiverAccount);

        transactionServiceLogger.info("---------------deposit executed successfully for receiver--------------------");
    }
    private void executeTransactionForSender(Account senderAccount, Account receiverAccount, DepositDto depositDto, String transactionReference) {
        transactionServiceLogger.info("---------------executing a deposit for sender--------------------");

        Transaction senderTransaction = new Transaction();
        senderTransaction.setTranType(ETranType.TRANSFER);
        senderTransaction.setTransactionAmount(depositDto.getAmount());
        senderTransaction.setNarrative("Sending amount " + depositDto.getAmount() + " to " + receiverAccount.getAccountNumber());
        senderTransaction.setReference(transactionReference);
        senderTransaction.setAccountNumber(senderAccount.getAccountNumber());
        senderTransaction.setComms("sender detailed message");

        transactionRepository.save(senderTransaction);

        BigDecimal newSenderBalance = senderAccount.getAccountBalance()
                .subtract(bankCharges)
                .subtract(depositDto.getAmount());

        senderAccount.setAccountBalance(newSenderBalance);
        accountService.updateAccount(senderAccount);

        applicationEventPublisher.publishEvent(senderTransaction);
        applicationEventPublisher.publishEvent(senderTransaction);

        transactionServiceLogger.info("---------------deposit executed successfully for sender--------------------");
    }

    /**
     *
     * @param accountNumber
     * @param eTranType
     * @return
     */
    public List<Transaction> transactionsByAccountNumberAndTransactionType(Long accountNumber, ETranType eTranType) {

        return transactionRepository.findTransactionByAccountNumberAndTranType(accountNumber, eTranType);
    }
    /**
     *
     * @param account
     * @param withDrawRequestDto
     * @param transactionReference
     */
    private void executeWithdrawalTransaction(Account account, WithDrawRequestDto withDrawRequestDto, String transactionReference) {
        transactionServiceLogger.info("---------------Start executing a withdraw--------------------");

        Transaction withdrawTransaction = new Transaction();
        withdrawTransaction.setTranType(ETranType.WITHDRAWAL);
        withdrawTransaction.setTransactionAmount(withDrawRequestDto.getAmount());
        withdrawTransaction.setNarrative("Withdrew amount " + withDrawRequestDto.getAmount() + " from " + withDrawRequestDto.getAccountNumber());
        withdrawTransaction.setReference(transactionReference);
        withdrawTransaction.setAccountNumber(account.getAccountNumber());
        withdrawTransaction.setReceiverAccount(withdrawTransaction.getReceiverAccount());
        withdrawTransaction.setTransactionAmount(withDrawRequestDto.getAmount());
        withdrawTransaction.setReceiverAccount(withDrawRequestDto.getAccountNumber());
        withdrawTransaction.setComms("withdrawal detailed message");

        transactionRepository.save(withdrawTransaction);

        BigDecimal accountNewBalance = account.getAccountBalance()
                .subtract(bankCharges)
                .subtract(withDrawRequestDto.getAmount());

        account.setAccountBalance(accountNewBalance);
        accountService.updateAccount(account);
        applicationEventPublisher.publishEvent(withdrawTransaction);

        transactionServiceLogger.info("---------------End  executed successfully for with draw--------------------");
    }


/*
    *//**
     * @param accountNumber
     * @param eTranType
     * @return
     *//*
    public List<Transaction> transactionsByAccountNumberAndTransactionType(Long accountNumber, ETranType eTranType) {

        return transactionRepository.findTransactionByAccountNumberAndTranType(accountNumber, eTranType);
    }*/

    /**
     * Used    for  deposit transactions
     *
     * @param depositDto the deposit object
     */
    public void handleDeposit(DepositDto depositDto) {
        Account receiverAccount = accountService.findAccountByAccountNumber(depositDto.getReceiverAccountNumber());
        Account senderAccount = accountService.findAccountByAccountNumber(depositDto.getSenderAccountNumber());
        String transactionReference = TransactionReferenceGenerator.getAlphaNumericString(32);

        validateSufficientFunds(senderAccount, depositDto.getAmount());

        if (receiverAccount != null) {
            executeTransactionForReceiver(receiverAccount, senderAccount, depositDto, transactionReference);
        }

        assert receiverAccount != null;
        executeTransactionForSender(senderAccount, receiverAccount, depositDto, transactionReference);
    }

    /**
     * @param listOfInterBankTransactionsDTo
     */
    @Transactional
    public void processTransactionsForOtherBanks(List<InterBankTransactionsDTo> listOfInterBankTransactionsDTo) {
        if (!listOfInterBankTransactionsDTo.isEmpty()) {
            for (InterBankTransactionsDTo interbankTransactions : listOfInterBankTransactionsDTo) {
                Optional<Bank> bankActingOnBehalf = bankService.findBankByBankCode(interbankTransactions.getActingOnBehalfBankCode());
                Optional<Bank> accountHolderBank = bankService.findBankByBankCode(interbankTransactions.getAccountHolderBankCode());
                if (bankActingOnBehalf.equals(accountHolderBank)) {
                    transactionServiceLogger.error("Transaction not permitted ");
                    throw new InterbankTransactionException("Can not  process transactions , banks are the same ");
                }
                //check if the  bank has that account linked  to it
                Account account = accountService.findAccountByAccountNumberAndBankId(interbankTransactions.getAccountNumber(), accountHolderBank.get().getId());
                if (account != null) {
                    EPostingType postingType = interbankTransactions.getEPostingType();
                    interbankTransactions.setETranType(ETranType.ACT_ON_BEHALF);
                    switch (postingType) {
                        case CREDIT:
                            transactionServiceLogger.info("Posting a CREDIT transaction for account number : {} , with an amount of : {}", interbankTransactions.getAccountNumber(), interbankTransactions.getTransactionAmount());
                            // code block
                            account.setAccountBalance(account.getAccountBalance().add(interbankTransactions.getTransactionAmount()));
                            accountService.updateAccount(account);
                            postBankActingOnBehalfOfTransactions(listOfInterBankTransactionsDTo);
                            break;
                        case DEBIT:
                            transactionServiceLogger.info("Posting a DEBIT transaction for account number : {} , with an amount of : {}", interbankTransactions.getAccountNumber(), interbankTransactions.getTransactionAmount());
                            // code block
                            account.setAccountBalance(account.getAccountBalance().subtract(interbankTransactions.getTransactionAmount()));
                            accountService.updateAccount(account);
                            postBankActingOnBehalfOfTransactions(listOfInterBankTransactionsDTo);
                            break;
                        default:
                            transactionServiceLogger.warn("______________________NO TRANSACTIONS TO PROCESS___________________");
                    }
                }
            }
        }
    }

    /**
     * @param listOfInterBankTransactionsDTo one or more transactions to process
     */
    private void postBankActingOnBehalfOfTransactions(List<InterBankTransactionsDTo> listOfInterBankTransactionsDTo) {
        if (!listOfInterBankTransactionsDTo.isEmpty()) {
            for (InterBankTransactionsDTo interBankTransactionsDTo : listOfInterBankTransactionsDTo) {
                transactionServiceLogger.info("posting transactions on behalf of bank started successfully", interBankTransactionsDTo.getActingOnBehalfBankCode());
                Transaction transaction = new Transaction();
                transaction.setTranType(interBankTransactionsDTo.getETranType());
                transaction.setNarrative(interBankTransactionsDTo.getNarrative());
                transaction.setTransactionAmount(interBankTransactionsDTo.getTransactionAmount());
                transaction.setTranType(ETranType.ACT_ON_BEHALF);
                transaction.setAcquiringInstitution(interBankTransactionsDTo.getActingOnBehalfBankCode());
                transaction.setEPostingType(interBankTransactionsDTo.getEPostingType());
                transactionRepository.save(transaction);
                transactionServiceLogger.info("posting transactions on behalf of bank completed successfully", interBankTransactionsDTo.getActingOnBehalfBankCode());

            }

        }
    }

    /**
     * @param internalTransactionDto
     * @throws Exception
     */
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
        postInternalTransactions(internalTransactionDto);
    }

    /**
     * Used to post internal transactions
     *
     * @param internalTransactionDto the  transaction  object
     */
    public void postInternalTransactions(InternalTransactionDto internalTransactionDto) {
        transactionServiceLogger.info("Creating a payment of : {} , from account : {} , to account :{}", internalTransactionDto.getTransactionAmount(), internalTransactionDto.getSenderAccount(), internalTransactionDto.getReceiverAccount());
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(internalTransactionDto.getTransactionAmount());
        transaction.setSenderAccount(internalTransactionDto.getSenderAccount());
        transaction.setReceiverAccount(internalTransactionDto.getReceiverAccount());
        transaction.setTransactionCharges(internalTransactionDto.getTransactionAmount().multiply(bankTransactionCharges));
        transaction.setReference(internalTransactionDto.getNarrative());
        transaction.setTranType(internalTransactionDto.getETranType());
        transactionRepository.save(transaction);
        applicationEventPublisher.publishEvent(transaction);  //publish event
        transactionServiceLogger.info("==========================================================================================================");
        transactionServiceLogger.info("---------------transaction executed successfully------------------");
    }

    /**
     *
     * @param accountNumber
     * @return
     */
    public List<Transaction> transactionsByAccountNumber(Long accountNumber) {

        return transactionRepository.findTransactionByAccountNumber(accountNumber);
    }
}
