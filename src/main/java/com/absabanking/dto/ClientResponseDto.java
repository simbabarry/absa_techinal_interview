package com.absabanking.dto;

import com.absabanking.enums.EPreferredContactType;
import com.absabanking.enums.ERace;
import com.absabanking.enums.ESex;
import com.absabanking.model.Address;
import com.absabanking.model.Contact;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClientResponseDto {
    private String clientName;
    private String clientSurname;
    private String clientIDNumber;
    private String clientPassportNumber;
    private int dependents ;
    private ESex esex ;
    private LocalDate dateOfBirth;
    private BigDecimal monthlyExpenses;
    private EPreferredContactType ePreferredContactType;
    private boolean receiveNotification ;
    private String eRace ;
    private Address clientAddress;
    private Contact clientContact;
    private Long accountNumber;
    private BigDecimal accountLimit;
    private BigDecimal accountBalance;
    private String accountType;
    private LocalDate servicingDate;

    public ClientResponseDto(String clientName, String clientSurname, String clientIDNumber, String clientPassportNumber, int dependents, ESex esex, LocalDate dateOfBirth, BigDecimal monthlyExpenses, EPreferredContactType ePreferredContactType, boolean receiveNotification, String eRace, Address clientAddress, Contact clientContact, Long accountNumber, BigDecimal accountLimit, BigDecimal accountBalance, String accountType, LocalDate servicingDate) {
        this.clientName = clientName;
        this.clientSurname = clientSurname;
        this.clientIDNumber = clientIDNumber;
        this.clientPassportNumber = clientPassportNumber;
        this.dependents = dependents;
        this.esex = esex;
        this.dateOfBirth = dateOfBirth;
        this.monthlyExpenses = monthlyExpenses;
        this.ePreferredContactType = ePreferredContactType;
        this.receiveNotification = receiveNotification;
        this.eRace = eRace;
        this.clientAddress = clientAddress;
        this.clientContact = clientContact;
        this.accountNumber = accountNumber;
        this.accountLimit = accountLimit;
        this.accountBalance = accountBalance;
        this.accountType = accountType;
        this.servicingDate = servicingDate;
    }


}
