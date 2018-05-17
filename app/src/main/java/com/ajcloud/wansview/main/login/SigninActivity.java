package com.ajcloud.wansview.main.login;

import android.content.Intent;
import android.os.Bundle;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin, false);

        userName = findViewById(R.id.editText_userName);
        password = findViewById(R.id.editText_password);
        signUpTextView = findViewById(R.id.textView_sign_up);
        forgotTextView = findViewById(R.id.textView_forgot_password);
        signinButton = findViewById(R.id.button_signin);
        signinButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.button_signin:
                startActivity(new Intent(SigninActivity.this, HomeActivity.class));
                break;
            default:
                break;
        }
    }
}
