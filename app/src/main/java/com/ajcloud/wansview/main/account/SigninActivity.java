package com.ajcloud.wansview.main.account;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.main.home.HomeActivity;
import com.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class SigninActivity extends BaseActivity {

    private MaterialEditText userName, password;
    private TextView signUpTextView, forgotTextView;
    private Button signinButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signin;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        userName = findViewById(R.id.editText_userName);
        password = findViewById(R.id.editText_password);
        signUpTextView = findViewById(R.id.textView_sign_up);
        forgotTextView = findViewById(R.id.textView_forgot_password);
        signinButton = findViewById(R.id.btn_signin);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        signinButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
        forgotTextView.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_signin:
                startActivity(new Intent(SigninActivity.this, HomeActivity.class));
                finish();
                break;
            case R.id.textView_sign_up:
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
                break;
            case R.id.textView_forgot_password:
                startActivity(new Intent(SigninActivity.this, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }
}
