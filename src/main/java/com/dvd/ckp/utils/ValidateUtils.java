/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author daond
 */
public class ValidateUtils {

    private static final String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String NUMBER_PATTERN = "[0-9.]+";

    public static boolean validateEmail(String hex) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static boolean validate(String value) {
        return (value == null && "".equals(value));
    }

    public static boolean validateNumber(String value) {

        return value.matches(NUMBER_PATTERN);
    }

    public static void main(String[] args) {
        System.out.println("Number: " + validateNumber("1.a3"));
    }
}
