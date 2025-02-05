package com.absabanking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WithDrawResponseDto {
    private Long account;
    private BigDecimal amountWithDrawn;
    private BigDecimal newAccountBalance;
    private String message;


}