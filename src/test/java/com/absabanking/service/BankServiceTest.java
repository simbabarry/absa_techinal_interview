package com.absabanking.service;

import com.absabanking.enums.EPreferredContactType;
import com.absabanking.model.Bank;
import com.absabanking.repository.BankRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.annotation.Resource;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RunWith(JUnit4.class)
class BankServiceTest {

    @Resource
    private BankRepository bankRepository;

    private Bank bank = new Bank("ABSA", "ABSA_" + new Random().nextInt(), EPreferredContactType.SMS);

    @AfterEach
    private void cleanUp() {
        bankRepository.delete(bank);
    }

    @Test
    public void saveBank() {
        addBank();
        Bank bank2 = bankRepository.findBankByBankCode(bank.getBankCode());
        assertEquals(bank.getBankCode(), bank2.getBankCode());
    }
    @Test
    void findBankByBankCode() {
        addBank();

        assertNotNull(bankRepository.findBankByBankCode(bank.getBankCode()), "Passing bank code should return a non null bank object");
    }

    @Test
    void doesBankAlreadyExist() {

        addBank();
        assertTrue(bankRepository.existsByBankCode(bank.getBankCode()), String.format("Bank with code %s already exists", bank.getBankCode()));
    }

    private void addBank() {
        bankRepository.save(bank);
    }
}