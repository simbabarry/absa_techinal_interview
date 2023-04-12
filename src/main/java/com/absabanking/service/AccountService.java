package com.absabanking.service;

import com.absabanking.dto.AccountDto;
import com.absabanking.enums.EAccountType;
import com.absabanking.model.Account;
import com.absabanking.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public synchronized long createAccount(AccountDto accountDto) {
        Account account = new Account();
        accountRepository.save(account);
        return account.getAccountNumber();
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
