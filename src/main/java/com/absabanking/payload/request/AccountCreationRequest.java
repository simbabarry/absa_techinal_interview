package com.absabanking.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreationRequest {
    private String clientIDNumber;
    private String passportNumber;

}
