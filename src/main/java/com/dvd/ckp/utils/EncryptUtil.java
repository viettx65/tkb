package com.dvd.ckp.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class EncryptUtil {

    private static final Logger logger = Logger.getLogger(EncryptUtil.class);

    private static final String key = "CKPManagement1.0";
    private static final String initVector = "CKPManagement1.0";

    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            logger.error("Error encrypt: " + ex.getMessage(), ex);
        }

        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            logger.error("Error decrypt: " + ex.getMessage(), ex);
        }

        return null;
    }

    public static void main(String[] args) {

        System.out.println("En:  " + encrypt("654321a@"));
//        System.out.println(decrypt("E3GheFvuOgnDoWcJeA3gZg=="));
//        System.out.println(new String(Base64.encodeBase64("654321a@".getBytes())));
//        System.out.println(new String(Base64.decodeBase64("NjU0MzIxYUA=")));
    }
}
