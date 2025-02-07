package com.absabanking.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class AddressDto {
    @NotBlank(message = "Address line  code cannot be empty")
    @Size(min = 3, max = 10, message = "Address line must be between 3 and 10 characters")
    private String addressLine1;
    private String addressLine2;
    @NotNull(message = "PostalCode contact must not be null")
    private Long postalCode;
    @NotNull(message = "City must not be null")
    private String city;
}