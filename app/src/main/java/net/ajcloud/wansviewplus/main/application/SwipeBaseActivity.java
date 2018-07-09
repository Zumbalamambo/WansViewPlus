package net.ajcloud.wansviewplus.main.application;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;
import net.ajcloud.wansviewplus.support.customview.SwipeBackLayout;
import net.ajcloud.wansviewplus.support.customview.dialog.ProgressDialogManager;
import net.ajcloud.wansviewplus.support.tools.TimeLock;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;

import java.lang.reflect.Field;

/**
 * Created 可向右边滑动关闭的窗口
 * 需要修改主题的背景颜色未透明
 * 此处代码只是作为记录使用
 */

public abstract class SwipeBaseActivity extends BaseActivity {

    protected final String TAG = this.getClass().getSimpleName();
    @Override
    public FrameLayout getRootView() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0x000000000));
        FrameLayout rootView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base, null, false);
        SwipeBackLayout swipeBackLayout = rootView.findViewById(R.id.swipeBackLayout);
        View ivShadow = rootView.findViewById(R.id.iv_shadow);
        swipeBackLayout.addView(super.getRootView());
        swipeBackLayout.setOnScroll((fs) -> ivShadow.setAlpha(1 - fs));
        return rootView;
    }
}
