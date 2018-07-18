package net.ajcloud.wansviewplus.main.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.customview.camera.ScaleFrameLayout;
import net.ajcloud.wansviewplus.support.utils.SizeUtil;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCUtil;
import org.videolan.vlc.util.VLCInstance;

public class PlayerView extends FrameLayout
        implements IVLCVout.Callback,
        IVLCVout.OnNewVideoLayoutListener,
        ScaleFrameLayout.OnScaleListener {

    private static final String TAG = PlayerView.class.getSimpleName();
    private static final int HW_ERROR = 1000;
    private Context  mContext;

    public interface OnChangeListener {

        void onBufferChanged(float buffer);

        void onLoadComplet();

        void onError();

        void onEnd();

        void onNetSlow();

        void onTimeChange(long time);
    }

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurface;
    private SurfaceHolder mSurfaceHolder;
    private ScaleFrameLayout mSurfaceFrame;

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum=1;
    private int mSarDen=1;
    private OnChangeListener mOnChangeListener;
    private boolean mCanSeek = false;

    private Uri uri;
    public static int OldEvent = 0;

    public PlayerView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void initPlayer(String url) {
        if (VLCUtil.validateLocation(url)) {
            this.uri = Uri.parse(url);
            if (!mMediaPlayer.getVLCVout().areViewsAttached()) {
                mMediaPlayer.getVLCVout().detachViews();
                mMediaPlayer.getVLCVout().setVideoSurface(mSurfaceHolder.getSurface(), mSurfaceHolder);
                mMediaPlayer.getVLCVout().attachViews();
            }

            final Media media = new Media(VLCInstance.get(), uri);
            media.setHWDecoderEnabled(false, false);
            mMediaPlayer.setMedia(media);
            media.release();
        }
    }

    private void init() {
        mMediaPlayer = newMediaPlayer();
        mMediaPlayer.setEventListener(mMediaPlayerListener);

        LayoutInflater.from(getContext()).inflate(R.layout.view_player, this);
        //video view
        mSurface = findViewById(R.id.player_surface);
        mSurfaceHolder = mSurface.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);

        mSurfaceFrame = findViewById(R.id.player_surface_frame);
        mSurfaceFrame.setScaleListener(this);
        mSurfaceFrame.setScalable(false); // 允许缩放
        mSurfaceFrame.setDragable(false); // 允许拖动

        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mSurface);
        mVideoVisibleWidth = 960;
        mVideoVisibleHeight = 540;
        mVideoWidth = 960;
        mVideoHeight = 540;
        vlcVout.setWindowSize(mVideoWidth, mVideoHeight);
        mSurface.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof CautionActivity) {
                    ((CautionActivity)mContext).showControl();
                }
            }
        });
        changeSurfaceSize();
    }


    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    // 设置播放比例16:9
    public void setSurfaceViewer16To9() {
        mCurrentSize = SURFACE_16_9;
    }

    private final Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (format == PixelFormat.RGBX_8888)
                Log.d(TAG, "Pixel format is RGBX_8888");
            else if (format == PixelFormat.RGB_565)
                Log.d(TAG, "Pixel format is RGB_565");
            else if (format == ImageFormat.YV12)
                Log.d(TAG, "Pixel format is YV12");
            else
                Log.d(TAG, "Pixel format is other/unknown");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (getMediaPlayer() != null) {
                getMediaPlayer().getVLCVout().detachViews();
            }
        }
    };

    public void setOnChangeListener(OnChangeListener listener) {
        mOnChangeListener = listener;
    }

    public void changeSurfaceSize() {
        mSurfaceFrame.resetScale(); // 重置缩放比例记录
        mSurfaceFrame.setVisibleWPct((float) mVideoVisibleWidth / (float) mVideoWidth);
        mSurfaceFrame.setVisibleHPct((float) mVideoVisibleHeight / (float) mVideoHeight);

        changeSurfaceSize(1.0f, 1.0f, 0.0f, 0.0f);
    }

    /**
     * 调整视频播放布局
     *
     * @param preScale    前次缩放比例
     * @param scale       当前缩放比例
     * @param pivotX      缩放中心X轴位置
     * @param pivotY      缩放中心Y轴位置
     */
    public void changeSurfaceSize(float preScale, float scale, float pivotX, float pivotY) {
        int sw;
        int sh;
        sw = SizeUtil.getScreenWidth(getContext());
        sh = SizeUtil.getScreenHeigth(getContext());
        double dw = sw, dh = sh;
        boolean isPortrait;
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }
        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }
        // compute the aspect ratio
        double ar, vw;
        if (mSarDen == mSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double) mSarNum / mSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (mCurrentSize) {
        case SURFACE_BEST_FIT:
            if (dar < ar)
                dh = dw / ar;
            else
                dw = dh * ar;
            break;
        case SURFACE_FIT_HORIZONTAL:
            dh = dw / ar;
            break;
        case SURFACE_FIT_VERTICAL:
            dw = dh * ar;
            break;
        case SURFACE_FILL:
            break;
        case SURFACE_16_9:
            ar = 16.0 / 9.0;
            if (dar < ar)
                dh = dw / ar;
            else
                dw = dh * ar;
            break;
        case SURFACE_4_3:
            ar = 4.0 / 3.0;
            if (dar < ar)
                dh = dw / ar;
            else
                dw = dh * ar;
            break;
        case SURFACE_ORIGINAL:
            dh = mVideoVisibleHeight;
            dw = vw;
            break;
        }

        SurfaceView surface;
        SurfaceHolder surfaceHolder;
        FrameLayout surfaceFrame;

        surface = mSurface;
        surfaceHolder = mSurfaceHolder;
        surfaceFrame = mSurfaceFrame;

        // force surface buffer size
        surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);

//        Log.d(TAG, "[preScale: " + preScale + " scale: " + scale + " pivot(" + pivotX + ", " + pivotY + ")]");
//        Log.d(TAG, "SurfaceFrame positon(" + surfaceFrame.getLeft() + ", " + surfaceFrame.getTop() + ", " + surfaceFrame.getRight() + ", " + surfaceFrame.getBottom() + ")");
//        Log.d(TAG, "Surface      positon(" + surface.getLeft() + ", " + surface.getTop() + ", " + surface.getRight() + ", " + surface.getBottom() + ")");

        // set display size
//        Log.d(TAG, "FrameVisible Width: " + mVideoVisibleWidth + " Height: " + mVideoVisibleHeight);
//        Log.d(TAG, "FrameSize    Width: " + mVideoWidth + " Height: " + mVideoHeight);

////        按照帧可见区域与帧尺寸的比例，根据视频播放窗口大小（可见区域）计算视频SurfaceView尺寸
//        Log.d(TAG, "VideoScreen  Width: " + dw + " Height: " + dh);
        double layoutW = Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        double layoutH = Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
//        Log.d(TAG, "VideoLayout  Width: " + layoutW + " Height: " + layoutH);

        MarginLayoutParams lp = (MarginLayoutParams) surface.getLayoutParams();

//        根据缩放比例调整视频画面尺寸
//        Log.d(TAG, "Surface fromW: " + lp.width + " fromH: " + lp.height);
        lp.width = (int) (layoutW * scale);
        lp.height = (int) (layoutH * scale);
//        Log.d(TAG, "Surface toW: " + lp.width + " toH: " + lp.height);

//        竖屏时播放窗最大高度为屏幕的3/4，横屏时全屏
        double maxVisableH = (double) SizeUtil.getScreenHeigth(getContext());
        if (isPortrait)
        {
            maxVisableH = maxVisableH * 3 / 4;
        }
//        Log.d(TAG, "maxVisableH: " + maxVisableH);

        int visibleW = (int) Math.floor(dw);
        int visibleH = (int) Math.min(Math.floor(dh * scale), maxVisableH);

//        Log.d(TAG, "Surface fromMargins(" + lp.leftMargin + ", " + lp.topMargin + ", " + lp.rightMargin + ", " + lp.bottomMargin + ")");
        if (scale == 1)         {
            lp.setMargins(0, 0, 0, 0);
        } else {// 根据缩放比与缩放中心位置，对视频布局位置进行偏移调整，保证视频画面缩放时一直在缩放中心处变化
//            根据视频画面当前偏移量，加上缩放造成的中心点漂移量，计算出视频画面应该偏移的位置
            int changeX = (int) Math.ceil(pivotX * (preScale - scale));
            int changeY = (int) Math.ceil(pivotY * (preScale - scale));

//            int offsetX = lp.leftMargin + changeX;
//            int offsetY = lp.topMargin + changeY;
            int offsetX = surface.getLeft() + changeX;
            int offsetY = surface.getTop() + changeY;

//            根据视频播放窗和缩放比，计算视频画面最大偏移，缩放时视频画面边界不能偏移到播放窗内
            int maxOffsetX = (int) (dw * (1 - scale));
            int maxOffsetY = (int) (((visibleH == maxVisableH) ? visibleH : 0) * (1 - scale)); //视频播放窗会跟随视频画面放大，当达到播放窗最大高度时停止放大，但视频画面依然可以继续放大（产生边界点位移）

            lp.setMargins(Math.max(maxOffsetX, Math.min(offsetX, 0)), Math.max(maxOffsetY, Math.min(offsetY, 0)), 0, 0);
        }
//        Log.d(TAG, "Surface toMargins(" + lp.leftMargin + ", " + lp.topMargin + ", " + lp.rightMargin + ", " + lp.bottomMargin + ")");

        surface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = (MarginLayoutParams) surfaceFrame.getLayoutParams();
//        Log.d(TAG, "SurfaceFrame fromW: " + lp.width + " fromH: " + lp.height);
        lp.width = visibleW;
        lp.height = visibleH;
//        Log.d(TAG, "SurfaceFrame toW: " + lp.width + " toH: " + lp.height);
        surfaceFrame.setLayoutParams(lp);

        surface.invalidate();
        //subtitlesSurface.invalidate();
    }

    public void start() {
        if (uri == null) {
            return;
        }
        mSurface.setKeepScreenOn(true);

        getMediaPlayer().getVLCVout().setVideoSurface(mSurfaceHolder.getSurface(), mSurfaceHolder);
        mMediaPlayer.getVLCVout().attachViews();

        final Media media = new Media(VLCInstance.get(), uri);
        media.setHWDecoderEnabled(false, false);
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();
    }

    public void play() {
        getMediaPlayer().play();
        mSurface.setKeepScreenOn(true);
    }

    public void pause() {
        getMediaPlayer().pause();
        mSurface.setKeepScreenOn(false);
    }

    public void stop() {
        getMediaPlayer().stop();
        mSurface.setKeepScreenOn(false);
    }

    public void destroy () {
        if (mMediaPlayer != null) {
            final Media media = mMediaPlayer.getMedia();
            if (media != null) {
                media.setEventListener(null);

                mMediaPlayer.setEventListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.setMedia(null);
                media.release();
            }
        }
    }

    public long getTime() {
        return getMediaPlayer().getTime();
    }

    public long getLength() {
        return getMediaPlayer().getLength();
    }

    public void setTime(long time) {
        getMediaPlayer().setTime(time);
    }

    public void setRate(float rate) {
        if (getRate() * rate > 4.0 || getRate() * rate < 0.25) {
            return;
        }
        getMediaPlayer().setRate(getRate() * rate);
    }

    public float getRate() {
        return  getMediaPlayer().getRate();
    }

    public boolean canSeekable() {
        return mCanSeek;
    }

    public boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    public boolean isSeekable() {
        return getMediaPlayer().isSeekable();
    }

    public int getPlayerState() {
        return getMediaPlayer().getPlayerState();
    }

    public int getVolume() {
        return getMediaPlayer().getVolume();
    }

    public void setVolume(int volume) {
        getMediaPlayer().setVolume(volume);
    }

    public void seek(int delta) {
        // unseekable stream
        if (getMediaPlayer().getLength() <= 0 || !mCanSeek)
            return;

        long position = getMediaPlayer().getTime() + delta;
        if (position < 0)
            position = 0;
        getMediaPlayer().setTime(position);
    }

    public void setposition(float position) {
        if (!mCanSeek)
            return;
        getMediaPlayer().setPosition(position);
    }

    public float getPosition() {
        if (!mCanSeek)
            return 0.0f;
        return getMediaPlayer().getPosition();
    }

    private final MediaPlayer.EventListener mMediaPlayerListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {

            switch (event.type) {
                case MediaPlayer.Event.Playing:
                    Log.i(TAG, "MediaPlayer.Event.Playing");
                    if (PlayerView.this.mOnChangeListener != null) {
                        PlayerView.this.mOnChangeListener.onLoadComplet();
                    }
                    break;
                case MediaPlayer.Event.Paused:
                    Log.i(TAG, "MediaPlayer.Event.Paused");
                    break;
                case MediaPlayer.Event.Stopped:
                    Log.i(TAG, "MediaPlayer.Event.Stopped");
                    break;
                case MediaPlayer.Event.EndReached:
                    Log.d(TAG, "MediaPlayerEndReached");
                    if (PlayerView.this.mOnChangeListener != null) {
                        if(OldEvent == Media.State.Buffering){
                            PlayerView.this.mOnChangeListener.onError();
                        } else{
                            PlayerView.this.mOnChangeListener.onEnd();
                        }
                    }
                    break;
                case MediaPlayer.Event.EncounteredError:
                    if (PlayerView.this.mOnChangeListener != null) {
                        PlayerView.this.mOnChangeListener.onError();
                    }
                    break;
                case MediaPlayer.Event.TimeChanged:
                    if (PlayerView.this.mOnChangeListener != null) {
                        PlayerView.this.mOnChangeListener.onTimeChange(event.getTimeChanged());
                    }
                    break;
                case MediaPlayer.Event.PositionChanged:
                    if (!PlayerView.this.mCanSeek) {
                        PlayerView.this.mCanSeek = true;
                    }
                    break;
                case MediaPlayer.Event.Buffering:
                    if (PlayerView.this.mOnChangeListener != null) {
                        PlayerView.this.mOnChangeListener.onBufferChanged(event.getBufferingChanged());
                    }
                    break;
                case MediaPlayer.Event.Vout:
                    break;
                case MediaPlayer.Event.ESAdded:
                    break;
                case MediaPlayer.Event.ESDeleted:
                    break;
                case MediaPlayer.Event.PausableChanged:
                    break;
                case MediaPlayer.Event.SeekableChanged:
                    break;
                case MediaPlayer.Event.ESDelay:
                    break;
                default:
                    Log.d(TAG, String.format("Event not handled (0x%x)", event.type));
                    break;
            }

            OldEvent = mMediaPlayer.getPlayerState();
        }
    };

    private MediaPlayer newMediaPlayer() {
        MediaPlayer mp = new MediaPlayer(VLCInstance.get());
        mp.getVLCVout().addCallback(this);
        return  mp;
    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth  = visibleWidth;
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

    @Override
    public void onScaleChange(float preScale, float scale, float pivotX, float pivotY)
    {
        changeSurfaceSize(preScale, scale, pivotX, pivotY);
    }
}
