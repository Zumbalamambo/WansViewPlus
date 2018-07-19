package net.ajcloud.wansviewplus.main.device;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.WVFragment;
import net.ajcloud.wansviewplus.main.cloud.AboutCloudActivity;
import net.ajcloud.wansviewplus.main.device.type.camera.AudioSender_RealTime;
import net.ajcloud.wansviewplus.main.video.PlayerView;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.GroupListBean;
import net.ajcloud.wansviewplus.support.customview.ReplayTimeAxisView;
import net.ajcloud.wansviewplus.support.utils.DateUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.ReverseAudioInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/07/17.
 * Function:    云存储回看
 */
public class ReplayCloudFragment extends WVFragment implements View.OnClickListener, PlayerView.OnChangeListener, Handler.Callback {

    private static final int SHOW_PROGRESS = 0;
    private static final int ON_LOADED = 1;
    private SwipeRefreshLayout layout_refresh;
    private LinearLayout ll_content;
    private LinearLayout ll_empty;
    private TextView tv_cloud_introduction;
    private Button btn_cloud_buy;
    private ReplayTimeAxisView replay_time;
    private FrameLayout fl_delete;
    private FrameLayout fl_download;
    private ImageView iv_cover;
    private LinearLayout ll_loading;
    private TextView tv_buffer;
    private FrameLayout fl_play;
    private RelativeLayout small_screen_layout;
    private ImageView iv_fullscreen;
    private LinearLayout full_screen_layout;
    private ImageView fullscreen_small_screen;
    private PlayerView pv_video;
    private LinearLayout ll_bottom;
    private TextView tv_speed;

    private AudioSender_RealTime audioSender_Realtime = null;
    private Handler hPlayVlcAudioHandler = new Handler();
    private ReverseAudioInfo audioInfo;
    private Handler mHandler;
    private Handler hHandler;
    private PopupWindow pop_speed;

    private DeviceApiUnit deviceApiUnit;
    private String deviceId;
    private boolean isFirst = true;
    private String cdate;
    private String picUrl;
    private String videoUrl;
    private long cts = -1;
    private int AudioPlaySample;
    private boolean isLandScape;
    private List<GroupListBean.GroupInfo> records;

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
                    audioSender_Realtime = new AudioSender_RealTime(mActivity, null, audioInfo, AudioPlaySample);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_replay_cloud, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout_refresh = view.findViewById(R.id.layout_refresh);
        ll_content = view.findViewById(R.id.ll_content);
        ll_empty = view.findViewById(R.id.ll_empty);
        tv_cloud_introduction = view.findViewById(R.id.tv_cloud_introduction);
        btn_cloud_buy = view.findViewById(R.id.btn_cloud_buy);
        replay_time = view.findViewById(R.id.replay_time);
        fl_delete = view.findViewById(R.id.fl_delete);
        fl_download = view.findViewById(R.id.fl_download);
        iv_cover = view.findViewById(R.id.iv_cover);
        ll_loading = view.findViewById(R.id.ll_loading);
        tv_buffer = view.findViewById(R.id.tv_buffer);
        fl_play = view.findViewById(R.id.fl_play);
        small_screen_layout = view.findViewById(R.id.small_screen_layout);
        iv_fullscreen = view.findViewById(R.id.iv_fullscreen);
        full_screen_layout = view.findViewById(R.id.full_screen_layout);
        fullscreen_small_screen = view.findViewById(R.id.fullscreen_small_screen);
        pv_video = view.findViewById(R.id.pv_video);
        ll_bottom = view.findViewById(R.id.ll_bottom);
        tv_speed = view.findViewById(R.id.tv_speed);

        refreshUI(0);
        initData();
        initListener();
    }

    private void initListener() {
        pv_video.setOnChangeListener(this);
        fl_download.setOnClickListener(this);
        fl_delete.setOnClickListener(this);
        tv_cloud_introduction.setOnClickListener(this);
        btn_cloud_buy.setOnClickListener(this);
        fl_play.setOnClickListener(this);
        iv_fullscreen.setOnClickListener(this);
        fullscreen_small_screen.setOnClickListener(this);
        tv_speed.setOnClickListener(this);
        replay_time.setOnSlideListener(new ReplayTimeAxisView.OnSlideListener() {

            @Override
            public void onSlide(long startTime, float position) {
                onMediaStop();

                GroupListBean.GroupInfo groupInfo = getCurrentM3u8Info(startTime * 1000);
                if (groupInfo != null) {
                    picUrl = groupInfo.intraPicture;
                    videoUrl = groupInfo.m3u8Url;
                    Glide.with(mActivity).load(picUrl).placeholder(R.mipmap.figure_big).into(iv_cover);
                    pv_video.setposition(position);
                }
            }

            @Override
            public void onSelected(long startTime, long endTime) {

            }
        });
    }

    private void initData() {
        deviceId = ((ReplayActivity) getActivity()).getDeviceId();
        deviceApiUnit = new DeviceApiUnit(mActivity);
        mHandler = new Handler(this);
        hHandler = new Handler(this);
        pv_video.setSurfaceViewer16To9();
        tv_speed.setText("X1");
        getReplay();
    }

    @Override
    public void onPause() {
        onMediaPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hPlayVlcAudioHandler.removeCallbacks(PlayVlcAudioRunnable);
        mHandler.removeCallbacks(playingStateMonitorRunnable);
        TurnOffPlayAudio();
        pv_video.destroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pv_video.changeSurfaceSize();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cloud_introduction:
                startActivity(new Intent(mActivity, AboutCloudActivity.class));
                break;
            case R.id.btn_cloud_buy:
                break;
            case R.id.fl_delete:
                break;
            case R.id.fl_download:
                break;
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
            case R.id.tv_speed:
                showVideoSpeedPopupwindow(tv_speed);
                break;
        }
    }

    /**
     * 0:empty/fail
     * 1:success
     * 2:pause
     */
    private void refreshUI(int state) {
        if (state == 0) {
            layout_refresh.setEnabled(true);
            ll_content.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        } else if (state == 1) {
            layout_refresh.setEnabled(false);
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            fl_play.setVisibility(View.GONE);
        } else if (state == 2) {
            layout_refresh.setEnabled(false);
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            fl_play.setVisibility(View.VISIBLE);
        }
    }

    private void onMediaPlay(String url) {
        if (null != pv_video && !TextUtils.isEmpty(url)) {
            refreshUI(1);
            ll_loading.setVisibility(View.VISIBLE);
            pv_video.initPlayer(url);
            pv_video.play();
            hPlayVlcAudioHandler.postDelayed(PlayVlcAudioRunnable, 500);
        }
    }

    private void onMediaPause() {
        if (null != pv_video) {
            pv_video.pause();
        }
    }

    private void onMediaStart() {
        if (null != pv_video) {
            pv_video.play();
        }
    }

    private void onMediaStop() {
        if (null != pv_video) {
            refreshUI(2);
            mHandler.removeMessages(SHOW_PROGRESS);
            hPlayVlcAudioHandler.removeCallbacks(PlayVlcAudioRunnable);
            pv_video.stop();
            ll_loading.setVisibility(View.GONE);
            iv_cover.setVisibility(View.VISIBLE);

        }
    }

    private void showVideoSpeedPopupwindow(View anchor) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view;
        view = inflater.inflate(R.layout.popupwindow_speed_horizontal, null);

        // 创建PopupWindow对象
        pop_speed = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, anchor.getMeasuredHeight(), false);
        // 需要设置一下此参数，点击外边可消失
        pop_speed.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外边窗口消失
        pop_speed.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop_speed.setFocusable(true);

        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (!rect.contains((int) event.getX(), (int) event.getY())) {
                            pop_speed.dismiss();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });

        view.findViewById(R.id.ll_speed_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(tv_speed.getText().toString(), "X1")) {
                    pop_speed.dismiss();
                    pv_video.setRate(1f);
                    tv_speed.setText("X" + (int) pv_video.getRate());
                }
            }
        });

        view.findViewById(R.id.ll_speed_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(tv_speed.getText().toString(), "X2")) {
                    pop_speed.dismiss();
                    pv_video.setRate(2f);
                    tv_speed.setText("X" + (int) pv_video.getRate());
                }
            }
        });

        view.findViewById(R.id.ll_speed_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(tv_speed.getText().toString(), "X4")) {
                    pop_speed.dismiss();
                    pv_video.setRate(4f);
                    tv_speed.setText("X" + (int) pv_video.getRate());
                }
            }
        });

        view.findViewById(R.id.ll_speed_8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(tv_speed.getText().toString(), "X8")) {
                    pop_speed.dismiss();
                    pv_video.setRate(8f);
                    tv_speed.setText("X" + (int) pv_video.getRate());
                }
            }
        });

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        pop_speed.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + anchor.getMeasuredWidth(), location[1]);
    }

    void TurnOffPlayAudio() {
        if (audioSender_Realtime != null) {
            audioSender_Realtime.stopPlayVlcAudio();
        }
    }

    private void fullScreen() {
        isLandScape = true;
        ((ReplayActivity) mActivity).setToolBarVisible(false);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        small_screen_layout.setVisibility(View.GONE);
        full_screen_layout.setVisibility(View.VISIBLE);
        ll_bottom.setVisibility(View.GONE);
    }

    private void exitFullScreen() {
        isLandScape = false;
        ((ReplayActivity) mActivity).setToolBarVisible(true);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        small_screen_layout.setVisibility(View.VISIBLE);
        full_screen_layout.setVisibility(View.GONE);
        ll_bottom.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onBackPressed() {
//        if (isLandScape) {
//            exitFullScreen();
//        } else {
//            mActivity.finish();
//        }
//    }

    private void getReplay() {
        layout_refresh.setRefreshing(true);
        deviceApiUnit.getGroupList(deviceId, DateUtil.getTimesmorning(), DateUtil.getTimesnight(), new OkgoCommonListener<GroupListBean>() {
            @Override
            public void onSuccess(GroupListBean bean) {
                layout_refresh.setRefreshing(false);
                if (bean != null && bean.groups != null) {
                    refreshUI(2);
                    records = bean.groups;
                    List<Pair<Long, Long>> list = new ArrayList<>();
                    for (int i = 0; i < bean.groups.size(); i++) {
                        Pair<Long, Long> time = new Pair<>(bean.groups.get(i).tsStart / 1000, bean.groups.get(i).tsEnd / 1000);
                        list.add(time);
                        if (isFirst && i == 0) {
                            isFirst = false;
                            replay_time.setMidTimeStamp(bean.groups.get(i).tsStart);
                            picUrl = bean.groups.get(i).intraPicture;
                            videoUrl = bean.groups.get(i).m3u8Url;
                            Glide.with(mActivity).load(picUrl).into(iv_cover);
                        }
                    }
                    //TODO
                    replay_time.setRecordList(list);
                } else {
                    refreshUI(0);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                layout_refresh.setRefreshing(false);
                refreshUI(0);
            }
        });
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
        replay_time.scrollMidTimeStamp(time);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS:
                int time = (int) pv_video.getTime();
                if (time >= 0) {
//                    replay_time.setMidTimeStamp();
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

    /**
     * 获取当前时间对应的m3u8开始时间
     */
    private GroupListBean.GroupInfo getCurrentM3u8Info(long time) {
        if (records == null || records.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).tsStart == time) {
                    return records.get(i);
                }
            }
            return null;
        }
    }
}
