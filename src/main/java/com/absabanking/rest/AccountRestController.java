package com.absabanking.rest;

import com.absabanking.dto.AccountDto;
import com.absabanking.service.AccountService;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRestController {

    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Description("Create Account")
    @PostMapping(value = "/account/create/")
    public long createAccount(@RequestBody AccountDto accountDto) {
        return accountService.createAccount(accountDto);
    }
}
