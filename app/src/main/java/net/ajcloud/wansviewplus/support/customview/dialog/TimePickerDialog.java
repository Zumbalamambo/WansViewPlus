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
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.customview.picker.wheelview.WheelView;
import net.ajcloud.wansviewplus.support.customview.picker.wheelview.adapter.NumericWheelAdapter;

/**
 * Created by mamengchao on 2018/07/10.
 * Function:    时间选择器
 */
public class TimePickerDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private WheelView wv_hours;
    private WheelView wv_minutes;
    private OnTimeSelectListener listener;
    private int hour, min;

    public void setOnTimeSelectListener(OnTimeSelectListener listener) {
        this.listener = listener;
    }

    public TimePickerDialog(@NonNull Context context) {
        this(context, R.style.FullscreenDialog);
    }

    public TimePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pickerview);

        Window window = getWindow();
        window.setWindowAnimations(R.style.picker_view_slide_anim);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = screenWidth;
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
        wv_hours = findViewById(R.id.hour);
        wv_minutes = findViewById(R.id.min);

        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setLineSpacingMultiplier(4.0f);
        wv_hours.setCyclic(true);
        wv_hours.setCurrentItem(hour);

        wv_minutes.setAdapter(new NumericWheelAdapter(0, 59));
        wv_minutes.setLineSpacingMultiplier(4.0f);
        wv_minutes.setCyclic(true);
        wv_minutes.setCurrentItem(min);

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }


    public void setDate(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_confirm:
                StringBuilder time = new StringBuilder();
                if (wv_hours.getCurrentItem() > 9) {
                    time.append(wv_hours.getCurrentItem());
                } else {
                    time.append(0);
                    time.append(wv_hours.getCurrentItem());
                }
                if (wv_minutes.getCurrentItem() > 9) {
                    time.append(wv_minutes.getCurrentItem());
                } else {
                    time.append(0);
                    time.append(wv_minutes.getCurrentItem());
                }
                time.append("00");
                listener.onTimeSelected(time.toString());
                break;
            default:
                break;
        }
    }

    public interface OnTimeSelectListener {
        //time: 240000
        void onTimeSelected(String time);
    }
}
