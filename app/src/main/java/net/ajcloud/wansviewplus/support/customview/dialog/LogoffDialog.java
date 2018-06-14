package net.ajcloud.wansviewplus.support.customview.dialog;

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
import android.widget.Button;

import net.ajcloud.wansviewplus.R;

/**
 * Created by mamengchao on 2018/06/14.
 * Function:
 */
public class LogoffDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private Button logoffButton;
    private Button cancelButton;
    private OnDialogClickListener listener;

    public LogoffDialog(@NonNull Context context) {
        this(context, R.style.FullscreenDialog);
        this.context = context;
    }

    public LogoffDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnDialogClickListener {
        void logoff();
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logoff);

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

        logoffButton = findViewById(R.id.btn_logoff);
        cancelButton = findViewById(R.id.btn_cancel);
        logoffButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_logoff:
                listener.logoff();
                break;
            default:
                break;
        }
    }
}