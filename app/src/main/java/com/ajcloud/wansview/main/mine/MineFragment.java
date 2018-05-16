package com.ajcloud.wansview.main.mine;

import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseFragment;
import com.ajcloud.wansview.support.customview.ReplayTimeAxisView;

/**
 * Created by mamengchao on 2018/05/15.
 * 我的
 */
public class MineFragment extends BaseFragment {

    @Override
    protected int layoutResID() {
        return R.layout.fragment_mine;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initTittle() {

    }

    @Override
    protected void initView(View rootView) {
        ReplayTimeAxisView test = rootView.findViewById(R.id.test);
        long initTime = System.currentTimeMillis() / 1000 - 60 * 60;
        initTime = (long) (60 * Math.round(initTime / (float) 60));
        test.setMidTimeStamp(initTime);
    }
}
