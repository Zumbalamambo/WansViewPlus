package net.ajcloud.wansview.main.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;

public class SignupVerifyActivity extends BaseActivity {

    private TextView sendAgainTextView;
    private Button signinButton;

    public static void start(Context context) {
        Intent intent = new Intent(context, SignupVerifyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_signup_verify;
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
            toolbar.setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        }
        sendAgainTextView = findViewById(net.ajcloud.wansview.R.id.tv_send_again);
        signinButton = findViewById(net.ajcloud.wansview.R.id.btn_signin);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        sendAgainTextView.setOnClickListener(this);
        signinButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.tv_send_again:
                break;
            case net.ajcloud.wansview.R.id.btn_signin:
                startActivity(new Intent(SignupVerifyActivity.this, SigninActivity.class));
                break;
            default:
                break;
        }
    }
}
