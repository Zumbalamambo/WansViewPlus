package net.ajcloud.wansviewplus.support.customview.lockgesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/14.
 * Function:    手势密码缩略图view
 */
public class GestureIndicatorView extends View {

    private Context context;
    //边长
    private int length;
    //小圆点半径比例
    private float circleRadius;
    //小圆点颜色
    private int circleColor;
    //选中小圆点颜色
    private int selectedColor;
    //小圆点列表
    private List<Point> points;
    private Paint mPaint;

    public GestureIndicatorView(Context context) {
        this(context, null);
    }

    public GestureIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GestureIndicatorView, defStyleAttr, 0);
        circleColor = a.getColor(R.styleable.GestureIndicatorView_indicatorCircleColor, getResources().getColor(R.color.gray_line));
        selectedColor = a.getColor(R.styleable.GestureIndicatorView_selectedColor, getResources().getColor(R.color.colorPrimary));
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(circleColor);
        mPaint.setStyle(Paint.Style.FILL);
        points = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Point point = new Point(false);
            points.add(point);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(DisplayUtil.dip2Pix(context, 64), widthMeasureSpec);
        int height = getSize(DisplayUtil.dip2Pix(context, 64), heightMeasureSpec);
        length = Math.min(width, height);
        setMeasuredDimension(length, length);
        circleRadius = length / 12;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cenX = 2 * circleRadius;
        float cenY = 2 * circleRadius;
        for (int i = 0; i < 9; i++) {
            //第一行
            if (i < 3) {
                mPaint.setColor(points.get(i).isFocus()?selectedColor:circleColor);
                canvas.drawCircle(cenX + i * 4 * circleRadius, cenY, circleRadius, mPaint);
                continue;
            }
            cenY = 6 * circleRadius;
            //第二行
            if (i < 6) {
                mPaint.setColor(points.get(i).isFocus()?selectedColor:circleColor);
                canvas.drawCircle(cenX + (i - 3) * 4 * circleRadius, cenY, circleRadius, mPaint);
                continue;
            }
            cenY = 10 * circleRadius;
            mPaint.setColor(points.get(i).isFocus()?selectedColor:circleColor);
            canvas.drawCircle(cenX + (i - 6) * 4 * circleRadius, cenY, circleRadius, mPaint);
            //第三行
        }
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

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
        invalidate();
    }

    public static class Point {
        boolean focus;

        public Point(boolean focus) {
            this.focus = focus;
        }

        public boolean isFocus() {
            return focus;
        }

        public void setFocus(boolean focus) {
            this.focus = focus;
        }
    }
}
