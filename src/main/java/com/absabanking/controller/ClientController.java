package com.absabanking.controller;

import com.absabanking.dto.ClientResponseDto;
import com.absabanking.dto.ClientRequestDto;
import com.absabanking.model.Client;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.CardRepository;
import com.absabanking.service.BankService;
import com.absabanking.service.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Api(value = "clients", description = "Manage clients")
@CrossOrigin
public class ClientController {

    private final ClientService clientService;
    private final BankService bankService;
    private AccountRepository accountRepository;
    private CardRepository cardRepository;

    @Autowired
    public ClientController(ClientService clientService, BankService bankService, AccountRepository accountRepository, CardRepository cardRepository) {
        this.clientService = clientService;
        this.bankService = bankService;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
    }

   /* @GetMapping(value = {"", "/"})
    @ApiOperation(value = "Home for the bank rest service")
    public ResponseEntity home() {
        return new ResponseEntity(ResponseCode.B001, HttpStatus.OK);
    }*/

 /*   @PostMapping("/{bankCode}/create")
    @ApiOperation(value = "Create new bank client")
    ResponseEntity createClient(@RequestBody Client client, @PathVariable String bankCode) {
        clientService.createBankClient(client, bankCode);
        return new ResponseEntity(ResponseCode.C000.getDescription(), HttpStatus.OK);
    }*/


    @PostMapping("/client/")
    @ApiOperation(value = "Create new bank client")
    ResponseEntity<ClientRequestDto> createClient( @RequestBody ClientRequestDto client) {
        clientService.createBankClient(client);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    @GetMapping("/client/idNumber")
    @ApiOperation(value = "Get client by id number")
    public ResponseEntity<ClientResponseDto> getClientByIDNumber(@RequestParam int clientIDNumber) {
        ClientResponseDto client = clientService.findClientByClientIDNumber(clientIDNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with clientIDNumber = " + clientIDNumber));
        return new ResponseEntity<>(client, HttpStatus.OK);
    }
    @GetMapping("/client/passport")
    @ApiOperation(value = "Get client by passport number")
    public ResponseEntity<ClientResponseDto> getClientByPassPortNumber(@RequestParam String passPortNumber) {
        ClientResponseDto client = clientService.findClientByPassportNumber(passPortNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with passport = " + passPortNumber));
        return new ResponseEntity<>(client, HttpStatus.OK);
    }
    @GetMapping("/client/account")
    @ApiOperation(value = "Get client by account number")
    public ResponseEntity<Client> getClientByAccountNumber(@RequestParam Long accountNumber) {
        Client client = clientService.findClientByClientAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with account Number = " + accountNumber));
        return new ResponseEntity<>(client, HttpStatus.OK);
    }
 /*   @GetMapping("/list")
    @ApiOperation(value = "View a list of available clients", response = Iterable.class)
    public Iterable list() {
        return clientService.GetAllClients();
    }

    @GetMapping(value = "/find/{id}", produces = "application/json")
    @ApiOperation(value = "Search a client with an ID", response = Bank.class)
    public Client getBranchById(@PathVariable("id") long id) {
        return clientService.findClientById(id);
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "Total number of clients")
    public long clientsCount() {
        return clientService.clientCount();
    }*/
}
