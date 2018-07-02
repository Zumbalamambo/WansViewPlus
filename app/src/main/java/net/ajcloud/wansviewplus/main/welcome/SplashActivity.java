package net.ajcloud.wansviewplus.main.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.account.SigninActivity;
import net.ajcloud.wansviewplus.main.account.SigninTwiceActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.type.DeviceHomeActivity;
import net.ajcloud.wansviewplus.main.device.type.camera.MainCameraFragment;
import net.ajcloud.wansviewplus.main.home.HomeActivity;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.bean.AppConfigBean;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable startHomeTask = new Runnable() {
        @Override
        public void run() {
//            DeviceHomeActivity.startCamerHomeActivity(SplashActivity.this, "oid", MainCameraFragment.class);

            getAppConfig();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //若HomeActivity已经在运行，则直接跳转
        List<Activity> activityList = MainApplication.getApplication().getActivities();
        for (Activity activity : activityList) {
            if (TextUtils.equals(activity.getClass().getSimpleName(), HomeActivity.class.getSimpleName())) {
                if (activity.isDestroyed() || activity.isFinishing()) {
                    break;
                } else {
                    handler.post(startHomeTask);
                    return;
                }
            }
        }
        setContentView(R.layout.activity_splash);

        startApp();
    }

    private void startApp() {
        handler.postDelayed(startHomeTask, 1000);
    }

    private void getAppConfig() {
        new UserApiUnit(this).getAppConfig(new OkgoCommonListener<AppConfigBean>() {
            @Override
            public void onSuccess(AppConfigBean bean) {
                signin();
            }

            @Override
            public void onFail(int code, String msg) {
                startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                finish();
            }
        });
    }

    private void signin() {
        if (SigninAccountManager.getInstance().isAutoSignin()) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else {
            if (TextUtils.isEmpty(SigninAccountManager.getInstance().getCurrentAccountMail())) {
                startActivity(new Intent(SplashActivity.this, SigninActivity.class));
            } else {
                SigninTwiceActivity.start(SplashActivity.this, SigninAccountManager.getInstance().getCurrentAccountMail());
            }
        }
        finish();
    }

}
