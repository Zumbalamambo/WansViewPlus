package net.ajcloud.wansview.main.account;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.dialog.SigninMoreDialog;
import net.ajcloud.wansview.support.customview.lockgesture.LockGestureLayout;
import net.ajcloud.wansview.support.utils.ToastUtil;

public class SigninGestureActivity extends BaseActivity {

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
        return net.ajcloud.wansview.R.layout.activity_signin_gesture;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        userNameTextView = findViewById(net.ajcloud.wansview.R.id.tv_userName);
        moreTextView = findViewById(net.ajcloud.wansview.R.id.tv_more);
        lockGestureView = findViewById(R.id.lockGestureLayout);
        hintTextView = findViewById(R.id.tv_hint);
        signinMoreDialog = new SigninMoreDialog(this);
        signinMoreDialog.setFirstText("Password sign in");
        signinMoreDialog.setSecondText("Switch account");
        lockGestureView.setmAnswer("12369");
        lockGestureView.setTimes(5);
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
                ToastUtil.single("success");
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
            case net.ajcloud.wansview.R.id.tv_more:
                if (!signinMoreDialog.isShowing()) {
                    signinMoreDialog.show();
                }
                break;
            default:
                break;
        }
    }
}
