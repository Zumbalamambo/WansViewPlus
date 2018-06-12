package net.ajcloud.wansviewplus.support.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;

/**
 * Created by mamengchao on 2018/05/22.
 */
public class SigninMoreDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView firstTextView, secondTextView, cancleTextView;
    private OnDialogClickListener listener;
    private String firstText, secondText;

    public interface OnDialogClickListener {
        void onfirst();

        void onSecond();
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

        firstTextView = findViewById(R.id.tv_first);
        secondTextView = findViewById(R.id.tv_second);
        cancleTextView = findViewById(R.id.tv_cancel);
        if (!TextUtils.isEmpty(firstText)) {
            firstTextView.setText(firstText);
            firstTextView.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(secondText)) {
            secondTextView.setText(secondText);
            secondTextView.setVisibility(View.VISIBLE);
        }
        firstTextView.setOnClickListener(this);
        secondTextView.setOnClickListener(this);
        cancleTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_first:
                listener.onfirst();
                break;
            case R.id.tv_second:
                listener.onSecond();
                break;
            default:
                break;
        }
    }

    public void setFirstText(String text) {
        firstText = text;
    }

    public void setSecondText(String text) {
        secondText = text;
    }
}
