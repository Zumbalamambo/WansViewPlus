package net.ajcloud.wansviewplus.support.customview.lockgesture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by mamengchao on 2018/05/15.
 * 手势解锁自定义view
 */
public class LockGestureView extends View {

    //边长
    private int length;
    //小圆点半径
    private float circleRadius = 0.1F;
    //选中圆环半径
    private float ringRadius = 0.3F;
    //小圆点颜色
    private int circleColor;
    //选中小圆点颜色
    private int selectedInnerColor;
    //选中圆环颜色
    private int selectedOuterColor;
    //验证失败小圆点颜色
    private int errorInnerColor;
    //验证失败圆环颜色
    private int errorOuterColor;
    private Paint circlePaint;
    private Paint ringPaint;

    enum MODE {
        NORMAL, VERIFY, ERROR
    }

    private MODE currentMode = MODE.NORMAL;

    public LockGestureView(Context context, int circleColor, int selectedInnerColor, int selectedOuterColor, int errorInnerColor, int errorOuterColor) {
        super(context);
        this.circleColor = circleColor;
        this.selectedInnerColor = selectedInnerColor;
        this.selectedOuterColor = selectedOuterColor;
        this.errorInnerColor = errorInnerColor;
        this.errorOuterColor = errorOuterColor;
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.FILL);

        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        ringPaint.setColor(selectedOuterColor);
        ringPaint.setStyle(Paint.Style.FILL);
        ringPaint.setStrokeWidth(1);
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
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        length = measureWidth > measureHeight ? measureHeight : measureWidth;
        setMeasuredDimension(length, length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (currentMode) {
            case NORMAL:
                circlePaint.setColor(circleColor);
                canvas.drawCircle(length / 2, length / 2, length * circleRadius, circlePaint);
                break;
            case VERIFY:
                circlePaint.setColor(selectedInnerColor);
                ringPaint.setColor(selectedOuterColor);
                canvas.drawCircle(length / 2, length / 2, length * circleRadius, circlePaint);
                canvas.drawCircle(length / 2, length / 2, length * ringRadius, ringPaint);
                break;
            case ERROR:
                circlePaint.setColor(errorInnerColor);
                ringPaint.setColor(errorOuterColor);
                canvas.drawCircle(length / 2, length / 2, length * circleRadius, circlePaint);
                canvas.drawCircle(length / 2, length / 2, length * ringRadius, ringPaint);
                break;
        }
    }

    public void setMode(MODE mode) {
        this.currentMode = mode;
        invalidate();
    }
}
