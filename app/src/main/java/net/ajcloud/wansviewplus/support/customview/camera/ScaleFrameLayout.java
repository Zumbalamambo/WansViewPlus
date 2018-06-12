package net.ajcloud.wansviewplus.support.customview.camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import net.ajcloud.wansviewplus.R;


public class ScaleFrameLayout extends FrameLayout {
    public interface OnScaleListener {
        /**
         * 多指缩放操作变化事件
         *
         * @param preScale 前一次事件中的缩放比例
         * @param scale    当前缩放比例
         * @param pivotX   当前缩放中心点X轴位置
         * @param pivotY   当前缩放中心点Y轴位置
         */
        void onScaleChange(float preScale, float scale, float pivotX, float pivotY);
    }

    private static final String TAG = ScaleFrameLayout.class.getSimpleName();


    private float visibleWPct = 1.0f; // 子视图中的可视面积宽度占比
    private float visibleHPct = 1.0f; // 子视图中的可视面积高度占比

    private boolean isScalable;
    private float minScale; // 最小缩放比
    private float maxScale; // 最大缩放比
    private boolean isDragable;
    private boolean isSlideable;
    private int slideRangeX; // 子控件在拖拽动作松手后，继续滑动的水平距离

    public float getVisibleWPct() {
        return visibleWPct;
    }

    public void setVisibleWPct(float visibleWPct) {
        this.visibleWPct = visibleWPct;
    }

    public float getVisibleHPct() {
        return visibleHPct;
    }

    public void setVisibleHPct(float visibleHPct) {
        this.visibleHPct = visibleHPct;
    }

    public boolean isScalable() {
        return this.isScalable;
    }

    public void setScalable(boolean scalable) {
        this.isScalable = scalable;
    }

    public boolean isDragable() {
        return isDragable;
    }

    public void setDragable(boolean dragable) {
        isDragable = dragable;
    }

    public boolean isSlideable() {
        return isSlideable;
    }

    public void setSlideable(boolean slideable) {
        isSlideable = slideable;
    }

    private OnScaleListener mListener;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private float scale;
    private volatile float preScale;
    private ViewDragHelper mDragHelper;
    private float changeScale = 0;
    private int marginLeft;

    public void changeSurfaceScale(float changeScale) {
        this.changeScale = changeScale;
        resetScale();
//        Log.e("lgwell", "doubleClick: " + changeScale + " preScale: " + preScale);
    }


    public ScaleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ScaleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public void setScaleListener(OnScaleListener listener) {
        try {
            this.mListener = listener;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetScale() {
        marginLeft = Integer.MAX_VALUE;
        this.scale = minScale;
        this.preScale = minScale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        final int pointers = event.getPointerCount();
        try {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_POINTER_UP: {
                    if (pointers > 1) {
//                        多指时处理屏幕缩放
                        mScaleGestureDetector.onTouchEvent(event);
                        return false;
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                    break;
            }
            mDragHelper.processTouchEvent(event);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return shouldInterceptTouchEvent(ev);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleFrameLayout);
        isScalable = typedArray.getBoolean(net.ajcloud.wansviewplus.R.styleable.ScaleFrameLayout_scaleable, false);
        minScale = typedArray.getFloat(net.ajcloud.wansviewplus.R.styleable.ScaleFrameLayout_minScale, 1.0f);
        maxScale = typedArray.getFloat(net.ajcloud.wansviewplus.R.styleable.ScaleFrameLayout_maxScale, 4.0f);
        isDragable = typedArray.getBoolean(net.ajcloud.wansviewplus.R.styleable.ScaleFrameLayout_dragable, false);
        isSlideable = typedArray.getBoolean(net.ajcloud.wansviewplus.R.styleable.ScaleFrameLayout_dragSlideable, true);
        slideRangeX = typedArray.getInt(net.ajcloud.wansviewplus.R.styleable.ScaleFrameLayout_dragSlideRangeX, 300);
        typedArray.recycle();

        init(context);
    }

    private void init(Context context) {
//        Log.e("lgwell", "init");

//        手势缩放处理
        scale = minScale;
        preScale = minScale;
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.e("lgwell", "detector.getScaleFactor(): " + detector.getScaleFactor());
                scale = preScale * detector.getScaleFactor();
                scale = Math.max(minScale, Math.min(scale, maxScale));
                mListener.onScaleChange(preScale, scale, detector.getFocusX(), detector.getFocusY());
                Log.e("lgwell", "onScale preScale: " + preScale + " scale: " + scale + " increace: " + (scale - preScale)
                        + "  detector.getFocusX(): " + detector.getFocusX() + "  detector.getFocusY(): " + detector.getFocusY());
                preScale = scale;
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                if (changeScale != 0) {
                    preScale = changeScale;
                }
                Log.d("lgwell", "onScaleBegin preScale:" + preScale + "  changeScale:" + changeScale);
                return isScalable;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                Log.d("lgwell", "onScaleEnd scale: " + scale);
                changeScale = 0;
                preScale = scale;
            }
        });

//        手势拖动处理
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                Log.d(TAG, "tryCaptureView");
                // 只允许第一个子视图移动
                return (child == getChildAt(0)) && isDragable;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                int visibleW = (int) (child.getWidth() * visibleWPct);
                Log.d(TAG, "getViewHorizontalDragRange[childW: " + child.getWidth() + " visibleW: " + visibleW + " screenW: " + getWidth() + "]");
//                只允许子视图可视面积比当前视图框大时（放大后），进行移动
                return visibleW - getWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                int visibleH = (int) (child.getHeight() * visibleHPct);
                Log.d(TAG, "getViewVerticalDragRange[childH: " + child.getHeight() + " visibleH: " + visibleH + " screenH: " + getHeight() + "]");
//                只允许子视图可视面积比当前视图框大时（放大后），进行移动
                return visibleH - getHeight() > 0 ? visibleH - getHeight() : 0;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                int visibleW = (int) (child.getWidth() * visibleWPct);
                Log.d(TAG, "clampViewPositionHorizontal[childW: " + child.getWidth() + " visibleW: " + visibleW + " screenW: " + getWidth() + "]");
//                触摸移动时，子视图的可视边界不能偏移到当前视图框内
                return Math.max((getWidth() - visibleW), Math.min(left, 0));
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                int visibleH = (int) (child.getHeight() * visibleHPct);
                Log.d(TAG, "clampViewPositionVertical[childH: " + child.getHeight() + " visibleH: " + visibleH + " screenH: " + getHeight() + "]");
//                触摸移动时，子视图的可视边界不能偏移到当前视图框内
                int distance;
                if (getHeight() - visibleH >= 0) {
                    distance = (getHeight() - visibleH) / 2;
                } else {
                    distance = Math.max((getHeight() - visibleH), Math.min(top, 0));
                }
                Log.d(TAG, "clampViewPositionVertical top:" + top + "  dy:" + dy + "  distance:" + distance);
                return distance;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (!isSlideable) {
                    return;
                }

                Log.d(TAG, "onViewReleased(releasedChild, " + xvel + ", " + yvel + ")");
                Log.d(TAG, "child fromPositon(" + releasedChild.getLeft() + ", " + releasedChild.getTop() + ", " + releasedChild.getRight() + ", " + releasedChild.getBottom() + ")");

//                取子控件当前位置
                int finalLeft = releasedChild.getLeft();
                int finalTop = releasedChild.getTop();

//                取子控件可视面积区域
                int visibleW = (int) (releasedChild.getWidth() * visibleWPct);
                int visibleH = (int) (releasedChild.getHeight() * visibleHPct);

//                根据子控件运动趋势判断是否要滑动
                if (xvel < 0) {
//                    松手前，子控件有向左运动的趋势，则继续向左滑动
                    finalLeft -= slideRangeX;
                } else if (xvel > 0) {
//                    松手前，子控件有向右运动的趋势，则继续向右滑动
                    finalLeft += slideRangeX;
                }

                if (yvel < 0) {
//                    松手前，子控件有向上运动的趋势，则继续向上滑动
                    finalTop -= slideRangeX * visibleW / visibleH;
                } else if (yvel > 0) {
//                    松手前，子控件有向下运动的趋势，则继续向下滑动
                    finalTop += slideRangeX * visibleW / visibleH;
                }

//                滑动时，子视图的可视边界不能偏移到当前视图框内
                finalLeft = Math.max((getWidth() - visibleW), Math.min(finalLeft, 0));
                finalTop = Math.max((getHeight() - visibleH), Math.min(finalTop, 0));
                Log.d(TAG, "child toPositon(" + finalLeft + ", " + finalTop + ", ...)");
                marginLeft = finalLeft;
                if (mDragHelper.settleCapturedViewAt(finalLeft, finalTop > 0 ? finalTop / 2 : finalTop)) {
                    ViewCompat.postInvalidateOnAnimation(ScaleFrameLayout.this);
                }
            }
        });
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    private long firstPressTime;

    /**
     * 根据用户触摸动作，判断是否要在本层消费该事件
     *
     * @param ev 触摸事件消息
     * @return 是否消费该消息（调用本层onTouchEvent）
     */
    private boolean shouldInterceptTouchEvent(MotionEvent ev) {
        final boolean b = mDragHelper.shouldInterceptTouchEvent(ev);
        final int action = ev.getActionMasked();
        final int pointers = ev.getPointerCount();

//        Log.d(TAG, "onInterceptTouchEvent(" + action + ") pointers: " + pointers);
//        Log.d(TAG, "ViewDragHelper.shouldInterceptTouchEvent " + b);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstPressTime = System.currentTimeMillis();
//                Log.d(TAG, "child will handle this");
                return false;

            case MotionEvent.ACTION_POINTER_DOWN: {
                long last = System.currentTimeMillis() - firstPressTime;
//                Log.d(TAG, "Since the first press: " + last + "ms");
                if (last < 150) {
//                    150毫秒内的多指事件透传给子层，让子层可处理用户点击
//                    Log.d(TAG, "child will handle this");
                    return false;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                long last = System.currentTimeMillis() - firstPressTime;
//                Log.d(TAG, "Since the first press: " + last + "ms");
                if (last < 150) {
//                    150毫秒内移动事件透传给子层，用户点击时可能有轻微抖动，让子层可以处理
//                    Log.d(TAG, "child will handle this");
                    return false;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                long last = System.currentTimeMillis() - firstPressTime;
//                Log.d(TAG, "Since the first press: " + last + "ms");
                if (last < 500) {
//                    500毫秒内抬手的点击事件透传给子层，让子层可处理用户点击
//                    Log.d(TAG, "child will handle this");
                    return false;
                }
                break;
            }
        }

//        Log.d(TAG, "we will handle this");
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean isDragging() {
        return mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }
}