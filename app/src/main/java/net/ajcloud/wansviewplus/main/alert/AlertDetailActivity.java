package net.ajcloud.wansviewplus.main.alert;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.type.camera.AudioSender_RealTime;
import net.ajcloud.wansviewplus.main.video.PlayerView;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.DateUtil;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.ReverseAudioInfo;

public class AlertDetailActivity extends BaseActivity implements PlayerView.OnChangeListener {

    private RelativeLayout rl_play_content;
    private PlayerView pv_video;
    private FrameLayout fl_play;
    private ImageView iv_fullscreen;
    private ImageView iv_download;
    private ImageView iv_play_pause;
    private TextView tv_time;
    private RecyclerView rv_alarm_list;
    private TextView tv_date;
    private ImageView iv_arrow;

    private String deviceId;
    private String date;
    private Camera camera;
    private AudioSender_RealTime audioSender_Realtime = null;
    private Handler hPlayVlcAudioHandler = new Handler();
    private int AudioPlaySample;
    private ReverseAudioInfo audioInfo;

    private Runnable PlayVlcAudioRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("send_usetime", "PlayVlcAudioRunnable");
            AudioPlaySample = pv_video.getMediaPlayer().GetAudioSampleRate();

            if (!(pv_video.getMediaPlayer().getPlayerState() == Media.State.Playing)
                    || AudioPlaySample == 0) {
                hPlayVlcAudioHandler.postDelayed(this, 100);
                return;
            }

            audioInfo = new ReverseAudioInfo();
            audioInfo.setiSampleRate(AudioPlaySample);

            if (audioSender_Realtime == null) {
                try {
                    Log.d("send_usetime", "audioSender_RealTime");
                    audioSender_Realtime = new AudioSender_RealTime(AlertDetailActivity.this, null, audioInfo, AudioPlaySample);
                    audioSender_Realtime.mMediaPlayer = pv_video.getMediaPlayer();
                    audioSender_Realtime.IsMute = false;
                    audioSender_Realtime.startPlayVlcAudio();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("send_usetime", "audioSender_RealTime2");
                audioSender_Realtime.mMediaPlayer = pv_video.getMediaPlayer();
                audioSender_Realtime.IsMute = false;
                audioSender_Realtime.frequency_play = AudioPlaySample;
                audioSender_Realtime.startPlayVlcAudio();
            }
        }
    };

    public static void start(Context context, String deviceId, String date) {
        Intent intent = new Intent(context, AlertDetailActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("date", date);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alert_detail;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setLeftImg(R.mipmap.ic_back);
        getToolbar().setTittleCalendar(R.string.alert_detail);
        tv_date = getToolbar().getTextDate();
        iv_arrow = getToolbar().getImgArrow();

        rl_play_content = findViewById(R.id.rl_play_content);
        pv_video = findViewById(R.id.pv_video);
        fl_play = findViewById(R.id.fl_play);
        iv_fullscreen = findViewById(R.id.iv_fullscreen);
        iv_download = findViewById(R.id.iv_download);
        iv_play_pause = findViewById(R.id.iv_play_pause);
        tv_time = findViewById(R.id.tv_time);
        rv_alarm_list = findViewById(R.id.rv_alarm_list);
        refreshUI(1);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            date = getIntent().getStringExtra("date");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
            tv_date.setText(DateUtil.getFormatDate(date));
        }
        pv_video.setSurfaceViewer16To9();
    }

    @Override
    protected void initListener() {
        pv_video.setOnChangeListener(this);
        fl_play.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);
        iv_download.setOnClickListener(this);
        iv_play_pause.setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pv_video.destroy();
    }

    /**
     * 1:第一次进入；
     * 2:播放；
     * 3:暂停；
     * 4:未购买云存储服务（只有图片无视频）；
     */
    private void refreshUI(int state) {
        switch (state) {
            case 1:
                fl_play.setVisibility(View.VISIBLE);
                iv_play_pause.setVisibility(View.GONE);
                iv_download.setVisibility(View.GONE);
                tv_time.setVisibility(View.GONE);
                break;
            case 2:
                fl_play.setVisibility(View.GONE);
                iv_play_pause.setVisibility(View.VISIBLE);
                iv_download.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.VISIBLE);
                iv_play_pause.setImageResource(R.mipmap.ic_stop_white);
                break;
            case 3:
                fl_play.setVisibility(View.GONE);
                iv_play_pause.setVisibility(View.VISIBLE);
                iv_download.setVisibility(View.GONE);
                tv_time.setVisibility(View.VISIBLE);
                iv_play_pause.setImageResource(R.mipmap.ic_play_white);
                break;
            case 4:
                fl_play.setVisibility(View.GONE);
                iv_play_pause.setVisibility(View.GONE);
                iv_download.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.GONE);
                break;
        }
    }

    private void onMediaPlay(String url) {
        if (null != pv_video && !TextUtils.isEmpty(url)) {
            pv_video.initPlayer(url);
            pv_video.play();
            hPlayVlcAudioHandler.postDelayed(PlayVlcAudioRunnable, 500);
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.fl_play:
                onMediaPlay("https://f000.backblazeb2.com/file/wsc-alarm/9c17a6b08deb0b670d88c264bc99b5de/1531218996.alarm.ts?Authorization=3_20180710103643_3973ec72b2f7f9981341659a_621dc5af9e324ce1f534b21b580996171626f4cd_000_20180717103643_0032_dnld&expires=1531823003&stor=b2");
//                if (!pv_video.isPlaying()) {
//                    pv_video.play();
//                }
                refreshUI(2);
                break;
            case R.id.iv_fullscreen:
                break;
            case R.id.iv_download:
                break;
            case R.id.iv_play_pause:
                break;
        }
    }

    @Override
    public void onBufferChanged(float buffer) {

    }

    @Override
    public void onLoadComplet() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onNetSlow() {

    }
}
