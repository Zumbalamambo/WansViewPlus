package net.ajcloud.wansviewplus.main.cloud;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.WVFragment;

/**
 * Created by mamengchao on 2018/07/02.
 * Function:
 */
public class CloudFragment extends WVFragment implements View.OnClickListener {

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private LinearLayout ll_cloud_none;
    private TextView tv_cloud_introduction;
    private TextView btn_cloud_buy;
    private NestedScrollView ll_cloud_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        toolbar.setOverflowIcon(null);
        toolbarLayout.setTitle(getResources().getString(R.string.cloud));
        toolbarLayout.setExpandedTitleColor(Color.BLACK);
        toolbarLayout.setExpandedTitleGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        toolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);

        ll_cloud_none = view.findViewById(R.id.ll_cloud_none);
        tv_cloud_introduction = view.findViewById(R.id.tv_cloud_introduction);
        btn_cloud_buy = view.findViewById(R.id.btn_cloud_buy);
        ll_cloud_list = view.findViewById(R.id.ll_cloud_list);

        tv_cloud_introduction.setOnClickListener(this);
        btn_cloud_buy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cloud_introduction:
                startActivity(new Intent(mActivity, AboutCloudActivity.class));
                break;
            case R.id.btn_cloud_buy:
                break;
        }
    }

}