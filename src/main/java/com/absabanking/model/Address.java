package com.absabanking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
    private String addressLine1;
    private String addressLine2;
    private Long postalCode;
    private String city;
}
