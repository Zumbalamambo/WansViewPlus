package net.ajcloud.wansviewplus.main.mine.security;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.dialog.LogoffDialog;
import net.ajcloud.wansviewplus.support.utils.preference.PreferenceKey;
import net.ajcloud.wansviewplus.support.utils.preference.SPUtil;

public class SecurityActivity extends BaseActivity {

    private SwitchCompat gestureSwitch;
    private RelativeLayout setGestureLayout;
    private RelativeLayout setPasswordLayout;
    private RelativeLayout logoffLayout;
    private LogoffDialog logoffDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_security;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Security settings");
        getToolbar().setLeftImg(R.mipmap.ic_back);

        gestureSwitch = findViewById(R.id.sc_gesture);
        setGestureLayout = findViewById(R.id.rl_set_gesture);
        setPasswordLayout = findViewById(R.id.rl_change_password);
        logoffLayout = findViewById(R.id.rl_log_off);

        logoffDialog = new LogoffDialog(this);

        if ((boolean) SPUtil.getSPUtil(this, PreferenceKey.sp_name.account).get(PreferenceKey.sp_key.USE_GESTURE, false)) {
            gestureSwitch.setChecked(true);
            setGestureLayout.setVisibility(View.VISIBLE);
        } else {
            gestureSwitch.setChecked(false);
            setGestureLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {
        setGestureLayout.setOnClickListener(this);
        setPasswordLayout.setOnClickListener(this);
        logoffLayout.setOnClickListener(this);
        gestureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtil.getSPUtil(SecurityActivity.this, PreferenceKey.sp_name.account).put(PreferenceKey.sp_key.USE_GESTURE, isChecked);
                showSettsLinear(isChecked);
            }
        });
        logoffDialog.setDialogClickListener(new LogoffDialog.OnDialogClickListener() {
            @Override
            public void logoff() {
                startActivity(new Intent(SecurityActivity.this, LogoffActivity.class));
            }
        });
    }


    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.rl_set_gesture:
                ModifyGestureActivity.start(this);
                break;
            case R.id.rl_change_password:
                startActivity(new Intent(SecurityActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.rl_log_off:
                if (!logoffDialog.isShowing()){
                    logoffDialog.show();
                }
                break;
            default:
                break;
        }
    }

    private static class ViewWrapper {
        private View mViewTarget;

        public ViewWrapper(View viewTarget) {
            mViewTarget = viewTarget;
        }

        public void setHeight(int height) {
            mViewTarget.getLayoutParams().height = height;
            mViewTarget.requestLayout();
        }

        public int getHeight() {
            return mViewTarget.getLayoutParams().height;
        }
    }

    private void showSettsLinear(boolean isPush) {
        setGestureLayout.setVisibility(isPush ? View.VISIBLE : View.GONE);
//        ViewWrapper wrapper = new ViewWrapper(setGestureLayout);
//        if (isPush) {
//            setGestureLayout.setVisibility(View.VISIBLE);
//            ObjectAnimator animator = ObjectAnimator.ofInt(wrapper, "height", getResources().getDimensionPixelSize(R.dimen.me_item_height)).setDuration(700);
//            animator.setInterpolator(new LinearOutSlowInInterpolator());
//            animator.start();
//        } else {
//            ObjectAnimator animator = ObjectAnimator.ofInt(wrapper, "height", 0).setDuration(700);
//            animator.setInterpolator(new LinearOutSlowInInterpolator());
//            animator.start();
//            setGestureLayout.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    setGestureLayout.setVisibility(View.GONE);
//                }
//            }, 700);
//        }
    }
}
