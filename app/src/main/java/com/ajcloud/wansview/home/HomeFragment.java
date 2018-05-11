package com.ajcloud.wansview.home;

import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.application.BaseFragment;

public class HomeFragment extends BaseFragment {
    @Override
    public int layoutResID() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initTittle() {
        setTittle("wanview");
        setLeftImg(R.drawable.ic_all);
    }

    @Override
    protected void initView(View rootView) {

    }

}
