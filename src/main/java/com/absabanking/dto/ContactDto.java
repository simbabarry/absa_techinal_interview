package com.absabanking.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
public class ContactDto {

    private Long cellNumber;
    @Email(message = "Email must not be empty")
    private String email;
    @NotNull(message = "Bank contact must not be null")
    private Long homePhone;


}