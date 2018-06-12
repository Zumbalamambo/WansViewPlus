package net.ajcloud.wansviewplus.main.device.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.ajcloud.wansviewplus.R;

/**
 * Created by mamengchao on 2018/06/04.
 * Function:时区裂变间隔线
 */
public class TimezoneDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private Paint mPaint;
    private int dividerHeight;
    private int margin;

    public TimezoneDecoration(Context context) {
        this.context = context;
        dividerHeight = context.getResources().getDimensionPixelSize(R.dimen.default_divider_Height);
        margin = context.getResources().getDimensionPixelSize(R.dimen.default_margin_left);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.gray_line));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft() + margin;
        int right = parent.getWidth() - parent.getPaddingRight() - margin;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View itemView = parent.getChildAt(i);
            int top = itemView.getBottom();
            int bottom = itemView.getBottom() + dividerHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
