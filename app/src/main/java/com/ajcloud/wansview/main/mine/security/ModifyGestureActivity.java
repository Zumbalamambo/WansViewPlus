package com.ajcloud.wansview.main.mine.security;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;

import com.ajcloud.wansview.main.application.BaseActivity;

public class ModifyGestureActivity extends BaseActivity {


    public static void start(Context context) {
        Intent intent = new Intent(context, ModifyGestureActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ajcloud.wansview.R.layout.activity_modify_gesture, true);
    }

    @Override
    protected void initTittle() {
        super.initTittle();
        toolbar.setTitle("Set gesture graphics");
        toolbar.setLeftImg(com.ajcloud.wansview.R.drawable.ic_all);
    }
}
