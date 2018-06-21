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
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;

/**
 * Created by mamengchao on 2018/06/14.
 * Function:
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tittleTextView;
    private Button confirmButton;
    private Button cancelButton;
    private String tittle;
    private OnDialogClickListener listener;

    public ConfirmDialog(@NonNull Context context) {
        this(context, R.style.FullscreenDialog);
        this.context = context;
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface OnDialogClickListener {
        void confirm();
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logoff_confirm);

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

        tittleTextView = findViewById(R.id.tv_tittle);
        confirmButton = findViewById(R.id.btn_confirm);
        cancelButton = findViewById(R.id.btn_cancel);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        if (!TextUtils.isEmpty(tittle)) {
            tittleTextView.setText(tittle);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_confirm:
                listener.confirm();
                break;
            default:
                break;
        }
    }

    public void setTittle(String text) {
        this.tittle = text;
    }
}
