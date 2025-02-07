package com.absabanking.exception;

public class InvalidEmailAddressException  extends RuntimeException{
    public InvalidEmailAddressException(String message) {
        super(message);
    }
}
