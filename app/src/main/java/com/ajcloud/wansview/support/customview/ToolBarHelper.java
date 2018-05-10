package com.ajcloud.wansview.support.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.application.MainApplication;
import com.ajcloud.wansview.support.utils.DisplayUtil;

/**
 * Created by mamengchao on 2018/05/10.
 */
public class ToolBarHelper {

    private Context mContext;
    private FrameLayout mContentView;
    private MyToolbar mToolBar;
    /*用户定义的view*/
    private View mUserView;
    private View toolbarView;

    private LayoutInflater mInflater;

    private int toolbarHeight;
    private int statusBarHeight;

    public ToolBarHelper(Context context, int layoutId, int statusBarHeight) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        toolbarHeight = DisplayUtil.dip2Pix(MainApplication.getApplication(), 48);
        this.statusBarHeight = statusBarHeight;

        initContentView();
        initToolBar();
        initUserView(layoutId);
    }

    private void initContentView() {
        /*直接创建一个帧布局，作为视图容器的父容器*/
        mContentView = new FrameLayout(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
    }

    private void initToolBar() {
        toolbarView = mInflater.inflate(R.layout.tool_bar, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, toolbarHeight + statusBarHeight);
        mContentView.addView(toolbarView, params);
        mToolBar = toolbarView.findViewById(R.id.toolbar);
        mToolBar.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initUserView(int layoutId) {
        mUserView = mInflater.inflate(layoutId, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.topMargin = toolbarHeight + statusBarHeight;
        mContentView.addView(mUserView, 0, params);
    }

    public FrameLayout getContentView() {
        return mContentView;
    }

    /**
     * 获得头部之外的，用户交互布局
     */
    public View getmUserView() {
        return mUserView;
    }

    public MyToolbar getToolBar() {
        return mToolBar;
    }

    public void setToolBarVisible(boolean visible) {
        if (visible) {
            toolbarView.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mUserView.getLayoutParams();
            params.topMargin = toolbarHeight + statusBarHeight;
            mUserView.setLayoutParams(params);
        } else {
            toolbarView.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mUserView.getLayoutParams();
            params.topMargin = 0;
            mUserView.setLayoutParams(params);
        }
    }
}
