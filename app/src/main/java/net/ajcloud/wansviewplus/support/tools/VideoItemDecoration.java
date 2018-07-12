package net.ajcloud.wansviewplus.support.tools;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import net.ajcloud.wansviewplus.support.utils.DisplayUtil;

/**
 * Created by mamengchao on 2018/07/12.
 * Function:
 */
public class VideoItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;

    public VideoItemDecoration(Context context) {
        this.context = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        int spanCount = 2;
        int childCount = parent.getAdapter().getItemCount();
        if (itemPosition % 2 == 0) {
            if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
            {
                outRect.set(0, 0, DisplayUtil.dip2Pix(context, 20), 0);
            } else {
                outRect.set(0, 0, DisplayUtil.dip2Pix(context, 20),
                        DisplayUtil.dip2Pix(context, 24));
            }
        } else {
            if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
            {
                outRect.set(DisplayUtil.dip2Pix(context, 20), 0, 0, 0);
            } else {
                outRect.set(DisplayUtil.dip2Pix(context, 20), 0, 0,
                        DisplayUtil.dip2Pix(context, 24));
            }
        }
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
