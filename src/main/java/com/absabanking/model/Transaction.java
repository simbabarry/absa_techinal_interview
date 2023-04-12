package com.absabanking.model;

import com.absabanking.enums.ECurrency;
import com.absabanking.enums.EPostingType;
import com.absabanking.enums.ETranType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderAccount;
    private Long receiverAccount;
    private String narrative;
    @Column(name = "txn_amount")
    private BigDecimal transactionAmount = BigDecimal.ZERO;
    @Column(name = "txn_charges")
    private BigDecimal transactionCharges = BigDecimal.ZERO;
    private String reference;
    private String cardNumber;
    @Column(length = 4, name = "mti")
    private String mti;
    private String processingCode;
    @Column(name = "settlement_amount")
    private long settlementAmount;
    @Column(name = "txn_datetime")
    private LocalDateTime transmissionDateTime = LocalDateTime.now();
    @Column(name = "stan")
    private String stan;
    @Column(name = "local_txn_time")
    private LocalDateTime localTransactionTime = LocalDateTime.now();
    @Column(name = "local_txn_date")
    private LocalDateTime localTransactionDate = LocalDateTime.now();
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate = LocalDateTime.now();
    @Column(name = "settlement_date")
    private LocalDateTime settlementDate = LocalDateTime.now();
    @Column(name = "pos_entry_mode")
    private int pointOfServiceEntryMode;
    @Column(name = "pos_condition_code")
    private int pointOfServiceConditionCode;
    @Column(name = "pos_capture_code")
    private int pointOfServiceCaptureCode;
    @Column(name = "currency_conversion_date")
    private LocalDateTime currencyConversionDate = LocalDateTime.now();
    @Column(name = "merchant_type")
    private int merchantType;
    @Column(name = "acquiring_institute")
    private String acquiringInstitution;
    @Column(name = "amount_txn_fee")
    private BigDecimal amountTransactionFee = BigDecimal.ZERO;
    @Column(name = "amount_txn_")
    private BigDecimal amountTransactionProcessingFee = BigDecimal.ZERO;
    @Column(name = "rrn")
    private String rrn;
    @Column(name = "response_code")
    private String responseCode;
    @Column(name = "svc_restriction_code")
    private String serviceRestrictionCode;
    @Column(name = "card_acceptor_terminal_id")
    private String cardAcceptorTerminalId;
    @Column(name = "card_acceptor_id_code")
    private String cardAcceptorIdentificationCode;
    @Column(name = "card_acceptor_name")
    private String cardAcceptorName;

    @Column(name = "txn_currency_code")
    private String transactionCurrencyCode = ECurrency.ZAR.toString();
    @Transient
    private String additionalAmounts;

    @Column(name = "transaction_code")
    private String transactionCode;
    @Column(name = "card_acceptor_id")
    private long cardAcceptorId;
    @Column(name = "branch_id")
    private long branchId;
    @Column(name = "source")
    private String source;
    @Transient
    private boolean reversal;
    @Column(name = "purchase_amount")
    private String purchaseAmount;
    @Column(name = "channel")
    private String channel;
    @Column(name = "pos_type")
    private String posType;
    @Column(name = "transaction_type_category")
    private String transactionTypeCategory;

    @Transient
    private boolean isOnlineTransaction;
    private long batchId;
    @Enumerated(EnumType.STRING)
    private EPostingType ePostingType;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<TransactionState> transactionStates;
    @Enumerated(EnumType.STRING)
    private ETranType tranType;

}
