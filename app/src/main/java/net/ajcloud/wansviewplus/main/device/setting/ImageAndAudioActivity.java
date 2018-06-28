package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.dialog.CommonDialog;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class ImageAndAudioActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String LOADING = "LOADING";
    private RelativeLayout placementLayout;
    private TextView placementTextView, volumeTextView;
    private SwitchCompat nightSwitch, microphoneSwitch, lightSwitch;
    private AppCompatSeekBar volumeSeekbar;
    private CommonDialog placeDialog;
    private String deviceId;
    private Camera camera;
    private Camera cloneCamera;
    private DeviceApiUnit deviceApiUnit;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, ImageAndAudioActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_and_audio;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Image and audio");
        getToolbar().setLeftImg(R.mipmap.ic_back);
        placementLayout = findViewById(R.id.item_placement);
        placementTextView = findViewById(R.id.item_placement_mode);
        volumeTextView = findViewById(R.id.item_volume_level);
        nightSwitch = findViewById(R.id.item_night_switch);
        microphoneSwitch = findViewById(R.id.item_microphone_switch);
        lightSwitch = findViewById(R.id.item_light_switch);
        volumeSeekbar = findViewById(R.id.item_volume_seekbar);
    }

    @Override
    protected void initData() {
        deviceApiUnit = new DeviceApiUnit(this);
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
            cloneCamera = (Camera) camera.deepClone();
        }
        refreshUI();
    }

    @Override
    protected void initListener() {
        placementLayout.setOnClickListener(this);
        nightSwitch.setOnCheckedChangeListener(this);
        microphoneSwitch.setOnCheckedChangeListener(this);
        lightSwitch.setOnCheckedChangeListener(this);
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeTextView.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cloneCamera.audioConfig.speakerVolume = String.valueOf(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_placement:
                showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_night_switch:
                if (isChecked) {
                    cloneCamera.nightMode = "2";
                } else {
                    cloneCamera.nightMode = "0";
                }
                break;
            case R.id.item_microphone_switch:
                if (isChecked) {
                    cloneCamera.audioConfig.micEnable = "1";
                } else {
                    cloneCamera.audioConfig.micEnable = "0";
                }
                break;
            case R.id.item_light_switch:
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        if (placeDialog == null) {
            placeDialog = new CommonDialog.Builder(this)
                    .canceledOnTouchOutside(false)
                    .view(R.layout.dialog_place)
                    .width(327)
                    .addViewOnclickListener(R.id.iv_close, dialogClickListener)
                    .addViewOnclickListener(R.id.iv_positioning, dialogClickListener)
                    .addViewOnclickListener(R.id.iv_inverted, dialogClickListener)
                    .build();
        }
        if (!placeDialog.isShowing()) {
            placeDialog.show();
        }
    }

    private View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (placeDialog != null && placeDialog.isShowing()) {
                placeDialog.dismiss();
            }
            switch (v.getId()) {
                case R.id.iv_positioning:
                    cloneCamera.orientationValue = "0";
                    refreshUI();
                    break;
                case R.id.iv_inverted:
                    cloneCamera.orientationValue = "3";
                    refreshUI();
                    break;
                default:
                    break;
            }
        }
    };

    private void refreshUI() {
        if (cloneCamera != null) {
            if (TextUtils.equals(cloneCamera.orientationValue, "0")) {
                placementTextView.setText("positioning");
            } else {
                placementTextView.setText("inverted");
            }

            nightSwitch.setChecked(TextUtils.equals(cloneCamera.nightMode, "2"));

            if (cloneCamera.audioConfig != null) {
                microphoneSwitch.setChecked(TextUtils.equals(cloneCamera.audioConfig.micEnable, "1"));
                if (!TextUtils.isEmpty(camera.audioConfig.speakerVolume)) {
                    volumeTextView.setText(camera.audioConfig.speakerVolume);
                    volumeSeekbar.setProgress(Integer.valueOf(camera.audioConfig.speakerVolume));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        progressDialogManager.showDialog(LOADING, ImageAndAudioActivity.this);
        deviceApiUnit.setPlacement(cloneCamera.getGatewayUrl(), cloneCamera.deviceId, "3", new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                camera.orientationValue = cloneCamera.orientationValue;
                deviceApiUnit.setNightVersion(cloneCamera.getGatewayUrl(), cloneCamera.deviceId, cloneCamera.nightMode, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        camera.nightMode = cloneCamera.nightMode;
                        deviceApiUnit.setAudioConfig(cloneCamera.getGatewayUrl(), cloneCamera.deviceId, cloneCamera.audioConfig, new OkgoCommonListener<Object>() {
                            @Override
                            public void onSuccess(Object bean) {
                                progressDialogManager.dimissDialog(LOADING, 0);
                                camera.audioConfig = cloneCamera.audioConfig;
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
                    public void onFail(int code, String msg) {
                        progressDialogManager.dimissDialog(LOADING, 0);
                        ToastUtil.single(msg);
                        finish();
                    }
                });
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
