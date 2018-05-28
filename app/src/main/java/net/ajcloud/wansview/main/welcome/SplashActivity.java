package net.ajcloud.wansview.main.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.account.SigninTwiceActivity;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.main.home.HomeActivity;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable startHomeTask = new Runnable() {
        @Override
        public void run() {
            SigninTwiceActivity.start(SplashActivity.this, "121321323@Gmail.com");
//            Intent intent = new Intent(SplashActivity.this, DeviceHomeActivity.class);
//            intent.putExtra("class", MainCameraFragment.class);
//            startActivity(intent);
            finish();
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
}
