package com.ajcloud.wansview.support.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.support.tools.WLog;
import com.ajcloud.wansview.support.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by mamengchao on 2018/05/16.
 * 回看时间轴控件
 */
public class ReplayTimeAxisView extends View {

    private int width;
    private int height;
    //当前中间刻度时间点
    private long currentMidTimeStamp;
    //画笔宽度
    private int strokeWidth;
    //默认刻度间间隔
    private int spacing;
    //较长刻度线长度
    private float longScale;
    //较短刻度线长度
    private float shortScale;
    //线条颜色
    private int lineColor;
    //中线颜色
    private int midLineColor;
    //文字颜色
    private int textColor;
    //有回看部分的矩形框颜色
    private int recordRectColor;
    //文字大小
    private int textSize;
    private Paint linePaint;
    private Paint textPaint;

    //TODO 根据具体业务修改格式 回看列表   开始时间  结束时间
    private List<Pair<Long, Long>> recordList;
    private Paint recordRectPaint;
    private RectF recordRect;

    private Calendar calendar;
    private float mLastX;
    //最小化滑动距离
    private int minSlideScale;
    //是否滑动
    private boolean isSlide;
    //时间轴滑动监听
    private OnSlideListener listener;

    public interface OnSlideListener {
        void onSlide(long timeStamp);
    }

    public void setOnSlideListener(OnSlideListener listener) {
        this.listener = listener;
    }

    public ReplayTimeAxisView(Context context) {
        this(context, null);
    }

    public ReplayTimeAxisView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReplayTimeAxisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ReplayTimeAxisView, defStyleAttr, 0);
        longScale = a.getInteger(R.styleable.ReplayTimeAxisView_longScale, 40);
        shortScale = a.getInteger(R.styleable.ReplayTimeAxisView_shortScale, 30);
        lineColor = a.getColor(R.styleable.ReplayTimeAxisView_lineColor, getResources().getColor(R.color.gesture_select_blue));
        midLineColor = a.getColor(R.styleable.ReplayTimeAxisView_midLineColor, getResources().getColor(R.color.colorPrimary));
        textColor = a.getColor(R.styleable.ReplayTimeAxisView_textColor, getResources().getColor(R.color.gesture_select_blue));
        recordRectColor = a.getColor(R.styleable.ReplayTimeAxisView_recordRectColor, 0xFF000000);
        textSize = a.getInteger(R.styleable.ReplayTimeAxisView_textSize, 40);
        spacing = a.getInteger(R.styleable.ReplayTimeAxisView_spacing, DisplayUtil.dip2Pix(context, 1));
        a.recycle();

        init(context);
    }

    private void init(Context context) {
        recordList = new ArrayList<>();
        recordRect = new RectF();
        strokeWidth = DisplayUtil.dip2Pix(context, 1);
        calendar = Calendar.getInstance(Locale.getDefault());
        minSlideScale = ViewConfiguration.get(context)
                .getScaledTouchSlop();

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setColor(lineColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(strokeWidth);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(textSize);

        recordRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        recordRectPaint.setStyle(Paint.Style.FILL);
        recordRectPaint.setStrokeWidth(strokeWidth);
        recordRectPaint.setColor(recordRectColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getSize(200, widthMeasureSpec);
        height = getSize(50, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //一格120s
        int unitSeconds = 120;
        //左侧第一格时间
        int leftRemainTimeStamp = (int) currentMidTimeStamp
                % unitSeconds;
        //左侧第一个长度
        float midLeftFirstStart = 0;
        if (leftRemainTimeStamp == 0) {
            midLeftFirstStart = strokeWidth / 2f;
        } else {
            midLeftFirstStart = (leftRemainTimeStamp / (float) unitSeconds)
                    * spacing + strokeWidth;
        }
        //左侧总格数
        int leftCount = (int) Math.ceil((width / 2 - midLeftFirstStart)
                / (spacing + strokeWidth));
        //当前偏移（初始为0，最左侧）
        float currentOffset = width / 2 - midLeftFirstStart - leftCount
                * (spacing + strokeWidth);
        //当前时间（初始为0）
        long currentTimeStamp = currentMidTimeStamp - leftRemainTimeStamp
                - leftCount * unitSeconds;
        //最后一格时间
        long lastTimeStamp = (long) (currentTimeStamp + (width - currentOffset)
                * unitSeconds / (spacing + strokeWidth));

        //画最上最下两条线
        linePaint.setColor(lineColor);
        canvas.drawLine(0, 0, width, 0, linePaint);
        canvas.drawLine(0, height, width, height, linePaint);
        //画有回看部分
        if (recordList.size() > 0) {
            for (int i = 0; i < recordList.size(); i++) {
                Pair<Long, Long> pair = recordList.get(i);
                if (pair.first >= lastTimeStamp) {
                    break;
                }
                if (pair.first <= lastTimeStamp
                        && pair.second > currentTimeStamp) {
                    float startX = currentOffset
                            + (pair.first - currentTimeStamp)
                            * (spacing + strokeWidth)
                            / unitSeconds;
                    float endX = 0;
                    if (pair.second >= lastTimeStamp) {
                        endX = width;
                    } else {
                        endX = currentOffset + (pair.second - currentTimeStamp + 60)
                                * (spacing + strokeWidth)
                                / unitSeconds;
                    }
                    recordRect.set(startX, 0, endX, height);
                    canvas.drawRect(recordRect, recordRectPaint);
                }
            }
        }
        //画刻度
        linePaint.setColor(lineColor);
        while (currentOffset <= width) {
            calendar.setTimeInMillis(currentTimeStamp * 1000);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int mines = calendar.get(Calendar.MINUTE);
            int allMinsBy24Hours = (hours * 60 + mines) / 2;
            int remainderBy6 = allMinsBy24Hours % 6;
            int remainderBy5 = allMinsBy24Hours % 5;
            if (remainderBy6 == 0 && remainderBy5 == 0) {
                canvas.drawLine(currentOffset, 0, currentOffset,
                        height,
                        linePaint);
                String text = "" + (hours < 10 ? "0" + hours : hours) + ":"
                        + (mines < 10 ? "0" + mines : mines);
                Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
                int baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                canvas.drawText(text, currentOffset + 5, baseline, textPaint);
            } else if (remainderBy6 != 0 && remainderBy5 == 0) {
                canvas.drawLine(currentOffset, 0, currentOffset,
                        longScale, linePaint);
                canvas.drawLine(currentOffset, height
                        - longScale, currentOffset, height, linePaint);
            } else {
                canvas.drawLine(currentOffset, 0, currentOffset,
                        shortScale,
                        linePaint);
                canvas.drawLine(currentOffset, height - shortScale,
                        currentOffset, height, linePaint);
            }

            currentTimeStamp += unitSeconds;
            currentOffset += (spacing + strokeWidth);
        }
        //画中线
        linePaint.setColor(midLineColor);
        canvas.drawLine(width / 2, 0, width / 2, height, linePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = eventX;
                break;
            case MotionEvent.ACTION_UP:
                if (isSlide) {
                    isSlide = false;
                    if (null != listener) {
                        listener.onSlide(getMidTimeStamp());
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    float dx = eventX - mLastX;
                    if (!isSlide) {
                        isSlide = isSlide(dx);
                    }
                    if (isSlide) {
                        //滑动距离对应时长
                        int timeStampOffset = (int) (120 * ((dx) / (spacing + strokeWidth)));

                        currentMidTimeStamp = currentMidTimeStamp
                                - currentMidTimeStamp % 60;

                        long tempTimeStamp = (long) (60 * Math
                                .round(timeStampOffset
                                        / (float) 60));
                        tempTimeStamp = currentMidTimeStamp - tempTimeStamp;
                        if (tempTimeStamp != currentMidTimeStamp) {
                            currentMidTimeStamp = tempTimeStamp;
                            WLog.d("replayTest", "eventX:" + eventX + "  mLastX:" + mLastX);
                            invalidate();
                            mLastX = eventX;
                        }
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 初始化长宽
     */
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

    /**
     * 设置中线时间
     */
    public void setMidTimeStamp(long time) {
        currentMidTimeStamp = (long) (60 * Math.round(time / 1000 / (float) 60));
        invalidate();
    }

    /**
     * 获取中线时间
     */
    public long getMidTimeStamp() {
        return currentMidTimeStamp * 1000;
    }

    /**
     * 判断是否是滑动
     */
    private boolean isSlide(float dx) {
        return Math.abs(dx) >= minSlideScale;
    }

    /**
     * 设置有回看的时间段
     */
    public void setRecordList(List<Pair<Long, Long>> list) {
        //TODO 根据具体业务修改格式
        recordList.clear();
        if (list != null) {
            recordList.addAll(list);
        }
        invalidate();
    }
}
