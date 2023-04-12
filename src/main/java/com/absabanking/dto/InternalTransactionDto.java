package com.absabanking.dto;

import com.absabanking.enums.ETranType;
import com.absabanking.model.AbstractEntity;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;


@Data
public class InternalTransactionDto extends AbstractEntity {
    @NotNull
    private Long senderAccount;
    @NotNull
    private Long receiverAccount;
    private String narrative;
    private BigDecimal transactionAmount;
    @Enumerated(EnumType.STRING)
    private ETranType eTranType;
}
