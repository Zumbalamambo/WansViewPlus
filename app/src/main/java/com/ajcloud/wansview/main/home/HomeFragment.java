package com.ajcloud.wansview.main.home;

import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseFragment;
import com.ajcloud.wansview.support.utils.dialog.CommonDialog;
import com.ajcloud.wansview.support.utils.dialog.ProgressDialogManager;

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
        rootView.findViewById(R.id.get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogManager.getDialogManager().showDialog("test", getActivity(), 10000);
            }
        });
    }
}
