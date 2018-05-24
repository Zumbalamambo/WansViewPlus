package com.ajcloud.wansview.main.account;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.MyToolbar;
import com.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import com.ajcloud.wansview.support.tools.RegularTool;

public class ForgotPasswordActivity extends BaseActivity {

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
                String mail = userName.getText().toString();
                if (!RegularTool.isLegalEmailAddress(mail)) {
                    userName.setError("Please enter the correct email");
                }
                break;
            default:
                break;
        }
    }
}
