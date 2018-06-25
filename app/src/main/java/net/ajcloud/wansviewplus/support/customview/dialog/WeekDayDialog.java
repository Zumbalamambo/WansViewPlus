package net.ajcloud.wansviewplus.support.customview.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mamengchao on 2018/06/20.
 * Function: 选择星期dialog
 */
public class WeekDayDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Context context;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private AppCompatCheckBox cb_1;
    private AppCompatCheckBox cb_2;
    private AppCompatCheckBox cb_3;
    private AppCompatCheckBox cb_4;
    private AppCompatCheckBox cb_5;
    private AppCompatCheckBox cb_6;
    private AppCompatCheckBox cb_7;
    private OnDialogClickListener listener;
    private List<AppCompatCheckBox> cbs;
    private List<Integer> weekdays;
    private Set<Integer> weekdaySet;

    public WeekDayDialog(@NonNull Context context, List<Integer> weekdays) {
        this(context, R.style.FullscreenDialog, weekdays);
    }

    public WeekDayDialog(@NonNull Context context, int themeResId, List<Integer> weekdays) {
        super(context, themeResId);
        this.context = context;
        this.weekdays = weekdays;
        cbs = new ArrayList<>();
        weekdaySet = new HashSet<>(weekdays);
    }

    public interface OnDialogClickListener {
        void confirm(List<Integer> weekdays);
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_weekday);

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
        cb_1 = findViewById(R.id.cb_1);
        cb_2 = findViewById(R.id.cb_2);
        cb_3 = findViewById(R.id.cb_3);
        cb_4 = findViewById(R.id.cb_4);
        cb_5 = findViewById(R.id.cb_5);
        cb_6 = findViewById(R.id.cb_6);
        cb_7 = findViewById(R.id.cb_7);
        cbs.add(cb_1);
        cbs.add(cb_2);
        cbs.add(cb_3);
        cbs.add(cb_4);
        cbs.add(cb_5);
        cbs.add(cb_6);
        cbs.add(cb_7);
        for (Integer num : weekdays) {
            cbs.get(num - 1).setChecked(true);
        }
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        cb_1.setOnCheckedChangeListener(this);
        cb_2.setOnCheckedChangeListener(this);
        cb_3.setOnCheckedChangeListener(this);
        cb_4.setOnCheckedChangeListener(this);
        cb_5.setOnCheckedChangeListener(this);
        cb_6.setOnCheckedChangeListener(this);
        cb_7.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_confirm:
                weekdays = new ArrayList<>(weekdaySet);
                listener.confirm(new ArrayList<>(weekdaySet));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.cb_1:
                    weekdaySet.add(1);
                    break;
                case R.id.cb_2:
                    weekdaySet.add(2);
                    break;
                case R.id.cb_3:
                    weekdaySet.add(3);
                    break;
                case R.id.cb_4:
                    weekdaySet.add(4);
                    break;
                case R.id.cb_5:
                    weekdaySet.add(5);
                    break;
                case R.id.cb_6:
                    weekdaySet.add(6);
                    break;
                case R.id.cb_7:
                    weekdaySet.add(7);
                    break;
            }
        } else {
            switch (buttonView.getId()) {
                case R.id.cb_1:
                    weekdaySet.remove(1);
                    break;
                case R.id.cb_2:
                    weekdaySet.remove(2);
                    break;
                case R.id.cb_3:
                    weekdaySet.remove(3);
                    break;
                case R.id.cb_4:
                    weekdaySet.remove(4);
                    break;
                case R.id.cb_5:
                    weekdaySet.remove(5);
                    break;
                case R.id.cb_6:
                    weekdaySet.remove(6);
                    break;
                case R.id.cb_7:
                    weekdaySet.remove(7);
                    break;
            }
        }
    }
}
