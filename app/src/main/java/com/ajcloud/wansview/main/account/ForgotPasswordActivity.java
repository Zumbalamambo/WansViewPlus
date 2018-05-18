package com.ajcloud.wansview.main.account;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.MyToolbar;
import com.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

public class ForgotPasswordActivity extends BaseActivity {

    private MaterialEditText userName, password;
    private Button nextButton;

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
        userName = findViewById(R.id.editText_userName);
        password = findViewById(R.id.editText_password);
        nextButton = findViewById(R.id.btn_next);

        userName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void initListener() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle("Email verification");
            toolbar.setLeftImg(R.mipmap.icon_back);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                finish();
                break;
            default:
                break;
        }
    }
}
