package com.absabanking.controller;

import com.absabanking.model.Bank;
import com.absabanking.service.BankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "banks", description = "Manage banks")
@CrossOrigin
public class BankController {
    private final BankService bankService;

    @Autowired
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping(value = {"", "/"})
    @ApiOperation(value = "Home for the bank rest service")
    public ResponseEntity home() {
        return new ResponseEntity("Bank service is running", HttpStatus.OK);
    }


    @PostMapping("/bank")
    @ApiOperation(value = "Create new bank")
    ResponseEntity<Bank> createBank(@RequestBody Bank bank) {
        bankService.createOrUpdateBank(bank);
        return new ResponseEntity<Bank>(bank, HttpStatus.CREATED);
    }
    @GetMapping("/bank/id/{id}")
    public ResponseEntity<Bank> getBankById(@PathVariable("id") long id) {
        Bank bank = bankService.findBankById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with id = " + id));

        return new ResponseEntity<>(bank, HttpStatus.OK);
    }

    @GetMapping("/bank/code")
    public ResponseEntity<Bank> getBankByBankCode(@RequestParam String bankCode) {
        Bank bank = bankService.findBankByBankCode(bankCode)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with bank code = " + bankCode));

        return new ResponseEntity<>(bank, HttpStatus.OK);
    }


    @GetMapping("/banks")
    public ResponseEntity<List<Bank>> getAllBanks() {
        List<Bank> banks = bankService.getAllBanks();
        return new ResponseEntity<>(banks, HttpStatus.OK);
    }

    @PutMapping("/bank/{bankCode}")
    public ResponseEntity<Bank> updateBank(@PathVariable("bankCode") String bankCode, @RequestBody Bank updatedBankDetails) {
        Bank bank = bankService.findBankByBankCode(bankCode)
                .orElseThrow(() -> new ResourceNotFoundException("Bank Not found with code = " + bankCode));
        bank.setBankAddress(updatedBankDetails.getBankAddress());
        bank.setBankContact(updatedBankDetails.getBankContact());
        bank.setBankCode(updatedBankDetails.getBankCode());
        bank.setBankName(updatedBankDetails.getBankName());
        bank.setEPreferredContactType(updatedBankDetails.getEPreferredContactType());
        bankService.updateBank(bank);
        return new ResponseEntity<>(bank, HttpStatus.OK);
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "Total number of banks")
    public long banksCount() {
        return bankService.banksCount();
    }
}
