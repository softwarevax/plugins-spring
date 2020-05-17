package org.wit.ctw.plugin.dictionary.utils;

public class StringUtils {

    public static boolean isEmpty(String str) {
        if(str == null || "".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean hasText( String str) {
        return str != null && !str.isEmpty();
    }
}
