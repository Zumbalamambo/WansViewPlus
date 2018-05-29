package net.ajcloud.wansview.main.account;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.core.api.UserApiUnit;
import net.ajcloud.wansview.support.core.bean.SigninBean;
import net.ajcloud.wansview.support.customview.MyToolbar;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansview.support.tools.RegularTool;
import net.ajcloud.wansview.support.utils.ToastUtil;

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
            toolbar.setTittle("Reset password");
            toolbar.setLeftImg(R.mipmap.icon_back);
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
            case R.id.img_left:
                finish();
                break;
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
        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pwd)) {
            ToastUtil.single("cant be empty");
            return;
        } else if (!RegularTool.isLegalEmailAddress(mail)) {
            ToastUtil.single("E-mail format is incorrect");
            return;
        }

        progressDialogManager.showDialog(FORGOT, this, getResources().getInteger(R.integer.http_timeout));
        new UserApiUnit(this).register(mail, pwd, new UserApiUnit.UserApiCommonListener<Object>() {
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
