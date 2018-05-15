package com.ajcloud.wansview.support.customview.lockgesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ajcloud.wansview.R;

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

    /**
     * 每个边上的LockGestureView的个数
     */
    private int mCount = 3;
    /**
     * 存储密码
     */
    private int[] mAnswer = {};
    /**
     * 保存用户选中的LockGestureView的id
     */
    private List<Integer> mChoose = new ArrayList<Integer>();

    private Paint mPaint;

    /**
     * 每个LockGestureView中间的间距 设置为：mLockGestureViewWidth * 25%
     */
    private int mMarginBetweenLockView = 30;
    /**
     * LockGestureView的边长 4 * mWidth / ( 5 * mCount + 1 )
     */
    private int mLockGestureViewWidth;

    /**
     * LockGestureView无手指触摸的状态下内圆的颜色
     */
    private int circleColor = 0xFF555555;
    /**
     * LockGestureView手指触摸的状态下内圆和外圆的颜色
     */
    private int selectedColor = 0xcc66CCFF;

    private Path mPath;
    /**
     * 指引线的开始位置x
     */
    private int mLastPathX;
    /**
     * 指引线的开始位置y
     */
    private int mLastPathY;
    /**
     * 指引下的结束位置
     */
    private Point mTmpTarget = new Point();

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
        selectedColor = a.getColor(R.styleable.LockGestureLayout_selectedColor, getResources().getColor(R.color.colorPrimary));
        mCount = a.getInteger(R.styleable.LockGestureLayout_mCount, 3);
        a.recycle();

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        //强制为矩形
        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;
        setMeasuredDimension(mWidth, mHeight);
        // 初始化mLockGestureViews
        if (mLockGestureViews == null) {
            mLockGestureViews = new LockGestureView[mCount * mCount];
            // 计算每个LockGestureView的边长
            mLockGestureViewWidth = (int) (4 * mWidth * 1.0f / (5 * mCount + 1));
            //计算每个LockGestureView的间距  
            mMarginBetweenLockView = (int) (mLockGestureViewWidth * 0.25);

            for (int i = 0; i < mLockGestureViews.length; i++) {
                //初始化每个LockGestureView  
                mLockGestureViews[i] = new LockGestureView(getContext(),
                        circleColor, selectedColor);
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

                // 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                if (i >= 0 && i < mCount)// 第一行
                {
                    topMargin = mMarginBetweenLockView;
                }
                if (i % mCount == 0)// 第一列  
                {
                    leftMagin = mMarginBetweenLockView;
                }

                lockerParams.setMargins(leftMagin, topMargin, rightMargin,
                        bottomMargin);
                mLockGestureViews[i].setMode(LockGestureView.MODE.TOUCH_DOWN);
                addView(mLockGestureViews[i], lockerParams);
            }
        }
    }
}
