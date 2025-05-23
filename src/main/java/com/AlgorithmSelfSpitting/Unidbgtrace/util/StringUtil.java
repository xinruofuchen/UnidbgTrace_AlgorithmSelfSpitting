package com.AlgorithmSelfSpitting.Unidbgtrace.util;

public class StringUtil {
    public static String updateargs(String str) {
        String withoutFirstChar = str.substring(2);
        // 去除尾字符
        String result = withoutFirstChar.substring(0, withoutFirstChar.length() - 1);
        return result;
    }

}
