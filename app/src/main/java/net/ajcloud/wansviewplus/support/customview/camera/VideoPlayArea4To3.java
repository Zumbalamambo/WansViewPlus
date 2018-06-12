package net.ajcloud.wansviewplus.support.customview.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.ajcloud.wansviewplus.support.utils.SizeUtil;

/**
 * Created by zte on 2016/2/16.
 */
public class VideoPlayArea4To3 extends FrameLayout {


    public VideoPlayArea4To3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayArea4To3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VideoPlayArea4To3(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(widthMeasureSpec) * 3 / 4;
        int tmpheightMeasureSpec = MeasureSpec.makeMeasureSpec(height > SizeUtil.getScreenHeigth(this.getContext()) ?
                        SizeUtil.getScreenHeigth(this.getContext()) : height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, tmpheightMeasureSpec);
    }
}
