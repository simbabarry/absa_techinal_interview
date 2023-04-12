package com.absabanking.enums;

public enum ResponseCode {
    B000("Bank saved successfully"),
    B001("Bank saved successfully"),
    C000("Customer saved successfully"),
    E708("Bank with given code already exists"),
    E709("Bank with given ID does not exist");
    ResponseCode(String description) {
        this.description = description;
    }
    private String description;
    public  String getDescription() {
        return this.description;
    }
}