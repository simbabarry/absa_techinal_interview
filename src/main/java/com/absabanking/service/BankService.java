package com.absabanking.service;

import com.absabanking.exception.BankExitsException;
import com.absabanking.model.Address;
import com.absabanking.model.Bank;
import com.absabanking.model.Contact;
import com.absabanking.repository.BankRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BankService {
    private final BankRepository bankRepository;

    @Autowired
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    /**
     * Entry point  to create a new Bank
     *
     * @param bank the  bank to create
     */
    public void createBank(Bank bank) {
        bankRepository.save(bank);
    }

    /**
     * @param bank
     */
    public void createOrUpdateBank(Bank bank) {
        if (bankRepository.existsByBankCode(bank.getBankCode())) {
            throw new BankExitsException("Bank already exist... did you want to update ?");
        }
        bankRepository.save(bank);
    }

    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }

    /**
     *
     * @param id look up a bank by the bank id
     * @return the bank
     */
    public Optional<Bank> findBankById(long id) {

        return bankRepository.findById(id);
    }

    public long banksCount() {
        return bankRepository.count();
    }

    public Optional<Bank> findBankByBankCode(String findBankByBankCode) {
        return Optional.ofNullable(bankRepository.findBankByBankCode(findBankByBankCode));
    }
    public boolean doesBankAlreadyExist(String bankCode) {
        return bankRepository.existsByBankCode(bankCode);
    }

    /**
     *
     * @param bank the bank to be updated
     */
    public void updateBank(Bank bank) {
        bankRepository.save(bank);
    }
}
