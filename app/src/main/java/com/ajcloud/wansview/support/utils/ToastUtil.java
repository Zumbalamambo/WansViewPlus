package com.ajcloud.wansview.support.utils;

import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.ajcloud.wansview.application.MainApplication;

/**
 * Created by mamengchao on 2018/05/10.
 * 统一弹Toast
 */

public class ToastUtil {
    private static Toast toast;

    public static void show(String text) {
        Toast.makeText(MainApplication.getApplication(), text, Toast.LENGTH_SHORT).show();
    }

    public static void show(@StringRes int resId) {
        Toast.makeText(MainApplication.getApplication(), resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出多个toast时, 不会一个一个的弹, 后面一个要显示的内容直接显示在当前的toast上
     */
    public static void single(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void singleLong(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 多行居中显示
     */
    public static void singleCenter(@StringRes int msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        ((TextView) toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        toast.show();
    }

    /**
     * 多行居中显示
     */
    public static void singleCenter(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        ((TextView) toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        toast.show();
    }

    /**
     * 弹出多个toast时, 不会一个一个的弹, 后面一个要显示的内容直接显示在当前的toast上
     */
    public static void single(@StringRes int msg) {
        if (toast == null) {
            toast = Toast.makeText(MainApplication.getApplication(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
