package net.ajcloud.wansview.main.account;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.home.HomeActivity;
import net.ajcloud.wansview.support.core.api.OkgoCommonListener;
import net.ajcloud.wansview.support.core.api.UserApiUnit;
import net.ajcloud.wansview.support.core.bean.SigninBean;
import net.ajcloud.wansview.support.customview.dialog.SigninMoreDialog;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansview.support.utils.ToastUtil;

public class SigninTwiceActivity extends BaseActivity {

    private static final String SIGNIN = "SIGNIN";
    private MaterialEditText password;
    private TextView userNameTextView, forgotTextView, moreTextView;
    private Button signinButton;
    private SigninMoreDialog signinMoreDialog;
    private String userName;
    private SigninAccountManager signinAccountManager;

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
        signinAccountManager = new SigninAccountManager(this);
        if (signinAccountManager.getCurrentAccountGesture() != null) {
            signinMoreDialog.setFirstText("Gesture");
        }
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
                SigninGestureActivity.start(SigninTwiceActivity.this, userName);
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
                doSignin();
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

    private void doSignin() {
        final String mail = userName;
        final String pwd = password.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            password.setError("cant be empty");
            return;
        }

        progressDialogManager.showDialog(SIGNIN, this, getResources().getInteger(R.integer.http_timeout));
        new UserApiUnit(this).signin(mail, pwd, new OkgoCommonListener<SigninBean>() {
            @Override
            public void onSuccess(SigninBean bean) {
                progressDialogManager.dimissDialog(SIGNIN, 0);
                Intent intent = new Intent(SigninTwiceActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SIGNIN, 0);
                ToastUtil.single(msg);
            }
        });
    }
}
