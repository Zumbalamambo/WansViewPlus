package net.ajcloud.wansview.main.account;

import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.core.api.UserApiUnit;
import net.ajcloud.wansview.support.customview.MyToolbar;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansview.support.tools.RegularTool;
import net.ajcloud.wansview.support.utils.ToastUtil;
import net.ajcloud.wansview.support.utils.preference.PreferenceKey;
import net.ajcloud.wansview.support.utils.preference.SPUtil;

public class SignupActivity extends BaseActivity {

    private static final String SIGNUP = "SIGNUP";
    private MaterialEditText userName, password;
    private TextView termTextView;
    private AppCompatCheckBox agreeCheckBox;
    private Button nextButton;

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_signup;
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
            toolbar.setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        }
        userName = findViewById(net.ajcloud.wansview.R.id.editText_userName);
        password = findViewById(net.ajcloud.wansview.R.id.editText_password);
        termTextView = findViewById(net.ajcloud.wansview.R.id.tv_terms);
        agreeCheckBox = findViewById(net.ajcloud.wansview.R.id.cb_agree);
        nextButton = findViewById(net.ajcloud.wansview.R.id.btn_next);
        nextButton.setEnabled(false);
    }

    @Override
    protected void initData() {

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
            case net.ajcloud.wansview.R.id.btn_next:
                doRegister();
                break;
            case net.ajcloud.wansview.R.id.tv_terms:
                startActivityForResult(new Intent(SignupActivity.this, TermsActivity.class), 0);
                break;
            default:
                break;
        }
    }

    private void doRegister() {

        final String mail = userName.getText().toString();
        final String pwd = password.getText().toString();
        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pwd)) {
            ToastUtil.single("cant be empty");
            return;
        } else if (!RegularTool.isLegalEmailAddress(mail)) {
            ToastUtil.single("E-mail format is incorrect");
            return;
        }

        progressDialogManager.showDialog(SIGNUP, this, getResources().getInteger(R.integer.http_timeout));
        UserApiUnit userApiUnit = new UserApiUnit(this);
        userApiUnit.register(mail, pwd, new UserApiUnit.UserApiCommonListener<Object>() {
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
