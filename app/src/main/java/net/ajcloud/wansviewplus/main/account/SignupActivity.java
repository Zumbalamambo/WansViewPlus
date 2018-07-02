package net.ajcloud.wansviewplus.main.account;

import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansviewplus.support.tools.RegularTool;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class SignupActivity extends BaseActivity {

    private static final String SIGNUP = "SIGNUP";
    private MaterialEditText userName, password;
    private TextView termTextView;
    private AppCompatCheckBox agreeCheckBox;
    private Button nextButton;

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansviewplus.R.layout.activity_signup;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle("Sign up");
            toolbar.setLeftImg(net.ajcloud.wansviewplus.R.mipmap.ic_back);
        }
        userName = findViewById(net.ajcloud.wansviewplus.R.id.editText_userName);
        password = findViewById(net.ajcloud.wansviewplus.R.id.editText_password);
        termTextView = findViewById(net.ajcloud.wansviewplus.R.id.tv_terms);
        agreeCheckBox = findViewById(net.ajcloud.wansviewplus.R.id.cb_agree);
        nextButton = findViewById(net.ajcloud.wansviewplus.R.id.btn_next);
        nextButton.setEnabled(false);
    }

    @Override
    protected void initListener() {
        nextButton.setOnClickListener(this);
        termTextView.setOnClickListener(this);
        agreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansviewplus.R.id.btn_next:
                doRegister();
                break;
            case net.ajcloud.wansviewplus.R.id.tv_terms:
                TermsActivity.start(SignupActivity.this, true);
                break;
            default:
                break;
        }
    }

    private void doRegister() {

        final String mail = userName.getText().toString();
        final String pwd = password.getText().toString();
        if (TextUtils.isEmpty(mail)) {
            userName.setError("cant be empty");
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            password.setError("cant be empty");
            return;
        } else if (!RegularTool.isLegalEmailAddress(mail)) {
            password.setError("E-mail format is incorrect");
            return;
        }

        progressDialogManager.showDialog(SIGNUP, this, getResources().getInteger(R.integer.http_timeout));
        UserApiUnit userApiUnit = new UserApiUnit(this);
        userApiUnit.register(mail, pwd, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(SIGNUP, 0);
                SignupVerifyActivity.start(SignupActivity.this, mail, pwd);
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SIGNUP, 0);
                ToastUtil.single(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            agreeCheckBox.setChecked(true);
        }
    }
}
