package net.ajcloud.wansview.main.account;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansview.support.tools.RegularTool;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;
import net.ajcloud.wansview.support.tools.RegularTool;

public class ForgotPasswordActivity extends BaseActivity {

    private MaterialEditText userName, password;
    private Button resetButton;

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_forgot_password;
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
            toolbar.setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        }
        userName = findViewById(net.ajcloud.wansview.R.id.editText_userName);
        password = findViewById(net.ajcloud.wansview.R.id.editText_password);
        resetButton = findViewById(net.ajcloud.wansview.R.id.btn_reset);

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
            case net.ajcloud.wansview.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansview.R.id.btn_reset:
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
