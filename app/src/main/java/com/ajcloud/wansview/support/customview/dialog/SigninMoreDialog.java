package com.ajcloud.wansview.support.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ajcloud.wansview.R;

/**
 * Created by mamengchao on 2018/05/22.
 */
public class SigninMoreDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView gestureTextView, switchAccountTextView, cancleTextView;
    private OnDialogClickListener listener;

    public interface OnDialogClickListener {
        void onGesture();

        void onSwitchAccount();
    }


    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public SigninMoreDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signin_more);

        Window window = getWindow();
        window.setWindowAnimations(R.style.BottomInOutAnim);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = screenWidth;
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失

        gestureTextView = findViewById(R.id.tv_gesture);
        switchAccountTextView = findViewById(R.id.tv_switch_account);
        cancleTextView = findViewById(R.id.tv_cancel);
        gestureTextView.setOnClickListener(this);
        switchAccountTextView.setOnClickListener(this);
        cancleTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_gesture:
                listener.onGesture();
                break;
            case R.id.tv_switch_account:
                listener.onSwitchAccount();
                break;
            default:
                break;
        }
    }
}
