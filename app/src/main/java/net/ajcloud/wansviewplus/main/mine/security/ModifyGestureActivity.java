package net.ajcloud.wansviewplus.main.mine.security;

import android.content.Context;
import android.content.Intent;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;

import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;

public class ModifyGestureActivity extends BaseActivity {


    public static void start(Context context) {
        Intent intent = new Intent(context, ModifyGestureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansviewplus.R.layout.activity_modify_gesture;
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
            toolbar.setLeftImg(net.ajcloud.wansviewplus.R.mipmap.icon_back);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
