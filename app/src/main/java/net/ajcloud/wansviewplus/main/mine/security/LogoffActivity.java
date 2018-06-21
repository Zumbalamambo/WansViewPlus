package net.ajcloud.wansviewplus.main.mine.security;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.dialog.ConfirmDialog;
import net.ajcloud.wansviewplus.support.customview.materialEditText.MaterialEditText;

public class LogoffActivity extends BaseActivity {

    private static String LOGOFF = "LOGOFF";
    private MaterialEditText emailEditText;
    private MaterialEditText pwdEditText;
    private Button logoffButton;
    private ConfirmDialog confirmDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_logoff;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Log off");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        emailEditText = findViewById(R.id.et_email);
        pwdEditText = findViewById(R.id.et_pwd);
        logoffButton = findViewById(R.id.btn_logoff);

        confirmDialog = new ConfirmDialog(this);
        confirmDialog.setTittle("Log off confirmation");
        emailEditText.setText(SigninAccountManager.getInstance().getCurrentAccountMail());
        pwdEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void initListener() {
        logoffButton.setOnClickListener(this);
        confirmDialog.setDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
            @Override
            public void confirm() {
                doLogoff();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_logoff:
                showConfirmDialog();
                break;
            default:
                break;
        }
    }

    private void showConfirmDialog() {
        String email = emailEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("cant empty");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            pwdEditText.setError("cant empty");
            return;
        }
        //TODO 本地校验
        if (!confirmDialog.isShowing()){
            confirmDialog.show();
        }
    }

    private void doLogoff(){

    }
}
