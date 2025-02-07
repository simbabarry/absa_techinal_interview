package com.absabanking.dto;

import com.absabanking.enums.EPreferredContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.Objects;


@Getter
@Setter
public class BankDto {
    @NotBlank(message = "Bank name cannot be empty")
    @Size(min = 20, max = 50, message = "Bank name must be between 3 and 50 characters")
    private String bankName;
    @NotBlank(message = "Bank code cannot be empty")
    @Size(min = 3, max = 10, message = "Bank code must be between 3 and 10 characters")
    private String bankCode;
    private AddressDto bankAddress;
    private ContactDto bankContact;
    @Enumerated
    private EPreferredContactType ePreferredContactType;

    public BankDto(String bankName, String bankCode, EPreferredContactType ePreferredContactType) {
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.ePreferredContactType = ePreferredContactType;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BankDto bank = (BankDto) o;
        return bankName.equals(bank.bankName) && bankCode.equals(bank.bankCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bankName, bankCode);
    }
}
