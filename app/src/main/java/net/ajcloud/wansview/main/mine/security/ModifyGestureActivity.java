package net.ajcloud.wansview.main.mine.security;

import android.content.Context;
import android.content.Intent;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;

public class ModifyGestureActivity extends BaseActivity {


    public static void start(Context context) {
        Intent intent = new Intent(context, ModifyGestureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_modify_gesture;
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
            toolbar.setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
