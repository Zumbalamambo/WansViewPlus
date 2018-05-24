package com.ajcloud.wansview.main.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.dialog.SigninMoreDialog;

public class SigninGestureActivity extends BaseActivity {

    private TextView userNameTextView, moreTextView;
    private SigninMoreDialog signinMoreDialog;
    private String userName;

    public static void start(Context context, String userName) {
        Intent intent = new Intent(context, SigninGestureActivity.class);
        intent.putExtra("userName", userName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signin_gesture;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        userNameTextView = findViewById(R.id.tv_userName);
        moreTextView = findViewById(R.id.tv_more);
        signinMoreDialog = new SigninMoreDialog(this);
        signinMoreDialog.setFirstText("Password sign in");
        signinMoreDialog.setSecondText("Switch account");
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            userName = getIntent().getStringExtra("userName");
            userNameTextView.setText(userName);
        }
    }

    @Override
    protected void initListener() {
        moreTextView.setOnClickListener(this);
        signinMoreDialog.setDialogClickListener(new SigninMoreDialog.OnDialogClickListener() {
            @Override
            public void onfirst() {
                SigninTwiceActivity.start(SigninGestureActivity.this, "121321323@Gmail.com");
                finish();
            }

            @Override
            public void onSecond() {
                startActivity(new Intent(SigninGestureActivity.this, SigninActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_more:
                if (!signinMoreDialog.isShowing()) {
                    signinMoreDialog.show();
                }
                break;
            default:
                break;
        }
    }
}
