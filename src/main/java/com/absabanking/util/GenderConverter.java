package com.absabanking.util;

import com.absabanking.enums.ESex;

public class  GenderConverter {
    public static String genderConverter(String sexType) {
        if (sexType == ESex.FEMALE.toString()) {
            return "Mr";
        } else return "Mrs";
    }
}