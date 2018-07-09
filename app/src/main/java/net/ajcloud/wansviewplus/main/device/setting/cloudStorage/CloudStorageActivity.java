package net.ajcloud.wansviewplus.main.device.setting.cloudStorage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.CloudStorBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.dialog.AudioQualityDialog;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.List;

public class CloudStorageActivity extends BaseActivity {

    private static final String LOADING = "LOADING";
    private SwitchCompat storageSwitch;
    private RelativeLayout timeLayout, qualityLayout;
    private TextView timeTextView, qualityTextView;
    private Button expireButton, replenishButton;
    private AudioQualityDialog audioQualityDialog;
    private DeviceApiUnit deviceApiUnit;
    private String deviceId;
    private Camera camera;
    private CloudStorBean cloneBean;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, CloudStorageActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cloud_storage;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Cloud storage");
        getToolbar().setLeftImg(R.mipmap.ic_back);

        storageSwitch = findViewById(R.id.item_storage_switch);
        timeLayout = findViewById(R.id.item_time);
        qualityLayout = findViewById(R.id.item_quality);
        timeTextView = findViewById(R.id.item_time_time);
        qualityTextView = findViewById(R.id.item_quality_time);
        expireButton = findViewById(R.id.btn_expire);
        replenishButton = findViewById(R.id.btn_replenish);
        audioQualityDialog = new AudioQualityDialog(this);
    }

    @Override
    protected void initData() {

        deviceApiUnit = new DeviceApiUnit(this);
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
            if (camera.localStorConfig != null) {
                cloneBean = (CloudStorBean) camera.cloudStorConfig.deepClone();
            }
        }
        refreshUI();
    }

    @Override
    protected void initListener() {
        timeLayout.setOnClickListener(this);
        qualityLayout.setOnClickListener(this);
        replenishButton.setOnClickListener(this);
        storageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cloneBean.enable = "1";
                } else {
                    cloneBean.enable = "0";
                }
                refreshUI();
            }
        });
        audioQualityDialog.setDialogClickListener(new AudioQualityDialog.OnDialogClickListener() {
            @Override
            public void onClick(String quality) {
                cloneBean.quality = quality;
                refreshUI();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_time:
                CloudDetectionTimeActivity.start(CloudStorageActivity.this, cloneBean);
                break;
            case R.id.item_quality:
                if (!audioQualityDialog.isShowing()) {
                    audioQualityDialog.show();
                }
                break;
            default:
                break;
        }
    }

    private void refreshUI() {
        if (cloneBean != null) {
            if (TextUtils.equals(cloneBean.enable, "1")) {
                storageSwitch.setChecked(true);
                timeLayout.setVisibility(View.VISIBLE);
                qualityLayout.setVisibility(View.VISIBLE);

                if (TextUtils.equals(cloneBean.quality, "1")) {
                    qualityTextView.setText("HD");
                } else if (TextUtils.equals(cloneBean.quality, "5")) {
                    qualityTextView.setText("FHD");
                }

                List<CloudStorBean.Policy> policyList = cloneBean.policies;
                for (CloudStorBean.Policy policy : policyList) {
                    if (TextUtils.equals(policy.no, "1")) {
                        if (TextUtils.equals(policy.enable, "1")) {
                            timeTextView.setText("24-hour");
                        } else {
                            StringBuilder time = new StringBuilder();
                            for (CloudStorBean.Policy policy_2 : policyList) {
                                if (TextUtils.equals(policy_2.no, "2")) {
                                    if (TextUtils.equals(policy_2.enable, "1")) {
                                        time.append(" period1");
                                    }
                                } else if (TextUtils.equals(policy_2.no, "3")) {
                                    if (TextUtils.equals(policy_2.enable, "1")) {
                                        time.append(" period2");
                                    }
                                }
                            }
                            timeTextView.setText(time.toString());
                        }
                    }
                }
            } else {
                storageSwitch.setChecked(false);
                timeLayout.setVisibility(View.GONE);
                qualityLayout.setVisibility(View.GONE);
            }
        }
    }

    private void doSet() {
        progressDialogManager.showDialog(LOADING, this);
        deviceApiUnit.setCloudStor(camera.getGatewayUrl(), deviceId, cloneBean, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(LOADING, 0);
                camera.cloudStorConfig = cloneBean;
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

    @Override
    public void onBackPressed() {
        doSet();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            CloudStorBean cloudStorBean = (CloudStorBean) data.getSerializableExtra("CloudStorBean");
            cloneBean = cloudStorBean;
            refreshUI();
        }
    }
}
