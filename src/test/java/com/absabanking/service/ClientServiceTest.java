package com.absabanking.service;

import com.absabanking.enums.EPreferredContactType;
import com.absabanking.enums.ESex;
import com.absabanking.exception.ClientAlreadyExistsException;
import com.absabanking.model.*;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.BankRepository;
import com.absabanking.repository.CardRepository;
import com.absabanking.repository.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@RunWith(JUnit4.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private BankRepository bankRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ClientRepository clientRepository;

    /*@BeforeEach
    public void setUp() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
    }*/

    @Test
    public void clientExistingShouldThrowClientAlreadyExistsException() {

        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();
        clientService.createBankClient(client, bank.getBankCode());

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        Client client2 = createClient();

        Mockito.when(clientRepository.existsByIdNumber(client2.getIdNumber())).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class,
                () -> clientService.createBankClient(client2, bank.getBankCode()));
    }

    @Test
    public void givenSeedForAccountNumberReturnSeedIfClientIsFirst() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();


        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(null);

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(client, bank.getBankCode());

        assertEquals(new ArrayList<>(client.getAccounts()).get(0).getBankCards().get(0).getCardNumber(), 519700000000l);
    }

    @Test
    public void givenSeedForAccountNumberReturnSeedPlusOneIfClientIsNotFirst() {

    }

    /*
    Card accountBankCard = new Card();
        Card lastSavedCardNumber = cardRepository.findTopByOrderByCardNumberDesc();
        accountBankCard.setCardNumber(lastSavedCardNumber != null ? lastSavedCardNumber.getCardNumber() + 1 : 519700000000l);
        accountBankCard.setEBankCardStatus(EBankCardStatus.NEW);
     */

    /*
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
     */

    @Test
    void createBankClient() {
    }

    public Bank createBank() {
        return new Bank("ABSA", "ABSA_" + new Random().nextInt(), EPreferredContactType.SMS);
    }

    private Client createClient() {
        Client client = new Client();
        Address address = new Address("Unit 151 Chianti Lifestyle", "80 Leeukwop road", 2196L, "Sandton");
        client.setClientAddress(address);
        Contact contact = new Contact(774989776L, "sm@gmail.com",9929332L);
        client.setClientContact(contact);
        client.setDateOfBirth(LocalDate.now().minusYears(20));
        client.setDependents(4);
        client.setEducation("Bachelors");
        client.setESex(ESex.FEMALE);
        client.setIdNumber(96665676L);
        client.setMonthlyExpenses(200);
        client.setEPreferredContactType(EPreferredContactType.SMS);
        client.setPassportNumber("AAD7654");
        client.setRace("African");
        client.setReceiveNotification(true);
        client.setSurname("Makwangudze");
        return client;
    }
}