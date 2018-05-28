package net.ajcloud.wansview.main.account;

import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.core.api.UserApiUnit;
import net.ajcloud.wansview.support.customview.MyToolbar;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class SignupActivity extends BaseActivity {

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

//        userName.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        nextButton.setOnClickListener(this);
        termTextView.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.btn_next:
//                SignupVerifyActivity.start(this);
//                register();
                break;
            case net.ajcloud.wansview.R.id.tv_terms:
                startActivity(new Intent(SignupActivity.this, TermsActivity.class));
                break;
            default:
                break;
        }
    }

    private void register() {
        UserApiUnit userApiUnit = new UserApiUnit(this);
        userApiUnit.register(userName.getText().toString(), password.getText().toString(), new UserApiUnit.UserApiCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
