package com.absabanking.exception;

public class InterbankTransactionException extends RuntimeException{
    public InterbankTransactionException(String message) {
        super(message);
    }
}
