package com.absabanking.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithDrawRequestDto {
    private Long accountNumber;
    private BigDecimal amount;

}