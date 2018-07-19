package net.ajcloud.wansviewplus.main.alert;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.alert.adapter.AlertListDetailAdapter;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.type.camera.AudioSender_RealTime;
import net.ajcloud.wansviewplus.main.video.PlayerView;
import net.ajcloud.wansviewplus.support.core.api.AlertApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.core.bean.AlarmListBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.EndlessRecyclerOnScrollListener;
import net.ajcloud.wansviewplus.support.utils.DateUtil;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.ReverseAudioInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AlertDetailActivity extends BaseActivity implements PlayerView.OnChangeListener, Handler.Callback {

    private static final int SHOW_PROGRESS = 0;
    private static final int ON_LOADED = 1;
    private RelativeLayout rl_play_content;
    private RelativeLayout small_screen_layout;
    private LinearLayout full_screen_layout;
    private ImageView fullscreen_play;
    private ImageView fullscreen_stop;
    private ImageView fullscreen_download;
    private ImageView fullscreen_small_screen;
    private LinearLayout ll_loading;
    private PlayerView pv_video;
    private FrameLayout fl_play;
    private ImageView iv_fullscreen;
    private ImageView iv_download;
    private ImageView iv_play_pause;
    private TextView tv_time;
    private TextView tv_buffer;
    private RecyclerView rv_alarm_list;
    private TextView tv_date;
    private ImageView iv_arrow;
    private ImageView iv_cover;
    private FrameLayout fl_load;
    private FrameLayout fl_end;
    private SwipeRefreshLayout layout_refresh;

    private AudioSender_RealTime audioSender_Realtime = null;
    private Handler hPlayVlcAudioHandler = new Handler();
    private ReverseAudioInfo audioInfo;
    private Handler mHandler;
    private Handler hHandler;
    private AlertListDetailAdapter adapter;
    private AlertApiUnit alertApiUnit;

    private Camera camera;
    private List<AlarmBean> alarmList;
    private String deviceId;
    private String picUrl;
    private String videoUrl;
    private String cdate;
    private long cts = -1;
    private int AudioPlaySample;
    private boolean isPlaying;
    private boolean hasMore = true;
    private boolean isLandScape;
    private boolean isLoading = true;
    private boolean isFirst = true;

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

    private Runnable playingStateMonitorRunnable = new Runnable() {
        @Override
        public void run() {
            int nowState = pv_video.getPlayerState();
            if (nowState == -1)
                nowState = 0;

            hHandler.postDelayed(this, 500);

            Message message = new Message();
            message.what = nowState;
//            playingStateMonitorHandler.sendMessage(message);
        }
    };

    public static void start(Context context, String deviceId, String picUrl, String videoUrl, String date) {
        Intent intent = new Intent(context, AlertDetailActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("picUrl", picUrl);
        intent.putExtra("videoUrl", videoUrl);
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
        small_screen_layout = findViewById(R.id.small_screen_layout);
        full_screen_layout = findViewById(R.id.full_screen_layout);
        fullscreen_play = findViewById(R.id.fullscreen_play);
        fullscreen_stop = findViewById(R.id.fullscreen_stop);
        fullscreen_download = findViewById(R.id.fullscreen_download);
        fullscreen_small_screen = findViewById(R.id.fullscreen_small_screen);
        pv_video = findViewById(R.id.pv_video);
        fl_play = findViewById(R.id.fl_play);
        iv_fullscreen = findViewById(R.id.iv_fullscreen);
        iv_download = findViewById(R.id.iv_download);
        iv_play_pause = findViewById(R.id.iv_play_pause);
        tv_time = findViewById(R.id.tv_time);
        rv_alarm_list = findViewById(R.id.rv_alarm_list);
        ll_loading = findViewById(R.id.ll_loading);
        tv_buffer = findViewById(R.id.tv_buffer);
        iv_cover = findViewById(R.id.iv_cover);
        fl_load = findViewById(R.id.fl_load);
        fl_end = findViewById(R.id.fl_end);
        layout_refresh = findViewById(R.id.layout_refresh);

        layout_refresh.setEnabled(false);
        layout_refresh.setRefreshing(true);
        adapter = new AlertListDetailAdapter(this);
        rv_alarm_list.setAdapter(adapter);
        rv_alarm_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                if (itemPosition % 2 == 0) {
                    outRect.set(0, 0, DisplayUtil.dip2Pix(AlertDetailActivity.this, 20),
                            DisplayUtil.dip2Pix(AlertDetailActivity.this, 24));
                } else {
                    outRect.set(DisplayUtil.dip2Pix(AlertDetailActivity.this, 20), 0, 0,
                            DisplayUtil.dip2Pix(AlertDetailActivity.this, 24));
                }
            }
        });
        rv_alarm_list.setNestedScrollingEnabled(false);
        rv_alarm_list.setLayoutManager(new GridLayoutManager(this, 2));
        ((SimpleItemAnimator) rv_alarm_list.getItemAnimator()).setSupportsChangeAnimations(false);
        Animation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(200);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation, 0.5F);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        rv_alarm_list.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            picUrl = getIntent().getStringExtra("picUrl");
            videoUrl = getIntent().getStringExtra("videoUrl");
            cdate = getIntent().getStringExtra("date");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
            tv_date.setText(DateUtil.getFormatDate(cdate));
            Glide.with(this).load(picUrl).into(iv_cover);
        }
        alertApiUnit = new AlertApiUnit(this);
        mHandler = new Handler(this);
        hHandler = new Handler(this);
        pv_video.setSurfaceViewer16To9();
        getAlarmList();
        if (TextUtils.isEmpty(videoUrl)) {
            refreshUI(4);
        } else {
            refreshUI(1);
        }
    }

    @Override
    protected void initListener() {
        pv_video.setOnChangeListener(this);
        fl_play.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);
        iv_download.setOnClickListener(this);
        iv_play_pause.setOnClickListener(this);
        fullscreen_play.setOnClickListener(this);
        fullscreen_stop.setOnClickListener(this);
        fullscreen_download.setOnClickListener(this);
        fullscreen_small_screen.setOnClickListener(this);
        adapter.setListener(new AlertListDetailAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position, String imgUrl, String videoUrl) {
                if (TextUtils.isEmpty(videoUrl)) {
                    Glide.with(AlertDetailActivity.this).load(imgUrl).into(iv_cover);
                    refreshUI(4);
                } else {
                    onMediaPlay(videoUrl);
                }
            }
        });
        rv_alarm_list.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadNextPage(View view) {
                super.onLoadNextPage(view);
                if (!isLoading) {
                    if (hasMore) {
                        getAlarmList();
                    } else {
                        if (adapter.getItemCount() > 10) {
                            changeFootState(0);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        onMediaPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hPlayVlcAudioHandler.removeCallbacks(PlayVlcAudioRunnable);
        mHandler.removeCallbacks(playingStateMonitorRunnable);
        TurnOffPlayAudio();
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

                fullscreen_play.setVisibility(View.GONE);
                fullscreen_stop.setVisibility(View.GONE);
                fullscreen_download.setVisibility(View.GONE);
                break;
            case 2:
                fl_play.setVisibility(View.GONE);
                iv_play_pause.setVisibility(View.VISIBLE);
                iv_download.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.VISIBLE);
                iv_play_pause.setImageResource(R.mipmap.ic_stop_white);

                fullscreen_play.setVisibility(View.VISIBLE);
                fullscreen_stop.setVisibility(View.VISIBLE);
                fullscreen_download.setVisibility(View.VISIBLE);
                break;
            case 3:
                fl_play.setVisibility(View.GONE);
                iv_play_pause.setVisibility(View.VISIBLE);
                iv_download.setVisibility(View.GONE);
                tv_time.setVisibility(View.VISIBLE);
                iv_play_pause.setImageResource(R.mipmap.ic_play_white);

                fullscreen_play.setVisibility(View.VISIBLE);
                fullscreen_stop.setVisibility(View.VISIBLE);
                fullscreen_download.setVisibility(View.VISIBLE);
                break;
            case 4:
                fl_play.setVisibility(View.GONE);
                iv_play_pause.setVisibility(View.GONE);
                iv_download.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.GONE);

                fullscreen_play.setVisibility(View.GONE);
                fullscreen_stop.setVisibility(View.GONE);
                fullscreen_download.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 0：normal
     * 1:loading
     * 2:end
     */
    protected void changeFootState(int state) {
        switch (state) {
            case 0:
                fl_load.setVisibility(View.GONE);
                fl_end.setVisibility(View.GONE);
                break;
            case 1:
                fl_load.setVisibility(View.VISIBLE);
                fl_end.setVisibility(View.GONE);
                break;
            case 2:
                fl_load.setVisibility(View.GONE);
                fl_end.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void onMediaPlay(String url) {
        if (null != pv_video && !TextUtils.isEmpty(url)) {
            ll_loading.setVisibility(View.VISIBLE);
            refreshUI(2);
            isPlaying = true;
            pv_video.initPlayer(url);
            pv_video.play();
            hPlayVlcAudioHandler.postDelayed(PlayVlcAudioRunnable, 500);
        }
    }

    private void onMediaPause() {
        if (null != pv_video) {
            isPlaying = false;
            pv_video.pause();
            refreshUI(3);
        }
    }

    private void onMediaStart() {
        if (null != pv_video) {
            isPlaying = true;
            pv_video.play();
            refreshUI(2);
        }
    }

    private void onMediaStop() {
        if (null != pv_video) {
            pv_video.stop();
        }
    }

    void TurnOffPlayAudio() {
        if (audioSender_Realtime != null) {
            audioSender_Realtime.stopPlayVlcAudio();
        }
    }

    private void fullScreen() {
        isLandScape = true;
        setToolBarVisible(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        small_screen_layout.setVisibility(View.GONE);
        full_screen_layout.setVisibility(View.VISIBLE);
        rv_alarm_list.setVisibility(View.GONE);
    }

    private void exitFullScreen() {
        isLandScape = false;
        setToolBarVisible(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        small_screen_layout.setVisibility(View.VISIBLE);
        full_screen_layout.setVisibility(View.GONE);
        rv_alarm_list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pv_video.changeSurfaceSize();
    }

    @Override
    public void onBackPressed() {
        if (isLandScape) {
            exitFullScreen();
        } else {
            finish();
        }
    }

    //时间转换
    private String millisToString(long millis, boolean text) {
        boolean negative = millis < 0;
        millis = Math.abs(millis);
        int mini_sec = (int) millis % 1000;
        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");

        DecimalFormat format2 = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        format2.applyPattern("000");
        if (text) {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + "h" + format.format(min) + "min";
            else if (min > 0)
                time = (negative ? "-" : "") + min + "min";
            else
                time = (negative ? "-" : "") + sec + "s";
        } else {
            if (millis > 0)
                time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec) /*+ ":" + format2.format(mini_sec)*/;
            else
                time = (negative ? "-" : "") + min + ":" + format.format(sec) /*+ ":" + format2.format(mini_sec)*/;
        }
        return time;
    }

    private void getAlarmList() {
        isLoading = true;
        if (isFirst) {
            isFirst = false;
        } else {
            changeFootState(1);
        }
        alertApiUnit.getAlarmsList(deviceId, cts, cdate, 10, new OkgoCommonListener<AlarmListBean>() {
            @Override
            public void onSuccess(AlarmListBean bean) {
                layout_refresh.setRefreshing(false);
                changeFootState(0);
                isLoading = false;
                if (bean != null) {
                    if (bean.alarms != null) {
                        adapter.addData(bean.alarms);
                        if (bean.alarms.size() > 0) {
                            cts = Long.parseLong(bean.alarms.get(bean.alarms.size() - 1).cts);
                            cdate = bean.alarms.get(bean.alarms.size() - 1).cdate;
                        }
                        hasMore = bean.alarms.size() == 10;
                    }
                }
            }

            @Override
            public void onFail(int code, String msg) {
                layout_refresh.setRefreshing(false);
                changeFootState(0);
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.fl_play:
                if (!TextUtils.isEmpty(videoUrl)) {
                    onMediaPlay(videoUrl);
                }
                break;
            case R.id.iv_fullscreen:
                fullScreen();
                break;
            case R.id.fullscreen_small_screen:
                exitFullScreen();
                break;
            case R.id.iv_download:
            case R.id.fullscreen_download:
                break;
            case R.id.iv_play_pause:
                if (isPlaying) {
                    onMediaPause();
                } else {
                    onMediaStart();
                }
                break;
            case R.id.fullscreen_play:
                onMediaStart();
                break;
            case R.id.fullscreen_stop:
                onMediaPause();
                break;
        }
    }

    @Override
    public void onBufferChanged(float buffer) {
        if (buffer >= 100) {
            ll_loading.setVisibility(View.GONE);
        } else {
            ll_loading.setVisibility(View.VISIBLE);
        }
        tv_buffer.setText(getString(R.string.play_buffing) + (int) buffer + "%");
    }

    @Override
    public void onLoadComplet() {
        mHandler.sendEmptyMessage(ON_LOADED);
    }

    @Override
    public void onError() {
        ToastUtil.show(R.string.videoActivity_network_slow);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onNetSlow() {
        ToastUtil.show(R.string.app_error_network_error);
    }

    @Override
    public void onTimeChange(long time) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS:
                int time = (int) pv_video.getTime();
                if (time >= 0) {
                    tv_time.setText(millisToString(time, false));
                }
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 50);
                break;
            case ON_LOADED:
                mHandler.sendEmptyMessage(SHOW_PROGRESS);
                ll_loading.setVisibility(View.GONE);
                iv_cover.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        return false;
    }
}
