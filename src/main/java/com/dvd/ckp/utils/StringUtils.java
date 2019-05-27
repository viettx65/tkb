/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.utils;

import java.text.DecimalFormat;

/**
 *
 * @author dmin
 */
public class StringUtils {

    /**
     * True is value not empty. False is value empty
     *
     * @param input
     * @return
     */
    public static boolean isValidString(String input) {
        return input != null && !"".equals(input);
    }

    public static String formatPrice(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        if (price == null || price == 0d) {
            return "0";
        }
        return formatter.format(price);
    }

    public static Double reFormatTotal(String value) {
        value = value.replace(",", "").replace(".", "");
        Double valueReturn = Double.valueOf(value);
        return valueReturn;

    }

    public static boolean regexNumber(String value) {
        String regex = "[0-9]+";
        return value.matches(regex);
    }

    public static String format(String value) {
        String valReturn = "";
        if (!isValidString(value)) {
            return valReturn;
        }
        Integer val = Integer.valueOf(value.replace("S", "")
                .replace("C", "").replace("T", ""));
        if (val < 10) {
            valReturn = "0" + val;
        } else {
            valReturn = String.valueOf(val);
        }
        return valReturn;

    }

    public static void main(String[] arg) {
        System.out.println("Chuyên viên K71".matches(".* viên.*"));
    }

}
