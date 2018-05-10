package com.ajcloud.wansview.application;

import android.app.Activity;
import android.app.Application;

import com.ajcloud.wansview.BuildConfig;
import com.ajcloud.wansview.support.tools.CrashHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/10.
 */

public class MainApplication extends Application {

    private static MainApplication mInstance = null;

    private List<Activity> activities = new ArrayList<>();

    public static synchronized MainApplication getApplication() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (!BuildConfig.DEBUG) {
            //自定义崩溃处理
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }
    }

    public void pushActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void finshActivitys() {
        for (int i = (activities.size() - 1); i > 0; i--) {
            activities.get(i).finish();
        }
    }
}
