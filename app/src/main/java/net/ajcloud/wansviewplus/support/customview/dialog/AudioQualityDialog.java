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
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/21.
 * Function:    视频质量选择
 */
public class AudioQualityDialog extends Dialog {
    private Context context;
    private LinearLayout ll_qualities;
    private OnDialogClickListener listener;
    private List<String> qualities;

    public interface OnDialogClickListener {
        void onClick(String quality);
    }


    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public AudioQualityDialog(@NonNull Context context, List<String> qualities) {
        super(context);
        this.context = context;
        this.qualities = qualities;
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

        ll_qualities = findViewById(R.id.ll_qualities);

        for (int i = 0; i < ll_qualities.getChildCount(); i++) {
            if (qualities.size() >= (i + 1) && qualities.get(i) != null) {
                String[] qualityInfo = qualities.get(i).split(":");
                TextView textView = (TextView) ll_qualities.getChildAt(i);
                textView.setVisibility(View.VISIBLE);
                textView.setText(qualityInfo[1]);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (listener == null) {
                            return;
                        }
                        listener.onClick(qualityInfo[0]);
                    }
                });
            }
        }
    }
}
