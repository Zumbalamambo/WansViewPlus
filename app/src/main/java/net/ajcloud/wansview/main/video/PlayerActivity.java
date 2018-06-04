package net.ajcloud.wansview.main.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.camera.Camera;
import net.ajcloud.wansview.entity.camera.CameraToken;
import net.ajcloud.wansview.entity.camera.CameraTokenStream;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.device.type.camera.AudioSender_RealTime;
import net.ajcloud.wansview.main.device.type.camera.PlayMethod;
import net.ajcloud.wansview.main.video.adapter.VideoAdapter;
import net.ajcloud.wansview.support.utils.ToastUtil;
import org.httprelay.HttpRelay;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.ReverseAudioInfo;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PlayerActivity extends BaseActivity implements PlayerView.OnChangeListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, Handler.Callback {

    public static void start(Context context, String url, boolean isHideControl) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("isHideControl", isHideControl);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, int PlayMethod, String oid, Camera camera, boolean isHideControl) {
        Intent intent = new Intent(context, PlayerActivity.class);
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
    private String mUrl;
    private TextView tvBuffer, tvTime, tvLength;
    private SeekBar sbVideo;
    private ImageButton ibLock, ibFarward, ibBackward, ibPlay, ibSize;
    private View llOverlay;
    private Handler mHandler;
    private boolean isFinishing = false;
    private AudioSender_RealTime audioSender_Realtime = null;
    private SharedPreferences mSettings;
    private int mScreenOrientation;
    private int mUiVisibility = -1;
    private Toolbar toolbar;
    private TextView rate_text;
    private boolean isLock = false;
    private boolean isHideControl = false;
    private String ip;
    private String port;
    private int playMethod;
    private String oid = null;
    private String emid = null;
    private Process HttpRelayProcess = null;
    public HttpRelay httpRelay = new HttpRelay();
    public boolean isOff = false;
    private String version;
    private int TFPlayType = 0;    //0:http 1:rtsp
    private Camera camera = null;
    private String LocalStoreType = "tf";
    private Boolean WidthHeight16To9Flag = false;
    private RecyclerView payList;
    private VideoAdapter videoAdapter;



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
    private int GetTfPlayType(Camera camera) {
        if (camera == null) {
            return 0;
        }

        String[] playType = camera.getCapAbility().getFeatures().getHistoryvideo();
        if (playType == null) {
            return 0;
        }

        for (int i = 0; i < playType.length; i++) {
            if (playType[i].equals("http")) {
                return 0;
            }
        }

        for (int i = 0; i < playType.length; i++) {
            if (playType[i].equals("rtsp")) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getIntent().getStringExtra("URL");
        playMethod = getIntent().getIntExtra("PlayMethod", -1);
        oid = getIntent().getStringExtra("oid");
        emid = getIntent().getStringExtra("emid");
        isHideControl = getIntent().getBooleanExtra("isHideControl", false);
        camera = (Camera) getIntent().getSerializableExtra("camera");
        WidthHeight16To9Flag = getIntent().getBooleanExtra("WidthHeight16To9", false);
        if (camera != null) {
            TFPlayType = GetTfPlayType(camera);
            isHideControl = TFPlayType != 0;

            if (camera.getStorageSetting().getType().equals("0")) {
                LocalStoreType = "tf";
            } else {
                LocalStoreType = "nas";
            }

            /*if (Utils.isCameraInLan(oid, camera.getCameraState().getGwmac()) &&
                    !TextUtils.isEmpty(camera.getCameraState().getLocalip())) {
                PlayMethod = com.ztesoft.homecare.entity.PlayMethod.LAN;
                String tmpUrl = "http://" + camera.getCameraState().getLocalip() + ":52869/web/" + LocalStoreType + "/" + mUrl;
                if (!TextUtils.isEmpty(camera.getCameraState().getLocalstoragehost())) {
                    tmpUrl = camera.getCameraState().getLocalstoragehost() + "web/" + LocalStoreType + "/" + mUrl;

                }
                mUrl = tmpUrl;
            } else {
                if (TFPlayType == 0) {  //http
                    PlayMethod = com.ztesoft.homecare.entity.PlayMethod.HTTP_RELAY;
                } else {//rtsp
                    PlayMethod = com.ztesoft.homecare.entity.PlayMethod.TCP_RELAY;
                }
            }*/
        }

        if (TextUtils.isEmpty(mUrl)) {
            Toast.makeText(this, "error:no url in intent!", Toast.LENGTH_SHORT).show();
            return;
        }

        //mUrl = "http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4";
        //mUrl = "http://m3u8.ztehome.com.cn/api/cloud/get-camera-tsinfos?sid=579e160e-b422-4d4f-9fbb-6130cc3ba694.UcItUcYx1kcB8x.12&stream=1459151214-7-32KycGRZ4HcwkKDJqQrTv&apptype=3&localtz=28800&oid=HN1A005G1500015&locale=zh_CN";
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mScreenOrientation = Integer.valueOf(mSettings.getString("screen_orientation_value", "4" /*SCREEN_ORIENTATION_SENSOR*/));
        setRequestedOrientation(mScreenOrientation != 100 ? mScreenOrientation : getScreenOrientation());

        setContentView(R.layout.activity_player);

        mHandler = new Handler(this);

        tvTime =  findViewById(R.id.tv_time);
        tvLength =  findViewById(R.id.tv_length);
        sbVideo =  findViewById(R.id.sb_video);
        sbVideo.setOnSeekBarChangeListener(this);
        ibLock =  findViewById(R.id.ib_lock);
        ibLock.setOnClickListener(this);
        ibBackward =  findViewById(R.id.ib_backward);
        ibBackward.setOnClickListener(this);
        ibPlay =  findViewById(R.id.ib_play);
        ibPlay.setOnClickListener(this);
        ibFarward =  findViewById(R.id.ib_forward);
        ibFarward.setOnClickListener(this);
        ibSize =  findViewById(R.id.ib_size);
        ibSize.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar_actionbar_portrait);
        //toolbar.setBackgroundColor(Utils.resToColor(R.color.tint_color_black));
        rate_text =  findViewById(R.id.rate_text);
        llOverlay = findViewById(R.id.ll_overlay);

        rlLoading = findViewById(R.id.rl_loading);
        tvBuffer = findViewById(R.id.tv_buffer);

        //使用步骤
        //第一步 ：通过findViewById或者new PlayerView()得到mPlayerView对象
        //mPlayerView= new PlayerView(PlayerActivity.this);
        mPlayerView = (PlayerView) findViewById(R.id.pv_video);

        //第三步:初始化播放器
        mPlayerView.initPlayer(mUrl);
        if (WidthHeight16To9Flag) {
            mPlayerView.setSurfaceViewer16To9();
        }

        //第四步:设置事件监听，监听缓冲进度等
        mPlayerView.setOnChangeListener(this);

        hHandler.postDelayed(playingStateMonitorRunnable, 500);

        showLoading();
        hideOverlay();
        //第五步：开始播放
        if (playMethod == -1 || playMethod == PlayMethod.LAN) {
            mPlayerView.start();
        } else {
            try {
                /*HttpAdapterManger.getCameraRequest().getToken(AppApplication.devHostPresenter.getDevHost(oid), PlayMethod, 3, -1,
                        new ZResponse(CameraRequest.GetStreamTokenUrl, PlayerActivity.this));*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        hPlayVlcAudioHandler.postDelayed(PlayVlcAudioRunnable, 500);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
    }

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
        //toolbar.setBackgroundColor(Utils.resToColor(R.color.tint_color_black));
        rate_text = findViewById(R.id.rate_text);
        llOverlay = findViewById(R.id.ll_overlay);

        rlLoading = findViewById(R.id.rl_loading);
        tvBuffer = findViewById(R.id.tv_buffer);

        //使用步骤
        //第一步 ：通过findViewById或者new PlayerView()得到mPlayerView对象
        //mPlayerView= new PlayerView(PlayerActivity.this);
        mPlayerView = findViewById(R.id.pv_video);
        payList = findViewById(R.id.item_recyclerview);

    }

    @Override
    protected void initData() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mScreenOrientation = Integer.valueOf(mSettings.getString("screen_orientation_value", "4" /*SCREEN_ORIENTATION_SENSOR*/));
        setRequestedOrientation(mScreenOrientation != 100 ? mScreenOrientation : getScreenOrientation());

        mPlayerView.setSurfaceViewer16To9();

        //初始化适配
        VideoAdapter.OnClickItemListener listener = new VideoAdapter.OnClickItemListener() {
            @Override
            public void onClick(int position) {

            }
        };

        videoAdapter = new VideoAdapter(this, null, listener);

    }

    @Override
    protected void initListener() {
        mPlayerView.setOnChangeListener(this);
    }

    private int getScreenOrientation() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation();
        /*
         * Since getRotation() returns the screen's "natural" orientation,
         * which is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT,
         * we have to invert the SCREEN_ORIENTATION value if it is "naturally"
         * landscape.
         */
        @SuppressWarnings("deprecation")
        boolean defaultWide = display.getWidth() > display.getHeight();
        if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
            defaultWide = !defaultWide;
        if (defaultWide) {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                case Surface.ROTATION_270:
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                            : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                default:
                    return 0;
            }
        } else {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                            : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                case Surface.ROTATION_270:
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                    // Level 9+
                    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                default:
                    return 0;
            }
        }
    }

    private int getScreenRotation() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /* Android 2.2 has getRotation */) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation");
                return (Integer) m.invoke(display);
            } catch (Exception e) {
                return Surface.ROTATION_0;
            }
        } else {
            return display.getOrientation();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            showControl();
        }
        return false;
    }

    public void showControl() {
        if (!isHideControl) {
            if (llOverlay.getVisibility() != View.VISIBLE) {
                showOverlay();
            } else {
                hideOverlay();
            }
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
        super.onPause();
        super.finish();
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

        /*if (!TextUtils.isEmpty(emid) && !TextUtils.isEmpty(oid)) {
            HttpAdapterManger.getMessageRequest().setEmcRead(AppApplication.devHostPresenter.getDevHost(oid),
                    oid, String.valueOf(ODM.CAMERA), emid, EMAction.INTERACT_VIDEO, EMCate.INTERACT, 1l, System.currentTimeMillis(), new
                            ZResponse(MessageRequest.SetEmcRead, new ResponseListener() {
                        @Override
                        public void onSuccess(String api, Object object) {

                        }

                        @Override
                        public void onError(String api, int errorCode) {

                        }
                    }));
        }*/
    }

    private void notFullscreen() {
        getSupportActionBar().show();
        View v = getWindow().getDecorView();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    private void fullscreen() {
        getSupportActionBar().hide();

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
        isFinishing = true;
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onDestroy() {
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
        finish();
    }

    @Override
    public void onEnd() {
        finish();
    }

    @Override
    public void onNetSlow() {
        ToastUtil.show(R.string.videoplayer_network_open_tips);
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
                //mPlayerView.seek(10000);
                break;
            case R.id.ib_play:
                if (mPlayerView.isPlaying()) {
                    mPlayerView.pause();
                    ibPlay.setBackgroundResource(R.drawable.ic_play);
                } else {
                    mPlayerView.play();
                    ibPlay.setBackgroundResource(R.drawable.ic_pause);
                }
                break;

            case R.id.ib_backward:
                mPlayerView.setRate(0.5f);
                upgrateRate();
                //mPlayerView.seek(-10000);
                break;
            case R.id.ib_size:
                break;
            default:
                break;
        }
    }

    private void upgrateRate() {
        if (mPlayerView != null) {
            float rate = mPlayerView.getRate();
            if (rate >= 4) {

            } else if (rate <= 0.25) {

            }
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
        //toolbar.setVisibility(View.VISIBLE);
        llOverlay.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        mHandler.removeMessages(HIDE_OVERLAY);
        mHandler.sendEmptyMessageDelayed(HIDE_OVERLAY, 5 * 1000);
    }

    private void hideOverlay() {
        //toolbar.setVisibility(View.GONE);
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
                if (!isHideControl) {
                    showOverlay();
                }

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
                    audioSender_Realtime = new AudioSender_RealTime(PlayerActivity.this, null, audioInfo, AudioPlaySample);
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
                            ToastUtil.show(R.string.videoplayer_network_open_tips);
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

    private String GetPlayURL(CameraToken cameraToken) {
        String PlayURL = "";

        try {
            int reqtype = cameraToken.getReqtype();
            final String token = cameraToken.getToken();
            CameraTokenStream streamUrl = cameraToken.getStreamUrl();

            if (GetTfPlayType(camera) == 0) {  //http
                if (reqtype ==PlayMethod.LAN) {
                    String urlExample = streamUrl.getLocalurl();
                    int index = urlExample.indexOf(":", 8);
                    urlExample = urlExample.substring(4, index);
                    urlExample = "http" + urlExample + ":52869/web/" + LocalStoreType + "/" + mUrl;
                    PlayURL = urlExample;
                } else {
//                PlayURL = "http://127.0.0.1:56789" +  "/web/tf/" + mUrl;
                    PlayURL = mUrl;
                    String reqserver = cameraToken.getReqserver();
                    int index = reqserver.indexOf(":", 0);
                    ip = reqserver.substring(0, index);
                    port = reqserver.substring(index + 1);
                }
            } else {//rtsp
                if (reqtype == PlayMethod.LAN) {
                    String urlExample = streamUrl.getLocalurl();
                    int index = urlExample.indexOf("/", 8);
                    urlExample = urlExample.substring(0, index);
                    urlExample = urlExample + "/file/" + mUrl;
                    PlayURL = urlExample + "&token=" + token;
                } else {
                    PlayURL = "rtsp://" + cameraToken.getReqserver() + "/file/" + mUrl + "&token=" + token;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return PlayURL;
    }
}
