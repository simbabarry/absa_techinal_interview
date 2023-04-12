package com.absabanking.service;

import com.absabanking.enums.EAccountType;
import com.absabanking.enums.EBankCardStatus;
import com.absabanking.exception.BankExitsException;
import com.absabanking.exception.ClientAlreadyExistsException;
import com.absabanking.model.Account;
import com.absabanking.model.Bank;
import com.absabanking.model.Card;
import com.absabanking.model.Client;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.BankRepository;
import com.absabanking.repository.CardRepository;
import com.absabanking.repository.ClientRepository;
import com.absabanking.rest.TransactionRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ClientService {
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
    private BankRepository bankRepository;
    private CardRepository cardRepository;
    private AccountRepository accountRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, BankRepository bankRepository, CardRepository cardRepository, AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.bankRepository = bankRepository;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    public List<Client> GetAllClients() {
        return clientRepository.findAll();
    }

    public Client findClientById(long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public long clientCount() {
        return clientRepository.count();
    }

    /**
     * Used  to  create  a new bank client
     *
     * @param client   the  new bank client  to be created
     * @param bankCode the  bank  which the client belongs to
     */
    public void createBankClient(Client client, String bankCode) {
        if (clientRepository.existsByIdNumber(client.getIdNumber())) {
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
    }

}
