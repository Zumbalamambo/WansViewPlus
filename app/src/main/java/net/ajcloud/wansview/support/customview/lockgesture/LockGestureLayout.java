package net.ajcloud.wansview.support.customview.lockgesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.support.tools.WLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/15.
 * 手势解锁自定义layout
 */
public class LockGestureLayout extends RelativeLayout {

    private int mWidth;

    private int mHeight;

    private LockGestureView[] mLockGestureViews;

    //每个边上的LockGestureView的个数
    private int mCount = 3;
    //LockGestureView间距：mLockGestureViewWidth * 25%
    private int mMarginBetweenLockView;
    //LockGestureView的边长 4 * mWidth / ( 5 * mCount + 1 )
    private int mLockGestureViewWidth;
    //LockGestureView无手指触摸的状态下内圆的颜色
    private int circleColor = 0xCC555555;
    //LockGestureView手指触摸的状态下内圆和外圆的颜色
    private int selectedColor = 0xCC66CCFF;
    //LockGestureView验证失败颜色
    private int errorColor = 0xCCFF0000;
    //指引线的开始位置x
    private int mLastPathX;
    //指引线的开始位置y
    private int mLastPathY;
    //指引下的结束位置
    private Point mTmpTarget = new Point();
    //存储密码
    private int[] mAnswer = {};
    //保存用户选中的LockGestureView的id
    private List<Integer> mChoose = new ArrayList<>();

    //是否初次设置密码
    private boolean isFirst;
    private Paint mPaint;
    private Path mPath;
    private static final int RESET = 1000;
    private Handler resetHander = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            reset();
            invalidate();
        }
    };

    private OnLockGestureResultListenner listenner;

    public void setLockGestureResultListenner(OnLockGestureResultListenner listenner) {
        this.listenner = listenner;
    }

    public LockGestureLayout(Context context) {
        super(context);
    }

    public LockGestureLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockGestureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LockGestureLayout, defStyleAttr, 0);
        circleColor = a.getColor(R.styleable.LockGestureLayout_circleColor, getResources().getColor(R.color.light_gray));
        selectedColor = a.getColor(R.styleable.LockGestureLayout_selectedColor, getResources().getColor(R.color.gesture_select_blue));
        mCount = a.getInteger(R.styleable.LockGestureLayout_mCount, 3);
        a.recycle();

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
    }

    private int getSize(int defaultSize, int measureSpec) {
        int finalSize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {
                finalSize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                finalSize = Math.min(size, defaultSize);
                break;
            }
            case MeasureSpec.EXACTLY: {
                finalSize = size;
                break;
            }
        }
        return finalSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getSize(350, widthMeasureSpec);
        mHeight = getSize(350, heightMeasureSpec);
        //强制为矩形
        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;
        // 初始化mLockGestureViews
        if (mLockGestureViews == null) {
            mLockGestureViews = new LockGestureView[mCount * mCount];
            // 计算每个LockGestureView的边长
//            mLockGestureViewWidth = (int) (4 * mWidth * 1.0f / (5 * mCount + 1));
            mLockGestureViewWidth = (mWidth/mCount);
            //计算每个LockGestureView的间距
            mMarginBetweenLockView = 0;
//            mMarginBetweenLockView = (int) (mLockGestureViewWidth * 0.25);
            mPaint.setStrokeWidth(mLockGestureViewWidth * 0.1f);

            for (int i = 0; i < mLockGestureViews.length; i++) {
                //初始化每个LockGestureView  
                mLockGestureViews[i] = new LockGestureView(getContext(),
                        circleColor, selectedColor, errorColor);
                mLockGestureViews[i].setId(i + 1);
                //设置参数，主要是定位LockGestureView间的位置  
                RelativeLayout.LayoutParams lockerParams = new RelativeLayout.LayoutParams(
                        mLockGestureViewWidth, mLockGestureViewWidth);

                // 不是每行的第一个，则设置位置为前一个的右边  
                if (i % mCount != 0) {
                    lockerParams.addRule(RelativeLayout.RIGHT_OF,
                            mLockGestureViews[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面  
                if (i > mCount - 1) {
                    lockerParams.addRule(RelativeLayout.BELOW,
                            mLockGestureViews[i - mCount].getId());
                }
                //设置右下左上的边距  
                int rightMargin = mMarginBetweenLockView;
                int bottomMargin = mMarginBetweenLockView;
                int leftMagin = 0;
                int topMargin = 0;

                // 默认每个View都有右边距和下边距 第一行的有上边距 第一列的有左边距
                if (i < mCount) {       // 第一行
                    topMargin = mMarginBetweenLockView;
                }
                if (i % mCount == 0) {  // 第一列
                    leftMagin = mMarginBetweenLockView;
                }

                lockerParams.setMargins(leftMagin, topMargin, rightMargin,
                        bottomMargin);
                mLockGestureViews[i].setMode(LockGestureView.MODE.NORMAL);
                addView(mLockGestureViews[i], lockerParams);
            }
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                resetHander.removeMessages(RESET);
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(selectedColor);
                LockGestureView child = getChildIdByPos(x, y);
                if (child != null) {
                    int cId = child.getId();
                    if (!mChoose.contains(cId)) {
                        mChoose.add(cId);
                        child.setMode(LockGestureView.MODE.VERIFY);
                        // 设置指引线的起点
                        mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
                        mLastPathY = child.getTop() / 2 + child.getBottom() / 2;

                        if (mChoose.size() == 1) {  // 当前添加为第一个
                            mPath.moveTo(mLastPathX, mLastPathY);
                        } else {                    // 非第一个，将两者使用线连上
                            mPath.lineTo(mLastPathX, mLastPathY);
                        }

                    }
                }
                // 指引线的终点  
                mTmpTarget.x = x;
                mTmpTarget.y = y;
                break;
            case MotionEvent.ACTION_UP:
                // 将终点设置位置为起点，即取消指引线
                mTmpTarget.x = mLastPathX;
                mTmpTarget.y = mLastPathY;
                if (isFirst) {
                    isFirst = false;
                    //TODO 保存密码
                    resetHander.removeMessages(RESET);
                    resetHander.sendEmptyMessageDelayed(RESET, 500);
                } else {
                    if (checkPassword()) {
                        if (listenner != null) {
                            listenner.onSuccess();
                        }
                    } else {
                        mPaint.setColor(errorColor);
                        changeItemMode(LockGestureView.MODE.ERROR);
                        resetHander.removeMessages(RESET);
                        resetHander.sendEmptyMessageDelayed(RESET, 500);
                        if (listenner != null) {
                            listenner.onFail();
                        }
                    }
                }
                break;

        }
        invalidate();
        return true;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制LockGestureView间的连线
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChoose.size() > 0) {
            if (mLastPathX != 0 && mLastPathY != 0)
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x,
                        mTmpTarget.y, mPaint);
        }

    }

    /**
     * 重置
     */
    private void reset() {
        mChoose.clear();
        mPath.reset();
        for (LockGestureView lockGestureView : mLockGestureViews) {
            lockGestureView.setMode(LockGestureView.MODE.NORMAL);
        }
    }

    /**
     * 检查当前坐标是否在child中
     */
    private boolean checkPositionInChild(View child, int x, int y) {
        int padding = (int) (mLockGestureViewWidth * 0.15);
        if (x >= child.getLeft() + padding && x <= child.getRight() - padding
                && y >= child.getTop() + padding
                && y <= child.getBottom() - padding) {
            return true;
        }
        return false;
    }

    /**
     * 通过x,y定位LockGestureView
     */
    private LockGestureView getChildIdByPos(int x, int y) {
        for (LockGestureView lockGestureView : mLockGestureViews) {
            if (checkPositionInChild(lockGestureView, x, y)) {
                return lockGestureView;
            }
        }
        return null;
    }

    /**
     * 改变已选LockGestureView模式
     */
    private void changeItemMode(LockGestureView.MODE mode) {
        for (LockGestureView LockGestureView : mLockGestureViews) {
            if (mChoose.contains(LockGestureView.getId())) {
                LockGestureView.setMode(mode);
            }
        }
    }

    /**
     * 验证密码是否正确
     */
    private boolean checkPassword() {
        //TODO
        for (int key : mChoose) {
            WLog.d("LockGestureLayout", key);
        }
        return false;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    interface OnLockGestureResultListenner {
        void onSuccess();

        void onFail();

        void onOverTime();
    }
}
