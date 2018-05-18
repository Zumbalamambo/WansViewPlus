package com.ajcloud.wansview.main.mine.security;

import android.content.Context;
import android.content.Intent;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.MyToolbar;

public class ModifyGestureActivity extends BaseActivity {


    public static void start(Context context) {
        Intent intent = new Intent(context, ModifyGestureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_gesture;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Set gesture graphics");
            toolbar.setLeftImg(R.mipmap.icon_back);
        }
    }

    @Override
    protected void initListener() {

    }
}
