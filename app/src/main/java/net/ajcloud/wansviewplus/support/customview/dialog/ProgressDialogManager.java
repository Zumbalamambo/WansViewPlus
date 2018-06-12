package net.ajcloud.wansviewplus.support.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import net.ajcloud.wansviewplus.R;

import java.util.HashMap;
import java.util.Map;


public class ProgressDialogManager {
    private static ProgressDialogManager instance;

    private Map<String, CustomProgressDialog> dialogMap = new HashMap();

    private ProgressDialogManager() {
    }

    public static ProgressDialogManager getDialogManager() {
        if (instance == null) instance = new ProgressDialogManager();
        return instance;
    }

    public Dialog showDialog(String key, Context context) {
        return showDialog(key, context, null, null, CustomProgressDialog.DELAYMILLIS_20);
    }

    public Dialog showDialog(String key, Context context, int timeout) {
        return showDialog(key, context, null, null, timeout);
    }

    public Dialog showDialog(String key, Context context, String msg,
                             CustomProgressDialog.OnDialogDismissListener listener) {
        return showDialog(key, context, msg, listener, CustomProgressDialog.DELAYMILLIS_20);
    }

    public Dialog showDialog(String key, Context context, String msg,
                             CustomProgressDialog.OnDialogDismissListener listener, int timeout) {
        CustomProgressDialog dialog = dialogMap.get(key);
        if (dialog != null) {
            try {
                dialog.dismissProgressDialog();
            } catch (Exception e) {

            }
        }
        dialogMap.remove(key);
        dialog = new CustomProgressDialog((Activity) context);
        dialog.setProgressDrawable(R.mipmap.icon_loading);
        dialogMap.put(key, dialog);
        dialog.showDialog(key, msg, timeout);
        if (listener != null)
            dialog.setOnDialogDismissListener(listener);
        return dialog;

    }

    public boolean containsDialog(String key) {
        return dialogMap.get(key) != null;
    }

    public CustomProgressDialog getDialog(String key) {
        return dialogMap.get(key);
    }

    public void dimissDialog(String key, int keycode) {
        CustomProgressDialog dialog = dialogMap.get(key);
        dialogMap.remove(key);
        if (dialog != null) {
            dialog.dismissProgressDialog(keycode);
        }
    }
}