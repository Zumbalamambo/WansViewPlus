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
 * Created by mamengchao on 2018/06/21.
 * Function:    视频质量选择
 */
public class AudioQualityDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView fhdTextView, hdTextView;
    private OnDialogClickListener listener;
    private String firstText, secondText;

    public interface OnDialogClickListener {
        void onClick(String quality);
    }


    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public AudioQualityDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_audio_quality);

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

        fhdTextView = findViewById(R.id.tv_fhd);
        hdTextView = findViewById(R.id.tv_hd);
        if (!TextUtils.isEmpty(firstText)) {
            fhdTextView.setText(firstText);
            fhdTextView.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(secondText)) {
            hdTextView.setText(secondText);
            hdTextView.setVisibility(View.VISIBLE);
        }
        fhdTextView.setOnClickListener(this);
        hdTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_fhd:
                listener.onClick("5");
                break;
            case R.id.tv_hd:
                listener.onClick("1");
                break;
            default:
                break;
        }
    }
}
