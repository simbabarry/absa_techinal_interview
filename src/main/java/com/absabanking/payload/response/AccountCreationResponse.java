package com.absabanking.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class AccountCreationResponse {
    private Long accountNumber;
    private BigDecimal accountLimit;
    private BigDecimal accountBalance;
    private String accountType;
    private LocalDate servicingDate;

}
