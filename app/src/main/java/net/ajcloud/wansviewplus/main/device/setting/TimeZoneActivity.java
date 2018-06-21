package net.ajcloud.wansviewplus.main.device.setting;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.TimezoneInfo;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.adapter.TimezoneAdapter;
import net.ajcloud.wansviewplus.main.device.adapter.TimezoneDecoration;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.FileUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.List;

import static net.ajcloud.wansviewplus.main.device.setting.DeviceSettingActivity.TIMEZONE;

public class TimeZoneActivity extends BaseActivity {

    private static final String LOADING = "LOADING";
    private RecyclerView timezoneRecycler;
    private TimezoneAdapter timezoneAdapter;
    private List<TimezoneInfo> timezoneInfos;
    private String deviceId;
    private Camera camera;
    private Camera cloneCamera;
    private DeviceApiUnit deviceApiUnit;

    public static void start(Activity context, String deviceId) {
        Intent intent = new Intent(context, TimeZoneActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivityForResult(intent, TIMEZONE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_time_zone;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Camera time zone");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        timezoneRecycler = findViewById(R.id.rv_timezone);
        timezoneAdapter = new TimezoneAdapter(this);
        timezoneRecycler.setLayoutManager(new LinearLayoutManager(this));
        timezoneRecycler.addItemDecoration(new TimezoneDecoration(this));
        timezoneRecycler.setAdapter(timezoneAdapter);
    }

    @Override
    protected void initData() {
        deviceApiUnit = new DeviceApiUnit(this);
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
            cloneCamera = (Camera) camera.deepClone();
        }

        timezoneInfos = FileUtil.getTimeZoneDataFromJson(this);

        int currentSelected = 0;
        for (TimezoneInfo info : timezoneInfos) {
            if (TextUtils.equals(info.en, cloneCamera.timeConfig.tzName)) {
                currentSelected = timezoneInfos.indexOf(info);
            }
        }
        timezoneAdapter.setData(timezoneInfos, currentSelected);
    }

    @Override
    protected void initListener() {
        timezoneAdapter.setOnItemClickListener(new TimezoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, TimezoneInfo bean) {
                cloneCamera.timeConfig.tzName = bean.en;
                cloneCamera.timeConfig.tzValue = bean.timeZone;
                deSet();
            }
        });
    }

    private void deSet() {
        progressDialogManager.showDialog(LOADING, this);
        deviceApiUnit.setTimeZone(cloneCamera.getGatewayUrl(), deviceId, cloneCamera.timeConfig, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(LOADING, 0);
                camera.timeConfig = cloneCamera.timeConfig;
                Intent intent = new Intent();
                intent.putExtra("timezone", cloneCamera.timeConfig.tzName);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single(msg);
                finish();
            }
        });
    }
}
