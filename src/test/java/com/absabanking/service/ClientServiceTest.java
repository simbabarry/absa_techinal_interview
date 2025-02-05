package com.absabanking.service;

import com.absabanking.dto.ClientRequestDto;
import com.absabanking.enums.EPreferredContactType;
import com.absabanking.enums.ERace;
import com.absabanking.enums.ESex;
import com.absabanking.enums.Eeducation;
import com.absabanking.exception.ClientAlreadyExistsException;
import com.absabanking.model.*;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.BankRepository;
import com.absabanking.repository.CardRepository;
import com.absabanking.repository.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.math.BigDecimal;
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


    /*@Test
    @Disabled
    public void clientWithCorrectDetailsShouldSuccessfulyCreateNewClient() {

        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();

        Mockito.when(clientRepository.existsByIdNumber(Long.valueOf(client.getClientIDNumber()))).thenReturn(false);
        Mockito.when(clientRepository.existsByPassportNumber(client.getPassportNumber())).thenReturn(false);

        Mockito.when(clientRepository.save(client)).thenReturn(client);
        clientService.createBankClient(createClientRequestDto());

        Mockito.verify(clientRepository).save(client);
    }*/

  /*  @Test
    @Disabled
    public void clientExistingShouldThrowClientAlreadyExistsException() {

        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();
        clientService.createBankClient(createClientRequestDto());

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        Client client2 = createClient();

        Mockito.when(clientRepository.existsByIdNumber(Long.valueOf(client2.getClientIDNumber()))).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class,
                () -> clientService.createBankClient(createClientRequestDto()));
    }*/

   /* @Test
    @Disabled
    public void clientWithExistingIdShouldThrowClientAlreadyExistsException() {

        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();
        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(createClientRequestDto());

        Client client2 = createClient();

        Mockito.when(clientRepository.existsByIdNumber(Long.valueOf(client2.getClientIDNumber()))).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class,
                () -> clientService.createBankClient(createClientRequestDto()));
    }*/


    @Test
    @Disabled
    public void givenSeedForAccountNumberReturnSeedIfClientIsFirst() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();


        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(null);

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(createClientRequestDto());

        assertEquals(new ArrayList<>(client.getAccounts()).get(0).getBankCards().get(0).getCardNumber(), 519700000000l);
    }

    @Test
    void createBankClient() {
    }

    @Test
    @Disabled
    public void givenSeedForAccountNumberReturnSeedPlusOneIfClientIsNotFirst() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();
        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(new Card(519700000000l));

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(createClientRequestDto());

        assertEquals(new ArrayList<>(client.getAccounts()).get(0).getBankCards().get(0).getCardNumber(), 519700000000l + 1);
    }

    @Test
    @Disabled
    public void givenNewClientMustHaveTwoAccountsCreated() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();


        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(null);

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(createClientRequestDto());

        assertEquals(2, client.getAccounts().size());
    }

    public Bank createBank() {
        return new Bank("ABSA", "ABSA_" + new Random().nextInt(), EPreferredContactType.SMS);
    }

    private Client createClient() {

        Client client = new Client();
        Address address = new Address("Unit 151 Chianti Lifestyle", "80 Leeukwop road", 2196L, "Sandton");
        client.setClientAddress(address);
        Contact contact = new Contact(774989776L, "smakwangudze@gmail.com", 9929332L);
        client.setClientContact(contact);
        client.setDateOfBirth(LocalDate.now().minusYears(20));
        client.setDependents(4);
        client.setEducation(Eeducation.PHD);
        client.setESex(ESex.FEMALE);
        client.setClientIDNumber(String.valueOf(96665676));
        client.setMonthlyExpenses(BigDecimal.valueOf(200));
        client.setEPreferredContactType(EPreferredContactType.SMS);
        client.setPassportNumber("AAD7654");
        client.setRace("African");
        client.setReceiveNotification(true);
        client.setSurname("Makwangudze");
        return client;
    }

    public ClientRequestDto createClientRequestDto() {

        return ClientRequestDto.builder()
                .clientAddress(new Address("Unit 151 Chianti Lifestyle", "80 Leeukwop road", 2196L, "Sandton"))
                .clientContact(new Contact(774989776L, "smakwangudze@gmail.com", 9929332L))
                .clientIDNumber(String.valueOf(96665676))
                .clientPassportNumber("AAD7654")
                .clientSurname("Makwangudze")
                .eRace(ERace.ASIAN)
                .esex(ESex.FEMALE)
                .clientName("Boss")
                .dependents(2).ePreferredContactType(EPreferredContactType.EMAIL).monthlyExpenses(BigDecimal.valueOf(34443L)).receiveNotification(Boolean.TRUE)
                .build();
    }
}