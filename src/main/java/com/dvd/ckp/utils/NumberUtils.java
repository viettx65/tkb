package com.dvd.ckp.utils;

public class NumberUtils {

    public static boolean isNumber(String value) {
        String regex = "[0-9]+";
        return value.matches(regex);
    }

    public static void main(String[] arg) {
        String regex = "[0-9]+";
        String data = "23343453a";
        System.out.println(data.matches(regex));
    }
}
