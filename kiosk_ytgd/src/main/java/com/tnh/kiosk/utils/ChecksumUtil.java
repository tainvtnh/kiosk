package com.tnh.kiosk.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class ChecksumUtil {

    public String calculateInquiryChecksum(String strGetBillCheckSum) {
        return md5(strGetBillCheckSum);
    }

    public String calculatePaymentChecksum(String strPaymentCheckSum) {
        return md5(strPaymentCheckSum);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
