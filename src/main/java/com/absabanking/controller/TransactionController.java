package com.absabanking.controller;

import com.absabanking.dto.*;
import com.absabanking.enums.ETranType;
import com.absabanking.model.Transaction;
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
@RequestMapping("/api/transaction")
@Api(value = "banks", description = "Manage transactions")
@CrossOrigin
public class TransactionController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Transactional
    @PostMapping("/internal-transfers")
    @ApiOperation(value = "Customers should be able to move money between their accounts")
    public ResponseEntity internalTransfers(@RequestBody InternalTransactionDto internalTransactionDto) throws Exception {
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

    @Transactional
    @PostMapping("/deposit")
    //@ApiOperation(value = "deposit- transaction")
    public void deposit(@RequestBody DepositDto depositDto) {
        transactionService.handleDeposit(depositDto);
    }

    @GetMapping("/list")
    @ApiOperation(value = "View a list of all transactions", response = Iterable.class)
    public Iterable list() {
        return transactionService.getAllTransactions();
    }


    @Transactional
    @PostMapping("/withdraw")
    //@ApiOperation(value = "deposit- transaction")
    public ResponseEntity<WithDrawResponseDto> withdrawal(@RequestBody WithDrawRequestDto withDrawRequestDto) {
        WithDrawResponseDto withDrawResponseDto = transactionService.handleWithdrawal(withDrawRequestDto);
        return new ResponseEntity<>(withDrawResponseDto, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/cashDeposit")
    @ApiOperation(value = "deposit- transaction")
    public void cashDeposit(@RequestBody DepositDto depositDto) {
        transactionService.handleCashDeposit(depositDto);
    }

    @Transactional
    @GetMapping("/{accountNumber}")
    @ApiOperation(value = "find transactions by account number")
    public List<Transaction> transactionsByAccountNumber(@PathVariable Long accountNumber) {
        List<Transaction> tranList;
        tranList = transactionService.transactionsByAccountNumber(accountNumber);
        return tranList;
    }

    @Transactional
    @GetMapping("/tranTypeAndAccountNumber/{accountNumber}/{eTranType}")
    @ApiOperation(value = "deposit- transaction")
    public List<Transaction> transactionsByAccountNumberAndType(@PathVariable Long accountNumber, @PathVariable ETranType eTranType) {
        List<Transaction> tranList;
        tranList = transactionService.transactionsByAccountNumberAndTransactionType(accountNumber, eTranType);
        return tranList;

    }

}
