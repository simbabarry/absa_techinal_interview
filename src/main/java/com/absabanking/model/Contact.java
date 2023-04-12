package com.absabanking.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Contact {

    private Long cellNumber;
    private String email;
    private Long homePhone;


}