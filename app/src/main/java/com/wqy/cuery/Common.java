package com.wqy.cuery;

import java.util.regex.Pattern;

/**
 * Created by wqy on 16-10-6.
 */

public class Common {

    private static String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String makeString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(possible.charAt((int) (Math.random() * possible.length())));
        }
        return sb.toString();
    }

    private static String emailExp = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
    private static Pattern pattern = Pattern.compile(emailExp);

    public static String makeEmail(int length, String domain) {
        StringBuilder sb = new StringBuilder(length + domain.length() + 1);
        return sb.append(makeString(length))
                .append("@")
                .append(domain)
                .toString();
    }

    public static boolean emailIsEvalidate(String email) {
        return pattern.matcher(email).matches();
    }
}
