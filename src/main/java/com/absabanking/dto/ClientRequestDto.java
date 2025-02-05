package com.absabanking.dto;

import com.absabanking.enums.EPreferredContactType;
import com.absabanking.enums.ERace;
import com.absabanking.enums.ESex;
import com.absabanking.model.Address;
import com.absabanking.model.Contact;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ClientRequestDto {

    private String clientName;
    private String clientSurname;
    private String clientIDNumber;
    private String clientPassportNumber;
    private int dependents ;
    private ESex esex ;
    private LocalDate dateOfBirth;
    private BigDecimal  monthlyExpenses;
    private EPreferredContactType ePreferredContactType;
    private boolean receiveNotification ;
    private ERace eRace ;
    private Address clientAddress;
    private Contact clientContact;

}