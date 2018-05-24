package com.ajcloud.wansview.support.tools;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mamengchao on 2018/05/10.
 * 正则表达式工具
 */

public class RegularTool {

    private static String SPECIAL_CHAR = "`\\\\~!@#$%^&*.()+=|{}':;'\\[\\],<>/?";

    /**
     * 检测手机号是否合法
     *
     * @param text 手机号
     */
    public static boolean isLegalChinaPhoneNumber(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 检测邮箱地址是否合法
     *
     * @param text 邮箱
     */
    public static boolean isLegalEmailAddress(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
//        String regExp = "[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
        String regExp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 是否包含非法字符
     *
     * @param text
     */
    private static boolean isIllegal(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String regExp = "[^0-9a-zA-Z" + SPECIAL_CHAR + "]";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count > 0;
    }
}
