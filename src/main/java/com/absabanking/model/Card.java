package com.absabanking.model;

import com.absabanking.enums.EBankCardStatus;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private long cardNumber;

    private long pin ;
    private int cvv ;
    @Enumerated(EnumType.STRING)

    private EBankCardStatus eBankCardStatus;
    @Column(name = "card_expiry")
    private LocalDateTime cardExpiry =LocalDateTime.now().plusYears(1);
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;
    public Card(long cardNumber) {
        this.cardNumber = cardNumber;
    }
}
