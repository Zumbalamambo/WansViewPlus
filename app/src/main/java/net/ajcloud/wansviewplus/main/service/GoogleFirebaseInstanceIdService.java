package net.ajcloud.wansviewplus.main.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.bean.AppConfigBean;
import net.ajcloud.wansviewplus.support.tools.WLog;

public class GoogleFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceIdService";
    private UserApiUnit userApiUnit;

    @Override
    public void onCreate() {
        super.onCreate();
        userApiUnit = new UserApiUnit(this);
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        WLog.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        // TODO: Implement this method to send token to your app server.

        userApiUnit.getAppConfig(new OkgoCommonListener<AppConfigBean>() {
            @Override
            public void onSuccess(AppConfigBean bean) {
                userApiUnit.pushSetting("upsert", token, null, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
