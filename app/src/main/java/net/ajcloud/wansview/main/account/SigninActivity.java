package net.ajcloud.wansview.main.account;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.home.HomeActivity;
import net.ajcloud.wansview.support.core.api.UserApiUnit;
import net.ajcloud.wansview.support.core.bean.SigninBean;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansview.support.tools.RegularTool;
import net.ajcloud.wansview.support.utils.ToastUtil;
import net.ajcloud.wansview.support.utils.preference.PreferenceKey;
import net.ajcloud.wansview.support.utils.preference.SPUtil;

public class SigninActivity extends BaseActivity {

    private static final String SIGNIN = "SIGNIN";
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
        String account = (String) SPUtil.getSPUtil(this, PreferenceKey.sp_name.account).get(PreferenceKey.sp_key.CURRENT_ACCOUNT, "");
        if (!TextUtils.isEmpty(account)) {
            userName.setText(account);
        }
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
                doSignin();
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

    private void doSignin() {
        final String mail = userName.getText().toString();
        final String pwd = password.getText().toString();
        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pwd)) {
            ToastUtil.single("cant be empty");
            return;
        } else if (!RegularTool.isLegalEmailAddress(mail)) {
            ToastUtil.single("E-mail format is incorrect");
            return;
        }

        progressDialogManager.showDialog(SIGNIN, this, getResources().getInteger(R.integer.http_timeout));
        new UserApiUnit(this).signin(mail, pwd, new UserApiUnit.UserApiCommonListener<SigninBean>() {
            @Override
            public void onSuccess(SigninBean bean) {
                progressDialogManager.dimissDialog(SIGNIN, 0);
                startActivity(new Intent(SigninActivity.this, HomeActivity.class));
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
