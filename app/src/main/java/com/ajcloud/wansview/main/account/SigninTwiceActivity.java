package com.ajcloud.wansview.main.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.main.home.HomeActivity;
import com.ajcloud.wansview.support.customview.dialog.SigninMoreDialog;
import com.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class SigninTwiceActivity extends BaseActivity {

    private MaterialEditText password;
    private TextView userNameTextView, forgotTextView, moreTextView;
    private Button signinButton;
    private SigninMoreDialog signinMoreDialog;

    private String userName;

    public static void start(Context context, String userName) {
        Intent intent = new Intent(context, SigninTwiceActivity.class);
        intent.putExtra("userName", userName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signin_twice;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        userNameTextView = findViewById(R.id.tv_userName);
        password = findViewById(R.id.editText_password);
        moreTextView = findViewById(R.id.tv_more);
        forgotTextView = findViewById(R.id.textView_forgot_password);
        signinButton = findViewById(R.id.btn_signin);
        signinMoreDialog = new SigninMoreDialog(this);
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
        signinButton.setOnClickListener(this);
        moreTextView.setOnClickListener(this);
        forgotTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_signin:
                startActivity(new Intent(SigninTwiceActivity.this, HomeActivity.class));
                finish();
                break;
            case R.id.tv_more:
                if (!signinMoreDialog.isShowing()) {
                    signinMoreDialog.show();
                }
                break;
            case R.id.textView_forgot_password:
                startActivity(new Intent(SigninTwiceActivity.this, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }
}
