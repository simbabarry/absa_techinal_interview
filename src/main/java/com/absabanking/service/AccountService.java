package com.absabanking.service;

import com.absabanking.enums.EAccountType;
import com.absabanking.enums.EBankCardStatus;
import com.absabanking.model.Account;
import com.absabanking.model.Bank;
import com.absabanking.model.Card;
import com.absabanking.model.Client;
import com.absabanking.payload.request.AccountCreationRequest;
import com.absabanking.payload.response.AccountCreationResponse;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class AccountService {
    @Autowired
    public AccountService(BankService bankService, CardRepository cardRepository, AccountRepository accountRepository, ClientService clientService, ApplicationEventPublisher applicationEventPublisher) {
        this.bankService = bankService;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.clientService = clientService;
        this.applicationEventPublisher = applicationEventPublisher;
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AccountService.class);
    private final BankService bankService;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final ApplicationEventPublisher applicationEventPublisher;




    @Value("${bank.joining_fee.savings_account}")
    private BigDecimal savingsAccountJoiningBonus;
    @Value("${bank.joining_fee.current_account}")
    private BigDecimal currentAccountJoiningBonus;
    @Value("${bank.account.limit.savings}")
    private BigDecimal savingsAccountLimit;
    @Value("${bank.account.limit.current}")
    private BigDecimal currentAccountLimit;

    public AccountCreationResponse createAccount(AccountCreationRequest accountCreationRequest, String bankCode) {
        Account accountCreation = new Account();
        Card accountBankCard = new Card();
        //retrieve bank details
        Optional<Bank> clientBank = bankService.findBankByBankCode(bankCode);
        if (clientBank.isPresent()) {
            accountCreation.setBank(clientBank.get());
        }
        if (StringUtils.hasText(String.valueOf(accountCreationRequest.getClientIDNumber()))) {
            //create a  card linked to both accounts savings and current
            Card lastSavedCardNumber = cardRepository.findTopByOrderByCardNumberDesc();
            accountBankCard.setCardNumber(lastSavedCardNumber != null ? lastSavedCardNumber.getCardNumber() + 1 : 519700000000l);
            accountBankCard.setEBankCardStatus(EBankCardStatus.NEW);

            //add the  card to a  List
            List<Card> savingsAccountBannkCardList = new ArrayList<>();
            savingsAccountBannkCardList.add(accountBankCard);
            Client client = clientService.findClientByPassportNumberOrClientIDNUmber(accountCreationRequest.getPassportNumber(),accountCreationRequest.getClientIDNumber());

            //create a savings account;
            accountCreation.setClient(client);
            accountCreation.setAccountType(EAccountType.SAVINGS.toString());
            //retrieve the most recent account_number allocated and add 1 to the value or just a use default/start from the provided
            Account lastSavedAccountForTypeSavings = accountRepository.findTopByOrderByAccountNumberDesc();
            accountCreation.setAccountNumber(lastSavedAccountForTypeSavings != null ? lastSavedAccountForTypeSavings.getAccountNumber() + 1 : 100000000000l);

            accountCreation.setAccountBalance(savingsAccountJoiningBonus);
            accountCreation.setAccountLimit(savingsAccountLimit);
            accountCreation.addCards(savingsAccountBannkCardList);
        } else if (StringUtils.hasText(String.valueOf(accountCreationRequest.getPassportNumber()))) {
            //create a  card linked to both accounts savings and current
            Card lastSavedCardNumber = cardRepository.findTopByOrderByCardNumberDesc();
            Client client = clientService.findClientByPassportNumberOrClientIDNUmber(accountCreationRequest.getPassportNumber(),accountCreationRequest.getClientIDNumber());

            accountBankCard.setCardNumber(lastSavedCardNumber != null ? lastSavedCardNumber.getCardNumber() + 1 : 519700000000l);
            accountBankCard.setEBankCardStatus(EBankCardStatus.NEW);

            //add the  card to a  List
            List<Card> savingsAccountBannkCardList = new ArrayList<>();
            savingsAccountBannkCardList.add(accountBankCard);

            //create a savings account;
            accountCreation.setClient(client);

            accountCreation.setAccountType(EAccountType.SAVINGS.toString());
            //retrieve the most recent account_number allocated and add 1 to the value or just a use default/start from the provided
            Account lastSavedAccountForTypeSavings = accountRepository.findTopByOrderByAccountNumberDesc();
            accountCreation.setAccountNumber(lastSavedAccountForTypeSavings != null ? lastSavedAccountForTypeSavings.getAccountNumber() + 1 : 100000000000l);
            accountCreation.setAccountBalance(savingsAccountJoiningBonus);
            accountCreation.setAccountLimit(savingsAccountLimit);
            accountCreation.addCards(savingsAccountBannkCardList);
        }
        accountRepository.save(accountCreation);
        //TODO :TICKET 101 check why the  account is not publishing
        applicationEventPublisher.publishEvent(accountCreation);
        AccountCreationResponse accountCreationResponse = AccountCreationResponse.builder().
                accountNumber(accountCreation.getAccountNumber()).
                accountBalance(accountCreation.getAccountBalance()).
                accountLimit(accountCreation.getAccountLimit()).
                accountType(accountCreation.getAccountType()).
                servicingDate(accountCreation.getServicingDate())
                .build();
        return accountCreationResponse;
    }
    public Account findAccountByAccountNumberAndAccountTypeIsCurrentAccount(Long accountNumber) {
        return accountRepository.findAccountByAccountNumber(accountNumber);
    }

    public Account findAccountByAccountNumber(Long accountNumber) {
        return accountRepository.findAccountByAccountNumber(accountNumber);
    }

    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

    public Account findAccountByAccountNumberAndBankId(Long accountNumber, Long bankId) {
        return accountRepository.findAccountByAccountNumberAndBankId(accountNumber, bankId);
    }
}
