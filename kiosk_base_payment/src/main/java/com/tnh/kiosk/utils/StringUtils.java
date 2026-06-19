package com.tnh.kiosk.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Slf4j
public class StringUtils {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private StringUtils() {
    }

    public static String generateRandomString(int length) {
        var sb = new StringBuilder(length);
        for (var i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String shortenString(String inputStr, String delimiter, Boolean removeAscii) {
        // get first letter of each word and get all for last word
        String[] words = inputStr.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            result.append(words[i].charAt(0)).append(delimiter);
        }
        if (words.length > 0) {
            result.append(words[words.length - 1]);
        }
        if (removeAscii) {
            result = new StringBuilder(removeAccents(result.toString()));
        }
        return result.toString();
    }

    public static String generateRandomStringNum(int length) {
        var sb = new StringBuilder(length);
        for (var i = 0; i < length; i++) {
            sb.append(CHARACTERS.concat(NUMBERS).charAt(RANDOM.nextInt(CHARACTERS.concat(NUMBERS).length())));
        }
        return sb.toString();
    }

    public static String generateRandomNumber(int length) {
        var sb = new StringBuilder(length);
        for (var i = 0; i < length; i++) {
            sb.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        }
        return sb.toString();
    }

    public static String removeAccents(String text) {
        var normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        var pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replace("đ", "d").replace("Đ", "D");
    }

    public static String toSlug(String input) {
        var slug = input.toLowerCase().trim();
        slug = removeVietnameseAccent(slug);
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        slug = slug.replaceAll("\\s+", "_");
        slug = slug.replaceAll("(^_+)|(_+$)", "");
        return slug;
    }

    public static String removeVietnameseAccent(String input) {
        return removeAccents(input);
    }

}
