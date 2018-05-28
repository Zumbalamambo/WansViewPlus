package net.ajcloud.wansview.support.customview.camera;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.ajcloud.wansview.entity.camera.PtzCtrlType;

/**
 * Created
 */

public class NavigationPopupWindow implements View.OnClickListener {

    public interface NavigationListener {
        boolean mobileTrackerSwitch(boolean state);

        boolean navigation(int type);
    }

    private Context context;
    private NavigationListener listener;
    private PopupWindow popupWindow;
    private View view;
    private ToggleButton mobileTracker;
    private TextView upDownNavigation;
    private TextView leftRightNavigation;
    private TextView stopNavigation;
    private ImageView dismissImg;
    private int height;
    private boolean state;

    public NavigationPopupWindow(Context context, int height, boolean state, NavigationListener listener) {
        this.context = context;
        this.listener = listener;
        this.height = height;
        this.state = state;
        initView();
    }

    public void initView() {
        view = View.inflate(context, net.ajcloud.wansview.R.layout.popup_window_navigation, null);
        mobileTracker = (ToggleButton) view.findViewById(net.ajcloud.wansview.R.id.mobile_tracker);
        mobileTracker.setChecked(state);
        mobileTracker.setOnClickListener(this);
        upDownNavigation = (TextView) view.findViewById(net.ajcloud.wansview.R.id.up_down_navigation);
        leftRightNavigation = (TextView) view.findViewById(net.ajcloud.wansview.R.id.left_right_navigation);
        stopNavigation = (TextView) view.findViewById(net.ajcloud.wansview.R.id.stop_navigation);
        dismissImg = (ImageView) view.findViewById(net.ajcloud.wansview.R.id.dismiss_img);
        upDownNavigation.setOnClickListener(this);
        leftRightNavigation.setOnClickListener(this);
        stopNavigation.setOnClickListener(this);
        dismissImg.setOnClickListener(this);

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, height);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
    }

    public void show(View anchorView, int offsetY) {
        if (popupWindow != null) {
            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, 0, offsetY);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.mobile_tracker:
                if (!listener.mobileTrackerSwitch(mobileTracker.isChecked())) {
                    popupWindow.dismiss();
                }
                break;
            case net.ajcloud.wansview.R.id.up_down_navigation:
                upDownNavigation.setSelected(true);
                leftRightNavigation.setSelected(false);
                stopNavigation.setSelected(false);
                if (mobileTracker.isChecked()) {
                    mobileTracker.setChecked(false);
                }
                if (!listener.navigation(PtzCtrlType.CRUISE_VERTICAL)) {
                    popupWindow.dismiss();
                }
                break;
            case net.ajcloud.wansview.R.id.left_right_navigation:
                upDownNavigation.setSelected(false);
                leftRightNavigation.setSelected(true);
                stopNavigation.setSelected(false);
                if (mobileTracker.isChecked()) {
                    mobileTracker.setChecked(false);
                }
                if (!listener.navigation(PtzCtrlType.CRUISE_HORIZONTAL)) {
                    popupWindow.dismiss();
                }
                break;
            case net.ajcloud.wansview.R.id.stop_navigation:
                upDownNavigation.setSelected(false);
                leftRightNavigation.setSelected(false);
                stopNavigation.setSelected(true);
                if (mobileTracker.isChecked()) {
                    mobileTracker.setChecked(false);
                }
                if (!listener.navigation(PtzCtrlType.STOP)) {
                    popupWindow.dismiss();
                }
                break;
            case net.ajcloud.wansview.R.id.dismiss_img:
                popupWindow.dismiss();
                break;
        }
    }
}
