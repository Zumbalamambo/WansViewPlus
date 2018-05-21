package com.ajcloud.wansview.support.core.bean;

import android.text.TextUtils;

/**
 * Created by mamengchao on 2018/05/21.
 * 通用的应答bean
 */
public class ResponseBean<T> {
    public String status;
    public String code;
    public String message;
    public T result;

    public int getResultCode() {
        int resultCode = -1;
        try {
            resultCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return resultCode;
    }

    public boolean isSuccess() {
        return TextUtils.equals("ok", status.toLowerCase());
    }
}
