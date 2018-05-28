package net.ajcloud.wansview.main.welcome;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.account.SigninActivity;
import net.ajcloud.wansview.main.account.SigninTwiceActivity;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.main.device.type.DeviceHomeActivity;
import net.ajcloud.wansview.main.device.type.camera.MainCameraFragment;
import net.ajcloud.wansview.main.home.HomeActivity;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Handler handler = new Handler();
    private Runnable startHomeTask = new Runnable() {
        @Override
        public void run() {
            //SigninTwiceActivity.start(SplashActivity.this, "121321323@Gmail.com");
            Intent intent = new Intent(SplashActivity.this, DeviceHomeActivity.class);
            intent.putExtra("class", MainCameraFragment.class);
            startActivity(intent);
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
                    SigninTwiceActivity.start(SplashActivity.this, "121321323@Gmail.com");
//                    startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                    finish();
                    return;
                }
            }
        }
        setContentView(net.ajcloud.wansview.R.layout.activity_splash);
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, PERMISSION_READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_PHONE_STATE},
                    PERMISSION_REQUEST_CODE);
        } else {
            startApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean hasPermission = true;
            if (grantResults != null) {
                for (int grantResult : grantResults) {
                    hasPermission = hasPermission & (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
            if (hasPermission) {
                startApp();
            } else {
                // Permission Denied
                handler.removeCallbacksAndMessages(null);
                finish();
                System.exit(0);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startApp() {
        handler.postDelayed(startHomeTask, 1000);
    }
}
