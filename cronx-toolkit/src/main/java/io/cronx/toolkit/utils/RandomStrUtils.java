package io.cronx.toolkit.utils;

import java.util.Random;

public class RandomStrUtils {

    private static final char[] specialChars = { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-', '=' };

    /**
     * generator random string with number or character
     */
    public static String fixedLenRandomStr(int length) {
        if (length == 0) {
            return "";
        }

        char[] fixedLenRandomCharArr = new char[length];
        int flag = 0;
        for (int i = 0; i < length; i++) {
            flag = (int) (Math.random() * 2);
            if (flag == 0) {
                // 产生数字
                int charVal = (int) (Math.random() * 10 + 48);
                fixedLenRandomCharArr[i] = (char) charVal;
            } else {
                // 产生小写字母
                int charVal = (int) ((Math.random() * 26) + 97);
                fixedLenRandomCharArr[i] = (char) charVal;
            }

        }
        String result = new String(fixedLenRandomCharArr);
        return result;
    }

    /**
     * generate fixed length random string
     *
     * @param length
     * @return
     */
    public static String fixedLenRandomStrWithSpecialChars(int length) {
        if (length == 0) {
            return "";
        }

        char[] fixedLenRandomCharArr = new char[length];
        for (int i = 0; i < length; i++) {
            int flag = (int) (Math.random() * 3);
            if (flag == 0) {
                // 产生数字
                int charVal = (int) (Math.random() * 10 + 48);
                fixedLenRandomCharArr[i] = (char) charVal;
            } else if (flag == 1) {
                // 产生小写字母
                int charVal = (int) ((Math.random() * 26) + 97);
                fixedLenRandomCharArr[i] = (char) charVal;
            } else {
                Random r = new Random(System.nanoTime());
                char charVal = specialChars[r.nextInt(specialChars.length)];
                fixedLenRandomCharArr[i] = charVal;
            }
        }
        String result = new String(fixedLenRandomCharArr);
        return result;
    }

    /**
     * generator random string with number
     *
     * @param length
     * @return
     */
    public static String fixedLenRandomNumberStr(int length) {
        if (length == 0) {
            return "";
        }

        char[] fixedLenRandomCharArr = new char[length];
        boolean first = true;
        for (int i = 0; i < length; i++) {
            int charVal = (int) (Math.random() * 10 + 48);
            // first number can not be zero
            if (charVal == 0 && first) {
                i--;
                continue;
            }

            if (first) {
                first = false;
            }

            fixedLenRandomCharArr[i] = (char) charVal;
        }

        String result = new String(fixedLenRandomCharArr);
        return result;
    }
}
