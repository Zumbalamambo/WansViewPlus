package net.ajcloud.wansviewplus.main.account;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.tools.RegularTool;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class ForgotPasswordActivity extends BaseActivity {

    private static final String FORGOT = "FORGOT";
    private MaterialEditText userName, password;
    private Button resetButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle(getResources().getString(R.string.signin_reset_password));
            toolbar.setLeftImg(R.mipmap.ic_back);
        }
        userName = findViewById(R.id.editText_userName);
        password = findViewById(R.id.editText_password);
        resetButton = findViewById(R.id.btn_reset);

//        userName.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        resetButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                doForgot();
                break;
            default:
                break;
        }
    }

    private void doForgot() {
        final String mail = userName.getText().toString();
        final String pwd = password.getText().toString();
        if (TextUtils.isEmpty(mail)) {
            userName.setError(getResources().getString(R.string.editText_hint_empty));
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            password.setError(getResources().getString(R.string.editText_hint_empty));
            return;
        } else if (!RegularTool.isLegalEmailAddress(mail)) {
            password.setError(getResources().getString(R.string.editText_hint_email_error));
            return;
        }

        progressDialogManager.showDialog(FORGOT, this, getResources().getInteger(R.integer.http_timeout));
        new UserApiUnit(this).register(mail, pwd, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object object) {
                progressDialogManager.dimissDialog(FORGOT, 0);
                SignupVerifyActivity.start(ForgotPasswordActivity.this, mail, pwd);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(FORGOT, 0);
                ToastUtil.single(msg);
            }
        });
    }
}
