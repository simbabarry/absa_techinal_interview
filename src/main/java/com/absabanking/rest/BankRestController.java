package com.absabanking.rest;

import com.absabanking.exception.BankExitsException;
import com.absabanking.model.Bank;
import com.absabanking.service.BankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank")
@Api(value = "banks", description = "Manage banks")
@CrossOrigin
public class BankRestController {
    private final BankService bankService;

    @Autowired
    public BankRestController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping(value = {"", "/"})
    @ApiOperation(value = "Home for the bank rest service")
    public ResponseEntity home() {
        return new ResponseEntity("Bank service is running", HttpStatus.OK);
    }

    @PostMapping("/`create`")
    @ApiOperation(value = "Create new bank")
    ResponseEntity createBank(@RequestBody Bank bank) {
        bankService.createOrUpdateBank(bank);
        return new ResponseEntity("Bank saved successfully", HttpStatus.OK);
    }

    @GetMapping("/list")
    @ApiOperation(value = "View a list of available banks", response = Iterable.class)
    public Iterable list() {
        return bankService.getAllBanks();
    }

    @GetMapping(value = "/find/{id}", produces = "application/json")
    @ApiOperation(value = "Search a bank with an ID", response = Bank.class)
    public Bank getBranchById(@PathVariable("id") long id) {
        return bankService.findBankById(id);
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "Total number of banks")
    public long banksCount() {
        return bankService.banksCount();
    }
}
