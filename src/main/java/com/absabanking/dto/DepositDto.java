package com.absabanking.dto;

import com.absabanking.enums.ETranType;
import com.sun.istack.NotNull;
import lombok.Value;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Value
public class DepositDto {
    @NotNull
    private Long receiverAccountNumber;
    @NotNull
    private Long senderAccountNumber;
    @NotNull
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private ETranType eTranType;
}
