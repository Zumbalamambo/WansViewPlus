package com.ajcloud.wansview.main.application;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ajcloud.wansview.support.customview.MyToolbar;
import com.ajcloud.wansview.support.customview.ToolBarHelper;
import com.ajcloud.wansview.support.tools.TimeLock;
import com.ajcloud.wansview.support.utils.DisplayUtil;

import java.lang.reflect.Field;

/**
 * Created by mamengchao on 2018/05/10.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected final String TAG = this.getClass().getSimpleName();
    protected MainApplication application = MainApplication.getApplication();
    private TimeLock timeLock = new TimeLock();

    protected ToolBarHelper mToolBarHelper;
    protected MyToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application.pushActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.removeActivity(this);
    }

    /**
     * @param layoutResID 布局id
     * @param hasTittle   是否带标题栏
     */
    protected void setContentView(int layoutResID, boolean hasTittle) {
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
        mToolBarHelper = new ToolBarHelper(this, layoutResID, statusBarHeight, hasTittle);
        setContentView(mToolBarHelper.getContentView());
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        toolbar = mToolBarHelper.getToolBar();
        if (toolbar != null){
            setSupportActionBar(toolbar);
            toolbar.registerClickListener(this);
            initTittle();
        }
        initView();
        initData();
        initListener();
    }

    /**
     * 子类实现
     */
    protected void initTittle() {
    }

    protected void initView() {
    }

    protected void initData() {
    }

    protected void initListener() {
    }

    @Override
    public void onClick(View v) {
        if (timeLock.isLock()) {
            return;
        }
        timeLock.lock();
    }
}
