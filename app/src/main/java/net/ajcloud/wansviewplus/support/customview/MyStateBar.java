package net.ajcloud.wansviewplus.support.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/2/4.
 */
public class MyStateBar extends View {
    public MyStateBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyStateBar(Context context) {
        super(context);
    }

    public MyStateBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyStateBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int tmpheightMeasureSpec = MeasureSpec.makeMeasureSpec(getStatusBarHeight(getContext()),
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, tmpheightMeasureSpec);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
}
