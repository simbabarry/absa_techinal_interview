package com.absabanking.model;

import javax.persistence.*;

import com.absabanking.enums.EPreferredContactType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Bank extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bank_name")
    private String bankName;
    private String bankCode;
    @Embedded
    private Address bankAddress;
    @Embedded
    private Contact bankContact;
    @Enumerated
    private EPreferredContactType ePreferredContactType;

    public Bank(String bankName, String bankCode, EPreferredContactType ePreferredContactType) {
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.ePreferredContactType = ePreferredContactType;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Bank bank = (Bank) o;
        return bankName.equals(bank.bankName) && bankCode.equals(bank.bankCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bankName, bankCode);
    }
}
