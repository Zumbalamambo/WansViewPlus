package net.ajcloud.wansviewplus.main.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.home.HomeActivity;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.bean.SigninBean;
import net.ajcloud.wansviewplus.support.customview.dialog.SigninMoreDialog;
import net.ajcloud.wansviewplus.support.customview.lockgesture.LockGestureLayout;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class SigninGestureActivity extends BaseActivity {

    private static final String SIGNIN = "SIGNIN";
    private TextView userNameTextView, moreTextView;
    private SigninMoreDialog signinMoreDialog;
    private LockGestureLayout lockGestureView;
    private TextView hintTextView;
    private String userName;

    public static void start(Context context, String userName) {
        Intent intent = new Intent(context, SigninGestureActivity.class);
        intent.putExtra("userName", userName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansviewplus.R.layout.activity_signin_gesture;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        userNameTextView = findViewById(net.ajcloud.wansviewplus.R.id.tv_userName);
        moreTextView = findViewById(net.ajcloud.wansviewplus.R.id.tv_more);
        lockGestureView = findViewById(R.id.lockGestureLayout);
        hintTextView = findViewById(R.id.tv_hint);
        signinMoreDialog = new SigninMoreDialog(this);
        signinMoreDialog.setFirstText("Password sign in");
        signinMoreDialog.setSecondText("Switch account");
        lockGestureView.setTimes(5);
        lockGestureView.setFirst(false);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            userName = getIntent().getStringExtra("userName");
            userNameTextView.setText(userName);
        }
    }

    @Override
    protected void initListener() {
        moreTextView.setOnClickListener(this);
        lockGestureView.setLockGestureResultListenner(new LockGestureLayout.OnLockGestureResultListenner() {
            @Override
            public void onSuccess() {
                doSignin();
            }

            @Override
            public void onFail(int restTimes) {
                hintTextView.setText(String.format(getResources().getString(R.string.signin_gesture_error_hint), restTimes + ""));
            }

            @Override
            public void onOverTime() {
                startActivity(new Intent(SigninGestureActivity.this, SigninActivity.class));
                finish();
            }
        });
        signinMoreDialog.setDialogClickListener(new SigninMoreDialog.OnDialogClickListener() {
            @Override
            public void onfirst() {
                SigninTwiceActivity.start(SigninGestureActivity.this, userName);
                finish();
            }

            @Override
            public void onSecond() {
                startActivity(new Intent(SigninGestureActivity.this, SigninActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansviewplus.R.id.tv_more:
                if (!signinMoreDialog.isShowing()) {
                    signinMoreDialog.show();
                }
                break;
            default:
                break;
        }
    }

    private void doSignin() {
        final String mail = userName;
        final String pwd = SigninAccountManager.getInstance().getCurrentAccountPassword();

        progressDialogManager.showDialog(SIGNIN, this, getResources().getInteger(R.integer.http_timeout));
        new UserApiUnit(this).signin(mail, pwd, new OkgoCommonListener<SigninBean>() {
            @Override
            public void onSuccess(SigninBean bean) {
                progressDialogManager.dimissDialog(SIGNIN, 0);
                Intent intent = new Intent(SigninGestureActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(SIGNIN, 0);
                ToastUtil.single(msg);
            }
        });
    }
}
