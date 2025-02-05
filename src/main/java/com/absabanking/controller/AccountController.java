package com.absabanking.controller;

import com.absabanking.payload.request.AccountCreationRequest;
import com.absabanking.payload.response.AccountCreationResponse;
import com.absabanking.service.AccountService;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @Description("Create Account")
    @PostMapping("/account/{bankCode}")
    ResponseEntity<AccountCreationResponse> createAccount(@PathVariable String bankCode, @RequestBody AccountCreationRequest accountCreationRequest) {
        AccountCreationResponse accountCreationResponse = accountService.createAccount(accountCreationRequest, bankCode);
        return new ResponseEntity<>(accountCreationResponse, HttpStatus.CREATED);
    }
}
