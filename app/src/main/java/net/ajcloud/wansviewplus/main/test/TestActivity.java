package net.ajcloud.wansviewplus.main.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hdl.m3u8.M3U8DownloadTask;
import com.hdl.m3u8.bean.OnDownloadListener;
import com.hdl.m3u8.utils.NetSpeedUtils;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.application.SwipeBaseActivity;
import net.ajcloud.wansviewplus.main.calendar.CalendarActivity;
import net.ajcloud.wansviewplus.support.core.api.AlertApiUnit;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.core.bean.GroupListBean;
import net.ajcloud.wansviewplus.support.core.bean.LiveSrcBean;
import net.ajcloud.wansviewplus.support.core.bean.ViewAnglesBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.ReplayTimeAxisView;
import net.ajcloud.wansviewplus.support.tools.WLog;
import net.ajcloud.wansviewplus.support.utils.FileUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TestActivity extends SwipeBaseActivity {

    long time;
    private ReplayTimeAxisView replayTimeAxisView;
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final int CALENDAR_REQUEST = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Test");
        getToolbar().setLeftImg(R.mipmap.ic_back);
        time = System.currentTimeMillis();
        replayTimeAxisView = findViewById(R.id.aaaaa);
        //设置中间时间点
        replayTimeAxisView.setMidTimeStamp(time);
        replayTimeAxisView.setOnSlideListener(new ReplayTimeAxisView.OnSlideListener() {
            @Override
            public void onSlide(long timeStamp) {
                ToastUtil.single(sDateFormat.format(timeStamp));
            }

            @Override
            public void onSelected(long startTime, long endTime) {
                ToastUtil.single(sDateFormat.format(startTime) + "\n" + sDateFormat.format(endTime));
            }
        });
        timeCount = new TimeCount(12 * 60 * 1000, 200);
//        timeCount.start();
    }

    @Override
    protected void initData() {
        final EditText etTest = findViewById(R.id.et_test);
        final TextView tvTest = findViewById(R.id.tv_test);
        findViewById(net.ajcloud.wansviewplus.R.id.button_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera camera = MainApplication.getApplication().getDeviceCache().get("K03868WPCGRPFDX4");
                new DeviceApiUnit(TestActivity.this).doSnapshot(camera.getGatewayUrl(), camera.deviceId, new OkgoCommonListener<String>() {
                    @Override
                    public void onSuccess(String bean) {
                        etTest.setText(bean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        etTest.setText(msg);
                    }
                });
            }
        });
        findViewById(net.ajcloud.wansviewplus.R.id.button_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera camera = MainApplication.getApplication().getDeviceCache().get("K03868KVLJNASXNC");
                new DeviceApiUnit(TestActivity.this).b2Upload(camera.deviceId, FileUtil.getFirstFramePath() + "/" + "K03868KVLJNASXNC.jpg", "cam-viewangle", "b2", 1, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
        findViewById(net.ajcloud.wansviewplus.R.id.button_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Camera camera = MainApplication.getApplication().getDeviceCache().get("K03868KVLJNASXNC");
                new DeviceApiUnit(TestActivity.this).getLiveSrcToken(camera.deviceId, 1, 1, new OkgoCommonListener<LiveSrcBean>() {
                    @Override
                    public void onSuccess(LiveSrcBean bean) {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
        findViewById(net.ajcloud.wansviewplus.R.id.delete_angle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Camera camera = MainApplication.getApplication().getDeviceCache().get("K03868EIVDXGXYBB");
                if (camera.viewAnglesConfig == null) {
                    return;
                }
                List<Integer> angles = new ArrayList<>();
                for (ViewAnglesBean.ViewAngle angle : camera.viewAnglesConfig.viewAngles
                        ) {
                    angles.add(angle.viewAngle);
                }
                new DeviceApiUnit(TestActivity.this).deleteAngles(camera.deviceId, angles, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
        findViewById(R.id.turn_to_angle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Camera camera = MainApplication.getApplication().getDeviceCache().get("K03868EIVDXGXYBB");
//
//                if (camera.viewAnglesConfig == null || camera.viewAnglesConfig.viewAngles.size() == 0) {
//                    return;
//                }
//                int angle = camera.viewAnglesConfig.viewAngles.get(0).viewAngle;
//                new DeviceApiUnit(TestActivity.this).turnToAngles(camera.deviceId, angle, new OkgoCommonListener<Object>() {
//                    @Override
//                    public void onSuccess(Object bean) {
//
//                    }
//
//                    @Override
//                    public void onFail(int code, String msg) {
//
//                    }
//                });

                replayTimeAxisView.setCurrentMode(ReplayTimeAxisView.Mode.DownLoad);
            }
        });
        findViewById(R.id.group_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Camera camera = MainApplication.getApplication().getDeviceCache().get("K3C876J4PAXFNGVW");

                new DeviceApiUnit(TestActivity.this).getGroupList("K3C876J4PAXFNGVW", getTimesmorning(), getTimesnight(), new OkgoCommonListener<GroupListBean>() {
                    @Override
                    public void onSuccess(GroupListBean bean) {
                        if (bean != null && bean.groups != null) {
                            List<Pair<Long, Long>> list = new ArrayList<>();
                            for (GroupListBean.GroupInfo info : bean.groups
                                    ) {
//                                if (!TextUtils.isEmpty(info.m3u8Url))
//                                    testM3u8(info.m3u8Url);
                                Pair<Long, Long> time = new Pair<>(info.tsStart / 1000, info.tsEnd / 1000);
                                list.add(time);

                            }
                            replayTimeAxisView.setRecordList(list);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
        findViewById(R.id.getAlarmsSummary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertApiUnit(TestActivity.this).getAlarmsSummary(new OkgoCommonListener<List<AlarmBean>>() {
                    @Override
                    public void onSuccess(List<AlarmBean> bean) {
                        tvTest.setText(new Gson().toJson(bean));
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
    }

    //上一秒的大小
    private long lastLength = 0;
    M3U8DownloadTask task1 = new M3U8DownloadTask("1001");

    private void testM3u8(String url) {
        task1.setSaveFilePath(MainApplication.fileIO.getCacheDir() + "oid" + File.separator + System.currentTimeMillis() + ".ts");
        task1.download(url, new OnDownloadListener() {
            @Override
            public void onDownloading(final long itemFileSize, final int totalTs, final int curTs) {
                WLog.e("=====", task1.getTaskId() + "下载中.....itemFileSize=" + itemFileSize + "\ttotalTs=" + totalTs + "\tcurTs=" + curTs);
            }

            /**
             * 下载成功
             */
            @Override
            public void onSuccess() {
                WLog.e("=====", task1.getTaskId() + "下载完成了");
            }

            /**
             * 当前的进度回调
             *
             * @param curLenght
             */
            @Override
            public void onProgress(final long curLenght) {
                if (curLenght - lastLength > 0) {
                    final String speed = NetSpeedUtils.getInstance().displayFileSize(curLenght - lastLength) + "/s";
                    WLog.e("=====", task1.getTaskId() + "speed = " + speed);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WLog.e("=====", "更新了");
                            // tvSpeed1.setText(speed);
                            //EWWlog.e(tvSpeed1.getText().toString());
                        }
                    });
                    lastLength = curLenght;

                }
            }

            @Override
            public void onStart() {
                WLog.e("=====", task1.getTaskId() + "开始下载了");
            }

            @Override
            public void onError(Throwable errorMsg) {
                WLog.e("=====", task1.getTaskId() + "出错了" + errorMsg);
            }
        });
    }

    /**
     * 图片查看器
     */
    private void startImageLoader() {
        ArrayList<String> urls;
        urls = new ArrayList<>();
        //为了显示效果，重复添加了三次
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524477979306&di=3eb07e9302606048abe13d7b6a2bc601&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201406%2F12%2F20140612211118_YYXAC.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524133463580&di=1315bc4db30999f00b89ef79c3bb06e5&imgtype=0&src=http%3A%2F%2Fpic36.photophoto.cn%2F20150710%2F0005018721870517_b.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524133463575&di=6221f21bcb761675c5d161ebc53d5948&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201410%2F03%2F20141003112442_AkkuH.thumb.700_0.jpeg");

        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524477979306&di=3eb07e9302606048abe13d7b6a2bc601&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201406%2F12%2F20140612211118_YYXAC.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524133463580&di=1315bc4db30999f00b89ef79c3bb06e5&imgtype=0&src=http%3A%2F%2Fpic36.photophoto.cn%2F20150710%2F0005018721870517_b.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524133463575&di=6221f21bcb761675c5d161ebc53d5948&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201410%2F03%2F20141003112442_AkkuH.thumb.700_0.jpeg");

        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524477979306&di=3eb07e9302606048abe13d7b6a2bc601&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201406%2F12%2F20140612211118_YYXAC.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524133463580&di=1315bc4db30999f00b89ef79c3bb06e5&imgtype=0&src=http%3A%2F%2Fpic36.photophoto.cn%2F20150710%2F0005018721870517_b.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524133463575&di=6221f21bcb761675c5d161ebc53d5948&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201410%2F03%2F20141003112442_AkkuH.thumb.700_0.jpeg");

//        ImagePagerActivity.startImagePage(TestActivity.this,urls, 0, null);
    }

    /**
     * 启动日历
     */
    private void startCalendar() {
        HashMap<String, Boolean> record = new HashMap<>();
        Intent intent = new Intent(TestActivity.this, CalendarActivity.class);
        Bundle bundleSerializable = new Bundle();
        bundleSerializable.putSerializable("serializable", record);
        intent.putExtra("isShowNextDay", true);
        intent.putExtras(bundleSerializable);
        intent.putExtra("oid", getIntent().getStringExtra("cid"));
        startActivityForResult(intent, CALENDAR_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CALENDAR_REQUEST) {
            String strTime = data.getStringExtra("calendar");
        }
    }

    private JSONObject getReqBody(JSONObject data, String deviceId) {
        try {
            JSONObject metaJson = new JSONObject();
            metaJson.put("locale", MainApplication.getApplication().getLocalInfo().appLang);
            metaJson.put("localtz", MainApplication.getApplication().getLocalInfo().timeZone);
            String accessToken = SigninAccountManager.getInstance().getCurrentAccountAccessToken();
            if (!TextUtils.isEmpty(accessToken)) {
                metaJson.put("accessToken", accessToken);
            }
            if (!TextUtils.isEmpty(deviceId)) {
                metaJson.put("deviceId", deviceId);
            }

            JSONObject body = new JSONObject();
            body.put("meta", metaJson);
            body.put("data", data);
            return body;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TimeCount timeCount;

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time = time + 1000;
            replayTimeAxisView.setMidTimeStamp(time);
        }

        @Override
        public void onFinish() {

        }
    }

    //获得当天0点时间
    public static long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //获得当天24点时间
    public static long getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
