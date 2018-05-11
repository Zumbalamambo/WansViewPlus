package com.ajcloud.wansview.application;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.support.tools.TimeLock;
import com.ajcloud.wansview.support.utils.DisplayUtil;

/**
 * Created by mamengchao on 2018/05/11.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private LayoutInflater inflater;
    protected FrameLayout tittleView;
    protected LinearLayout leftView, rightView;
    protected ImageView leftImg, middleImg, rightImg;
    protected TextView leftText, middleText, rightText;
    private TimeLock timeLock = new TimeLock();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_base, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tittleView = view.findViewById(R.id.base_fragment_tittle);
        leftView = view.findViewById(R.id.ll_left);
        rightView = view.findViewById(R.id.ll_right);
        leftImg = view.findViewById(R.id.left_img);
        middleImg = view.findViewById(R.id.middle_img);
        rightImg = view.findViewById(R.id.right_img);
        leftText = view.findViewById(R.id.left_text);
        middleText = view.findViewById(R.id.middle_text);
        rightText = view.findViewById(R.id.right_text);

        initParentView();
        initStatebar();
        initTittle();
        initView(rootView);
    }

    private void initParentView() {
        View view = null;
        if (layoutResID() != 0) {
            view = inflater.inflate(layoutResID(), null);
        }
        LinearLayout parentView = rootView.findViewById(R.id.ll_base_fragment);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (view != null) {
            parentView.addView(view, layoutParams);
        }
    }

    private void initStatebar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = 0;
            //获取status_bar_height资源的ID
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            } else {
                statusBarHeight = DisplayUtil.dip2Pix(MainApplication.getApplication(), 20);
            }
            tittleView.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    protected abstract int layoutResID();

    protected abstract void initTittle();

    protected abstract void initView(View rootView);

    /*middle*/
    protected void setTittle(String tittle) {
        middleText.setVisibility(View.VISIBLE);
        middleText.setText(tittle);
    }

    protected void setTittle(int resId) {
        middleText.setVisibility(View.VISIBLE);
        middleText.setText(resId);
    }

    protected void setTittleImg(int resId) {
        middleImg.setVisibility(View.VISIBLE);
        middleImg.setImageResource(resId);
    }

    /*left*/
    protected void setLeftText(String tittle) {
        leftText.setVisibility(View.VISIBLE);
        leftText.setText(tittle);
        leftText.setOnClickListener(this);
    }

    protected void setLeftText(int resId) {
        leftText.setVisibility(View.VISIBLE);
        leftText.setText(resId);
        leftText.setOnClickListener(this);
    }

    protected void setLeftImg(int resId) {
        leftImg.setVisibility(View.VISIBLE);
        leftImg.setImageResource(resId);
        leftImg.setOnClickListener(this);
    }

    /*right*/
    protected void setRightText(String tittle) {
        rightText.setVisibility(View.VISIBLE);
        rightText.setText(tittle);
        rightText.setOnClickListener(this);
    }

    protected void setRightText(int resId) {
        rightText.setVisibility(View.VISIBLE);
        rightText.setText(resId);
        rightText.setOnClickListener(this);
    }

    protected void setRightImg(int resId) {
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setImageResource(resId);
        rightImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (timeLock.isLock()) {
            return;
        }
        timeLock.lock();
    }
}
