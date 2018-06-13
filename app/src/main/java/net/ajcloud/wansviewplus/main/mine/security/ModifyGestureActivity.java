package net.ajcloud.wansviewplus.main.mine.security;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;
import net.ajcloud.wansviewplus.support.customview.lockgesture.LockGestureLayout;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class ModifyGestureActivity extends BaseActivity {

    private TextView hintTextView;
    private LockGestureLayout gestureLayout;
    private int times = 1;

    public static void start(Context context) {
        Intent intent = new Intent(context, ModifyGestureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansviewplus.R.layout.activity_modify_gesture;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitle("Set gesture graphics");
            toolbar.setLeftImg(net.ajcloud.wansviewplus.R.mipmap.icon_back);
        }
        hintTextView = findViewById(R.id.tv_gesture_hint);
        gestureLayout = findViewById(R.id.lockGestureLayout);
    }

    @Override
    protected void initData() {
        gestureLayout.setTimes(1000);
        gestureLayout.setFirst(true);
    }

    @Override
    protected void initListener() {
        gestureLayout.setLockGestureResultListenner(new LockGestureLayout.OnLockGestureResultListenner() {
            @Override
            public void onSuccess(String password) {
                if (times == 1) {
                    gestureLayout.setTmpPassword(password);
                    hintTextView.setText(R.string.me_set_gesture_again);
                    times++;
                } else {
                    SigninAccountManager.getInstance().setCurrentAccountGesture(password);
                    ToastUtil.single("set success");
                    finish();
                }
            }

            @Override
            public void onFail(int restTimes) {
                if (times == 2) {
                    ToastUtil.single("set fail,please retry");
                    gestureLayout.setTmpPassword("");
                    hintTextView.setText(R.string.me_set_gesture);
                    gestureLayout.setFirst(true);
                    times = 1;
                }
            }

            @Override
            public void onOverTime() {

            }
        });
    }
}
