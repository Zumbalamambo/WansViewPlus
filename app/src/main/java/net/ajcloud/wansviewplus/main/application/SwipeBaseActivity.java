package net.ajcloud.wansviewplus.main.application;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.customview.SwipeBackLayout;

/**
 * Created 可向右边滑动关闭的窗口
 * 需要修改主题的背景颜色未透明
 * 此处代码只是作为记录使用
 */

public abstract class SwipeBaseActivity extends BaseActivity {

    protected final String TAG = this.getClass().getSimpleName();
    @Override
    public FrameLayout getRootView() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0x000000000));
        FrameLayout rootView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base, null, false);
        SwipeBackLayout swipeBackLayout = rootView.findViewById(R.id.swipeBackLayout);
        View ivShadow = rootView.findViewById(R.id.iv_shadow);
        swipeBackLayout.addView(super.getRootView());
        swipeBackLayout.setOnScroll((fs) -> ivShadow.setAlpha(1 - fs));
        return rootView;
    }
}
