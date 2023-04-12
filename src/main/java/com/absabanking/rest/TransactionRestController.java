package com.absabanking.rest;

import com.absabanking.dto.DepositDto;
import com.absabanking.dto.InterBankTransactionsDTo;
import com.absabanking.dto.InternalTransactionDto;
import com.absabanking.model.Account;
import com.absabanking.model.Bank;
import com.absabanking.service.AccountService;
import com.absabanking.service.BankService;
import com.absabanking.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;


@RestController
@RequestMapping("/api/bank/transaction")
@Api(value = "banks", description = "Manage transactions")
@CrossOrigin
public class TransactionRestController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionRestController.class);

    private final BankService bankService;
    private final AccountService accountService;

    private final TransactionService transactionService;


    @Autowired
    public TransactionRestController(BankService bankService, AccountService accountService, TransactionService transactionService) {
        this.bankService = bankService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @Transactional
    @PostMapping("/internal-transfers")
    @ApiOperation(value = "Customers should be able to move money between their accounts")
    ResponseEntity internalTransfers(@RequestBody InternalTransactionDto internalTransactionDto) throws Exception {
        transactionService.postInternalTransfer(internalTransactionDto);
        return new ResponseEntity("transaction was saved successful", HttpStatus.OK);
    }
    @PostMapping("/transaction-on-behalf")
    @ApiOperation(value = "Bank X also want to allow Bank Z to be able debit or credit the customer’s account for any " +
            "transactions that were handled by Bank Z on behalf of Bank X. Bank Z should be able to send a single immediate" +
            " transaction or a list of transactions which should be processed immediately.")
    ResponseEntity bankActingOnBehalfOf(@RequestBody List<InterBankTransactionsDTo> listOfInterBankTransactionsDTo) throws Exception {
        transactionService.processTransactionsForOtherBanks(listOfInterBankTransactionsDTo);
        return new ResponseEntity("transaction was saved successful posted", HttpStatus.OK);
    }
    @GetMapping("/list")
    @ApiOperation(value = "View a list of all banks", response = Iterable.class)
    public Iterable list() {
        return transactionService.getAllBanks();
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

    @Transactional
    @PostMapping("/deposit")
    @ApiOperation(value = "deposit- transaction")
    public void getBankTransactionCharges(DepositDto depositDto) {
        Account senderAccount = accountService.findAccountByAccountNumber(depositDto.getAccountNumber());
        transactionService.handleDeposit(depositDto, senderAccount);
    }


}
