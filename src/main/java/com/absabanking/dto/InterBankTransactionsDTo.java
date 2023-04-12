package com.absabanking.dto;

import com.absabanking.enums.EPostingType;
import com.absabanking.enums.ETranType;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
public class InterBankTransactionsDTo {
    @NotNull
    private Long accountNumber ;
    private String narrative;
    @NotNull

    private BigDecimal transactionAmount;
    @Enumerated(EnumType.STRING)
    private ETranType eTranType;
    //bank code of the  bank acting on behalf
    @NotNull

    private  String actingOnBehalfBankCode;
    // bank code  of the account which was transacting
    @NotNull

    private String accountHolderBankCode ;
    @Enumerated(EnumType.STRING)
    private EPostingType ePostingType;


}
