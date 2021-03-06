package net.ajcloud.wansviewplus.main.application;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;

import net.ajcloud.wansviewplus.BuildConfig;
import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.DaoMaster;
import net.ajcloud.wansviewplus.entity.DaoSession;
import net.ajcloud.wansviewplus.entity.LocalInfo;
import net.ajcloud.wansviewplus.main.manager.SigninAccountManager;
import net.ajcloud.wansviewplus.main.account.SigninActivity;
import net.ajcloud.wansviewplus.main.framework.FileIO;
import net.ajcloud.wansviewplus.main.framework.impl.AndroidFileIO;
import net.ajcloud.wansviewplus.support.core.api.OkSignatureInterceptor;
import net.ajcloud.wansviewplus.support.core.api.OkTokenInterceptor;
import net.ajcloud.wansviewplus.support.core.device.AlarmCountCache;
import net.ajcloud.wansviewplus.support.core.device.DeviceCache;
import net.ajcloud.wansviewplus.support.core.okgo.OkGo;
import net.ajcloud.wansviewplus.support.core.okgo.cache.CacheEntity;
import net.ajcloud.wansviewplus.support.core.okgo.cache.CacheMode;
import net.ajcloud.wansviewplus.support.core.okgo.cookie.CookieJarImpl;
import net.ajcloud.wansviewplus.support.core.okgo.cookie.store.SPCookieStore;
import net.ajcloud.wansviewplus.support.core.okgo.interceptor.HttpLoggingInterceptor;
import net.ajcloud.wansviewplus.support.tools.CrashHandler;
import net.ajcloud.wansviewplus.support.utils.preference.PreferenceKey;
import net.ajcloud.wansviewplus.support.utils.preference.SPUtil;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * Created by mamengchao on 2018/05/10.
 */

public class MainApplication extends MultiDexApplication {

    private LocalInfo localInfo;

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private static MainApplication mInstance = null;

    private List<Activity> activities = new ArrayList<>();

    public static FileIO fileIO;

    private DeviceCache deviceCache;

    private AlarmCountCache alarmCountCache;

    public static synchronized MainApplication getApplication() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (!BuildConfig.DEBUG) {
            //Firebase错误收集
            Fabric.with(this, new Crashlytics());
            //自定义崩溃处理
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }
        fileIO = new AndroidFileIO(getAssets());


        //初始化OkGo
        OkGo.getInstance().init(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //增加全局刷新token拦截器
        builder.addInterceptor(new OkTokenInterceptor());
        //增加签名拦截器
        builder.addInterceptor(new OkSignatureInterceptor());
        // 是否开启日志
        if (BuildConfig.LOG_OKGO) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
            //非必要情况，不建议使用，第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
            //builder.addInterceptor(new ChuckInterceptor(this));
        }
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        //使用内存保持cookie，app退出后，cookie消失
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        //方法一：信任所有证书,不安全有风险
        //HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        //HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        //builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        //builder.hostnameVerifier(new SafeHostnameVerifier());

        //HttpHeaders headers = new HttpHeaders();
        //headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        //headers.put("commonHeaderKey2", "commonHeaderValue2");
        //HttpParams params = new HttpParams();
        //params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        //params.put("commonParamsKey2", "这里支持中文参数");
        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(1);                        //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                      //全局公共头
//                .addCommonParams(params);

        // GreenDao 初始化/////////////////////////////////////////////
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "wansview-plus-db");
        final Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        //FCM主题订阅
        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        FirebaseMessaging.getInstance().subscribeToTopic("ads");
        initNotificationChannel();
        deviceCache = new DeviceCache();
        alarmCountCache = new AlarmCountCache();
    }

    public void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getResources().getString(R.string.channel_normal_id);
            String channelName = getResources().getString(R.string.channel_normal);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);

            channelId = getResources().getString(R.string.channel_alarm_id);
            channelName = getResources().getString(R.string.channel_alarm);
            importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public LocalInfo getLocalInfo() {
        return getLocalInfo(true);
    }

    public LocalInfo getLocalInfo(boolean readCache) {
        if (localInfo == null || !readCache) {
            localInfo = new LocalInfo();
            //获取设备唯一标识
            localInfo.deviceId = (String) SPUtil.getSPUtil(this, PreferenceKey.sp_name.account).get(PreferenceKey.sp_key.DEVICE_ID, "");
            if (TextUtils.isEmpty(localInfo.deviceId)) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                    //在有权限的情况下，先尝试获取IMEI码
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    localInfo.deviceId = manager.getDeviceId();
                }
                if (TextUtils.isEmpty(localInfo.deviceId)) {
                    //尝试获取SERIAL码
                    try {
                        localInfo.deviceId = android.os.Build.class.getField("SERIAL").get(null).toString();
                    } catch (Exception e) {
                        //发生错误的情况下，使用随机生成的UUID
                        localInfo.deviceId = UUID.randomUUID().toString();
                        e.printStackTrace();
                    }
                }
                SPUtil.getSPUtil(this, PreferenceKey.sp_name.account).put(PreferenceKey.sp_key.DEVICE_ID, localInfo.deviceId);
            }
            localInfo.deviceName = android.os.Build.MODEL;
            localInfo.timeZone = TimeZone.getDefault().getRawOffset() / 1000 / 60;
            Locale locale = getResources().getConfiguration().locale;
            localInfo.appLang = locale.getLanguage();
        }
        return localInfo;
    }

    public void logout(boolean isForce) {
        //清理缓存
        SigninAccountManager.getInstance().clearAccountSigninInfo();
        getDeviceCache().clear();
        getAlarmCountCache().clear();

        if (isForce) {
            finshActivitys();
            Intent intent = new Intent(this, SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public DeviceCache getDeviceCache() {
        return deviceCache;
    }

    public AlarmCountCache getAlarmCountCache() {
        return alarmCountCache;
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

    public Activity getLastestActivity() {
        return activities.get(activities.size() - 1);
    }

    public void finshActivitys() {
        for (int i = (activities.size() - 1); i > 0; i--) {
            activities.get(i).finish();
        }
    }
}
