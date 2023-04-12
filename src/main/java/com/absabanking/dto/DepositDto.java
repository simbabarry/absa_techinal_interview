package com.absabanking.dto;

import com.absabanking.enums.ETranType;
import com.absabanking.model.Account;
import com.sun.istack.NotNull;
import lombok.Value;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Value
public class DepositDto {
    @NotNull
    private Long accountNumber;
    @NotNull
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private ETranType eTranType;
}
