package net.ajcloud.wansviewplus.main.account;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;
import net.ajcloud.wansviewplus.support.tools.RegularTool;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class SignupVerifyActivity extends BaseActivity {

    private static final String SIGNUP = "SIGNUP";
    private TextView sendAgainTextView;
    private Button signinButton;
    private String mail;
    private String password;

    public static void start(Context context, String mail, String password) {
        Intent intent = new Intent(context, SignupVerifyActivity.class);
        intent.putExtra("mail", mail);
        intent.putExtra("password", password);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansviewplus.R.layout.activity_signup_verify;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle(getResources().getString(R.string.signin_mail_verify));
            toolbar.setLeftImg(net.ajcloud.wansviewplus.R.mipmap.ic_back);
        }
        sendAgainTextView = findViewById(net.ajcloud.wansviewplus.R.id.tv_send_again);
        signinButton = findViewById(net.ajcloud.wansviewplus.R.id.btn_signin);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            mail = getIntent().getStringExtra("mail");
            password = getIntent().getStringExtra("password");
        }
    }

    @Override
    protected void initListener() {
        sendAgainTextView.setOnClickListener(this);
        signinButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansviewplus.R.id.tv_send_again:
                register();
                break;
            case net.ajcloud.wansviewplus.R.id.btn_signin:
                startActivity(new Intent(SignupVerifyActivity.this, SigninActivity.class));
                break;
            default:
                break;
        }
    }

    private void register() {

        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)) {
            ToastUtil.single(getResources().getString(R.string.editText_hint_empty));
            return;
        } else if (!RegularTool.isLegalEmailAddress(mail)) {
            ToastUtil.single(getResources().getString(R.string.editText_hint_email_error));
            return;
        }

        progressDialogManager.showDialog(SIGNUP, this, getResources().getInteger(R.integer.http_timeout));
        UserApiUnit userApiUnit = new UserApiUnit(this);
        userApiUnit.register(mail, password, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(SIGNUP, 0);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SIGNUP, 0);
                ToastUtil.single(msg);
            }
        });
    }
}
