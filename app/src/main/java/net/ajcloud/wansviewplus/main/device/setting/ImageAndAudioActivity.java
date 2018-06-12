package net.ajcloud.wansviewplus.main.device.setting;

import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

public class ImageAndAudioActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private RelativeLayout placementLayout;
    private TextView placementTextView, volumeTextView;
    private SwitchCompat nightSwitch, microphoneSwitch, lightSwitch;
    private AppCompatSeekBar volumeSeekbar;

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
}
