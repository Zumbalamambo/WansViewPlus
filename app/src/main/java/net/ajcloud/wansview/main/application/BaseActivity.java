package net.ajcloud.wansview.main.application;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.support.customview.MyToolbar;
import net.ajcloud.wansview.support.tools.TimeLock;
import net.ajcloud.wansview.support.tools.WLog;
import net.ajcloud.wansview.support.utils.DisplayUtil;

import net.ajcloud.wansview.support.customview.MyToolbar;
import net.ajcloud.wansview.support.tools.TimeLock;
import net.ajcloud.wansview.support.tools.WLog;
import net.ajcloud.wansview.support.utils.DisplayUtil;

import java.lang.reflect.Field;

/**
 * Created by mamengchao on 2018/05/10.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected final String TAG = this.getClass().getSimpleName();
    private FrameLayout rootView;
    private View toolbarView;
    private View contentView;
    private MyToolbar toolbar;
    protected MainApplication application = MainApplication.getApplication();
    private TimeLock timeLock = new TimeLock();

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application.pushActivity(this);
        WLog.d(TAG, "-----step1------");
        initRootView();
        WLog.d(TAG, "-----step2------");
        setContentView();
        WLog.d(TAG, "-----step3------");

        if (null != toolbar) {
            toolbar.registerClickListener(this);
        }
        initView();
        initData();
        initListener();
    }

    private void initRootView() {
        /*直接创建一个帧布局，作为视图容器的父容器*/
        rootView = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(params);
    }

    public void setContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
        //透明状态栏和获取状态栏高度
        int statusBarHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //状态栏高度调整
            {
                //获取status_bar_height资源的ID
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    //根据资源ID获取响应的尺寸值
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                } else {
                    statusBarHeight = DisplayUtil.dip2Pix(MainApplication.getApplication(), 20);
                }
            }
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
        //添加toolbar
        if (hasTittle()) {
            toolbarView = LayoutInflater.from(this).inflate(net.ajcloud.wansview.R.layout.tool_bar, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2Pix(this, 48));
            params.topMargin = statusBarHeight;
            rootView.addView(toolbarView, params);
            toolbar = toolbarView.findViewById(net.ajcloud.wansview.R.id.toolbar);
        }
        //添加contentView
        contentView = LayoutInflater.from(this).inflate(getLayoutId(), null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        if (hasTittle()) {
            params.topMargin = statusBarHeight + DisplayUtil.dip2Pix(this, 48);
        } else {
            params.topMargin = 0;
        }
        rootView.addView(contentView, 0, params);

        setContentView(rootView);
    }

    protected abstract int getLayoutId();

    protected abstract boolean hasTittle();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        if (timeLock.isLock()) {
            return;
        }
        timeLock.lock();
        if (v.getId() == net.ajcloud.wansview.R.id.btn_left || v.getId() == net.ajcloud.wansview.R.id.img_left) {
            finish();
        }
        onClickView(v);
    }

    public void onClickView(View v) {//BaseActivity的子类复写此方法来处理点击事件，以便统一做防止重复点击处理

    }

    public View getContentView() {
        return contentView;
    }

    public MyToolbar getToolbar() {
        return hasTittle() ? toolbar : null;
    }

    public FrameLayout getRootView() {
        return rootView;
    }
}
