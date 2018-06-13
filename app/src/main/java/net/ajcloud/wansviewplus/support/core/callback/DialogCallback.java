package net.ajcloud.wansviewplus.support.core.callback;

import android.app.Activity;

import net.ajcloud.wansviewplus.support.core.okgo.request.base.Request;

import net.ajcloud.wansviewplus.support.customview.dialog.ProgressDialogManager;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:    统一添加等待框
 */
public abstract class DialogCallback<T> extends JsonCallback<T> {

    private static String WAIT = "waiting";

    private Activity activity;

    public DialogCallback(Activity activit) {
        this.activity = activit;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        ProgressDialogManager.getDialogManager().showDialog(WAIT, activity);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        ProgressDialogManager.getDialogManager().dimissDialog(WAIT, 0);
    }

}
