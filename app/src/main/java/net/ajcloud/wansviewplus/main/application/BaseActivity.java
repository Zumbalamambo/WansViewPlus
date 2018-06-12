package net.ajcloud.wansviewplus.main.application;

import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.device.addDevice.AddDeviceSelectActivity;
import net.ajcloud.wansviewplus.main.device.setting.DeviceSettingActivity;
import net.ajcloud.wansviewplus.main.device.setting.DeviceSettingAlertActivity;
import net.ajcloud.wansviewplus.main.device.setting.DeviceSettingInfoActivity;
import net.ajcloud.wansviewplus.main.device.setting.DeviceSettingNameActivity;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;
import net.ajcloud.wansviewplus.support.customview.dialog.ProgressDialogManager;
import net.ajcloud.wansviewplus.support.tools.TimeLock;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;

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
    private LayoutInflater mInflater;
    protected MainApplication application = MainApplication.getApplication();
    protected ProgressDialogManager progressDialogManager = ProgressDialogManager.getDialogManager();
    private TimeLock timeLock = new TimeLock();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application.pushActivity(this);
        mInflater = LayoutInflater.from(this);
        initRootView();
        setContentView();
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
            toolbarView = mInflater.inflate(R.layout.tool_bar, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2Pix(this, 48));
            params.topMargin = statusBarHeight;
            rootView.addView(toolbarView, params);
            toolbar = toolbarView.findViewById(R.id.toolbar);
        }
        //添加contentView
        contentView = mInflater.inflate(getLayoutId(), null);
        contentView.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        if (hasTittle()) {
            params.topMargin = DisplayUtil.dip2Pix(this, 48);
        }

        if (hasStateBar()) {
            params.topMargin += statusBarHeight;
        }

        rootView.addView(contentView, 0, params);

        setContentView(rootView);
//        setSupportActionBar(toolbar);
    }

    protected abstract int getLayoutId();

    protected abstract boolean hasTittle();

    protected void initView(){

    }

    protected void initData(){

    }

    protected void initListener(){

    }

    protected boolean hasStateBar() {
        return true;
    }

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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.img_left:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                onBackPressed();
                break;
            default:
                break;
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
