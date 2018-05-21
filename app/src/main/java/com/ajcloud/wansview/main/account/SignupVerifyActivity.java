package com.ajcloud.wansview.main.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.MyToolbar;

public class SignupVerifyActivity extends BaseActivity {

    private TextView sendAgainTextView;
    private Button signinButton;

    public static void start(Context context) {
        Intent intent = new Intent(context, SignupVerifyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signup_verify;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle("Mailbox verification");
            toolbar.setLeftImg(R.mipmap.icon_back);
        }
        sendAgainTextView = findViewById(R.id.tv_send_again);
        signinButton = findViewById(R.id.btn_signin);
    }

    @Override
    protected void initListener() {
        sendAgainTextView.setOnClickListener(this);
        signinButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_send_again:
                break;
            case R.id.btn_signin:
                startActivity(new Intent(SignupVerifyActivity.this, SigninActivity.class));
                break;
            default:
                break;
        }
    }
}
