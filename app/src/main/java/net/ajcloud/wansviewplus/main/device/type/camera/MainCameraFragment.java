package net.ajcloud.wansviewplus.main.device.type.camera;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.ajcloud.wansviewplus.BuildConfig;
import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.camera.Camera;
import net.ajcloud.wansviewplus.entity.camera.CameraModel;
import net.ajcloud.wansviewplus.entity.camera.CameraState;
import net.ajcloud.wansviewplus.entity.camera.CameraStatus;
import net.ajcloud.wansviewplus.entity.camera.PtzCtrlType;
import net.ajcloud.wansviewplus.entity.camera.VideoEncryptionInfo;
import net.ajcloud.wansviewplus.entity.camera.ViewSetting;
import net.ajcloud.wansviewplus.main.application.BaseFragment;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.setting.DeviceSettingActivity;
import net.ajcloud.wansviewplus.main.device.type.DeviceHomeActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.LiveSrcBean;
import net.ajcloud.wansviewplus.support.customview.MyStateBar;
import net.ajcloud.wansviewplus.support.customview.camera.AngleView;
import net.ajcloud.wansviewplus.support.customview.camera.CloudDirectionLayout;
import net.ajcloud.wansviewplus.support.customview.camera.DragLayout;
import net.ajcloud.wansviewplus.support.customview.camera.NavigationPopupWindow;
import net.ajcloud.wansviewplus.support.customview.camera.PTZView;
import net.ajcloud.wansviewplus.support.customview.camera.ScaleFrameLayout;
import net.ajcloud.wansviewplus.support.customview.camera.VideoPlayArea4To3;
import net.ajcloud.wansviewplus.support.customview.camera.VideoQuality;
import net.ajcloud.wansviewplus.support.utils.AudioUtils;
import net.ajcloud.wansviewplus.support.utils.CameraUtil;
import net.ajcloud.wansviewplus.support.utils.ConnectivityUtil;
import net.ajcloud.wansviewplus.support.utils.FileUtil;
import net.ajcloud.wansviewplus.support.utils.SizeUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.ReverseAudioInfo;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.vlc.util.VLCInstance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created
 */
public class MainCameraFragment extends BaseFragment
        implements IVLCVout.Callback,
        IVLCVout.OnNewVideoLayoutListener,
        PoliceHelper.PoliceControlListener,
        SeekBar.OnSeekBarChangeListener,
        SwipeRefreshLayout.OnRefreshListener,
        ScaleFrameLayout.OnScaleListener {

    private final static String TAG = MainCameraFragment.class.getSimpleName();
    private Handler mHandler = new Handler();
    private Handler hPlayVlcAudioHandler = new Handler();
    private Handler refreshVideoHandle = new Handler();
    private Handler keepAliveHandler = new Handler();
    private Handler hHideControl = new Handler();
    private Handler hShowControl = new Handler();
    private Handler handler = new Handler();
    private Handler hHandler = new Handler();
    private boolean isGetRealTimeImage = false;
    private String rtmpUrl = "";
    private long startTime = 0;
    private long endTime = 0;
    //private TipDialog tipDialog;
    /* 是否需要从ossx更新信息 */
    private boolean isUpdataUserInfo = false;
    private boolean isSleepMode = false;
    private int refreshNum = 0;
    private int online = 0;
    private String SnapShot_URL;
    private String Version_Now;
    private String Version_New;

    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private static int recordSecond = 0;
    private int mCurrentSize;
    private float videoScale = 1;
    private TextView recordTime;
    public TextView tips;
    private ProgressBar progressBar;
    private LinearLayout no_wifi_tips;
    private ImageView recordVoice;
    private FrameLayout voice_content;
    private LinearLayout voice_rcd_hint_anim_area;
    private RelativeLayout voice_rcd_hint_cancel_area;
    private ImageView sound_level;
    private SurfaceView mSurface;
    private FrameLayout mSurfaceFrame;
    private ScaleFrameLayout mPlayerFrame;
    private TextView playerInfo;
    private ImageView voiceSwitch;
    private ImageView fullVoiceSwitch;
    private CloudDirectionLayout cloud_directionview;
    private LinearLayout realTimeImageLayout;
    private ImageView realTimeImageImageView;
    private ImageView realTimeImage_Button;
    private TextView realTimeImage_tip;
    private ProgressBar realTimeImage_ProgressBar;
    private LinearLayout Offline_LinearLayout;
    private ImageView realTimeImagePlay;
    public ImageView full_screen;
    private ImageView fullscreen_play;
    private ImageView fullscreen_recordvideo;
    private ImageView fullscreen_snapshot;
    private ImageView fullscreen_stop;
    private FrameLayout fullscreen_quality_layout;
    private TextView fullscreen_quality;
    //private PullToRefreshScrollView myRefreshScroll;
    private TextView realtime_rate;
    private ImageView snapshot;
    private LinearLayout downBar;
    private RelativeLayout realTimeImagePlayRelativeLayout;
    private TextView realTimeImageNoWifiTip;
    private VideoPlayArea4To3 videoLayout;
    private FrameLayout realTimeImageFrameLayout;
    private LinearLayout dynamicAddLayout;
    private TextView PTZ;
    private TextView angle;
    private TextView dynamic;
    private TextView album;
    private LinearLayout selectViewLayout;
    private LinearLayout deleteAngleLayout;
    private LinearLayout functionLayout;
    private ImageView take_video;
    private ImageView offlineImg;
    private TextView offlineTip;
    private TextView offlineHelp;
    private TextView videoQuality;
    private RelativeLayout cameraLayout;
    private ImageView soundPressEffect;
    private LinearLayout Update_LinearLayout;
    private LinearLayout updateProgressLayout;
    private ProgressBar Update_ProgressBar;
    private TextView upgrade_info;
    private RelativeLayout showRealtimeImageLayout;
    private RelativeLayout showRealtimeContentLayout;
    private ImageView showRealtimeImage;
    private ImageView showRealtimeImageCancel;
    private LinearLayout fullScreenLayout;
    private LinearLayout moveTraceLayout;
    private ImageView moveTraceImg;
    private Toolbar myToolBar;
    private MyStateBar myStateBar;

    public int Orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private int HideRelayTime = 3000;
    private int AudioPlaySample;
    private ReverseAudioInfo audioInfo;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private PoliceHelper policeHelper;
    private VirtualCamera virtualCamera;
    private AudioSender_RealTime audioSender_Realtime = null;
    private boolean isRealTimeSpeechOn = false;
    //private TalkType RealTimeTalk_Type = TalkType.HALF_DUPLEX;
    private int mVideoWidth;
    private int mVideoHeight;
    private Timer recordTimer;
    private boolean showPlayType = false;
    private GestureDetector gestureDetector;
    private long lastClickTime = 0;
    private int clickNum = 0;
    private int playedRequestType;
    private boolean hasStartPlayerStatusMonitor = false;
    private boolean isChangeProperty = false;
    private boolean isScreenOn = true;
    private int preState;
    private long preStateBeginTime = 0l;
    private boolean hasReportPlayData = false;
    private boolean needTryNextPolicy = false;
    private boolean isResumed = false;
    private boolean hasTryAll;
    private String mLocation;
    // size of the video
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum = 1;
    private int mSarDen = 1;
    private boolean isFromRealTimeImageButton = false;
    private PopupWindow pop_quality;
    private boolean weakNet = false;
    private Map<String, Integer> urlRetry = new HashMap<>();
    private int RealTime_picture_wait_times = 3;
    private ControlPoint mCP;
    private boolean netReflashFirst = false;
    private float defaultScale;
    private boolean onlyRefreshData = false;
    //private DevHost camera;
    private boolean isSupportDirectionControl = false;

    //两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 400;
    //上次检测时间
    private long lastUpdateTime;
    private SensorManager sensorMag;
    private Sensor gravitySensor;
    //保存上一次记录
    private float lastX = 0;
    private AnimationDrawable drawable;
    private static final int operateInterval = 700;
    private boolean upgradeFromSetting = false;
    private boolean isSensorEnable = false;
    private int[][] defaultCameraQuality = {{416, 234}, {960, 540}, {1280, 720}, {1920, 1080}};
    private Camera camera;

    private void setSurfaceViewSize(int quality) {
        int width;
        int height;
        if (quality < 1) {
            width = defaultCameraQuality[0][0];
            height = defaultCameraQuality[0][1];
        } else if (quality > 4) {
            width = defaultCameraQuality[3][0];
            height = defaultCameraQuality[3][1];
        } else {
            width = defaultCameraQuality[quality - 1][0];
            height = defaultCameraQuality[quality - 1][1];
        }

        try {
            String resolution[] = getCamera().getCapAbility().getFeatures().getResolution();
            switch (quality) {
                case 4:
                    String FD[] = resolution[0].split("\\*");
                    width = Integer.valueOf(FD[0]);
                    height = Integer.valueOf(FD[1]);
                    break;
                case 3:
                    String SD[] = resolution[1].split("\\*");
                    width = Integer.valueOf(SD[0]);
                    height = Integer.valueOf(SD[1]);
                    break;
                case 2:
                    String LD[] = resolution[2].split("\\*");
                    width = Integer.valueOf(LD[0]);
                    height = Integer.valueOf(LD[1]);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
        }

        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setWindowSize(width, height);
        mSurfaceHolder.setFixedSize(width, height);
        mVideoVisibleWidth = width;
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleHeight = height;
    }

    /**
     * 初始化传感器
     */
    private void initGravitySensor() {
        sensorMag = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorMag.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private SensorEventListener sensorLis = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER
                    || !mMediaPlayer.isPlaying()
                    || Orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                refreshVideoHandle.removeCallbacksAndMessages(null);
                return;
            }
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;
            if (timeInterval < UPTATE_INTERVAL_TIME) {
                return;
            }
            lastUpdateTime = currentUpdateTime;
            float x = event.values[SensorManager.DATA_X];
            float deltaX = x - lastX;
            if (!isSensorEnable && lastX != 0 && Math.abs(deltaX) > 2) {
                isSensorEnable = true;
            }

            lastX = x;
            if (isSensorEnable) {
                refreshVideoHandle.removeCallbacksAndMessages(null);
                if (Math.abs(deltaX) > 0.5f && videoScale == defaultScale && !mPlayerFrame.isDragging()) {
                    translateView(deltaX);
                }
            }
        }
    };

    public static BaseFragment newInstance() {
        BaseFragment fr = new MainCameraFragment();
        return fr;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
        //EventBus.getDefault().register(this);
        initGravitySensor();
        sensorMag.registerListener(sensorLis, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        /*if (getActivity() instanceof HomecareActivity) {
            ((HomecareActivity) getActivity()).setmTintColor(R.color.black);
        }*/

        mMediaPlayer = newMediaPlayer();
        if (!VLCInstance.testCompatibleCPU(getActivity())) {
            Log.d(TAG, "LibVLC initialisation failed");
            return;
        }
        mMediaPlayer.setEventListener(mMediaPlayerListener);
        //VLCInstance.get().setOnHardwareAccelerationError(this);
        registerReceiver();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //ButterKnife.inject(getActivity());
        //tipDialog = new TipDialog(getActivity(), null);
        //cameraType = getActivity().getIntent().getIntExtra("cameraType", 0);
        /*switch (cameraType) {
            case 0:
                camera = AppApplication.devHostPresenter.getDevHost(getCamera().getOid(), CameraHost.class);
                break;
            case 1:
                camera = AppApplication.devHostPresenter.getDevHost(getCamera().getOid(), GroupCameraHost.class);
                break;
            case 2:
                camera = AppApplication.devHostPresenter.getDevHost(getCamera().getOid(), ShareCameraHost.class);
                break;
            default:
                camera = AppApplication.devHostPresenter.getDevHost(getCamera().getOid());
                break;
        }*/
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        upgradeFromSetting = false;
        view = inflater.inflate(R.layout.fragment_ws_main_camera, container, false);
        initVirtualCameraAndPoliceHelper();
        initView();
        initSurfaceView();
        ShowUpdate(UPDATE_STATUS.EXIT);
        return view;
    }*/

    @Override
    protected int layoutResID() {
        return net.ajcloud.wansviewplus.R.layout.fragment_ws_main_camera;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initTittle() {

    }

    @Override
    protected void initView(View rootView) {
        upgradeFromSetting = false;
        initVirtualCameraAndPoliceHelper();
        initCameraView(rootView);
        refreshMenuSetting(net.ajcloud.wansviewplus.R.menu.menu_camer_setting);
        initSurfaceView();
        ShowUpdate(UPDATE_STATUS.EXIT);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        isFromRealTimeImageButton = false;
        refreshNum = 0;
        //AppApplication.ChosedCamera = getCamera();
        refreshStatus();
        refreshView();

        /*
        videoLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                videoLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isShareCamera) {
                                lockLatestViewNavigation(realTimeImage_Button);
                            } else {
                                cameraShareNavigation(getView().findViewById(R.id.carmera_share));
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 200);
            }
        });*/

        /*初始化视频加密相关信息*/
        VideoEncryptionInfo.getVideoEncryptionInfo().setEncryptenabled(getCamera().getCameraState().isEncryptenabled());
        int[] encryptmode = getCamera().getCapAbility().getFeatures().getEncryptmode();
        VideoEncryptionInfo.getVideoEncryptionInfo().setEncryptmode(encryptmode);

        refreshDynamicView();
    }

    private void refreshMenuSetting(int menu) {
        myToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == net.ajcloud.wansviewplus.R.id.camer_setting) {
                    DeviceSettingActivity.start(getActivity(), ((DeviceHomeActivity) getActivity()).getOid());
                }
                return true;
            }
        });

        myToolBar.inflateMenu(menu);
        myToolBar.setNavigationIcon(R.mipmap.ic_back);
        myToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getActivity().finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void registerReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mConnectionReceiver, mFilter);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        getActivity().registerReceiver(mScreenReceiver, filter);
    }

    private Runnable setRefreshScrollRunnable = new Runnable() {
        @Override
        public void run() {
            // myRefreshScroll.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    };

    public void refreshView() {
        online = getCamera().getCameraState().getStatus();
        full_screen.setVisibility(View.VISIBLE);

        if (CameraStatus.OFFLINE == online) {
            File file = new File(MainApplication.fileIO.getRealTimePictureDirectory(virtualCamera.cid) + "/realtime_picture.jpg");
            //本地有图片则不去服务器获�?
            if (file.exists()) {
                Bitmap bitmap = CameraUtil.getLoacalBitmap(file.getPath(), false);
                realTimeImageImageView.setVisibility(View.VISIBLE);
                realTimeImageImageView.setImageBitmap(bitmap);
            } else {
                realTimeImageImageView.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_device_default);
            }
            realTimeImageLayout.setVisibility(View.VISIBLE);
            realTimeImage_ProgressBar.setVisibility(View.GONE);
            realTimeImage_tip.setVisibility(View.GONE);
            realTimeImagePlayRelativeLayout.setVisibility(View.GONE);

            realTimeImageFrameLayout.setVisibility(View.VISIBLE);
            realTimeImagePlay.setVisibility(View.GONE);
            Offline_LinearLayout.setVisibility(View.VISIBLE);
            offlineImg.setImageResource(net.ajcloud.wansviewplus.R.mipmap.camera_offline);
            offlineImg.setOnClickListener(null);
            offlineTip.setText(getString(net.ajcloud.wansviewplus.R.string.wv_device_not_online));
            offlineHelp.setText(getString(net.ajcloud.wansviewplus.R.string.wv_go_help_center));
            videoQuality.setVisibility(View.GONE);
            realTimeImageNoWifiTip.setVisibility(View.GONE);
        } else if (getCamera().getCameraState().isAnylock() || isSleepMode) {
            realTimeImageLayout.setVisibility(View.VISIBLE);
            realTimeImageFrameLayout.setVisibility(View.VISIBLE);
            realTimeImagePlay.setVisibility(View.GONE);
            full_screen.setVisibility(View.GONE);
            Offline_LinearLayout.setVisibility(View.VISIBLE);
//            videoHide.setVisibility(View.VISIBLE);
            realTimeImageImageView.setVisibility(View.INVISIBLE);
            offlineImg.setImageResource(net.ajcloud.wansviewplus.R.mipmap.home_sleep);
            offlineImg.setOnClickListener(null);
            offlineTip.setText(getString(net.ajcloud.wansviewplus.R.string.wv_device_sleep));
            offlineHelp.setText(getString(net.ajcloud.wansviewplus.R.string.wv_right_on));
            videoQuality.setVisibility(View.GONE);
            realTimeImageNoWifiTip.setVisibility(View.GONE);
            ShowUpdate(UPDATE_STATUS.EXIT);
        } else if (weakNet) {//网络质量差
            realTimeImageLayout.setVisibility(View.VISIBLE);
            realTimeImageFrameLayout.setVisibility(View.VISIBLE);
            realTimeImagePlay.setVisibility(View.GONE);
            realTimeImagePlayRelativeLayout.setVisibility(View.GONE);
            Offline_LinearLayout.setVisibility(View.VISIBLE);
            realTimeImageImageView.setVisibility(View.INVISIBLE);
            no_wifi_tips.setVisibility(View.GONE);
            offlineImg.setImageResource(net.ajcloud.wansviewplus.R.mipmap.play_white);
            offlineImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoQuality.setVisibility(Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? View.VISIBLE : View.GONE);
                    videoPlay();
                }
            });
            offlineTip.setText(getString(net.ajcloud.wansviewplus.R.string.wv_video_net_week));
            offlineHelp.setText(getString(net.ajcloud.wansviewplus.R.string.wv_goto_net_check));
            realTimeImageNoWifiTip.setVisibility(View.GONE);
            videoQuality.setVisibility(View.GONE);
            ShowUpdate(UPDATE_STATUS.EXIT);
        } else if (CameraStatus.FW_UPGRADE == online) {
            upgradeFromSetting = false;
            String info = "";//= MyPreferenceManager.getInstance().getUpdateInfo(getCamera().getOid());
            if (!TextUtils.isEmpty(info)) {
                String[] UpdateInfo = info.split("=");
                long now = System.currentTimeMillis();
                if (now - Long.parseLong(UpdateInfo[3]) < UpdateMaxWaitTime) {
                    ShowUpdate(UPDATE_STATUS.PROCESSING);
                } else {
                    ShowUpdate(UPDATE_STATUS.TIMEOUT);
                }
            } else {
                Version_Now = getCamera().getCameraState().getFwversion();
                Version_New = getCamera().getCameraState().getFwrlsver();
                long time = System.currentTimeMillis();
                String UpdateInfo = "show" + "=" + Version_Now + "=" + Version_New + "=" + String.valueOf(time);
                //MyPreferenceManager.getInstance().setUpdateInfo(getCamera().getOid(), UpdateInfo);
                ShowUpdate(UPDATE_STATUS.PROCESSING);
            }
            Offline_LinearLayout.setVisibility(View.GONE);
            realTimeImageLayout.setVisibility(View.GONE);
        } else if (CameraStatus.ACTIVED == online) {
            String info = "";//MyPreferenceManager.getInstance().getUpdateInfo(getCamera().getOid());
            if (!upgradeFromSetting && !TextUtils.isEmpty(info)) {
                String[] UpdateInfo = info.split("=");
                //当前版本号与之前最新的版本号一致，表明升级成功
                //MyPreferenceManager.getInstance().removeUpdateInfo(getCamera().getOid());
                if (!UpdateInfo[1].equals(getCamera().getCameraState().getFwversion())) {
                    ShowUpdate(UPDATE_STATUS.SUCCESS);
                } else {
                    ShowUpdate(UPDATE_STATUS.FAIL);
                }
            } else {
                ShowUpdate(UPDATE_STATUS.EXIT);
            }

            realTimeImage_tip.setVisibility(View.GONE);
            File file = new File(MainApplication.fileIO.getRealTimePictureDirectory(virtualCamera.cid) + "/realtime_picture.jpg");
            //本地有图片则不去服务器获�?
            if (file.exists()) {
                Bitmap bitmap = CameraUtil.getLoacalBitmap(file.getPath(), false);
                realTimeImageImageView.setVisibility(View.VISIBLE);
                realTimeImageImageView.setImageBitmap(bitmap);
            } else {
                realTimeImageImageView.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_device_default);
            }
            realTimeImageLayout.setVisibility(View.VISIBLE);
            realTimeImage_ProgressBar.setVisibility(View.GONE);
            realTimeImage_tip.setVisibility(View.GONE);
            realTimeImagePlayRelativeLayout.setVisibility(View.VISIBLE);
            realTimeImagePlay.setVisibility(View.VISIBLE);
            no_wifi_tips.setVisibility(View.GONE);
            videoQuality.setVisibility(View.VISIBLE);
            Offline_LinearLayout.setVisibility(View.GONE);
            if (virtualCamera.isMute) {
                voiceSwitch.setImageResource(R.drawable.volume_off_state);
                fullVoiceSwitch.setImageResource(R.drawable.volume_off_state);
            } else {
                voiceSwitch.setImageResource(net.ajcloud.wansviewplus.R.drawable.volume_on_state);
                fullVoiceSwitch.setImageResource(R.drawable.volume_on_state);
            }
        }

        initRealTimeTalk_Type();
        getActivity().unregisterReceiver(mConnectionReceiver);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mConnectionReceiver, mFilter);
    }

    public void refreshListAndStatus(boolean reflashDevInfo) {
        try {
            //网络差状态清空
            weakNet = false;
            Camera ca = new Camera();//= (Camera) getUserData();
            if (ca != null && ca.getCameraState() != null) {
                /* 暂时下拉刷新不调用ossx的url获取, 目前方案url不会迁移,如手动迁移则app重新登录即可
                 * 代码暂时不删除,防止后面流程在做调整 */
                if (reflashDevInfo || isUpdataUserInfo) {
                    isUpdataUserInfo = true;
                    /*HttpAdapterManger.getOssxRequest().getOssxDevinfo(ca.getOid(), ca.getCameraState().getOdm() + "",
                            new ZResponse(OssxRequest.GetOSSXDevInfo, ZResponse.UNTREATED, this));*/
                } else {
                    refreshStatus();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //myRefreshScroll.onRefreshComplete();
        }
    }

    public void refreshStatus() {
        /*
        try {
            HttpAdapterManger.getCameraRequest().getCamerHostInfo(camera,
                    new ZResponse(CameraRequest.GetCamerHostInfo, this));
        } catch (Exception ex) {
            ex.printStackTrace();
            myRefreshScroll.onRefreshComplete();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
        resetNetworkReflash(true);
        GetImageForReview();
    }

    @Override
    public void onStop() {
        stopVideoPlay(false);
        TurnOffPlayAudio();
        forceOffAudio();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        TurnOffPlayAudio();
        forceOffAudio();
        sensorMag.unregisterListener(sensorLis);
        //EventBus.getDefault().unregister(this);
        mMediaPlayer.setEventListener(null);
        mMediaPlayer.release();
        AudioUtils.setStreamMute(getActivity(), false);
        getActivity().unregisterReceiver(mConnectionReceiver);
        getActivity().unregisterReceiver(mScreenReceiver);
        super.onDestroy();
    }

    /*
    @Override
    public void onSuccess(String api, Object object) {
        if (OssxRequest.GetOSSXDevInfo.equals(api)) {
            try {
                JSONObject result = ((JSONObject) object).getJSONObject("result");
                Gson gson = new Gson();
                Type listType = new TypeToken<DevHost>() {
                }.getType();
                DevHost dh = (DevHost) gson.fromJson(result.toString(), listType);
                if (camera != null) {
                    camera.copy(dh);
                }
                isUpdataUserInfo = false;
                refreshStatus();
            } catch (Exception ex) {
                ex.printStackTrace();
                myRefreshScroll.onRefreshComplete();
            }
        } else if (CameraRequest.GetCamerHostInfo.equals(api)) {
            try {
                CameraOssx co = (CameraOssx) object;
                if (co.getState().getLocaltz() != -1) {
                    timezoneOffset.put(co.getState().getOid(), String.valueOf(co.getState().getLocaltz()));
                }
                co.getConfig().setCameraState(co.getState());
                //co.getConfig().setCapability(co.getHcm());
                //co.getConfig().setInventory(co.getInventory());
//                DevHost cdh = AppApplication.devHostPresenter.getDevHost(co.getConfig().getOid());
                co.getConfig().setName(camera.getName());
                camera.updata(co.getConfig(), ResideMenuState.RESIDE_MENU_STATE_OK);
                setCamera(co.getConfig());
                EventBus.getDefault().post(new ReflashCameraMessageOK(true));
                if (!onlyRefreshData) {
                    if (getCamera().getCameraState().getStatus() == CameraStatus.FW_UPGRADE) {

                        mHandler.removeCallbacks(FreshUpdateResult_Runnable);
                        if (isResumed) {
                            mHandler.postDelayed(FreshUpdateResult_Runnable, 5 * 1000);
                        }
                    } else if (getCamera().getCameraState().getStatus() == CameraStatus.OFFLINE) {
                        //配置网络后，自动刷新主界面
                        if (AppApplication.RefreshByNetworkConfig) {
                            onTimeGetCameraState();
                        }
                    } else if (getCamera().getCameraState().getStatus() == CameraStatus.ACTIVED) {
                        resetNetworkReflash(true);
                    }

                    RefreshDataToView();
                    AppApplication.isSendUpdateInfo = false;
                }
                onlyRefreshData = false;
                if (getActivity().getIntent().getBooleanExtra("play", false)) {
                    getActivity().getIntent().removeExtra("play");
                    if (getCamera().getCameraState().getStatus() == CameraStatus.ACTIVED) {
                        videoPlay();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                myRefreshScroll.onRefreshComplete();
            } finally {
                myRefreshScroll.onRefreshComplete();
            }
        } else if (CameraRequest.GetCameraSnap.equals(api)) {
            try {
                String imageUrl = ((CameraSnap) object).getUrl();
                urlRetry.put(imageUrl, 1);
                SnapShot_URL = imageUrl;
                onNoImageLoaded(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (CameraRequest.GetVideoSquareInfo.equals(api)) {
            try {
                CameraVideoSquare cameraVideoSquare = (CameraVideoSquare) object;
                if (cameraVideoSquare.getPubcam().getEnabled() == 1) {
                    rtmpUrl = cameraVideoSquare.getPubcam().getRtmpliveurl();
                } else {
                    rtmpUrl = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (CameraRequest.SetAnyLock.equals(api)) {
            tipDialog.dismiss();
            refreshStatus();
        } else if (MessageRequest.ListEMCDetail.equals(api)) {
            List<EventMessage> temp = ((EventListMessages) object).getMessages();
            if (temp == null || temp.isEmpty()) {
                ToastUtil.makeText(R.string.zhc_no_hi);
            } else {
                Intent intent = new Intent(getActivity(), ListHelloVideoActivity.class);
                intent.putExtra("oid", getCamera().getOid());
                AppApplication.ChosedCamera = getCamera();
                getActivity().startActivity(intent);
            }
        } else if (OssxRequest.DeleteShareCamera.equals(api)) {
            EventBus.getDefault().post(new RefreshDeviceListMessage(true, false, false, getCamera().getOid()));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        AppApplication.finishToActivity(MainActivity.class);
                        tipDialog.dismiss();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 1500);
        } else if (OssxRequest.OSSXKeepAlive.equals(api)) {
        }
    }
    */
    //@Override
    public void onError(String api, int errorCode) {
        /*if (CameraRequest.GetCameraSnap.equals(api)) {
            isGetRealTimeImage = false;
            realTimeImagePlayRelativeLayout.setVisibility(View.VISIBLE);
            realTimeImageFrameLayout.setVisibility(View.VISIBLE);
            realTimeImagePlay.setVisibility(View.VISIBLE);
            no_wifi_tips.setVisibility(View.GONE);
            realTimeImage_ProgressBar.setVisibility(View.GONE);
            realTimeImage_tip.setVisibility(View.GONE);
            realTimeImageImageView.setVisibility(View.GONE);
            Offline_LinearLayout.setVisibility(View.GONE);
        } else if (CameraRequest.GetVideoSquareInfo.equals(api)) {
            rtmpUrl = "";
        } else if (CameraRequest.SetAnyLock.equals(api)) {
            tipDialog.dismiss();
        }

        if (OssxRequest.GetOSSXDevInfo.equals(api) || CameraRequest.GetCamerHostInfo.equals(api)) {
            if (AppApplication.RefreshByNetworkConfig) {
                onTimeGetCameraState();
            } else {
                myRefreshScroll.onRefreshComplete();
            }

            if (CameraRequest.GetCamerHostInfo.equals(api)) {
                onlyRefreshData = false;
                EventBus.getDefault().post(new ReflashCameraMessageOK(false));
            }
        }
        if (OssxRequest.OSSXKeepAlive.equals(api)) {
            if (errorCode == 2004) {
                StopVideoByCancelShare(getCamera().getOid());
            }
        }*/
    }

    public Runnable FreshUpdateResult_Runnable = new Runnable() {
        @Override
        public void run() {
            /*if (TextUtils.isEmpty(AppApplication.access_token)) {
                mHandler.removeCallbacks(this);
                return;
            }*/
            refreshStatus();
        }
    };

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mSarNum = sarNum;
        mSarDen = sarDen;
        changeSurfaceSize();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    /***
     * 告警等信息来的时候
     */
    /*public void onEvent(CautionMessage cautionMessage) {
        if (!TextUtils.isEmpty(cautionMessage.getOid())) {
            if (getCamera().getOid().equals(cautionMessage.getOid())) {
                refreshListAndStatus(false);
            } else {
                EventBus.getDefault().post(new RefreshDeviceMessage(cautionMessage.getOid()));
            }
        }
    }*/

    /***
     * 配置界面修改后只刷新当前设备
     */
    /*public void onEvent(RefreshDevMessage refreshDevMessage) {
        refreshListAndStatus(refreshDevMessage.isReflashDevInfo());
    }

    public void onEvent(UpdateRomMessage cautionMessage) {
        upgradeFromSetting = true;
        Version_Now = getCamera().getCameraState().getFwversion();
        Version_New = getCamera().getCameraState().getFwrlsver();
        long time = System.currentTimeMillis();
        String UpdateInfo = "show" + "=" + Version_Now + "=" + Version_New + "=" + String.valueOf(time);

        MyPreferenceManager.getInstance().setUpdateInfo(getCamera().getOid(), UpdateInfo);
        refreshListAndStatus(false);
    }*/

    private Runnable keepAliveRunnalbe = new Runnable() {
        @Override
        public void run() {
            //HttpAdapterManger.getOssxRequest().OssxKeepAlive(getCamera().getOid(), new ZResponse(OssxRequest.OSSXKeepAlive, MainCameraFragment.this));
            keepAliveHandler.postDelayed(this, 240 * 1000);
        }
    };

    /*
    private AudioSender.AudioStateListener audioStateListener = new AudioSender.AudioStateListener() {
        @Override
        public void onFailCreateAudioRecord() {

            ToastUtil.show(getString(R.string.zhc_micorophone_init_failed), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailSend() {
            try {
                ToastUtil.show(R.string.videoplayer_audio_send_failure, Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onSucSend() {
            try {
                ToastUtil.show(R.string.videoplayer_audio_send_suc, Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onTimeOverflow(int timeLeft) {
            try {
                if (timeLeft <= 0) {
                    ToastUtil.makeText(R.string.videoplayer_audio_interupt, Toast.LENGTH_SHORT).show();
                    recordVoice.setImageResource(R.drawable.microphone_state);
                    voice_content.setVisibility(View.GONE);
                    if (audioSender != null && !audioSender.sendComplete) {
                        audioSender.stopRecord();
                    }
                } else {
                    ToastUtil.makeText(getString(R.string.videoplayer_audio_time_left, timeLeft), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onSoundLevel(final int level) {
            try {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "sound level:" + level);
                if (level >= 0 && level <= 6)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sound_level.setImageLevel(level);
                        }
                    });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };*/

    private Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            recordSecond++;
            int minute = recordSecond / 60;
            int second = recordSecond % 60;

            String timeString = "";
            if (minute < 10)
                timeString += "0" + minute;
            else
                timeString += minute;
            timeString += ":";
            if (second < 10)
                timeString += "0" + second;
            else
                timeString += second;
            recordTime.setText(timeString);
        }
    };

    private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (ConnectivityUtil.isConnected(getActivity())) {
                    if (ConnectivityUtil.isConnectedMobile(getActivity())) {
                        if (true/*MyPreferenceManager.getInstance().getNoWifiNotify()*/) {
                            if (getCamera().getCameraState().getStatus() == CameraStatus.ACTIVED) {

                                if (!weakNet || Offline_LinearLayout.getVisibility() != View.VISIBLE) {
                                    realTimeImageNoWifiTip.setVisibility(View.VISIBLE);
                                    no_wifi_tips.setVisibility(View.GONE);
                                }
                            }
                            return;
                        } else {
                            no_wifi_tips.setVisibility(View.GONE);
                        }
                    } else {
                        realTimeImageNoWifiTip.setVisibility(View.GONE);
                    }
                } else {
                    realTimeImageNoWifiTip.setVisibility(View.GONE);
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_error_network);
                    stopVideoPlay();
                }
            }
        }
    };

    private Runnable PlayVlcAudioRunnable = new Runnable() {
        @Override
        public void run() {
            /*用于语音发送的相关参数，不支持双向语音不用获取*/
            audioInfo = mMediaPlayer.ReverseAudioGetDesc();
            AudioPlaySample = mMediaPlayer.GetAudioSampleRate();
            int isDuplexvoice = getCamera().getCapAbility().getFeatures().getDuplexvoice();

            if (!(mMediaPlayer.getPlayerState() == Media.State.Playing)
                    || (audioInfo == null && isDuplexvoice == 1)
                    || AudioPlaySample == 0) {
                hPlayVlcAudioHandler.postDelayed(this, 100);
                return;
            }

            audioSender_Realtime = new AudioSender_RealTime(getActivity(), virtualCamera.cid, audioInfo, AudioPlaySample);
            audioSender_Realtime.mMediaPlayer = mMediaPlayer;
            audioSender_Realtime.IsMute = virtualCamera.isMute;
            audioSender_Realtime.startPlayVlcAudio();

            /*
            if (Utils.isSupportDuplexVoice(getCamera().getCapAbility())) {
                if (RealTimeTalk_Type == TalkType.DUPLEX) {
                    try {
                        audioSender_Realtime = new AudioSender_RealTime(getActivity(), virtualCamera.cid, audioInfo, AudioPlaySample);
                        audioSender_Realtime.mMediaPlayer = mMediaPlayer;
                        audioSender_Realtime.IsMute = virtualCamera.isMute;
                        audioSender_Realtime.startPlayVlcAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (RealTimeTalk_Type == TalkType.HALF_DUPLEX) {
                    try {
                        audioSender_Realtime_halfduplex = new AudioSender_RealTime_halfduplex(getActivity(), virtualCamera.cid, audioInfo, AudioPlaySample);
                        audioSender_Realtime_halfduplex.mMediaPlayer = mMediaPlayer;
                        audioSender_Realtime_halfduplex.IsMute = virtualCamera.isMute;
                        audioSender_Realtime_halfduplex.startPlayVlcAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (audioSender == null) {
                    Log.d("send_usetime", "audioSender startPlayVlcAudio");
                    try {
                        audioSender = new AudioSender(getActivity(), virtualCamera.cid, audioStateListener, 2, AudioPlaySample);
                        audioSender.mMediaPlayer = mMediaPlayer;
                        audioSender.IsMute = virtualCamera.isMute;
                        audioSender.startPlayVlcAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("send_usetime", "audioSender startPlayVlcAudio2");
                    audioSender.mMediaPlayer = mMediaPlayer;
                    audioSender.IsMute = virtualCamera.isMute;
                    audioSender.frequency = AudioPlaySample;
                    audioSender.startPlayVlcAudio();
                }
            } */
        }
    };

    void TurnOffPlayAudio() {
        if (audioSender_Realtime != null) {
            audioSender_Realtime.stopPlayVlcAudio();
        }

        /*
        if (audioSender_Realtime_halfduplex != null) {
            audioSender_Realtime_halfduplex.stopPlayVlcAudio();
        }

        if (audioSender != null) {
            audioSender.stopPlayVlcAudio();
        }*/
    }

    private View.OnTouchListener recordVoiceTouchListener_RealTime = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recordVoice.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_microphone_dark);
                    if (cannotOperateWhenOfflineOrNoplay()) {
                        hHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recordVoice.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_microphone_light);
                            }
                        }, 200);
                        return true;
                    }
                    if (isRealTimeSpeechOn) {
                        recordVoice.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_microphone_color);
                        isRealTimeSpeechOn = false;
                        if (audioSender_Realtime != null) {
                            audioSender_Realtime.stopRecord();
                            stopAnimation();
                        }
                    } else {
                        mMediaPlayer.SetRealTimeTalkFlag(0);
                        recordVoice.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_microphone_white);
                        isRealTimeSpeechOn = true;
                        if (audioSender_Realtime != null) {
                            try {
                                mMediaPlayer.CldAlgInit(AudioPlaySample);
                                audioSender_Realtime.startRecord();
                                startAnimation();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
            }

            return true;
        }
    };

/*
    private View.OnTouchListener recordVoiceTouchListener_RealTime_halfduplex = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recordVoice.setImageResource(R.drawable.control_microphone_click);
                    if (cannotOperateWhenShared() || cannotOperateWhenOfflineOrNoplay()) {
                        hHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recordVoice.setImageResource(R.drawable.control_microphone);
                            }
                        }, 200);
                        return true;
                    }
                    mMediaPlayer.SetRealTimeTalkFlag(0);
                    recordVoice.setImageResource(R.drawable.control_microphone_click);
                    isRealTimeSpeechOn = true;

                    break;

                case MotionEvent.ACTION_UP:
                    if (isRealTimeSpeechOn) {
                        recordVoice.setImageResource(R.drawable.control_microphone);
                        isRealTimeSpeechOn = false;
                    }

                    break;
            }

            return true;
        }
    };


    private View.OnTouchListener recordVoiceTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recordVoice.setImageResource(R.drawable.control_microphone_click);
                    if (cannotOperateWhenShared() || cannotOperateWhenOfflineOrNoplay()) {
                        hHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recordVoice.setImageResource(R.drawable.control_microphone);
                            }
                        }, 200);
                        return true;
                    }
                    Log.d("AudioSender", "audioSender.ACTION_DOWN");
                    if (audioSender != null && !audioSender.IsAudioRecordTaskRun()) {
                        try {
                            Log.d("AudioSender", "audioSender.startRecord();");
                            voice_content.setVisibility(View.VISIBLE);
                            recordVoice.setImageResource(R.drawable.control_microphone_click);
                            audioSender.startRecord();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.makeText(R.string.audio_sending, Toast.LENGTH_SHORT).show();
                        Log.d("AudioSender", "audioSender.stopRecord();");
                        audioSender.stopRecord();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("AudioSender", "audioSender.ACTION_MOVE");
                    if (event.getY() < 0) {
                        voice_rcd_hint_cancel_area.setVisibility(View.VISIBLE);
                        voice_rcd_hint_anim_area.setVisibility(View.GONE);
                    } else {
                        voice_rcd_hint_cancel_area.setVisibility(View.GONE);
                        voice_rcd_hint_anim_area.setVisibility(View.VISIBLE);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("AudioSender", "audioSender.ACTION_UP");
                    recordVoice.setImageResource(R.drawable.control_microphone);
                    voice_content.setVisibility(View.GONE);
                    voice_rcd_hint_cancel_area.setVisibility(View.GONE);
                    voice_rcd_hint_anim_area.setVisibility(View.GONE);
                    if (event.getY() < 0) {
                        if (audioSender != null && !audioSender.sendComplete) {
                            Log.d("AudioSender", "audioSender.cancelRecord();");
                            audioSender.cancelRecord();
                        }
                    } else {
                        if (audioSender != null) {
                            Log.d("AudioSender", "audioSender.stopRecord()2;");
                            audioSender.stopRecord();
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.d("AudioSender", "audioSender.ACTION_CANCEL");
                    recordVoice.setImageResource(R.drawable.control_microphone);
                    voice_content.setVisibility(View.GONE);
                    voice_rcd_hint_cancel_area.setVisibility(View.GONE);
                    voice_rcd_hint_anim_area.setVisibility(View.GONE);
                    if (audioSender != null && !audioSender.sendComplete) {
                        Log.d("AudioSender", "audioSender.stopRecord()3;");
                        audioSender.stopRecord();
                    }
                    break;
            }
            return true;
        }
    };
    */

    private void startAnimation() {
        if (drawable == null) {
            drawable = (AnimationDrawable) soundPressEffect.getDrawable();
            drawable.setOneShot(false);
        }
        soundPressEffect.setVisibility(View.VISIBLE);
        drawable.start();
    }

    private void stopAnimation() {
        soundPressEffect.setVisibility(View.GONE);
        if (drawable != null) {
            drawable.stop();
        }
    }

    /**
     * attach and disattach surface to the lib
     */
    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                mMediaPlayer.getVLCVout().detachViews();
            }
        }
    };

    void controlDirection(Boolean bShow) {
        if (bShow != null) {
            if (bShow) {
                cloud_directionview.setVisibility(View.VISIBLE);
                adjustControlposition();
            } else {
                cloud_directionview.setVisibility(View.GONE);
            }
        } else {
            if (cloud_directionview.getVisibility() == View.VISIBLE) {
                cloud_directionview.setVisibility(View.GONE);
            } else {
                cloud_directionview.setVisibility(View.VISIBLE);
                adjustControlposition();
            }
        }
    }

    private void adjustControlposition() {
        int sw = mSurfaceFrame.getWidth();
        if (ControlPoint.CP_RIGHT == mCP) {
            cloud_directionview.setX(sw - Utils.dp2px(getActivity(), 20 + 150));
        } else {
            cloud_directionview.setX(Utils.dp2px(getActivity(), 20));
        }
        mCP = ControlPoint.CP_LEFT;
    }

    private View.OnTouchListener onSurfaceTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int pointerCount = event.getPointerCount();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (pointerCount == 2) {
                        long currentTime = System.currentTimeMillis();
                        if (clickNum == 0) {
                            clickNum++;
                        } else {
                            if (currentTime - lastClickTime > 500) {
                                clickNum = 1;
                            } else {
                                if (clickNum == 4) {
                                    showPlayType = true;
                                } else if (clickNum < 4) {
                                    clickNum++;
                                } else {
                                }
                            }
                        }
                        lastClickTime = currentTime;
                    } else {
                        clickNum = 0;
                    }
                    break;

                case MotionEvent.ACTION_UP: {
//                    点击抬手时触发显示或隐藏方向控件
                    hShowControl.removeCallbacks(ShowControlRunnalbe);
                    hHideControl.removeCallbacks(HideControlRunnalbe);

                    if (event.getRawX() > mSurfaceFrame.getWidth() / 2) {
                        //mCP = ControlPoint.CP_RIGHT;
                        //设计图上都显示的是在左边
                        mCP = ControlPoint.CP_LEFT;
                    } else {
                        mCP = ControlPoint.CP_LEFT;
                    }
//                    显示或隐藏方向控件
                    hShowControl.postDelayed(ShowControlRunnalbe, 500);
//                    启动定时器，定时取消方向控件的显示
                    hHideControl.postDelayed(HideControlRunnalbe, HideRelayTime);
                    break;
                }
                default:
                    break;
            }
            gestureDetector.onTouchEvent(event);
            return true;
        }
    };

    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
                Log.d(TAG, "ACTION_SCREEN_OFF");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOn = true;
                Log.d(TAG, "ACTION_SCREEN_ON");
            }
        }
    };

    private Handler playingStateMonitorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSurface.setVisibility(View.VISIBLE);
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case Media.State.NothingSpecial:
                    Log.d(TAG, "Media.State.NothingSpecial");
                    showProgressAndTips(getString(net.ajcloud.wansviewplus.R.string.wv_trying_hard_to_play_and_wait));
                    policeHelper.getUrlAndPlay();
                    preState = Media.State.NothingSpecial;
                    break;
                case Media.State.Opening:
                    Log.d(TAG, "Media.State.Opening");
                    tips.setText(getString(net.ajcloud.wansviewplus.R.string.wv_opening_video_and_wait));
                    if (preState == Media.State.Opening) {
                        if (System.currentTimeMillis() - preStateBeginTime >= 6000) {
                            preStateBeginTime = System.currentTimeMillis();
                            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_videoplayer_network_open_tips);
                        }
                    } else {
                        preState = Media.State.Opening;
                        preStateBeginTime = System.currentTimeMillis();
                    }
                    break;
                case Media.State.Buffering:
                    Log.d(TAG, "Media.State.Buffering");
                    tips.setText(net.ajcloud.wansviewplus.R.string.wv_playing_video_and_wait);
                    if (preState == Media.State.Buffering) {
                        if (System.currentTimeMillis() - preStateBeginTime >= 4000) {
                            policeHelper.getUrlAndPlay();
                            preStateBeginTime = System.currentTimeMillis();
                            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_videoplayer_network_buffer_tips);
                        }
                    } else {
                        preState = Media.State.Buffering;
                        preStateBeginTime = System.currentTimeMillis();
                    }
                    break;
                case Media.State.Playing:
                    Log.v(TAG, "Media.State.Playing");
                    hideProgressAndTips();
                    if (!hasReportPlayData) {
                        keepAliveHandler.postDelayed(keepAliveRunnalbe, 5 * 1000);
                        hasReportPlayData = true;
                    }

                    if (preState == Media.State.Playing) {
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "needTryNextPolicy:" + needTryNextPolicy);
                        }
                        if (System.currentTimeMillis() - preStateBeginTime >= 8000 && !needTryNextPolicy) {
                            needTryNextPolicy = true;
                            preStateBeginTime = System.currentTimeMillis();
                        }
                    } else {
                        preState = Media.State.Playing;
                        preStateBeginTime = System.currentTimeMillis();
                    }
                    break;
                case Media.State.Paused:
                    Log.d(TAG, "Media.State.Paused");
                    break;
                case Media.State.Stopped:
                    Log.d(TAG, "Media.State.Stopped");
                    preState = Media.State.Paused;
                    stopRecord(false);
                    if (isResumed) {
                        mMediaPlayer.play();
                    }
                    break;
                case Media.State.Ended:
                    Log.d(TAG, "Media.State.Ended");
                    forceOffAudio();
                    stopRecord(false);
                    preState = Media.State.Ended;
                    showProgressAndTips(getString(net.ajcloud.wansviewplus.R.string.wv_loading_and_wait));
                    policeHelper.tryNextPolicy();
                    break;
                case Media.State.Error:
                    Log.d(TAG, "Media.State.Error");
                    forceOffAudio();
                    stopRecord(false);
                    preState = Media.State.Ended;
                    showProgressAndTips(getString(net.ajcloud.wansviewplus.R.string.wv_loading_and_wait));
                    policeHelper.tryNextPolicy();
                    break;
                default:
                    Log.d(TAG, "Media.State.default");
                    policeHelper.getUrlAndPlay();
                    showProgressAndTips(getString(net.ajcloud.wansviewplus.R.string.wv_loading_and_wait));
                    break;
            }
        }
    };

    private Runnable playingStateMonitorRunnable = new Runnable() {
        @Override
        public void run() {
            if (!ConnectivityUtil.isConnected(MainApplication.getApplication())) {
                Log.d(TAG, "network not available");
                return;
            }

            if (CameraUtil.isSupportAutoTrack(getCamera().getCapAbility())
                    && virtualCamera.isAutoTrace) {
                moveTraceLayout.setVisibility(View.VISIBLE);
                AnimationDrawable drawable = (AnimationDrawable) moveTraceImg.getDrawable();
                if (drawable != null) {
                    drawable.start();
                }
            } else {
                moveTraceLayout.setVisibility(View.GONE);
            }

            if (0 == refreshNum % 2) {
                float rate = mMediaPlayer.getBitRate();
                realtime_rate.setText((rate < 1024 ? ((int) rate + "K/s") : String.format("%.1fM/s", rate / 1024)));
            }
            refreshNum++;

            int nowState = mMediaPlayer.getPlayerState();
            if (nowState == -1)
                nowState = 0;
            Log.v(TAG, "now vlc state:" + nowState);
            if (showPlayType) {
                String playType = getPlayType(playedRequestType);
                String playState = getLibVlcStateString(nowState);
                playerInfo.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                if (mMediaPlayer.getStats() != null)
                    for (Map.Entry<String, Object> entry : mMediaPlayer.getStats().entrySet()) {
                        sb.append('\n').append(entry.toString());
                    }
                playerInfo.setText(playType + playState + sb.toString());
            } else {
                playerInfo.setText("");
            }
            if (nowState == Media.State.Playing)
                hideProgressAndTips();
            hHandler.postDelayed(this, 500);
            if (policeHelper.isBusy() || isChangeProperty || !isScreenOn || hasTryAll)
                return;
            Message message = new Message();
            message.what = nowState;
            playingStateMonitorHandler.sendMessage(message);
        }
    };

    private String getLibVlcStateString(int state) {
        switch (state) {
            case Media.State.NothingSpecial:
                return getString(net.ajcloud.wansviewplus.R.string.wv_trying_hard_to_ply);
            case Media.State.Opening:
                return getString(net.ajcloud.wansviewplus.R.string.wv_opening_video);
            case Media.State.Buffering:
                return getString(net.ajcloud.wansviewplus.R.string.wv_buffering_video);
            case Media.State.Playing:
                return getString(net.ajcloud.wansviewplus.R.string.wv_playing_video);
            case Media.State.Paused:
                return getString(net.ajcloud.wansviewplus.R.string.wv_video_paused);
            case Media.State.Stopped:
                return getString(net.ajcloud.wansviewplus.R.string.wv_video_stoped);
            case Media.State.Ended:
                return getString(net.ajcloud.wansviewplus.R.string.wv_video_ended);
            case Media.State.Error:
                return getString(net.ajcloud.wansviewplus.R.string.wv_video_error);
            default:
                return "";
        }
    }

    private String getPlayType(int playType) {
        switch (playType) {
            case PlayMethod.LAN:
                return getString(net.ajcloud.wansviewplus.R.string.wv_lan);
            case PlayMethod.UPNP:
                return getString(net.ajcloud.wansviewplus.R.string.wv_upnp);
//            case PlayMethod.P2P:
//                return getString(R.string.p2p);
            case PlayMethod.P2PX:
                return getString(net.ajcloud.wansviewplus.R.string.wv_p2px);
            case PlayMethod.TCP_RELAY:
                return getString(net.ajcloud.wansviewplus.R.string.wv_proxy);
            case PlayMethod.RTMP_RELAY:
                return getString(net.ajcloud.wansviewplus.R.string.wv_rtmp_relay);
        }
        return "";
    }

    private void initVirtualCameraAndPoliceHelper() {
        virtualCamera = new VirtualCamera(getActivity());
        String[] viewUrls = new String[8];
        if (getCamera().getViewSettings() != null && getCamera().getViewSettings().size() > 0) {
            for (ViewSetting viewSetting : getCamera().getViewSettings()) {
                int seq = viewSetting.getSeq() - 1;
                viewUrls[seq] = viewSetting.getViewurl();
            }
        }
        virtualCamera.viewSettingList = viewUrls;
        virtualCamera.streamPolicies = getCamera().getCameraState().getStreampolicies();
        virtualCamera.cid = getCamera().getOid();
        virtualCamera.progress = getCamera().getVideoSetting().getBrightness();
        virtualCamera.nightmode = getCamera().getVideoSetting().getNightmode();
        virtualCamera.gwMac = getCamera().getCameraState().getGwmac();
        virtualCamera.rmtAddr = getCamera().getCameraState().getRemoteaddr();
        virtualCamera.isAutoTrace = getCamera().getAutotrackSetting().getEnable() != 0;
        //virtualCamera.isMute = MyPreferenceManager.getInstance().getMute(getCamera().getOid());
        virtualCamera.ethMac = getCamera().getCameraState().getEthmac();
        virtualCamera.mQuality = 4;
        virtualCamera.state = getCamera().getCameraState().getStatus();
        policeHelper = new PoliceHelper(getActivity(), virtualCamera, this);
        String deviceId = ((DeviceHomeActivity) getActivity()).getOid();
        new DeviceApiUnit(getActivity()).getLiveSrcToken(deviceId, 1, 5, new OkgoCommonListener<LiveSrcBean>() {
            @Override
            public void onSuccess(LiveSrcBean bean) {
                if (bean.stream != null && !TextUtils.isEmpty(bean.stream.localUrl)) {
                    policeHelper.url = bean.stream.localUrl;
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    public void showCannotPlayDialog() {
        if (!getActivity().isFinishing())
            new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), net.ajcloud.wansviewplus.R.style.AlertDialogCustom))
                    .setCancelable(false)
                    .setTitle(net.ajcloud.wansviewplus.R.string.wv_cannot_play_video)
                    .setPositiveButton(net.ajcloud.wansviewplus.R.string.retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            videoPlay();
                            dialog.dismiss();
                            hideProgress();
                        }
                    }).setNegativeButton(net.ajcloud.wansviewplus.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopVideoPlay();
                    dialog.dismiss();
                    hideProgressAndTips();
                    hideProgress();
                }
            }).create().show();
    }

    private void showProgressAndTips(final String tip) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mSurface.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                tips.setVisibility(View.VISIBLE);
                tips.setText(tip);
            }
        });
    }

    private void hideProgressAndTips() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSurface.setVisibility(View.VISIBLE);
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                tips.setVisibility(View.GONE);
            }
        });
    }

    private void hideProgress() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void initCameraView(View view) {

        recordVoice = view.findViewById(net.ajcloud.wansviewplus.R.id.press_speak);
        voice_content = view.findViewById(net.ajcloud.wansviewplus.R.id.voice_content);
        voice_rcd_hint_cancel_area = view.findViewById(net.ajcloud.wansviewplus.R.id.voice_rcd_hint_cancel_area);
        voice_rcd_hint_anim_area = view.findViewById(net.ajcloud.wansviewplus.R.id.voice_rcd_hint_anim_area);
        sound_level = view.findViewById(net.ajcloud.wansviewplus.R.id.voice_rcd_hint_anim);
        mSurface = view.findViewById(net.ajcloud.wansviewplus.R.id.player_surface);
        mSurfaceFrame = view.findViewById(net.ajcloud.wansviewplus.R.id.content_part);
        mPlayerFrame = view.findViewById(net.ajcloud.wansviewplus.R.id.player_surface_frame);
        mPlayerFrame.setScaleListener(this);
        playerInfo = view.findViewById(net.ajcloud.wansviewplus.R.id.player_info);
        no_wifi_tips = view.findViewById(net.ajcloud.wansviewplus.R.id.videoplayer_no_wifi_notify);
        recordTime = view.findViewById(net.ajcloud.wansviewplus.R.id.record_time);
        recordTime.setVisibility(View.GONE);
        cloud_directionview = view.findViewById(net.ajcloud.wansviewplus.R.id.cloud_directionview);
        tips = view.findViewById(net.ajcloud.wansviewplus.R.id.tips);
        realTimeImageLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_Layout);
        realTimeImageImageView = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_ImageView);
        realTimeImage_Button = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage);
        realTimeImage_tip = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_tip);
        realTimeImage_ProgressBar = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_ProgressBar);
        realTimeImagePlay = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_Play);
        realTimeImagePlayRelativeLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_Play_RelativeLayout);
        full_screen = view.findViewById(net.ajcloud.wansviewplus.R.id.full_screen);
        fullscreen_play = view.findViewById(net.ajcloud.wansviewplus.R.id.fullscreen_play);
        fullscreen_recordvideo = view.findViewById(net.ajcloud.wansviewplus.R.id.fullscreen_recordvideo);
        fullscreen_snapshot = view.findViewById(net.ajcloud.wansviewplus.R.id.fullscreen_snapshot);
        fullscreen_stop = view.findViewById(net.ajcloud.wansviewplus.R.id.fullscreen_stop);
        fullscreen_quality_layout = view.findViewById(net.ajcloud.wansviewplus.R.id.fullscreen_quality_layout);
        fullscreen_quality = view.findViewById(net.ajcloud.wansviewplus.R.id.fullscreen_quality);
        //myRefreshScroll = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        realTimeImageNoWifiTip = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_No_Wifi_Tip);
        Offline_LinearLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.Offline_LinearLayout);
        downBar = view.findViewById(net.ajcloud.wansviewplus.R.id.DownBar);
        snapshot = view.findViewById(net.ajcloud.wansviewplus.R.id.snapshot);
        take_video = view.findViewById(net.ajcloud.wansviewplus.R.id.take_video);
        soundPressEffect = view.findViewById(net.ajcloud.wansviewplus.R.id.sound_press_effect);
        realtime_rate = view.findViewById(net.ajcloud.wansviewplus.R.id.realtime_rate);
        videoLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.Video_Area_FrameLayout);
        realTimeImageFrameLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.RealTimeImage_FrameLayout);
        dynamicAddLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.select_view_layout);
        PTZ = view.findViewById(net.ajcloud.wansviewplus.R.id.PTZ);
        PTZ.setOnClickListener(selectOnClickListener);
        angle = view.findViewById(net.ajcloud.wansviewplus.R.id.angle);
        angle.setOnClickListener(selectOnClickListener);
        dynamic = view.findViewById(net.ajcloud.wansviewplus.R.id.dynamic);
        dynamic.setOnClickListener(selectOnClickListener);
        selectViewLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.choose_view_layout);
        deleteAngleLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.delete_angleview_layout);
        functionLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.FunctionBar);
        Update_LinearLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.Update_LinearLayout);
        updateProgressLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.update_progress_layout);
        Update_ProgressBar = view.findViewById(net.ajcloud.wansviewplus.R.id.Update_ProgressBar);
        upgrade_info = view.findViewById(net.ajcloud.wansviewplus.R.id.upgrade_info);
        /*myRefreshScroll.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                netWorkChangeTOReflashOnlineEnd();
                refreshListAndStatus(false);
            }
        });*/
        progressBar = view.findViewById(net.ajcloud.wansviewplus.R.id.progressBar);
        progressBar.setIndeterminate(true);
        voiceSwitch = view.findViewById(net.ajcloud.wansviewplus.R.id.VoiceSwitch);
        fullVoiceSwitch = view.findViewById(R.id.full_voiceSwitch);
        offlineImg = view.findViewById(net.ajcloud.wansviewplus.R.id.offline_img);
        offlineHelp = view.findViewById(net.ajcloud.wansviewplus.R.id.offline_help);
        offlineTip = view.findViewById(net.ajcloud.wansviewplus.R.id.offline_tip);
        videoQuality = view.findViewById(net.ajcloud.wansviewplus.R.id.video_quality);
        cameraLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.cameraView);

        showRealtimeImageLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.realtime_image_show_layout);
        showRealtimeContentLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.realtime_image_show_content_layout);
        showRealtimeImage = view.findViewById(net.ajcloud.wansviewplus.R.id.realtime_imageview);
        showRealtimeImageCancel = view.findViewById(net.ajcloud.wansviewplus.R.id.realtime_image_cancel);
        fullScreenLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.full_screen_layout);
        moveTraceLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.in_move_trace_LinearLayout);
        moveTraceImg = view.findViewById(net.ajcloud.wansviewplus.R.id.move_trace_img);
        myToolBar = view.findViewById(net.ajcloud.wansviewplus.R.id.myToolBar);
        myStateBar = view.findViewById(net.ajcloud.wansviewplus.R.id.status_bar);

        realTimeImagePlay.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public void onClick(View v) {
                videoPlay();
            }
        });

        selectViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlyRefreshData = true;
                refreshStatus();
            }
        });

        voiceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cannotOperateWhenOfflineOrNoplay()) {
                    return;
                }
                if (virtualCamera.isMute) {
                    voiceSwitch.setImageResource(net.ajcloud.wansviewplus.R.drawable.volume_on_state);
                    fullVoiceSwitch.setImageResource(net.ajcloud.wansviewplus.R.drawable.volume_on_state);
                    virtualCamera.isMute = false;
                    if (audioSender_Realtime != null) {
                        audioSender_Realtime.CancelMute();
                    }
                } else {
                    voiceSwitch.setImageResource(net.ajcloud.wansviewplus.R.drawable.volume_off_state);
                    fullVoiceSwitch.setImageResource(net.ajcloud.wansviewplus.R.drawable.volume_off_state);
                    virtualCamera.isMute = true;
                    if (audioSender_Realtime != null) {
                        audioSender_Realtime.SetMute();
                    }
                }
            }
        });
        fullVoiceSwitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        voiceSwitch.performClick();
                    }
                }
        );

        view.findViewById(net.ajcloud.wansviewplus.R.id.videoplayer_no_wifi_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                policeHelper.getUrlAndPlay();
                startMonitorPlayerState();
                showProgressAndTips(getString(net.ajcloud.wansviewplus.R.string.wv_trying_hard_to_play_and_wait));
                no_wifi_tips.setVisibility(View.GONE);
            }
        });

        initRealTimeTalk_Type();
        snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cannotOperateWhenOfflineOrNoplay(false, 2000)) {
                    return;
                }
                handler.postDelayed(SnapshotRunnalbe, 200);
            }
        });

        take_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });

        album = (TextView) view.findViewById(net.ajcloud.wansviewplus.R.id.album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相册
            }
        });

        realTimeImage_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cannotOperateWhenOfflineOrPlay()) {
                    return;
                }

                if (isGetRealTimeImage)
                    isFromRealTimeImageButton = true;
                getSnapshot(10, true);
            }
        });

        if (virtualCamera.mQuality == 2 || virtualCamera.mQuality == 1) {
            videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low);
            fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low);
            virtualCamera.mQuality = 2;
        } else if (virtualCamera.mQuality == 3) {
            if (CameraUtil.isSupport1080P(getCamera().getCapAbility())) {
                videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
                fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
            } else {
                videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_normal);
                fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_normal);
            }
        } else if (virtualCamera.mQuality == 4) {
            if (CameraUtil.isSupport1080P(getCamera().getCapAbility())) {
                videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_best);
                fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_best);
            } else {
                videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
                fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
            }
        }

        videoQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraStatus.OFFLINE == online) {
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_device_offline);
                    return;
                }
                showVideoQualityPopupwindow(v);
            }
        });

        full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(ShowRealtimeImageRunnable);
                showRealtimeImageLayout.setVisibility(View.GONE);
                hHideControl.removeCallbacks(HideControlRunnalbe);
                hHideControl.postDelayed(HideControlRunnalbe, HideRelayTime);
                controlDirection(false);
                if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

                    Orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    isFullScreen(true);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    full_screen.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_small_screen_white);
                    //videoQuality.setVisibility(View.GONE);

                    if (isPlaying) {
                        fullScreenLayout.setVisibility(View.VISIBLE);
                    }

                    if (!mMediaPlayer.isPlaying()) {
                        fullscreen_play.setVisibility(View.VISIBLE);
                        fullscreen_stop.setVisibility(View.GONE);
                    } else {
                        fullscreen_play.setVisibility(View.GONE);
                        fullscreen_stop.setVisibility(View.VISIBLE);
                    }

                    //if (Utils.isSupportControlDirection(getCamera().getCapAbility())) {
                    if (mMediaPlayer.getPlayerState() == Media.State.Playing) {
                        controlDirection(true);
                    } else {
                        controlDirection(false);
                    }
                    //}

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Utils.dp2px(getActivity(), 40),
                            Utils.dp2px(getActivity(), 40));
                    params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                    params.bottomMargin = Utils.dp2px(getActivity(), 45);
                    params.rightMargin = Utils.dp2px(getActivity(), 16);
                    //realtime_rate.setLayoutParams(params);
                } else {
                    Orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    isFullScreen(false);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    full_screen.setImageResource(net.ajcloud.wansviewplus.R.mipmap.ic_full_screen_white);
                    fullScreenLayout.setVisibility(View.GONE);
                    //videoQuality.setVisibility(View.VISIBLE);

                    /*if (!mMediaPlayer.isPlaying()) {
                        refreshView();
                    } else {
                        realTimeImagePlay.setVisibility(View.GONE);
                        controlDirection(false);
                    }*/

                    if (!isPlaying) {
                        refreshView();
                    } else {
                        realTimeImagePlay.setVisibility(View.GONE);
                        controlDirection(false);
                    }

                    /*
                    if (!mMediaPlayer.isPlaying()) {
                        myRefreshScroll.setMode(PullToRefreshBase.Mode.DISABLED);
                    } else {
                        myRefreshScroll.setMode(PullToRefreshBase.Mode.DISABLED);
                    } */

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Utils.dp2px(getActivity(), 35),
                            Utils.dp2px(getActivity(), 35));
                    params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                    params.bottomMargin = Utils.dp2px(getActivity(), 10);
                    params.rightMargin = Utils.dp2px(getActivity(), 4);
                    //realtime_rate.setLayoutParams(params);
                }
            }
        });


        fullscreen_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hHideControl.removeCallbacks(HideControlRunnalbe);
                hHideControl.postDelayed(HideControlRunnalbe, HideRelayTime);
                videoPlay();
            }
        });

        fullscreen_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hHideControl.removeCallbacks(HideControlRunnalbe);
                hHideControl.postDelayed(HideControlRunnalbe, HideRelayTime);
                stopVideoPlay();
            }
        });

        fullscreen_recordvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hHideControl.removeCallbacks(HideControlRunnalbe);
                hHideControl.postDelayed(HideControlRunnalbe, HideRelayTime);
                record();
            }
        });

        fullscreen_snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hHideControl.removeCallbacks(HideControlRunnalbe);
                hHideControl.postDelayed(HideControlRunnalbe, HideRelayTime);
                snapshot();
            }
        });

        fullscreen_quality_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoQualityPopupwindow(v);
            }
        });

//        circle_wave_view = (RippleBackground) view.findViewById(R.id.circle_wave_view);
        offlineHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //网络质量差
                if (offlineHelp.getText().toString().equalsIgnoreCase(getString(net.ajcloud.wansviewplus.R.string.wv_goto_net_check))) {
                    /*Intent intent = new Intent(getActivity(), NetworkDiagnActivity.class);
                    intent.putExtra(NetworkDiagnActivity.INPUT_CAMERA, getCamera());
                    startActivity(intent);*/
                } else if (getCamera().getCameraState().isAnylock()) {
                    /*tipDialog.show();
                    HttpAdapterManger.getCameraRequest().setAnyLock(camera,
                            0, new ZResponse(CameraRequest.SetAnyLock, MainCameraFragment.this));*/
                } else {
                    /*Intent intent = new Intent(getActivity(), FAQActivity.class);
                    String url = AppApplication.getInstance().getServerInfo().getCamofflinehelpurl();
                    if (url == null) {
                        url = "http://help.ztehome.com.cn/help-detail.html?page=question1";
                    }
                    intent.putExtra("url", url);
                    intent.putExtra("help", true);
                    intent.putExtra("title", getString(R.string.setting_help_center_title));
                    if (getCamera().getCapAbility().getFeatures().getConnectivitydiagnose() == 1) {
                        intent.putExtra("show_extra_info", FAQActivity.SHOW_CAMERA_DIAGNOSIS);
                        intent.putExtra("Camera", getCamera());
                    }
                    startActivity(intent);*/
                }
            }
        });

        showRealtimeImageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showRealtimeAnimation();
                handler.removeCallbacks(ShowRealtimeImageRunnable);
                showRealtimeImageLayout.setVisibility(View.GONE);
            }
        });

        fullScreenLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.full_screen_layout);

        showRealtimeImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        initcloudDirectionview();
        myToolBar.setBackgroundResource(net.ajcloud.wansviewplus.R.drawable.grident_toolbar);
        if (CameraUtil.isSupportControlDirection(getCamera().getCapAbility())) {
            PTZ.setVisibility(View.VISIBLE);
            angle.setVisibility(View.VISIBLE);
            PTZ.setSelected(true);
        } else {
            PTZ.setVisibility(View.GONE);
            angle.setVisibility(View.GONE);
            dynamic.setSelected(true);
        }

        isSupportDirectionControl = CameraUtil.isSupportControlDirection(getCamera().getCapAbility());
    }

    private boolean isPlaying = false;

    private void videoPlay() {
        setSurfaceViewSize(virtualCamera.mQuality);
        mPlayerFrame.setMarginLeft(Integer.MAX_VALUE);
        //网络差状态清空
        weakNet = false;
        if (CameraStatus.OFFLINE == online) {
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_device_offline);
            return;
        }

        if (CameraStatus.FW_UPGRADE == online) {
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_device_updating_not_operate);
            return;
        }

        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = newMediaPlayer();
            }
        } catch (Exception e) {
            ToastUtil.show("LibVLC initialisation failed");
            return;
        }

        showSpeedRate();
        mHandler.removeCallbacks(setRefreshScrollRunnable);
        handler.removeCallbacks(HideVideoDelayTip);
        isResumed = true;
        policeHelper.getUrlAndPlay();
        startMonitorPlayerState();
        showProgressAndTips(getString(net.ajcloud.wansviewplus.R.string.wv_trying_hard_to_play_and_wait));
        no_wifi_tips.setVisibility(View.GONE);
        fullscreen_stop.setVisibility(View.VISIBLE);
        fullscreen_play.setVisibility(View.GONE);
        //myRefreshScroll.setMode(PullToRefreshBase.Mode.DISABLED);
        realTimeImageLayout.setVisibility(View.GONE);
        realTimeImagePlayRelativeLayout.setVisibility(View.GONE);
        realTimeImagePlay.setVisibility(View.GONE);

        mPlayerFrame.setScalable(true); //视频播放时允许缩放
        mPlayerFrame.setDragable(true); //视频播放时允许拖动
        mPlayerFrame.setVisibility(View.VISIBLE);

        hPlayVlcAudioHandler.postDelayed(PlayVlcAudioRunnable, 500);
        isPlaying = true;
    }

    public void stopVideoPlay() {
        stopVideoPlay(true);
    }

    public void stopVideoPlay(boolean isShotPic) {
        isPlaying = false;
        isSensorEnable = false;
        lastX = 0;
        hideSpeedRate();
        stopAnimation();
        stopRecord(mMediaPlayer.isRecording());
        if (isShotPic) {
            GetImageForReview();
        }
        if (CameraUtil.isSupportAutoTrack(getCamera().getCapAbility())
                && virtualCamera.isAutoTrace) {
            virtualCamera.setAutoTrackOff();
        }
        moveTraceLayout.setVisibility(View.GONE);
        hPlayVlcAudioHandler.removeCallbacks(PlayVlcAudioRunnable);
        controlDirection(false);
        TurnOffPlayAudio();
        forceOffAudio();
        mSurface.setKeepScreenOn(false);
        isResumed = false;
        hasStartPlayerStatusMonitor = false;

        if (playedRequestType == PlayMethod.RTMP_RELAY)
            virtualCamera.sendRtmpStatus();

        keepAliveHandler.removeCallbacks(keepAliveRunnalbe);
        hHandler.removeCallbacks(playingStateMonitorRunnable);
        realtime_rate.setVisibility(View.GONE);
        realTimeImageLayout.setVisibility(View.VISIBLE);
        if (!(CameraStatus.OFFLINE == getCamera().getCameraState().getStatus() || weakNet || Offline_LinearLayout.getVisibility() == View.VISIBLE)) {
            realTimeImagePlayRelativeLayout.setVisibility(View.VISIBLE);
            realTimeImagePlay.setVisibility(View.VISIBLE);
        }
        no_wifi_tips.setVisibility(View.GONE);
        playStop();
        hideProgressAndTips();
        hideProgress();

        fullscreen_stop.setVisibility(View.GONE);
        fullscreen_play.setVisibility(View.VISIBLE);

        if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //myRefreshScroll.setMode(PullToRefreshBase.Mode.DISABLED);
        } else {
            //myRefreshScroll.setMode(PullToRefreshBase.Mode.DISABLED);
        }

        mPlayerFrame.setScalable(false); // 视频暂停时禁止缩放
        mPlayerFrame.setDragable(false); // 视频暂停时禁止拖动
    }

    private View.OnClickListener addAngleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cannotOperateWhenOfflineOrNoplay(false, 2000)) {
                return;
            }
            String deviceId = ((DeviceHomeActivity) getActivity()).getOid();
            net.ajcloud.wansviewplus.support.core.device.Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
            int position = -1;
            if (camera.viewAnglesConfig == null || camera.viewAnglesConfig.viewAngles.size() == 0) {
                position = 0;
            } else {
                for (int i = 0; i < camera.viewAnglesConfig.viewAngles.size(); i++) {
                    if (TextUtils.isEmpty(camera.viewAnglesConfig.viewAngles.get(i).url)) {
                        position = i;
                        break;
                    }
                }
            }
            if (position == -1) {
                ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_angle_view_full);
                return;
            }
            snapViewAndUpload(position + 1);
        }
    };


    public void initRealTimeTalk_Type() {
        recordVoice.setOnTouchListener(recordVoiceTouchListener_RealTime);
        recordVoice.setImageResource(net.ajcloud.wansviewplus.R.drawable.ws_phone_state);
        /*
        //如该是分享摄像机语音功能屏蔽
        if (getCamera().GetPubStatus() == 1) {
            recordVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notSuportShareCameraTips();
                }
            });
        } else if (Utils.isSupportDuplexVoice(getCamera().getCapAbility())) {
            RealTimeTalk_Type = TalkType.values()[MyPreferenceManager.getInstance().getRealTimeTalkType(getCamera().getOid())];
            if (RealTimeTalk_Type == TalkType.DUPLEX) {
                recordVoice.setOnTouchListener(recordVoiceTouchListener_RealTime);
                recordVoice.setImageResource(R.drawable.phone_state);
            } else {
                recordVoice.setOnTouchListener(recordVoiceTouchListener_RealTime_halfduplex);
                recordVoice.setImageResource(R.drawable.microphone_state);
            }
        } else {
            recordVoice.setOnTouchListener(recordVoiceTouchListener);
            recordVoice.setImageResource(R.drawable.microphone_state);
        }*/
    }

    private void showVideoQualityPopupwindow(View anchor) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        // 引入窗口配置文件
        final View view;
        /*
        if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            view = inflater.inflate(net.ajcloud.wansview.R.layout.popupwindow_quality_horizontal, null);
        } else {
            view = inflater.inflate(net.ajcloud.wansview.R.layout.popupwindow_quality, null);
        }*/
        view = inflater.inflate(net.ajcloud.wansviewplus.R.layout.popupwindow_quality_horizontal, null);

        if (CameraUtil.isSupport1080P(getCamera().getCapAbility())) {
            view.findViewById(net.ajcloud.wansviewplus.R.id.quality_FD).setVisibility(View.VISIBLE);
            view.findViewById(net.ajcloud.wansviewplus.R.id.quality_HD).setVisibility(View.VISIBLE);
            view.findViewById(net.ajcloud.wansviewplus.R.id.quality_SD).setVisibility(View.GONE);
        } else {
            view.findViewById(net.ajcloud.wansviewplus.R.id.quality_FD).setVisibility(View.GONE);
            view.findViewById(net.ajcloud.wansviewplus.R.id.quality_HD).setVisibility(View.VISIBLE);
        }

        // 创建PopupWindow对象
        pop_quality = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, anchor.getMeasuredHeight(), false);
        /*if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            pop_quality = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, anchor.getMeasuredHeight(), false);
        } else {
            pop_quality = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        }*/
        // 需要设置一下此参数，点击外边可消失
        pop_quality.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外边窗口消失
        pop_quality.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop_quality.setFocusable(true);

        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!rect.contains((int) event.getX(), (int) event.getY())) {
                            pop_quality.dismiss();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });

        view.findViewById(net.ajcloud.wansviewplus.R.id.quality_FD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (virtualCamera.mQuality == 4) {
                    pop_quality.dismiss();
                    return;
                }
                if (mMediaPlayer.getPlayerState() == Media.State.Opening) {
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_opening_video_and_wait_to_change);
                    pop_quality.dismiss();
                    return;
                }


                if (!mMediaPlayer.isPlaying()) {
                    videoQuality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_best));
                    fullscreen_quality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_best));

                    videoScale = 1;
                    virtualCamera.mQuality = 4;
                    //MyPreferenceManager.getInstance().setVideoQuality(virtualCamera.cid, virtualCamera.mQuality);
                    Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                    //AppApplication.camera2quality.put(virtualCamera.cid, 4);
                    pop_quality.dismiss();
                    return;
                }
                videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_best);
                fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_best);
                videoScale = 1;
                virtualCamera.mQuality = 4;
                //AppApplication.camera2quality.put(virtualCamera.cid, 4);
                Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);

                replay();
                pop_quality.dismiss();
            }
        });

        view.findViewById(net.ajcloud.wansviewplus.R.id.quality_HD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraUtil.isSupport1080P(getCamera().getCapAbility())) {
                    if (virtualCamera.mQuality == 3) {
                        pop_quality.dismiss();
                        return;
                    }
                } else {
                    if (virtualCamera.mQuality == 4) {
                        pop_quality.dismiss();
                        return;
                    }
                }

                if (mMediaPlayer.getPlayerState() == Media.State.Opening) {
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_opening_video_and_wait_to_change);
                    pop_quality.dismiss();
                    return;
                }

                if (!mMediaPlayer.isPlaying()) {
                    if (CameraUtil.isSupport1080P(getCamera().getCapAbility())) {
                        virtualCamera.mQuality = 3;
                        //AppApplication.camera2quality.put(virtualCamera.cid, 3);
                    } else {
                        virtualCamera.mQuality = 4;
                        //AppApplication.camera2quality.put(virtualCamera.cid, 4);
                    }
                    //MyPreferenceManager.getInstance().setVideoQuality(virtualCamera.cid, virtualCamera.mQuality);
                    videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
                    fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
                    videoScale = 1;
                    Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                    pop_quality.dismiss();
                    return;
                }
                videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
                fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_high);
                videoScale = 1;
                if (CameraUtil.isSupport1080P(getCamera().getCapAbility())) {
                    virtualCamera.mQuality = 3;
                    Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                    //AppApplication.camera2quality.put(virtualCamera.cid, 3);
                } else {
                    virtualCamera.mQuality = 4;
                    Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                    //AppApplication.camera2quality.put(virtualCamera.cid, 4);
                }

                replay();

                pop_quality.dismiss();
            }
        });

        view.findViewById(net.ajcloud.wansviewplus.R.id.quality_SD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (virtualCamera.mQuality == 3) {
                    pop_quality.dismiss();
                    return;
                }
                if (mMediaPlayer.getPlayerState() == Media.State.Opening) {
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_opening_video_and_wait_to_change);
                    pop_quality.dismiss();
                    return;
                }

                if (!mMediaPlayer.isPlaying()) {
                    videoQuality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_normal));
                    fullscreen_quality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_normal));
                    videoScale = 1;
                    virtualCamera.mQuality = 3;
                    Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                    //AppApplication.camera2quality.put(virtualCamera.cid, 3);
                    //MyPreferenceManager.getInstance().setVideoQuality(virtualCamera.cid, virtualCamera.mQuality);
                    pop_quality.dismiss();
                    return;
                }
                videoQuality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_normal));
                fullscreen_quality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_normal));
                videoScale = 1;
                virtualCamera.mQuality = 3;
                Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                //AppApplication.camera2quality.put(virtualCamera.cid, 3);

                replay();
                pop_quality.dismiss();
            }
        });

        view.findViewById(net.ajcloud.wansviewplus.R.id.quality_Low).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (virtualCamera.mQuality == 2) {
                    pop_quality.dismiss();
                    return;
                }
                if (mMediaPlayer.getPlayerState() == Media.State.Opening) {
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_opening_video_and_wait_to_change);
                    pop_quality.dismiss();
                    return;
                }
                if (!mMediaPlayer.isPlaying()) {
                    videoQuality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low));
                    fullscreen_quality.setText(getString(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low));
                    videoScale = 1;
                    virtualCamera.mQuality = 2;
                    Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                    //AppApplication.camera2quality.put(virtualCamera.cid, 2);
                    //MyPreferenceManager.getInstance().setVideoQuality(virtualCamera.cid, virtualCamera.mQuality);
                    pop_quality.dismiss();
                    return;

                }
                videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low);
                fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low);
                videoScale = 1;
                virtualCamera.mQuality = 2;
                Log.d(TAG, "on ChangeQuality to " + virtualCamera.mQuality);
                //AppApplication.camera2quality.put(virtualCamera.cid, 2);

                replay();
                pop_quality.dismiss();
            }
        });

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        pop_quality.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + anchor.getMeasuredWidth(), location[1]);

        /*if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            pop_quality.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + anchor.getMeasuredWidth(), location[1]);
        } else {
            pop_quality.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + anchor.getMeasuredWidth() / 6, location[1] - anchor.getMeasuredHeight() * 3);
        }*/
    }

    private void replay() {
        //MyPreferenceManager.getInstance().setVideoQuality(virtualCamera.cid, virtualCamera.mQuality);

        stopVideoPlay(false);
        videoPlay();
        mSurface.setVisibility(View.GONE);
    }

    private boolean record() {
        if (mMediaPlayer == null) {
            return false;
        }
        if (cannotOperateWhenOfflineOrNoplay()) {
            return true;
        }
        if (!mMediaPlayer.isRecordable()) {
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_video_cannot_record);
            return true;
        }

        File directory = new File(MainApplication.fileIO.getVideoFileDirectory(virtualCamera.cid));
        if (!directory.exists())
            directory.mkdirs();
        if (!mMediaPlayer.isRecording()) {
            if (mMediaPlayer.isRecording()) {
                ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_video_already_recording);
                return true;
            } else {
                mMediaPlayer.startRecord(MainApplication.fileIO.getVideoFileDirectory(virtualCamera.cid));
                Log.d(TAG, "takeVideoRecord start");
            }
            if (recordTimer != null) {
                recordTimer.cancel();
                recordTimer.purge();
            }
            recordTimer = new Timer();
            recordSecond = 0;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    recordHandler.sendMessage(message);
                }
            };
            recordTime.setVisibility(View.VISIBLE);
            recordTimer.schedule(task, 1000, 1000);

            take_video.setSelected(true);
            fullscreen_recordvideo.setImageDrawable(getResources().getDrawable(net.ajcloud.wansviewplus.R.mipmap.ic_record_color));
        } else {
            stopRecord(true);
            take_video.setSelected(false);
            fullscreen_recordvideo.setImageDrawable(getResources().getDrawable(net.ajcloud.wansviewplus.R.mipmap.ic_record_dark));
        }

        return true;
    }

    String videoPicPath;

    private void stopRecord(boolean active) {
        if (recordTime.getVisibility() != View.VISIBLE) {
            return;
        }
        videoPicPath = getVideoPic();
        take_video.setSelected(false);
        fullscreen_recordvideo.setImageDrawable(getResources().getDrawable(net.ajcloud.wansviewplus.R.mipmap.ic_record_light));
        if (recordTimer != null) {
            recordTimer.cancel();
            recordTimer.purge();
        }
        recordSecond = 0;
        Log.d(TAG, "takeVideoRecord stop");
        recordTime.setText("00:00");
        recordTime.setVisibility(View.GONE);

        if (active) {
            if (mMediaPlayer != null && mMediaPlayer.isRecording()) {
                mMediaPlayer.stopRecord();
                //ToastUtil.makeText(R.string.have_save_to_photo, Toast.LENGTH_SHORT).show();

                if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && !TextUtils.isEmpty(videoPicPath)) {
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_have_save_to_photo);
                    addAnimationEffect(videoPicPath, album);
                    new File(videoPicPath).delete();
                } else {
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_have_save_to_photo);
                }
            }
        } else {
            if (mMediaPlayer.isRecording()) {
                ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_error_network_please_record_again);
            }
        }
    }

    private boolean snapshot() {
        File directory = new File(MainApplication.fileIO.getImageFileDirectory(virtualCamera.cid));
        if (!directory.exists())
            directory.mkdirs();
        if (!(mMediaPlayer.getPlayerState() == Media.State.Playing)) {
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_video_hasnot_play);
            return true;
        }
        if (mMediaPlayer.takeSnapShot(MainApplication.fileIO.getImageFileDirectory(virtualCamera.cid), 1920, 1080, true)) {
            if (Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_have_save_to_photo);
                addAnimationEffect(getLatestFile(MainApplication.fileIO.getImageFileDirectory(virtualCamera.cid)), album);
            } else {
                ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_have_save_to_photo);
            }

            Log.d(TAG, "screenshot success");
            return true;
        } else {
            Log.d(TAG, "screenshot failure");
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_video_snapshot_fail);
            return false;
        }
    }

    private String getVideoPic() {
        String filePath = "";
        File directory = new File(MainApplication.fileIO.getTempFilePath(virtualCamera.cid));
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (mMediaPlayer.takeSnapShot(MainApplication.fileIO.getTempFilePath(virtualCamera.cid), 1920, 1080, true)) {
            filePath = getLatestFile(MainApplication.fileIO.getTempFilePath(virtualCamera.cid));
        }
        return filePath;
    }

    private String getLatestFile(String directFile) {
        File[] files = new File(directFile).listFiles();
        ArrayList<File> list = new ArrayList<>(Arrays.asList(files));
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o2.getName().compareToIgnoreCase(o1.getName());
            }
        });
        return list.get(0).getAbsolutePath();
    }

    private void addAnimationEffect(String filePath, View view) {
        final ImageView imageView = new ImageView(getActivity());
        imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
        Rect rect = new Rect();
        videoLayout.getGlobalVisibleRect(rect);
        RelativeLayout.LayoutParams params = new DragLayout.LayoutParams(rect.right, rect.bottom - rect.top);
        cameraLayout.addView(imageView, params);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, location[0] + view.getWidth() / 2, 0, location[1] + view.getHeight() / 2);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(800);
        set.setFillBefore(true);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //cameraLayout.removeView(imageView);
                new Handler().post(new Runnable() {
                    public void run() {
                        cameraLayout.removeView(imageView);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        imageView.startAnimation(set);
    }

    void initcloudDirectionview() {
        cloud_directionview.setListener(directionControlerListener);
    }

    private CloudDirectionLayout.OnSteerListener directionControlerListener = new CloudDirectionLayout.OnSteerListener() {
        @Override
        public void onTopTouch() {
            if (cannotOperateWhenOfflineOrNoplay(true, 0)) {
                return;
            }
            Log.e("initcloudDirectionview", "onTopTouch");
            virtualCamera.onPtzCtrl(PtzCtrlType.TOP_NO_STOP);
        }

        @Override
        public void onBottomTouch() {
            if (cannotOperateWhenOfflineOrNoplay(true, 0)) {
                return;
            }
            Log.e("initcloudDirectionview", "onBottomTouch");
            virtualCamera.onPtzCtrl(PtzCtrlType.BOTTOM_NO_STOP);
        }

        @Override
        public void onRightTouch() {
            if (cannotOperateWhenOfflineOrNoplay(true, 0)) {
                return;
            }
            Log.e("initcloudDirectionview", "onRightTouch");
            virtualCamera.onPtzCtrl(PtzCtrlType.RIGHT_NO_STOP);
        }

        @Override
        public void onLeftTouch() {
            if (cannotOperateWhenOfflineOrNoplay(true, 0)) {
                return;
            }
            Log.e("initcloudDirectionview", "onLeftTouch");
            virtualCamera.onPtzCtrl(PtzCtrlType.LEFT_NO_STOP);
        }

        @Override
        public void onStop() {
            if (cannotOperateWhenOfflineOrNoplay(true, 0)) {
                return;
            }
            Log.e("initcloudDirectionview", "onStop:");
            virtualCamera.onPtzCtrl(PtzCtrlType.STOP);
        }

        @Override
        public void onTouchLeave() {
            if (cannotOperateWhenOfflineOrNoplay(true, 0)) {
                return;
            }
            Log.e("onTouchLeave", "onTouchLeave");
            hHideControl.removeCallbacks(HideControlRunnalbe);
            hHideControl.postDelayed(HideControlRunnalbe, HideRelayTime);
        }

        @Override
        public boolean canDrag() {
            return !cannotOperateWhenOfflineOrNoplay(false, 0);
        }

        @Override
        public void onTouchDown() {
            if (cannotOperateWhenOfflineOrNoplay(true, 0)) {
                return;
            }
            Log.e("onTouchLeave", "onTouchLeave");
            hHideControl.removeCallbacks(HideControlRunnalbe);
        }
    };

    private boolean cannotOperateWhenOfflineOrNoplay(boolean isAllowedContinueClick, int interval) {
        boolean isCanOperate = false;
        if (getCamera().getCameraState().getStatus() == CameraStatus.OFFLINE) {
            isCanOperate = true;
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_device_offline);
        } else if (getCamera().getCameraState().isAnylock() || isSleepMode) {
            isCanOperate = true;
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_main_sleep_mode_toast_tip);
        } else if (mMediaPlayer.getPlayerState() != Media.State.Playing) {
            isCanOperate = true;
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_video_hasnot_play);
        } else if (!isAllowedContinueClick && System.currentTimeMillis() - lastClickTime < interval) {
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_get_verify_quick_error);
            isCanOperate = true;
        } else if (!isAllowedContinueClick) {
            lastClickTime = System.currentTimeMillis();
        }
        return isCanOperate;
    }

    private boolean cannotOperateWhenOfflineOrNoplay() {
        return cannotOperateWhenOfflineOrNoplay(false, operateInterval);
    }

    private boolean cannotOperateWhenOfflineOrPlay(boolean isAllowedContinueClick, int interval) {
        boolean isCanOperate = false;
        if (getCamera().getCameraState().getStatus() == CameraStatus.OFFLINE) {
            isCanOperate = true;
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_device_offline);
        } else if (getCamera().getCameraState().isAnylock() || isSleepMode) {
            isCanOperate = true;
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_main_sleep_mode_toast_tip);
        } else if (isPlaying/*mMediaPlayer.getPlayerState() == Media.State.Playing*/) {
            isCanOperate = true;
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_playing_video_not_use);
        } else if (!isAllowedContinueClick && System.currentTimeMillis() - lastClickTime < interval) {
            ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_get_verify_quick_error);
            isCanOperate = true;
        } else if (!isAllowedContinueClick) {
            lastClickTime = System.currentTimeMillis();
        }
        return isCanOperate;
    }

    private boolean cannotOperateWhenOfflineOrPlay() {
        return cannotOperateWhenOfflineOrPlay(false, operateInterval);
    }

    void initSurfaceView() {
        mSurface.setVisibility(View.VISIBLE);
        mSurfaceHolder = mSurface.getHolder();
        String chroma = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("chroma_format", "");
        //mSurface.setZOrderOnTop(true);
        if (AndroidUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
            mSurfaceHolder.setFormat(ImageFormat.YV12);
        } else if (chroma.equals("RV16")) {
            mSurfaceHolder.setFormat(PixelFormat.RGB_565);
        } else {
            mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        }
        mSurfaceHolder.addCallback(mSurfaceCallback);
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (videoScale != defaultScale) {
                    onScaleChange(videoScale, defaultScale, e.getX(), e.getY());
                    videoScale = defaultScale;
                } else {
                    onScaleChange(videoScale, 4, e.getX(), e.getY());
                    videoScale = 4;
                }
                mPlayerFrame.changeSurfaceScale(videoScale);
                hShowControl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hShowControl.removeCallbacks(ShowControlRunnalbe);
                        hShowControl.post(HideControlRunnalbe);
                    }
                }, 300);
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mMediaPlayer.isPlaying() && Orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    stopVideoPlay();
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mSurface);
        mSurface.setOnTouchListener(onSurfaceTouch);
        vlcVout.addCallback(this);
//        vlcVout.setWindowSize(1920, 1280);
        vlcVout.attachViews(this);
    }

    private void showSpeedRate() {
        realtime_rate.setVisibility(View.VISIBLE);
        AlphaAnimation showAnimation = new AlphaAnimation(0, 1);
        showAnimation.setDuration(1000);
        showAnimation.setFillAfter(false);
        realtime_rate.setAnimation(showAnimation);
        showAnimation.startNow();
    }

    private void hideSpeedRate() {
        AlphaAnimation showAnimation = new AlphaAnimation(1, 0);
        showAnimation.setDuration(1000);
        showAnimation.setFillAfter(true);
        realtime_rate.setAnimation(showAnimation);
        showAnimation.startNow();
        realtime_rate.setVisibility(View.GONE);
    }

    private void snapViewAndUpload(final int index) {
        final File directory = new File(MainApplication.fileIO.getCacheDir() + virtualCamera.cid + File.separator + index);
        if (!directory.exists())
            directory.mkdirs();
        if (directory.length() > 0) {
            for (File file : directory.listFiles())
                file.delete();
        }
        if (mMediaPlayer.takeSnapShot(MainApplication.fileIO.getCacheDir() + virtualCamera.cid + File.separator + index, mVideoWidth, mVideoHeight, true)) {
            if (directory.listFiles().length == 1) {
                try {
                    String filePath = directory.listFiles()[0].getAbsolutePath();
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_camera_angle_tip);
                    addAnimationEffect(filePath, angle);
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 320, 180, false);
                    //create a file to write bitmap data
                    final File f = new File(MainApplication.fileIO.getCacheDir(), index + ".jpg");
                    f.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(f);
                    resized.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                    String deviceId = ((DeviceHomeActivity) getActivity()).getOid();
                    new DeviceApiUnit(getActivity()).b2Upload(deviceId, MainApplication.fileIO.getCacheDir() + index + ".jpg", "cam-viewangle", "b2", index, new OkgoCommonListener<Object>() {
                        @Override
                        public void onSuccess(Object bean) {
                            ToastUtil.single("success");
                        }

                        @Override
                        public void onFail(int code, String msg) {

                        }
                    });
                    /*    上传图片 todo
                    UploadFileUtils uploadFileUtils = new UploadFileUtils(getActivity(), f, virtualCamera.cid, "1",
                            UploadFileUtils.CAMERA_VIEWANGLE_V2, f.getName(), new UploadFileUtils.FileUploadResultListener() {
                        @Override
                        public void success(String viewUrl) {
                            try {
                                directory.listFiles()[0].delete();
                                f.delete();
                            } catch (Exception e) {
                                ExceptionHandler.handleError(getActivity(), e);
                            }
                            if (virtualCamera.viewSettingList == null) {
                                virtualCamera.viewSettingList = new String[8];
                            } else {
                                String oldUrl = virtualCamera.viewSettingList[index - 1];
                                if (!TextUtils.isEmpty(oldUrl)) {
                                    MyVolley.getInstance().getCache().remove(Utils.getBitmapCacheKey(oldUrl));
                                    MyVolley.getInstance().getRequestQueue().getCache().remove(oldUrl);
                                }
                            }
                            virtualCamera.viewSettingList[index - 1] = viewUrl;
                            getCamera().getViewSettings().get(index - 1).setViewurl(viewUrl);
                        }

                        @Override
                        public void failure() {
                            if (BuildConfig.DEBUG) Log.d(TAG, "ͼƬ�ϴ�ʧ��");
                        }

                        @Override
                        public void finish() {
                            if (BuildConfig.DEBUG) Log.d(TAG, "post ����");
                        }
                    });
                    uploadFileUtils.start(); */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else
            Log.d(TAG, "screenshot failure");
    }

    public void hideNavBarAndFullScreen() {
        View v = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        int uiOptions = v.getSystemUiVisibility();
        uiOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
        uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        v.setSystemUiVisibility(uiOptions);
    }

    public void showNavBar() {
        View v = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void isFullScreen(boolean enable) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) SizeUtil.dp2px(getContext(), 40), (int) SizeUtil.dp2px(getContext(), 40));
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.rightMargin = (int) SizeUtil.dp2px(getContext(), 4);
        if (enable) {
            params.topMargin = (int) SizeUtil.dp2px(getContext(), 10);
            hideNavBarAndFullScreen();
        } else {
            params.topMargin = (int) SizeUtil.dp2px(getContext(), 42);
            showNavBar();
        }
        //full_screen.setLayoutParams(params);
        downBar.setVisibility(enable ? View.GONE : View.VISIBLE);
        functionLayout.setVisibility(enable ? View.GONE : View.VISIBLE);
        myToolBar.setVisibility(enable ? View.GONE : View.VISIBLE);
        myStateBar.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void startMonitorPlayerState() {
        if (!hasStartPlayerStatusMonitor) {
            hasStartPlayerStatusMonitor = true;
            realtime_rate.setVisibility(View.VISIBLE);
            hHandler.postDelayed(playingStateMonitorRunnable, 500);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        virtualCamera.progress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("video seekbar", "��ʼ�϶�");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (virtualCamera.progress > 255)
            virtualCamera.progress = 255;
        if (virtualCamera.progress < 1)
            virtualCamera.progress = 1;
        virtualCamera.setMedia(virtualCamera.progress, null, null, null, null);
    }

    private void changeSurfaceSize() {
        mPlayerFrame.resetScale(); // 重置缩放比例记录
        mPlayerFrame.setVisibleWPct((float) mVideoVisibleWidth / (float) mVideoWidth);
        mPlayerFrame.setVisibleHPct((float) mVideoVisibleHeight / (float) mVideoHeight);

        changeSurfaceSize(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * 调整视频播放布局
     *
     * @param preScale 前次缩放比例
     * @param scale    当前缩放比例
     * @param pivotX   缩放中心X轴位置
     * @param pivotY   缩放中心Y轴位置
     */
    private void changeSurfaceSize(float preScale, float scale, float pivotX, float pivotY) {
        // get screen size
        int dw = getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getWidth();
        int dh = getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getHeight();
        // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (dw > dh && isPortrait || dw < dh && !isPortrait) {
            int d = dw;
            dw = dh;
            dh = d;
        }
        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0 || mVideoVisibleHeight * mVideoVisibleWidth == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }
        // compute the aspect ratio
        double ar, vw;
        double density = (double) mSarNum / (double) mSarDen;
        if (density == 1.0) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * density;
        }

        // compute the display aspect ratio
        double dar = (double) dw / (double) dh;
        switch (mCurrentSize) {
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = (int) vw;
                break;
        }
        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
//        按照帧可见区域与帧尺寸的比例，根据视频播放窗口大小（可见区域）计算视频SurfaceView尺寸
        double layoutW = Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        double layoutH = Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mSurface.getLayoutParams();
//        根据缩放比例调整视频画面尺寸
        double marginLeft;
        double marginTop;
        Rect rect = new Rect();
        videoLayout.getGlobalVisibleRect(rect);
        if (isPortrait) {
            defaultScale = 4f / 3;
        } else {
            defaultScale = mVideoWidth * 1f * dh / mVideoHeight / dw;
        }
        if ((preScale == 0 && scale == 0) || (isPortrait && scale == defaultScale) || (!isPortrait && scale == defaultScale)) {
            mSurface.setLeft(0);
            mSurface.setTop(0);
            scale = defaultScale;
            preScale = 1;
            pivotX = rect.right / 2;
            mPlayerFrame.changeSurfaceScale(scale);
            videoScale = scale;
//            根据视频画面当前偏移量，加上缩放造成的中心点漂移量，计算出视频画面应该偏移的位置
            int changeX = (int) Math.ceil(pivotX * (preScale - scale));
            int offsetX = mSurface.getLeft() + changeX;
//            根据视频播放窗和缩放比，计算视频画面最大偏移，缩放时视频画面边界不能偏移到播放窗内
            int maxOffsetX = (int) (dw * (1 - scale));
            marginLeft = Math.max(maxOffsetX, Math.min(offsetX, 0));
            marginTop = 0;
        } else if (scale >= 1 && scale < defaultScale) {
            int changeX = (int) Math.ceil(pivotX * (preScale - scale));
            int offsetX = mSurface.getLeft() + changeX;
//            根据视频播放窗和缩放比，计算视频画面最大偏移，缩放时视频画面边界不能偏移到播放窗内
            int maxOffsetX = (int) (dw * (1 - scale));
            marginLeft = Math.max(maxOffsetX, Math.min(offsetX, 0));
            if (isPortrait) {
                marginTop = (layoutW * 3 / 4 - layoutH * scale) / 2;
            } else {
                marginTop = (dh - scale * mVideoHeight) / 2;
            }
        } else {
            // 根据缩放比与缩放中心位置，对视频布局位置进行偏移调整，保证视频画面缩放时一直在缩放中心处变化
//            根据视频画面当前偏移量，加上缩放造成的中心点漂移量，计算出视频画面应该偏移的位置
            float dynamicAdjustRatio;
            if (isPortrait) {
                dynamicAdjustRatio = 0.85f;
            } else {
                dynamicAdjustRatio = 0.95f;
            }
            int changeX = (int) Math.ceil(pivotX * (preScale - scale) * dynamicAdjustRatio);
            int changeY = (int) Math.ceil(pivotY * (preScale - scale) * dynamicAdjustRatio);
            int offsetX = mSurface.getLeft() + changeX;
            int offsetY = mSurface.getTop() + changeY;
//            根据视频播放窗和缩放比，计算视频画面最大偏移，缩放时视频画面边界不能偏移到播放窗内
            int maxOffsetX = (int) (dw * (1 - scale));
            int maxOffsetY = (int) (dh * (1 - scale));
            marginLeft = Math.max(maxOffsetX, Math.min(offsetX, 0));
            marginTop = Math.max(maxOffsetY, Math.min(offsetY, 0));
        }
        lp.width = (int) (layoutW * scale);
        lp.height = (int) (layoutH * scale);
        lp.setMargins((int) marginLeft, (int) marginTop, 0, 0);
        Log.e("====gpr==", "width:" + lp.width + "; height:" + lp.height);

        Log.e("====gpr==", "marginLeft:" + marginLeft + "; marginTop:" + marginTop);
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    int num = UPTATE_INTERVAL_TIME / 100;

    private void translateView(float speed) {
        int dw = getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getWidth();
        int dh = dw * 9 / 16;
        final float maxOffsetX = dw * (1 - videoScale);
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mSurface.getLayoutParams();
        final int lastMarginTop = lp.topMargin;
        final int lastMarginLeft = mPlayerFrame.getMarginLeft() != Integer.MAX_VALUE ? mPlayerFrame.getMarginLeft() : lp.leftMargin;
        mPlayerFrame.setMarginLeft(Integer.MAX_VALUE);
        lp.leftMargin = lastMarginLeft;
        if (Math.abs(lastMarginLeft) >= Math.abs(maxOffsetX)) {
            return;
        }
        final float changeX = speed * maxOffsetX / 2;
        lp.width = (int) (dw * videoScale);
        lp.height = (int) (dh * videoScale);

        num = UPTATE_INTERVAL_TIME / 20;
        final float interval = changeX / num;
        refreshVideoHandle.post(new Runnable() {
            @Override
            public void run() {
                if (mPlayerFrame.isDragging()) {
                    refreshVideoHandle.removeCallbacksAndMessages(null);
                    return;
                }
                num--;
                float offsetX = lp.leftMargin + interval;
                float marginLeft = Math.max(maxOffsetX, Math.min(offsetX, 0));
                lp.setMargins((int) marginLeft, lastMarginTop, 0, 0);
                mSurface.setLayoutParams(lp);
                mSurface.invalidate();
                if (num > 0) {
                    refreshVideoHandle.postDelayed(this, 100);
                }
            }
        });
    }

    @Override
    public void onScaleChange(float preScale, float scale, float pivotX, float pivotY) {
        videoScale = scale;
        changeSurfaceSize(preScale, scale, pivotX, pivotY);
    }

    @Override
    public void onCannotPlay() {
        if (!isResumed) {
            return;
        }
        hasTryAll = true;
        forceOffAudio();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showCannotPlayDialog();
            }
        });
    }

    @Override
    public void onPlay(int playMethod, String url, int mVideoHeight, int mVideoWidth) {
        hasReportPlayData = false;
        playedRequestType = playMethod;
        mVideoHeight = mVideoHeight;
        mVideoWidth = mVideoWidth;
        mVideoVisibleHeight = mVideoHeight;
        mVideoVisibleWidth = mVideoWidth;
        if (mVideoHeight * 4 == mVideoWidth * 3)
            mCurrentSize = SURFACE_4_3;
        else if (mVideoHeight * 16 == mVideoWidth * 9)
            mCurrentSize = SURFACE_16_9;
        else
            mCurrentSize = SURFACE_4_3;
        mLocation = url;
        if (!rtmpUrl.isEmpty()) {
            mLocation = rtmpUrl;
        }

        hasTryAll = false;
        preState = -1;
        preStateBeginTime = 0l;
        changeSurfaceSize();
        loadMedia();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        changeSurfaceSize();
        super.onConfigurationChanged(newConfig);
    }

    private void endReached() {
        policeHelper.tryNextPolicy();
    }

    public Camera getCamera() {
        if (null == camera) {
            JSONObject JsonObject = null;
            try {
                JsonObject = new JSONObject(FileUtil.readRawFile(net.ajcloud.wansviewplus.R.raw.experience_camera));
                JSONObject result = JsonObject.getJSONObject("result");
                Gson gson = new Gson();
                Type listType = new TypeToken<Camera>() {
                }.getType();
                camera = gson.fromJson(result.toString(), listType);

                JsonObject = new JSONObject(FileUtil.readRawFile(net.ajcloud.wansviewplus.R.raw.experience_camera_state));
                result = JsonObject.getJSONObject("result");
                gson = new Gson();
                listType = new TypeToken<CameraState>() {
                }.getType();
                CameraState experience_state = gson.fromJson(result.toString(), listType);
                camera.setCameraState(experience_state);

                JsonObject = new JSONObject(FileUtil.readRawFile(net.ajcloud.wansviewplus.R.raw.experience_camera_ability));
                result = JsonObject.getJSONObject("result");
                gson = new Gson();
                listType = new TypeToken<CameraModel>() {
                }.getType();
                CameraModel experience_capability = gson.fromJson(result.toString(), listType);
                camera.setCapAbility(experience_capability);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return camera;
    }

    public void setCamera(Object camera) {
        //setUserData(camera);
    }

    @Override
    public void onRefresh() {
        //ptrLayout.setRefreshing(true);
        //refreshListAndStatus();
    }

    private void HandleMediaPlayerVideoDelay() {
        if (virtualCamera.mQuality > 2) {
//            tips.setText(getString(R.string.low_quality_try_again));
            forceOffAudio();

            videoQuality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low);
            fullscreen_quality.setText(net.ajcloud.wansviewplus.R.string.wv_videoplayer_menu_quality_low);
            videoScale = 1;
            virtualCamera.mQuality = 2;
            //AppApplication.camera2quality.put(virtualCamera.cid, 2);

            //MyPreferenceManager.getInstance().setVideoQuality(virtualCamera.cid, virtualCamera.mQuality);
            stopRecord(true);
            mMediaPlayer.stop();
            isChangeProperty = true;
            policeHelper.getUrlAndPlay();
            isChangeProperty = false;
        } else {
            //网络状态差界面
            stopVideoPlay();
            if ((getCamera().getCapAbility().getFeatures().getDiagnose() == 1)) {
                weakNet = true;
                RefreshDataToView();
            } else {
                tips.setText(net.ajcloud.wansviewplus.R.string.wv_current_network_not_well);
                tips.setVisibility(View.VISIBLE);
                handler.postDelayed(HideVideoDelayTip, 3000);
            }
        }
    }

    private void cacheImage(final String url) {
        try {
            if (isGetRealTimeImage == false)
                return;

            Integer count = urlRetry.get(url);
            if (count == null)
                return;

            if (count == RealTime_picture_wait_times) {
                isGetRealTimeImage = false;
                urlRetry.clear();
                if (isFromRealTimeImageButton) {
                    isFromRealTimeImageButton = false;
                    ToastUtil.show(net.ajcloud.wansviewplus.R.string.wv_get_network_fail_please_check);
                }
                realTimeImagePlayRelativeLayout.setVisibility(View.VISIBLE);
                realTimeImageFrameLayout.setVisibility(View.VISIBLE);
                realTimeImagePlay.setVisibility(View.VISIBLE);
                no_wifi_tips.setVisibility(View.GONE);
                realTimeImage_ProgressBar.setVisibility(View.GONE);
                realTimeImage_tip.setVisibility(View.GONE);
                realTimeImageImageView.setVisibility(View.GONE);
                Offline_LinearLayout.setVisibility(View.GONE);
                return;
            } else {
                urlRetry.put(url, ++count);
            }

            /*获取时图 --todo
            MyVolley.getInstance().getImageLoader().get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (!SnapShot_URL.equals(url))
                        return;
                    if (imageContainer.getBitmap() == null && b) {
                        return;
                    }

                    if (imageContainer.getBitmap() == null) {
                        Log.d(TAG, "null bitmap");
                        onNoImageLoaded(url);

                    } else {
                        if (isGetRealTimeImage == false)
                            return;
                        isFromRealTimeImageButton = false;
                        isGetRealTimeImage = false;
                        realTimeImagePlayRelativeLayout.setVisibility(View.VISIBLE);
                        realTimeImageFrameLayout.setVisibility(View.VISIBLE);
                        realTimeImagePlay.setVisibility(View.VISIBLE);
                        no_wifi_tips.setVisibility(View.GONE);
                        realTimeImage_ProgressBar.setVisibility(View.GONE);
                        realTimeImage_tip.setVisibility(View.GONE);
                        realTimeImageImageView.setVisibility(View.VISIBLE);
                        Offline_LinearLayout.setVisibility(View.GONE);
                        CameraUtils.saveBitmap(imageContainer.getBitmap(), virtualCamera.cid);
                        realTimeImageImageView.setImageBitmap(imageContainer.getBitmap());
                        RelativeLayout.LayoutParams showRealtimeImageParams = new RelativeLayout.LayoutParams(videoLayout.getMeasuredWidth(), videoLayout.getMeasuredWidth() * 9 / 16);
                        showRealtimeImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        showRealtimeContentLayout.setLayoutParams(showRealtimeImageParams);
                        showRealtimeImageLayout.setVisibility(View.VISIBLE);
                        showRealtimeImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        showRealtimeImage.setImageBitmap(imageContainer.getBitmap());
                        handler.postDelayed(ShowRealtimeImageRunnable, 3000);

                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(TAG, "error response");
                    onNoImageLoaded(url);
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable ShowRealtimeImageRunnable = new Runnable() {
        @Override
        public void run() {
            showRealtimeAnimation();
        }
    };

    private void onNoImageLoaded(final String url) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cacheImage(url);
            }
        }, 2500);
    }

    private void getSnapshot(int wait_time, boolean isShowView) {

        if (isGetRealTimeImage == true) {
            Log.d(TAG, "正在获取图片，请稍后");
            return;
        }
        RealTime_picture_wait_times = wait_time;
        isGetRealTimeImage = true;
        if (isShowView) {
            realTimeImageLayout.setVisibility(View.VISIBLE);
            realTimeImage_ProgressBar.setVisibility(View.VISIBLE);
            realTimeImage_tip.setVisibility(View.VISIBLE);
            realTimeImage_tip.setText(getString(net.ajcloud.wansviewplus.R.string.wv_get_latest_realtime_image));
            realTimeImageImageView.setVisibility(View.GONE);
            realTimeImagePlayRelativeLayout.setVisibility(View.GONE);
            realTimeImageFrameLayout.setVisibility(View.GONE);
        }

        try {
            /*HttpAdapterManger.getCameraRequest().getSnapShot(camera,
                    new ZResponse(CameraRequest.GetCameraSnap, this));*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void forceOffAudio() {
        if (isRealTimeSpeechOn) {
            /*if (Utils.isSupportDuplexVoice(getCamera().getCapAbility())) {
                RealTimeTalk_Type = TalkType.values()[MyPreferenceManager.getInstance().getRealTimeTalkType(getCamera().getOid())];
                if (RealTimeTalk_Type == TalkType.DUPLEX) {
                    recordVoice.setImageResource(R.drawable.phone_state);
                } else {
                    recordVoice.setImageResource(R.drawable.microphone_state);
                }
            } else {
                recordVoice.setImageResource(R.drawable.microphone_state);
            }*/
            recordVoice.setImageResource(net.ajcloud.wansviewplus.R.drawable.phone_state);

            isRealTimeSpeechOn = false;
            if (audioSender_Realtime != null) {
                audioSender_Realtime.stopRecord();
            }

//            circle_wave_view.stopRippleAnimation();
        }
    }

    private Runnable HideControlRunnalbe = new Runnable() {
        @Override
        public void run() {
            fullScreenLayout.setVisibility(View.GONE);
            controlDirection(false);
        }
    };

    private Runnable ShowControlRunnalbe = new Runnable() {
        @Override
        public void run() {
            if (Orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                if (!CameraUtil.isSupportControlDirection(getCamera().getCapAbility())) {
                    controlDirection(false);
                } else {
                    if (mMediaPlayer.getPlayerState() == Media.State.Playing) {
                        controlDirection(null);
                    } else {
                        controlDirection(false);
                    }
                }
                if (fullScreenLayout.getVisibility() == View.VISIBLE) {
                    fullScreenLayout.setVisibility(View.GONE);
                } else {
                    fullScreenLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    //@Override
    public void reflashFragment() {
        RefreshDataToView();
    }

    public void RefreshDataToView() {
        String[] viewUrls = new String[8];
        if (getCamera().getViewSettings() != null && getCamera().getViewSettings().size() > 0) {
            for (ViewSetting viewSetting : getCamera().getViewSettings()) {
                int seq = viewSetting.getSeq() - 1;
                viewUrls[seq] = viewSetting.getViewurl();
            }
        }
        virtualCamera.viewSettingList = viewUrls;
        virtualCamera.streamPolicies = getCamera().getCameraState().getStreampolicies();
        virtualCamera.cid = getCamera().getOid();
        virtualCamera.progress = getCamera().getVideoSetting().getBrightness();
        virtualCamera.nightmode = getCamera().getVideoSetting().getNightmode();
        virtualCamera.gwMac = getCamera().getCameraState().getGwmac();
        virtualCamera.rmtAddr = getCamera().getCameraState().getRemoteaddr();
        virtualCamera.isAutoTrace = getCamera().getAutotrackSetting().getEnable() != 0;
        virtualCamera.volume = getCamera().getAudioSetting().getVolume();
        virtualCamera.ethMac = getCamera().getCameraState().getEthmac();
        virtualCamera.state = getCamera().getCameraState().getStatus();
        policeHelper = new PoliceHelper(getActivity(), virtualCamera, this);
        refreshView();
    }

    private Runnable SnapshotRunnalbe = new Runnable() {
        @Override
        public void run() {
            snapshot();
        }
    };

    private Runnable HideVideoDelayTip = new Runnable() {
        @Override
        public void run() {
            tips.setVisibility(View.GONE);
        }
    };

    /**
     * 关闭刷新，恢复正常
     */
    private void netWorkChangeTOReflashOnlineEnd() {
        if (netReflashFirst) {
            netReflashFirst = false;
            //myRefreshScroll.setState(PullToRefreshBase.State.OVERSCROLLING);
        } else {
            resetNetworkReflash(false);
        }
    }

    /**
     * 清楚刷新标志及状态
     */
    private void resetNetworkReflash(boolean bReset) {
        if (bReset) {
            //if (!GuideView.isShowing()) {
            // myRefreshScroll.setState(PullToRefreshBase.State.RESET);
            //}
        }
        //AppApplication.RefreshByNetworkConfig = false;
        handler.removeCallbacks(reFreshView_Runnable);
    }

    /**
     * 配置网络成功时定时获取数据
     */
    private void onTimeGetCameraState() {
        endTime = System.currentTimeMillis();
        if (endTime - startTime < 30 * 1000) {
            handler.postDelayed(reFreshView_Runnable, 5000);
        } else {
            resetNetworkReflash(true);
        }
    }

    public Runnable reFreshView_Runnable = new Runnable() {
        @Override
        public void run() {
            refreshListAndStatus(false);
        }
    };

    /**
     * 硬加速回调
     */
    public void eventHardwareAccelerationError() {
        //mHandler_Player.sendEmptyMessage(HW_ERROR);
    }

    /**
     * External extras:
     * - position (long) - position of the video to start with (in ms)
     */
    @SuppressWarnings({"unchecked"})
    private void loadMedia() {
        mSurface.setKeepScreenOn(true);
        if (mLocation == null || mMediaPlayer == null) {
            return;
        }
        if (!mMediaPlayer.getVLCVout().areViewsAttached()) {
            mMediaPlayer.getVLCVout().detachViews();
            mMediaPlayer.getVLCVout().setVideoSurface(mSurfaceHolder.getSurface(), mSurfaceHolder);
            mMediaPlayer.getVLCVout().attachViews();
        }

        //VLCInstance.get().setOnHardwareAccelerationError(this);
        final Media media = new Media(VLCInstance.get(), Uri.parse(mLocation));
        media.setHWDecoderEnabled(false, false);
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.setEventListener(mMediaPlayerListener);
        mMediaPlayer.SetVisiableQuality(VideoQuality.FormatQualityToVLC(virtualCamera.mQuality, getCamera()));
        mMediaPlayer.play();
    }

    /**
     * 创建mediaplay 实例
     */
    private MediaPlayer newMediaPlayer() {
        MediaPlayer mp = new MediaPlayer(VLCInstance.get());
        mp.getVLCVout().addCallback(this);
        return mp;
    }

    /**
     * 播放停止
     */
    private void playStop() {
        if (mMediaPlayer == null)
            return;
        final Media media = mMediaPlayer.getMedia();
        if (media != null) {
            media.setEventListener(null);
            mMediaPlayer.setEventListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.setMedia(null);
            media.release();
            //VLCInstance.get().setOnHardwareAccelerationError(null);
        }
        mSurface.setKeepScreenOn(false);
    }

    private final MediaPlayer.EventListener mMediaPlayerListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {

            switch (event.type) {
                case MediaPlayer.Event.Stopped:
                    MainCameraFragment.this.stopRecord(false);
                    break;
                case MediaPlayer.Event.EndReached:
                    MainCameraFragment.this.endReached();
                    MainCameraFragment.this.stopRecord(false);
                    break;
                case MediaPlayer.Event.ESDelay:
                    MainCameraFragment.this.HandleMediaPlayerVideoDelay();
                    break;
                default:
                    Log.e(TAG, String.format("Other event handled (0x%x)", event.type));
                    break;
            }
        }
    };

    private void GetImageForReview() {
        if (!(mMediaPlayer.getPlayerState() == Media.State.Playing)) {
            return;
        }

        mMediaPlayer.takeSnapShot(MainApplication.fileIO.getRealTimePictureDirectory(virtualCamera.cid), 1280, 720, true);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CameraUtil.renameImage(virtualCamera.cid);
        File file = new File(MainApplication.fileIO.getRealTimePictureDirectory(virtualCamera.cid) + "/realtime_picture.jpg");
        if (file.exists()) {
            Bitmap bitmap = CameraUtil.getLoacalBitmap(file.getPath(), false);
            realTimeImageImageView.setVisibility(View.VISIBLE);
            realTimeImageImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isSleepMode = data.getBooleanExtra("isSleep", false);
    }

    private void refreshDynamicView() {
        for (int i = 0; i < selectViewLayout.getChildCount(); i++) {
            if (selectViewLayout.getChildAt(i).isSelected()) {
                selectViewLayout.getChildAt(i).performClick();
                break;
            }
        }
    }

    View.OnClickListener selectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dynamicAddLayout.removeAllViews();
            PTZ.setSelected(false);
            angle.setSelected(false);
            dynamic.setSelected(false);
            album.setSelected(false);
            View view = null;
            switch (v.getId()) {
                case net.ajcloud.wansviewplus.R.id.PTZ:
                    view = new PTZView(getActivity(), addAngleListener, navigationListener, directionControlerListener).getView();
                    PTZ.setSelected(true);

                    break;
                case net.ajcloud.wansviewplus.R.id.angle:
                    String deviceId = ((DeviceHomeActivity) getActivity()).getOid();
                    net.ajcloud.wansviewplus.support.core.device.Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
                    view = new AngleView(getActivity(), deviceId, camera.viewAnglesConfig.viewAngles, selectViewLayout, deleteAngleLayout, virtualCamera).getView();
                    angle.setSelected(true);

                    break;
                case net.ajcloud.wansviewplus.R.id.dynamic:
                    view = new DynamicView(getActivity(), getCamera().getOid(), false).getView();
                    dynamic.setSelected(true);
                    break;
            }
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (view != null) {
                dynamicAddLayout.addView(view, params);
            }
        }
    };

    View.OnClickListener navigationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cannotOperateWhenOfflineOrNoplay()) {
                return;
            }
            int height = SizeUtil.getStatusBarheight(getActivity()) + Utils.dp2px(getActivity(), 10);

            final NavigationPopupWindow popupWindow = new NavigationPopupWindow(getActivity(),
                    SizeUtil.getScreenHeigth(getContext()) - functionLayout.getTop() - height,
                    virtualCamera.isAutoTrace, new NavigationPopupWindow.NavigationListener() {
                @Override
                public boolean mobileTrackerSwitch(boolean state) {
                    if (!cannotOperateWhenOfflineOrNoplay()) {
                        virtualCamera.isAutoTrace = !state;
                        virtualCamera.setAutoTrack();
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean navigation(int type) {
                    if (!cannotOperateWhenOfflineOrNoplay()) {
                        if (PtzCtrlType.STOP == type) {
                            virtualCamera.interruptAutoTrace();
                        }
                        virtualCamera.onPtzCtrl(type);
                        return true;
                    }
                    return false;
                }
            });
            popupWindow.show(dynamicAddLayout, functionLayout.getTop() + height);
        }
    };

    public enum UPDATE_STATUS {
        SUCCESS, FAIL, PROCESSING, TIMEOUT, EXIT
    }

    public Handler HideUpdate_Handler = new Handler();

    public long UpdateMaxWaitTime = 1000 * 60 * 6;     //ms  max:5min
    private Runnable HideUpdateRunable = new Runnable() {
        @Override
        public void run() {
            Update_LinearLayout.setVisibility(View.GONE);
        }
    };

    public void ShowUpdate(UPDATE_STATUS status) {
        HideUpdate_Handler.removeCallbacks(HideUpdateRunable);
        switch (status) {
            case SUCCESS:
                Update_LinearLayout.setVisibility(View.VISIBLE);
                updateProgressLayout.setVisibility(View.GONE);
                upgrade_info.setVisibility(View.VISIBLE);
                upgrade_info.setText(net.ajcloud.wansviewplus.R.string.wv_camera_upgrade_success);
                HideUpdate_Handler.postDelayed(HideUpdateRunable, 8000);
                break;
            case FAIL:
            case TIMEOUT:
                Update_LinearLayout.setVisibility(View.VISIBLE);
                updateProgressLayout.setVisibility(View.GONE);
                upgrade_info.setVisibility(View.VISIBLE);
                upgrade_info.setText(net.ajcloud.wansviewplus.R.string.wv_camera_upgrade_failture);
                HideUpdate_Handler.postDelayed(HideUpdateRunable, 8000);
                break;
            case PROCESSING:
                Update_LinearLayout.setVisibility(View.VISIBLE);
                updateProgressLayout.setVisibility(View.VISIBLE);
                upgrade_info.setVisibility(View.GONE);
                videoQuality.setVisibility(View.GONE);
                break;
            case EXIT:
                HideUpdate_Handler.postDelayed(HideUpdateRunable, 3000);
                break;
        }
    }

    public void showRealtimeAnimation() {
        showRealtimeImageCancel.setVisibility(View.GONE);
        int orignalWidth = showRealtimeContentLayout.getMeasuredWidth();
        float rate = 1.0f * videoLayout.getMeasuredHeight() / showRealtimeContentLayout.getMeasuredHeight();
        final int terminalWidth = (int) (orignalWidth * rate);
        ValueAnimator animator = ValueAnimator.ofInt(orignalWidth, terminalWidth);
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) showRealtimeContentLayout.getLayoutParams();
                lp.width = curValue;
                lp.height = curValue * 9 / 16;
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                showRealtimeContentLayout.setLayoutParams(lp);
                showRealtimeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                showRealtimeContentLayout.postInvalidate();
                if (curValue == terminalWidth) {
                    showRealtimeImageLayout.setVisibility(View.GONE);
                    showRealtimeImageCancel.setVisibility(View.VISIBLE);
                }
            }
        });
        animator.start();
    }

    @Override
    public boolean canBackPressed() {
        if (Orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            full_screen.performClick();
            return false;
        } else {
            getActivity().finish();
            return true;
        }
    }

    @Override
    protected boolean hasStatusBar() {
        return false;
    }
}