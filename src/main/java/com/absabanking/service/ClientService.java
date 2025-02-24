package com.absabanking.service;

import com.absabanking.dto.ClientResponseDto;
import com.absabanking.dto.ClientRequestDto;
import com.absabanking.exception.ClientExistsException;
import com.absabanking.model.Client;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.BankRepository;
import com.absabanking.repository.CardRepository;
import com.absabanking.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class ClientService {
    @Autowired
    public ClientService(ClientRepository clientRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.clientRepository = clientRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;
    @Value("${bank.joining_fee.savings_account}")
    private BigDecimal savingsAccountJoiningBonus;
    @Value("${bank.joining_fee.current_account}")
    private BigDecimal currentAccountJoiningBonus;
    @Value("${bank.account.limit.savings}")
    private BigDecimal savingsAccountLimit;
    @Value("${bank.account.limit.current}")
    private BigDecimal currentAccountLimit;
    private final ApplicationEventPublisher applicationEventPublisher;




    public List<Client> GetAllClients() {
        return clientRepository.findAll();
    }

    public Client findClientById(long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public long clientCount() {
        return clientRepository.count();
    }

    public Optional<Client> findClientByPassportNumber(String clientPassportNumber) {
        return Optional.ofNullable(clientRepository.findClientByPassportNumber(clientPassportNumber));
    }

    public Optional<Client> findClientByClientIDNumber(String clientIDNumber) {
        return Optional.ofNullable(clientRepository.findClientByClientIDNumber(clientIDNumber));
    }

    public Client findClientByPassportNumberOrClientIDNUmber(String passportNumber, String clientIDNumber) {
        return clientRepository.findClientByPassportNumberOrClientIDNumber(passportNumber, clientIDNumber);
    }

    public Optional<Client> findClientByClientAccountNumber(Long accountNumber) {
        return Optional.ofNullable(clientRepository.findClientByAccountNumber(accountNumber));
    }

    /**
     * @param clientRequestDto
     */
    public void createBankClient(ClientRequestDto clientRequestDto) {


        if (clientRepository.existsByClientIDNumber(clientRequestDto.getClientIDNumber())) {
            logger.error("Could not create  a clientRequestDto  with an existing ID NUMBER : {} ", clientRequestDto.getClientIDNumber());
            throw new ClientExistsException("Client with Id number : {} already exist... did you want to perhaps update their accounts ?" + clientRequestDto.getClientIDNumber());
        }
        if (clientRepository.existsByPassportNumber(clientRequestDto.getClientPassportNumber())) {
            logger.error("Could not create  a clientRequestDto  with an existing Passport Number : {} ", clientRequestDto.getClientPassportNumber());
            throw new ClientExistsException("Client with Id passport number : {} already exist... did you want to perhaps update their accounts ?" + clientRequestDto.getClientPassportNumber());
        }
        //create the  Client to persist
        Client client = new Client();
        client.setClientAddress(clientRequestDto.getClientAddress());
        client.setClientName(clientRequestDto.getClientName());
        client.setSurname(clientRequestDto.getClientSurname());
        client.setDateOfBirth(clientRequestDto.getDateOfBirth());
        client.setDependents(clientRequestDto.getDependents());
        client.setEPreferredContactType(clientRequestDto.getEPreferredContactType());
        client.setESex(clientRequestDto.getEsex());
        client.setClientIDNumber(clientRequestDto.getClientIDNumber());
        client.setMonthlyExpenses(clientRequestDto.getMonthlyExpenses());
        client.setPassportNumber(clientRequestDto.getClientPassportNumber());
        client.setRace(String.valueOf(clientRequestDto.getERace()));
        client.setReceiveNotification(clientRequestDto.isReceiveNotification());
        client.setClientIDNumber(clientRequestDto.getClientIDNumber());
        client.setClientContact(clientRequestDto.getClientContact());
        client.setClientAddress(clientRequestDto.getClientAddress());

        clientRepository.save(client);
        applicationEventPublisher.publishEvent(client);
    }

  /*  *//**
     * Used  to  create  a new bank client
     *
     * @param client   the  new bank client  to be created
     * @param bankCode the  bank  which the client belongs to
     *//*
    public void createBankClient(Client client, String bankCode) {
        if (clientRepository.existsByIdNumber(client.getClientIDNumber())) {
            logger.error("Could not create  a client  with an existing ID NUMBER : {} ", client.getIdNumber());
            throw new ClientAlreadyExistsException("Client with Id number : {} already exist... did you want to perhaps update their accounts ?" + client.getIdNumber());
        }
        if (clientRepository.existsByPassportNumber(client.getPassportNumber())) {
            logger.error("Could not create  a client  with an existing Passport Number : {} ", client.getPassportNumber());
            throw new ClientAlreadyExistsException("Client with Id passport number : {} already exist... did you want to perhaps update their accounts ?" + client.getPassportNumber());
        }
        //retrieve bank details
        Bank clientBank = bankRepository.findBankByBankCode(bankCode);

        //create a  card linked to both accounts savings and current
        Card accountBankCard = new Card();
        Card lastSavedCardNumber = cardRepository.findTopByOrderByCardNumberDesc();
        accountBankCard.setCardNumber(lastSavedCardNumber != null ? lastSavedCardNumber.getCardNumber() + 1 : 519700000000l);
        accountBankCard.setEBankCardStatus(EBankCardStatus.NEW);

        //add the  card to a  List
        List<Card> savingsAccountBannkCardList = new ArrayList<>();
        savingsAccountBannkCardList.add(accountBankCard);

        //create a savings account;
        Account savingsAccount = new Account();
        savingsAccount.setBank(clientBank);
        savingsAccount.setAccountType(EAccountType.SAVINGS.toString());
        //retrieve the most recent account_number allocated and add 1 to the value or just a use default/start from the provided
        Account lastSavedAccountForTypeSavings = accountRepository.findTopByOrderByAccountNumberDesc();
        savingsAccount.setAccountNumber(lastSavedAccountForTypeSavings != null ? lastSavedAccountForTypeSavings.getAccountNumber() + 1 : 100000000000l);
        Long nextAvailableAccountNumber = savingsAccount.getAccountNumber();

        savingsAccount.setAccountBalance(savingsAccountJoiningBonus);
        savingsAccount.setAccountLimit(savingsAccountLimit);
        savingsAccount.addCards(savingsAccountBannkCardList);
        //create a current account;
        Account currentAccount = new Account();
        currentAccount.setBank(clientBank);
        currentAccount.setAccountType(EAccountType.CURRENT.toString());
        //retrieve the most recent account_number allocated and add 1 to the value or just a use default/start from the provided

        // Account lastSavedAccountForTypeCurrent = accountRepository.findTopByOrderByAccountNumberDesc();
        currentAccount.setAccountNumber(nextAvailableAccountNumber + 1);
        currentAccount.setAccountBalance(currentAccountJoiningBonus);
        currentAccount.setAccountLimit(currentAccountLimit);
        currentAccount.addCards(savingsAccountBannkCardList);

        //link the  2 accounts to  the client
        Set<Account> clientSetOfAccounts = new HashSet<>();
        clientSetOfAccounts.add(savingsAccount);
        clientSetOfAccounts.add(currentAccount);
        client.addAccounts(clientSetOfAccounts);

        //create the client
        client.setClientContact(client.getClientContact());
        client.setClientAddress(client.getClientAddress());
        client.setESex(client.getESex());

        clientRepository.save(client);
    }*/



}
