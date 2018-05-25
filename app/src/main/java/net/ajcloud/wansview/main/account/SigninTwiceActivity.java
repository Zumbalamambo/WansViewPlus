package net.ajcloud.wansview.main.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.home.HomeActivity;
import net.ajcloud.wansview.support.customview.dialog.SigninMoreDialog;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.dialog.SigninMoreDialog;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

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
        return net.ajcloud.wansview.R.layout.activity_signin_twice;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        userNameTextView = findViewById(net.ajcloud.wansview.R.id.tv_userName);
        password = findViewById(net.ajcloud.wansview.R.id.editText_password);
        moreTextView = findViewById(net.ajcloud.wansview.R.id.tv_more);
        forgotTextView = findViewById(net.ajcloud.wansview.R.id.textView_forgot_password);
        signinButton = findViewById(net.ajcloud.wansview.R.id.btn_signin);
        signinMoreDialog = new SigninMoreDialog(this);
        signinMoreDialog.setFirstText("Gesture");
        signinMoreDialog.setSecondText("Switch account");

//        password.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        signinMoreDialog.setDialogClickListener(new SigninMoreDialog.OnDialogClickListener() {
            @Override
            public void onfirst() {
                SigninGestureActivity.start(SigninTwiceActivity.this, "121321323@Gmail.com");
                finish();
            }

            @Override
            public void onSecond() {
                startActivity(new Intent(SigninTwiceActivity.this, SigninActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.btn_signin:
                startActivity(new Intent(SigninTwiceActivity.this, HomeActivity.class));
                finish();
                break;
            case net.ajcloud.wansview.R.id.tv_more:
                if (!signinMoreDialog.isShowing()) {
                    signinMoreDialog.show();
                }
                break;
            case net.ajcloud.wansview.R.id.textView_forgot_password:
                startActivity(new Intent(SigninTwiceActivity.this, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }
}
