package net.ajcloud.wansviewplus.main.video;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.camera.EventMessage;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.device.type.camera.AudioSender_RealTime;
import net.ajcloud.wansviewplus.main.device.type.camera.PlayMethod;
import net.ajcloud.wansviewplus.main.video.adapter.VideoAdapter;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.MyStateBar;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;
import org.httprelay.HttpRelay;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.ReverseAudioInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CautionActivity extends BaseActivity implements PlayerView.OnChangeListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, Handler.Callback {

    public static void start(Context context, String url, boolean isHideControl) {
        Intent intent = new Intent(context, CautionActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("isHideControl", isHideControl);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, int PlayMethod, String oid, Camera camera, boolean isHideControl) {
        Intent intent = new Intent(context, CautionActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("oid", oid);
        intent.putExtra("PlayMethod", PlayMethod);
        intent.putExtra("camera", camera);
        intent.putExtra("isHideControl", isHideControl);
        context.startActivity(intent);
    }

    private static final int SHOW_PROGRESS = 0;
    private static final int ON_LOADED = 1;
    private static final int HIDE_OVERLAY = 2;

    private View rlLoading;
    private PlayerView mPlayerView;
    private TextView tvBuffer, tvTime, tvLength;
    private SeekBar sbVideo;
    private ImageButton ibLock, ibFarward, ibBackward, ibPlay, ibSize;
    private View llOverlay;
    private Toolbar toolbar;
    private TextView rate_text;
    private Handler mHandler;
    private MyStateBar myStateBar = null;
    private RecyclerView videoList;
    private VideoAdapter videoAdapter;
    private ImageButton fullButton;

    private String mUrl;
    private AudioSender_RealTime audioSender_Realtime = null;
    private boolean isLock = false;
    private String ip;
    private String port;
    private int playMethod;
    private String oid = null;
    private String emid = null;
    private Process HttpRelayProcess = null;
    public HttpRelay httpRelay = new HttpRelay();
    public boolean isOff = false;
    private int TFPlayType = 0;    //0:http 1:rtsp
    private Camera camera = null;
    private String LocalStoreType = "tf";
    private List<EventMessage> cautionList = new ArrayList<>();
    private int Orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private Thread HttpRelayTask = new Thread(new Runnable() {
        @Override
        public void run() {
            int[] info = new int[2];
            int status = httpRelay.GetStatus(info);

            while (info[0] != 2 && !isOff) {
                try {
                    Thread.currentThread();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("tltest", "httpRelay.GetStatus");
                httpRelay.GetStatus(info);
            }

            if (!isOff) {
                mUrl = "http://127.0.0.1:" + info[1] + "/web/" + LocalStoreType + "/" + mUrl;
                hHandler.post(PlayVideoRunnable);
            }
        }
    });

    /*return 0:http, 1:rtsp*/
//    private int GetTfPlayType(Camera camera) {
//        if (camera == null) {
//            return 0;
//        }
//
//        String[] playType = camera.capability.;
//        if (playType == null) {
//            return 0;
//        }
//
//        for (int i = 0; i < playType.length; i++) {
//            if (playType[i].equals("http")) {
//                return 0;
//            }
//        }
//
//        for (int i = 0; i < playType.length; i++) {
//            if (playType[i].equals("rtsp")) {
//                return 1;
//            }
//        }
//        return 0;
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_player;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        tvTime = findViewById(R.id.tv_time);
        tvLength = findViewById(R.id.tv_length);
        sbVideo = findViewById(R.id.sb_video);
        sbVideo.setOnSeekBarChangeListener(this);
        ibLock = findViewById(R.id.ib_lock);
        ibLock.setOnClickListener(this);
        ibBackward = findViewById(R.id.ib_backward);
        ibBackward.setOnClickListener(this);
        ibPlay = findViewById(R.id.ib_play);
        ibPlay.setOnClickListener(this);
        ibFarward = findViewById(R.id.ib_forward);
        ibFarward.setOnClickListener(this);
        ibSize = findViewById(R.id.ib_size);
        ibSize.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar_actionbar_portrait);
        rate_text = findViewById(R.id.rate_text);
        llOverlay = findViewById(R.id.ll_overlay);

        rlLoading = findViewById(R.id.rl_loading);
        tvBuffer = findViewById(R.id.tv_buffer);

        //使用步骤
        //第一步 ：通过findViewById或者new PlayerView()得到mPlayerView对象
        //mPlayerView= new PlayerView(PlayerActivity.this);
        mPlayerView = findViewById(R.id.pv_video);
        videoList = findViewById(R.id.item_recyclerview);
        myStateBar = findViewById(R.id.myStateBar);

        fullButton = findViewById(R.id.im_full);
    }

    @Override
    protected void initData() {
        mHandler = new Handler(this);

        mPlayerView.setSurfaceViewer16To9();

        //初始化适配
        VideoAdapter.OnClickItemListener listener = new VideoAdapter.OnClickItemListener() {
            @Override
            public void onClick(int position) {
                //todo
                if (position < cautionList.size()) {
                    EventMessage eventMessage = cautionList.get(position);
                    onMediaPlay(eventMessage.getVideoUrl());
                }
            }
        };
        videoAdapter = new VideoAdapter(this, null, listener);
    }

    @Override
    protected void initListener() {
        mPlayerView.setOnChangeListener(this);

        fullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fullButton.setImageResource(R.mipmap.shrink);

                } else {
                    Orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fullButton.setImageResource(R.mipmap.full);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            showControl();
        }
        return false;
    }

    public void showControl() {
        if (fullButton.getVisibility() != View.VISIBLE) {
            if (!isLock) {
                showOverlay();
            }
        } else {
            hideOverlay();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mPlayerView.changeSurfaceSize();
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullscreen();
        } else {
            notFullscreen();
        }
    }

    @Override
    public void onPause() {
        onMediaPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //判断当前屏幕方向
        if (this.getResources().getConfiguration().orientation
                != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            fullscreen();
        } else {
            notFullscreen();
        }
        upgratePlayView();
    }

    private void notFullscreen() {
//        getSupportActionBar().show();
        myStateBar.setVisibility(View.VISIBLE);
        videoList.setVisibility(View.VISIBLE);
        View v = getWindow().getDecorView();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    private void fullscreen() {
//        getSupportActionBar().hide();
        myStateBar.setVisibility(View.GONE);
        videoList.setVisibility(View.GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(option);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onDestroy() {
        if (playMethod == PlayMethod.HTTP_RELAY) {
            isOff = true;
            httpRelay.Stop();
        }

        hPlayVlcAudioHandler.removeCallbacks(PlayVlcAudioRunnable);
        mHandler.removeCallbacks(playingStateMonitorRunnable);
        TurnOffPlayAudio();
        if (playMethod == PlayMethod.HTTP_RELAY) {
            httpRelay.Stop();
        }
        isOff = true;
        mPlayerView.destroy();
        if (HttpRelayProcess != null) {
            HttpRelayProcess.destroy();
        }

        super.onDestroy();
    }

    @Override
    public void onBufferChanged(float buffer) {
        if (buffer >= 100) {
            hideLoading();
        } else {
            showLoading();
        }
        tvBuffer.setText(getString(R.string.play_buffing) + (int) buffer + "%");
    }

    private void showLoading() {
        rlLoading.setVisibility(View.VISIBLE);

    }

    private void hideLoading() {
        rlLoading.setVisibility(View.GONE);
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
        mUrl = "";
    }

    @Override
    public void onNetSlow() {
        ToastUtil.show(R.string.app_error_network_error);
    }

    @Override
    public void onTimeChange(long time) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_lock:
                if (isLock) {
                    ibLock.setBackgroundResource(R.mipmap.ic_lock);
                    showOverlay();
                } else {
                    ibLock.setBackgroundResource(R.mipmap.ic_locked);
                }
                isLock = !isLock;
                break;
            case R.id.ib_forward:
                mPlayerView.setRate(2f);
                upgrateRate();
                break;
            case R.id.ib_play:
                if (TextUtils.isEmpty(mUrl)) {
                    ToastUtil.show(R.string.wv_first_select_video);
                    return;
                }
                if (mPlayerView.isPlaying()) {
                    onMediaPause();
                } else {
                    mPlayerView.play();
                }
                upgratePlayView();
                break;

            case R.id.ib_backward:
                mPlayerView.setRate(0.5f);
                upgrateRate();
                break;
            case R.id.ib_size:
                break;
            default:
                break;
        }
    }

    private void onMediaPlay(String url) {
        if (null != mPlayerView && !TextUtils.isEmpty(url)) {
            showLoading();
            hideOverlay();
            mPlayerView.initPlayer(mUrl);
            mPlayerView.play();
            hPlayVlcAudioHandler.postDelayed(PlayVlcAudioRunnable, 500);
        }
    }

    private void onMediaPause() {
        if (null != mPlayerView) {
            mPlayerView.pause();
        }
    }

    private void onMediaStop() {
        if (null != mPlayerView) {
            mPlayerView.stop();
        }
    }

    private void upgratePlayView() {
        if (mPlayerView != null && mPlayerView.isPlaying()) {
            ibPlay.setBackgroundResource(R.drawable.ic_play);
        } else {
            ibPlay.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    private void upgrateRate() {
        if (mPlayerView != null) {
            float rate = mPlayerView.getRate();
            if (rate >= 2) {
                rate_text.setText(getString(R.string.play_fast_rate) + " " + (int) rate + "X");
            } else if (rate <= 0.5) {
                rate = 1 / rate;
                rate_text.setText(getString(R.string.play_slow_rate) + " " + (int) rate + "X");
            } else {
                rate_text.setText("1X");
            }
        }
    }

    private void showOverlay() {
        fullButton.setVisibility(View.VISIBLE);
        llOverlay.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        mHandler.removeMessages(HIDE_OVERLAY);
        mHandler.sendEmptyMessageDelayed(HIDE_OVERLAY, 5 * 1000);
    }

    private void hideOverlay() {
        fullButton.setVisibility(View.GONE);
        if (!isLock) {
            llOverlay.setVisibility(View.GONE);
            mHandler.removeMessages(SHOW_PROGRESS);
        }
    }

    private int setOverlayProgress() {
        if (mPlayerView == null) {
            return 0;
        }
        int time = (int) mPlayerView.getTime();
        int length = (int) mPlayerView.getLength();
        boolean isSeekable = mPlayerView.canSeekable() && length > 0;
        ibFarward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
        ibBackward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
        rate_text.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
        sbVideo.setMax(length);
        sbVideo.setProgress(time);

        if (time >= 0) {
            tvTime.setText(millisToString(time, false));
        }
        if (length >= 0) {
            tvLength.setText(millisToString(length, false));
        }
        return time;
    }

    /**
     * 在拖动中--即值在改变
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && mPlayerView.canSeekable()) {
//            mPlayerView.setTime(progress);
//            setOverlayProgress();
        }
    }

    /**
     * 在拖动中会调用此方法
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.removeMessages(HIDE_OVERLAY);
    }

    /**
     * 停止拖动
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mPlayerView.canSeekable()) {
            mPlayerView.setTime(seekBar.getProgress());
            setOverlayProgress();
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 200);
        }
        mHandler.sendEmptyMessageDelayed(HIDE_OVERLAY, 5 * 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS:
                setOverlayProgress();
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 50);
                break;
            case ON_LOADED:
                showOverlay();
                hideLoading();
                break;
            case HIDE_OVERLAY:
                hideOverlay();
                break;
            default:
                break;
        }
        return false;
    }


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


    private Handler hPlayVlcAudioHandler = new Handler();
    private int AudioPlaySample;
    private ReverseAudioInfo audioInfo;

    private Runnable PlayVlcAudioRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("send_usetime", "PlayVlcAudioRunnable");
            AudioPlaySample = mPlayerView.getMediaPlayer().GetAudioSampleRate();

            if (!(mPlayerView.getMediaPlayer().getPlayerState() == Media.State.Playing)
                    || AudioPlaySample == 0) {
                hPlayVlcAudioHandler.postDelayed(this, 100);
                return;
            }

            audioInfo = new ReverseAudioInfo();
            audioInfo.setiSampleRate(AudioPlaySample);

            if (audioSender_Realtime == null) {
                try {
                    Log.d("send_usetime", "audioSender_RealTime");
                    audioSender_Realtime = new AudioSender_RealTime(CautionActivity.this, null, audioInfo, AudioPlaySample);
                    audioSender_Realtime.mMediaPlayer = mPlayerView.getMediaPlayer();
                    audioSender_Realtime.IsMute = false;
                    audioSender_Realtime.startPlayVlcAudio();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("send_usetime", "audioSender_RealTime2");
                audioSender_Realtime.mMediaPlayer = mPlayerView.getMediaPlayer();
                audioSender_Realtime.IsMute = false;
                audioSender_Realtime.frequency_play = AudioPlaySample;
                audioSender_Realtime.startPlayVlcAudio();
            }
        }
    };

    void TurnOffPlayAudio() {
        if (audioSender_Realtime != null) {
            audioSender_Realtime.stopPlayVlcAudio();
        }
    }

    private Handler hHandler = new Handler(this);

    private Runnable playingStateMonitorRunnable = new Runnable() {
        @Override
        public void run() {
            int nowState = mPlayerView.getPlayerState();
            if (nowState == -1)
                nowState = 0;

            hHandler.postDelayed(this, 500);

            Message message = new Message();
            message.what = nowState;
            playingStateMonitorHandler.sendMessage(message);
        }
    };

    private long preStateBeginTime = 0l;
    private int preState;

    private Handler playingStateMonitorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Media.State.Opening:
                    if (preState == Media.State.Opening) {
                        if (System.currentTimeMillis() - preStateBeginTime >= 6000) {
                            preStateBeginTime = System.currentTimeMillis();
                            ToastUtil.show(R.string.app_error_network_error);
                        }
                    } else {
                        preState = Media.State.Opening;
                        preStateBeginTime = System.currentTimeMillis();
                    }
                    break;
                default:
                    preState = -1;
                    break;
            }
        }
    };

    Runnable PlayVideoRunnable = new Runnable() {
        public void run() {
            Log.d("tltest", "PlayVideoRunnable start");
            mPlayerView.initPlayer(mUrl);
            mPlayerView.start();
            return;
        }
    };

    //@Override
    public void onSuccess(String api, Object object) {
        /*if (CameraRequest.GetStreamTokenUrl.equals(api)) {
            mUrl = GetPlayURL((CameraToken) object);

            if (GetTfPlayType(camera) == 0) {
                if (PlayMethod == com.ztesoft.homecare.entity.PlayMethod.LAN) {
                    mPlayerView.initPlayer(mUrl);
                    mPlayerView.start();
                } else {
                    httpRelay.Start(ip, port);
                    HttpRelayTask.start();
                }
            } else {
                mPlayerView.initPlayer(mUrl);
                mPlayerView.start();
            }

        }*/
    }

    //@Override
    public void onError(String api, int errorCode) {

    }

//    private String GetPlayURL(CameraToken cameraToken) {
//        String PlayURL = "";
//
//        try {
//            int reqtype = cameraToken.getReqtype();
//            final String token = cameraToken.getToken();
//            CameraTokenStream streamUrl = cameraToken.getStreamUrl();
//
////            if (GetTfPlayType(camera) == 0) {  //http
////                if (reqtype ==PlayMethod.LAN) {
////                    String urlExample = streamUrl.getLocalurl();
////                    int index = urlExample.indexOf(":", 8);
////                    urlExample = urlExample.substring(4, index);
////                    urlExample = "http" + urlExample + ":52869/web/" + LocalStoreType + "/" + mUrl;
////                    PlayURL = urlExample;
////                } else {
//////                PlayURL = "http://127.0.0.1:56789" +  "/web/tf/" + mUrl;
////                    PlayURL = mUrl;
////                    String reqserver = cameraToken.getReqserver();
////                    int index = reqserver.indexOf(":", 0);
////                    ip = reqserver.substring(0, index);
////                    port = reqserver.substring(index + 1);
////                }
////            } else {//rtsp
//                if (reqtype == PlayMethod.LAN) {
//                    String urlExample = streamUrl.getLocalurl();
//                    int index = urlExample.indexOf("/", 8);
//                    urlExample = urlExample.substring(0, index);
//                    urlExample = urlExample + "/file/" + mUrl;
//                    PlayURL = urlExample + "&token=" + token;
//                } else {
//                    PlayURL = "rtsp://" + cameraToken.getReqserver() + "/file/" + mUrl + "&token=" + token;
//                }
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return PlayURL;
//    }
}
