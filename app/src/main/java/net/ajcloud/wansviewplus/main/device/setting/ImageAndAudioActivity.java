package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.dialog.CommonDialog;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class ImageAndAudioActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private RelativeLayout placementLayout;
    private TextView placementTextView, volumeTextView;
    private SwitchCompat nightSwitch, microphoneSwitch, lightSwitch;
    private AppCompatSeekBar volumeSeekbar;
    private CommonDialog placeDialog;
    private String deviceId;
    private Camera camera;

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
        getToolbar().setLeftImg(R.mipmap.icon_back);
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
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
        if (camera != null) {
            nightSwitch.setChecked(camera.nightMode == 1);

            if (camera.audioConfig != null) {
                if (camera.audioConfig.enable == 1) {
                    microphoneSwitch.setChecked(true);
                    volumeSeekbar.setProgress(camera.audioConfig.volume);
                }
            }
        }
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
                ToastUtil.single(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_placement:
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_night_switch:
                break;
            case R.id.item_microphone_switch:
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
                    .height(368)
                    .width(327)
                    .addViewOnclickListener(net.ajcloud.wansviewplus.R.id.iv_close, dialogClickListener)
                    .build();
        }
        if (!placeDialog.isShowing()) {
            placeDialog.show();
        }
    }

    private View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case net.ajcloud.wansviewplus.R.id.iv_close:
                    if (placeDialog != null && placeDialog.isShowing()) {
                        placeDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
