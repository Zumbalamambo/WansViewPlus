package net.ajcloud.wansviewplus.support.customview.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import net.ajcloud.wansviewplus.R;

public class CirlView extends View {

    private final static String TAG = CirlView.class.getSimpleName();

    private Paint mTransparentPaint;

    int height;
    int width;

    int centerX;
    int centerY;
    int innerRadius;
    
    public CirlView(Context context) {
        super(context);
        initPaint();
    }

    private void initPaint() {
        mTransparentPaint = new Paint();
        mTransparentPaint.setColor(getResources().getColor(R.color.white));
        mTransparentPaint.setStyle(Paint.Style.FILL);
        mTransparentPaint.setAntiAlias(true);
    }

    public CirlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CirlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(0, widthMeasureSpec) / 3;
        height = width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        centerX = width / 2;
        centerY = width / 2;
        innerRadius = width / 2;
        mTransparentPaint.setStrokeWidth(0);
        canvas.drawCircle(centerX, centerY, innerRadius, mTransparentPaint);
    }
}
