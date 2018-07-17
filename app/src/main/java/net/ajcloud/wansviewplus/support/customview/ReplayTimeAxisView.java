package net.ajcloud.wansviewplus.support.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by mamengchao on 2018/05/16.
 * 回看时间轴控件
 */
public class ReplayTimeAxisView extends View {

    private String[] dates = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
    private int[] scaleMultiple = {1, 3, 4, 6};
    private int currentMultiple = 6;
    private Context context;
    //每一格时间
    private int unitSeconds;
    private int width;
    //总高度
    private int height;
    //顶部月份高度
    private int dateHeight;
    //时间轴高度
    private int timelineHeight;
    //当前中间刻度时间点
    private long currentMidTimeStamp;
    //画笔宽度
    private int strokeWidth;
    //默认刻度间间隔
    private float spacing;
    //较长刻度线长度
    private float longScale;
    //较短刻度线长度
    private float shortScale;
    //最长刻度线长度
    private float midScale;
    //下载区间圆形按钮半径
    private float circleRadius;
    //线条颜色
    private int lineColor;
    //中线颜色
    private int midLineColor;
    //文字颜色
    private int textColor;
    //有回看部分的矩形框颜色
    private int recordRectColor;
    //选择下载部分的矩形框颜色
    private int selectedRectColor;
    //文字大小
    private float textSize;
    //选择的开始时间，结束时间
    private long startTime;
    private long endTime;
    private Mode currentMode;
    //最小化滑动距离
    private int minSlideScale;
    //是否滑动
    private boolean isSlide;
    //是否选中选中框边界
    boolean isSelectedLeft, isSelectedRight;
    private Calendar calendar;
    private RectF recordRect;
    private RectF selectedRect;
    private Paint linePaint;
    private Paint textPaint;
    private Paint recordRectPaint;
    private Paint shadowPaint;
    //TODO 根据具体业务修改格式 回看列表   开始时间  结束时间
    private List<Pair<Long, Long>> recordList;
    //时间轴滑动监听
    private OnSlideListener listener;

    public interface OnSlideListener {
        void onSlide(long startTime, float position);

        void onSelected(long startTime, long endTime);
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
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ReplayTimeAxisView, defStyleAttr, 0);
        longScale = a.getDimension(R.styleable.ReplayTimeAxisView_longScale, DisplayUtil.dip2Pix(context, 32));
        shortScale = a.getDimension(R.styleable.ReplayTimeAxisView_shortScale, DisplayUtil.dip2Pix(context, 16));
        midScale = a.getDimension(R.styleable.ReplayTimeAxisView_midScale, DisplayUtil.dip2Pix(context, 40));
        textSize = a.getDimension(R.styleable.ReplayTimeAxisView_textSize, 40);
        lineColor = a.getColor(R.styleable.ReplayTimeAxisView_lineColor, getResources().getColor(R.color.colorPrimary));
        midLineColor = a.getColor(R.styleable.ReplayTimeAxisView_midLineColor, getResources().getColor(R.color.colorPrimary));
        textColor = a.getColor(R.styleable.ReplayTimeAxisView_textColor, getResources().getColor(R.color.colorPrimary));
        recordRectColor = a.getColor(R.styleable.ReplayTimeAxisView_recordRectColor, 0xFF000000);
        selectedRectColor = a.getColor(R.styleable.ReplayTimeAxisView_selectedRectColor, 0x55FFFFFF);
        spacing = a.getDimension(R.styleable.ReplayTimeAxisView_spacing, DisplayUtil.dip2Pix(context, 1));
        circleRadius = a.getDimension(R.styleable.ReplayTimeAxisView_circleRadius, DisplayUtil.dip2Pix(context, 6));
        a.recycle();

        init(context);
    }

    private void init(Context context) {
        recordList = new ArrayList<>();
        recordRect = new RectF();
        selectedRect = new RectF();
        strokeWidth = DisplayUtil.dip2Pix(context, 1);
        calendar = Calendar.getInstance(Locale.getDefault());
        minSlideScale = 2;

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
        textPaint.setTextAlign(Paint.Align.CENTER);

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(getResources().getColor(R.color.white));
        shadowPaint.setStrokeWidth(strokeWidth);

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
        dateHeight = height * 5 / 17;
        timelineHeight = height * 8 / 17;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //每一格时间
        unitSeconds = currentMultiple * 60;
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

        //画阴影
        shadowPaint.setShader(new LinearGradient(0, dateHeight, 0, dateHeight + shortScale, getResources().getColor(R.color.gray_first_translate), getResources().getColor(R.color.white), Shader.TileMode.CLAMP));
        canvas.drawRect(0, dateHeight, width, dateHeight + timelineHeight, shadowPaint);
        //画有回看部分
        recordRectPaint.setColor(recordRectColor);
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
                    recordRect.set(startX, dateHeight, endX, dateHeight + timelineHeight);
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
            int remainderBy6 = mines % 6;
            int remainderBy5 = mines % 5;
            int remainderBy2 = mines % 2;
            if (currentMultiple == 4 || currentMultiple == 6) {
                if (remainderBy6 == 0 && remainderBy5 == 0) {
                    canvas.drawLine(currentOffset, dateHeight, currentOffset,
                            dateHeight + midScale,
                            linePaint);
                    String text = "" + (hours < 10 ? "0" + hours : hours) + ":"
                            + (mines < 10 ? "0" + mines : mines);
                    Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
                    int baseline = (height - dateHeight - timelineHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top + dateHeight + timelineHeight;
                    canvas.drawText(text, currentOffset, baseline, textPaint);
                } else if (remainderBy6 != 0 && remainderBy5 == 0) {
                    canvas.drawLine(currentOffset, dateHeight, currentOffset,
                            dateHeight + longScale, linePaint);
                    String text = "" + (hours < 10 ? "0" + hours : hours) + ":"
                            + (mines < 10 ? "0" + mines : mines);
                    Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
                    int baseline = (height - dateHeight - timelineHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top + dateHeight + timelineHeight;
                    canvas.drawText(text, currentOffset, baseline, textPaint);
                } else {
                    canvas.drawLine(currentOffset, dateHeight, currentOffset,
                            dateHeight + shortScale,
                            linePaint);
                }
            } else if (currentMultiple == 3 || currentMultiple == 1) {
                if (remainderBy2 == 0 && remainderBy5 == 0) {
                    canvas.drawLine(currentOffset, dateHeight, currentOffset,
                            dateHeight + midScale,
                            linePaint);
                    String text = "" + (hours < 10 ? "0" + hours : hours) + ":"
                            + (mines < 10 ? "0" + mines : mines);
                    Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
                    int baseline = (height - dateHeight - timelineHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top + dateHeight + timelineHeight;
                    canvas.drawText(text, currentOffset, baseline, textPaint);
                } else if (remainderBy2 != 0 && remainderBy5 == 0) {
                    canvas.drawLine(currentOffset, dateHeight, currentOffset,
                            dateHeight + longScale, linePaint);
                    String text = "" + (hours < 10 ? "0" + hours : hours) + ":"
                            + (mines < 10 ? "0" + mines : mines);
                    Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
                    int baseline = (height - dateHeight - timelineHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top + dateHeight + timelineHeight;
                    canvas.drawText(text, currentOffset, baseline, textPaint);
                } else {
                    canvas.drawLine(currentOffset, dateHeight, currentOffset,
                            dateHeight + shortScale,
                            linePaint);
                }
            }


            currentTimeStamp += unitSeconds;
            currentOffset += (spacing + strokeWidth);
        }
        //画日期
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (dateHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(getMidDateText(), width / 2, baseline, textPaint);

        if (currentMode == Mode.DownLoad) {
            //画边线
            linePaint.setColor(midLineColor);
            canvas.drawLine(getCurrentXofTime(startTime), dateHeight, getCurrentXofTime(startTime), dateHeight + timelineHeight, linePaint);
            canvas.drawCircle(getCurrentXofTime(startTime), dateHeight, circleRadius, linePaint);
            canvas.drawLine(getCurrentXofTime(endTime), dateHeight, getCurrentXofTime(endTime), dateHeight + timelineHeight, linePaint);
            canvas.drawCircle(getCurrentXofTime(endTime), dateHeight, circleRadius, linePaint);
            //画选择框
            recordRectPaint.setColor(selectedRectColor);
            selectedRect.set(getCurrentXofTime(startTime), dateHeight, getCurrentXofTime(endTime), dateHeight + timelineHeight);
            canvas.drawRect(selectedRect, recordRectPaint);
        }
        //画中线
        linePaint.setColor(midLineColor);
        canvas.drawLine(width / 2, dateHeight, width / 2, dateHeight + timelineHeight, linePaint);

        float rectWidth = DisplayUtil.dip2Pix(context, 8);
        float rectHeight = DisplayUtil.dip2Pix(context, 12);
        //画中线箭头
        canvas.drawRect(width / 2 - rectWidth / 2, dateHeight, width / 2 + rectWidth / 2, dateHeight + rectHeight * 2 / 3, linePaint);
        Path path = new Path();
        path.moveTo(width / 2 - rectWidth / 2, dateHeight + rectHeight * 2 / 3);// 此点为多边形的起点
        path.lineTo(width / 2 + rectWidth / 2, dateHeight + rectHeight * 2 / 3);
        path.lineTo(width / 2, dateHeight + rectHeight);
        path.close(); // 使这些点
        canvas.drawPath(path, linePaint);

    }

    private float mLastX;
    int state = 0;//防止手指移动反复更新 只在手指按下和滑动时 启动
    int mode = 0;//0:单指滑动  1：双指缩放
    long downTime = 0;//用于判定是否双指
    private float startDistance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                mLastX = eventX;
                mode = 0;
                state = 1;
                isSelectedLeft = false;
                isSelectedRight = false;
                if (Math.abs(eventX - getCurrentXofTime(startTime)) < circleRadius * 2) {
                    isSelectedLeft = true;
                } else if (Math.abs(eventX - getCurrentXofTime(endTime)) < circleRadius * 2) {
                    isSelectedRight = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == 0)// 是一个手指拖动
                {
                    if (state == 1) {
                        if (isSlide) {
                            isSlide = false;
                            if (null != listener) {
                                if (currentMode == Mode.DownLoad) {
                                    if (isSelectedLeft || isSelectedRight) {
                                        listener.onSelected(getStartTime(), getEndTime());
                                    }
                                } else {
                                    long startTime = getCurrentM3u8Start(getMidTimeStamp() / 1000);
                                    float rate = getCurrentM3u8Rate(getMidTimeStamp() / 1000);
                                    listener.onSlide(startTime, rate);
                                }

                            }
                        }
                    }
                } else if (mode == 1) {
                    float endDistance = distance(event);
                    if (endDistance - startDistance > 50) {
                        ToastUtil.single("放大");
                        if (currentMultiple == 1) {

                        } else if (currentMultiple == 3) {
                            currentMultiple = scaleMultiple[0];
                        } else if (currentMultiple == 4) {
                            currentMultiple = scaleMultiple[1];
                        } else if (currentMultiple == 6) {
                            currentMultiple = scaleMultiple[2];
                        }
                    } else if (startDistance - endDistance > 50) {
                        ToastUtil.single("缩小");
                        if (currentMultiple == 1) {
                            currentMultiple = scaleMultiple[1];
                        } else if (currentMultiple == 3) {
                            currentMultiple = scaleMultiple[2];
                        } else if (currentMultiple == 4) {
                            currentMultiple = scaleMultiple[3];
                        } else if (currentMultiple == 6) {

                        }
                    }
                    invalidate();
                }
                mode = 0;
                state = 0;

                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == 0)// 是一个手指拖动
                {
                    if (state == 1) {
                        float dx = eventX - mLastX;
                        if (!isSlide) {
                            isSlide = isSlide(dx);
                        }
                        if (isSlide) {
                            if (currentMode == Mode.DownLoad) {
                                if (isSelectedLeft) {
                                    startTime = (endTime - startTime - (int) (dx * unitSeconds / (spacing + strokeWidth))) < unitSeconds ?
                                            startTime : (startTime + (int) (dx * unitSeconds / (spacing + strokeWidth)));
                                    mLastX = eventX;
                                    invalidate();
                                } else if (isSelectedRight) {
                                    endTime = (endTime - startTime + (int) (dx * unitSeconds / (spacing + strokeWidth))) < unitSeconds ?
                                            endTime : (endTime + (int) (dx * unitSeconds / (spacing + strokeWidth)));
                                    mLastX = eventX;
                                    invalidate();
                                } else {
                                    //滑动距离对应时长
                                    long timeStampOffset = (long) (unitSeconds * ((dx) / (spacing + strokeWidth)));
                                    timeStampOffset = (currentMidTimeStamp - timeStampOffset);
                                    if (timeStampOffset != currentMidTimeStamp) {
                                        currentMidTimeStamp = timeStampOffset;
                                        invalidate();
                                        mLastX = eventX;
                                    }
                                }
                            } else {
                                //滑动距离对应时长
                                long timeStampOffset = (long) (unitSeconds * ((dx) / (spacing + strokeWidth)));
                                timeStampOffset = (currentMidTimeStamp - timeStampOffset);
                                if (timeStampOffset != currentMidTimeStamp) {
                                    currentMidTimeStamp = timeStampOffset;
                                    invalidate();
                                    mLastX = eventX;
                                }
                            }
                        }
                    }
                } else if (mode == 1) // 两个手指滑动
                {

                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 第二个手指按下事件
                if (currentMode != Mode.DownLoad) {
                    long minus = System.currentTimeMillis() - downTime;
                    if (minus < 50) {
                        mode = 1;
                        startDistance = distance(event);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);

        return (float) Math.sqrt(dx * dx + dy * dy);
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
        if (!isSlide) {
            currentMidTimeStamp = (int) (time / 1000);
            invalidate();
        }
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

    public enum Mode {
        Play,
        DownLoad
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    /**
     * 改变模式，播放or选择下载
     */
    public void setCurrentMode(Mode currentMode) {
        this.currentMode = currentMode;
        if (currentMode == Mode.DownLoad) {
            startTime = currentMidTimeStamp - unitSeconds / 2;
            endTime = currentMidTimeStamp + unitSeconds / 2;
            listener.onSelected(getStartTime(), getEndTime());
        }
        invalidate();
    }

    public long getStartTime() {
        return startTime * 1000;
    }

    public long getEndTime() {
        return endTime * 1000;
    }

    /**
     * 获取当前坐标对应时间
     */
    private float getCurrentXofTime(long time) {
        return width / 2 - (currentMidTimeStamp - time) * (spacing + strokeWidth) / unitSeconds;
    }

    /**
     * 获取顶部日期文字
     */
    private String getMidDateText() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(currentMidTimeStamp * 1000);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayString = String.valueOf(day);
        if (day < 10) {
            dayString = "0" + day;
        }
        return dates[month] + "." + dayString;
    }

    /**
     * 获取当前时间对应的m3u8开始时间
     */
    private long getCurrentM3u8Start(long time) {
        if (recordList == null || recordList.size() == 0) {
            return 0;
        } else {
            for (int i = 0; i < recordList.size(); i++) {
                Pair<Long, Long> pairTime = recordList.get(i);
                if (pairTime.first <= time && pairTime.second >= time) {
                    return pairTime.first;
                }
            }
            return 0;
        }
    }

    /**
     * 获取当前时间所在的m3u8片段的比例
     */
    private float getCurrentM3u8Rate(long time) {
        if (recordList == null || recordList.size() == 0) {
            return 0;
        } else {
            for (int i = 0; i < recordList.size(); i++) {
                Pair<Long, Long> pairTime = recordList.get(i);
                if (pairTime.first <= time && pairTime.second >= time) {
                    return (float) (time - pairTime.first) / (pairTime.second - pairTime.first);
                }
            }
            return 0;
        }
    }
}
