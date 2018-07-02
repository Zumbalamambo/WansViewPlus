package net.ajcloud.wansviewplus.main.test;

import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.account.SigninAccountManager;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.GroupListBean;
import net.ajcloud.wansviewplus.support.core.bean.LiveSrcBean;
import net.ajcloud.wansviewplus.support.core.bean.ViewAnglesBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.ReplayTimeAxisView;
import net.ajcloud.wansviewplus.support.utils.FileUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends BaseActivity {

    private ReplayTimeAxisView replayTimeAxisView;
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        replayTimeAxisView = findViewById(R.id.aaaaa);
        replayTimeAxisView.setMidTimeStamp(System.currentTimeMillis());

        List<Pair<Long, Long>> list = new ArrayList<>();
        Pair<Long, Long> time1 = new Pair<>(System.currentTimeMillis()/1000 - 7000, System.currentTimeMillis()/1000 - 5000);
        Pair<Long, Long> time2 = new Pair<>(System.currentTimeMillis()/1000 - 4000, System.currentTimeMillis() /1000- 1500);
        Pair<Long, Long> time3 = new Pair<>(System.currentTimeMillis()/1000 - 1000, System.currentTimeMillis() /1000+ 1000);
        Pair<Long, Long> time4 = new Pair<>(System.currentTimeMillis()/1000 + 1500, System.currentTimeMillis()/1000+ 4000);
        Pair<Long, Long> time5 = new Pair<>(System.currentTimeMillis() /1000+ 5000, System.currentTimeMillis()/1000 + 7000);
        list.add(time1);
        list.add(time2);
        list.add(time3);
        list.add(time4);
        list.add(time5);
        replayTimeAxisView.setRecordList(list);
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

                    }

                    @Override
                    public void onFail(int code, String msg) {

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

                Camera camera = MainApplication.getApplication().getDeviceCache().get("K03868EIVDXGXYBB");

                new DeviceApiUnit(TestActivity.this).getGroupList("K03868EIVDXGXYBB", System.currentTimeMillis() - 2000000, System.currentTimeMillis() + 2000000, new OkgoCommonListener<GroupListBean>() {
                    @Override
                    public void onSuccess(GroupListBean bean) {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        });
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
}
