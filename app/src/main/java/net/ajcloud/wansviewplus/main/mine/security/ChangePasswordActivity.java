package net.ajcloud.wansviewplus.main.mine.security;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.bean.SigninBean;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class ChangePasswordActivity extends BaseActivity {

    private static String CHANGE = "CHANGE";
    private MaterialEditText pwdEditText;
    private MaterialEditText newPwdEditText;
    private Button okButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Change password");
        getToolbar().setLeftImg(R.mipmap.ic_back);

        pwdEditText = findViewById(R.id.et_pwd);
        newPwdEditText = findViewById(R.id.et_new_pwd);
        okButton = findViewById(R.id.btn_ok);
    }

    @Override
    protected void initListener() {
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                doChange();
                break;
            default:
                break;
        }
    }

    private void doChange() {
        String pwd = pwdEditText.getText().toString();
        String newPwd = newPwdEditText.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            pwdEditText.setError("cant empty");
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            newPwdEditText.setError("cant empty");
            return;
        }
        //TODO 本地校验
        progressDialogManager.showDialog(CHANGE, this);
        new UserApiUnit(this).changePassword(SigninAccountManager.getInstance().getCurrentAccountMail(), pwd, newPwd, new OkgoCommonListener<SigninBean>() {
            @Override
            public void onSuccess(SigninBean bean) {
                progressDialogManager.dimissDialog(CHANGE, 0);
                ToastUtil.single("success");
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(CHANGE, 0);
                ToastUtil.single(msg);
            }
        });
    }
}
