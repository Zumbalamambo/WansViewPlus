package net.ajcloud.wansviewplus.main.alert;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.alert.adapter.AlertAdapter;
import net.ajcloud.wansviewplus.support.core.api.AlertApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;

import java.util.List;

/**
 * Created by mamengchao on 2018/05/15.
 * 消息页
 */
public class AlertFragment extends Fragment implements View.OnClickListener {

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView alertLayout;
    private LinearLayout noAlertLayout;
    private RecyclerView alertsRecycleView;
    private AlertAdapter alertAdapter;
    private AlertApiUnit alertApiUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        toolbar.setOverflowIcon(null);
        toolbarLayout.setTitle("Inbox");
        toolbarLayout.setExpandedTitleColor(Color.BLACK);
        toolbarLayout.setExpandedTitleGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        toolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);

        alertLayout = view.findViewById(R.id.ll_alert_list);
        noAlertLayout = view.findViewById(R.id.ll_alert_none);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        alertsRecycleView = view.findViewById(R.id.rv_message_list);
        alertsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        alertAdapter = new AlertAdapter(getActivity());
        alertsRecycleView.setAdapter(alertAdapter);
        alertsRecycleView.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator) alertsRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        Animation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(200);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation, 0.5F);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        alertsRecycleView.setLayoutAnimation(layoutAnimationController);

        initData();
        initListener();
    }

    private void initData() {
        alertApiUnit = new AlertApiUnit(getActivity());
        getAlertList();
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAlertList();
            }
        });
    }

    private void getAlertList() {
        refreshLayout.setRefreshing(true);
        alertApiUnit.getAlarmsSummary(new OkgoCommonListener<List<AlarmBean>>() {
            @Override
            public void onSuccess(List<AlarmBean> bean) {
                refreshLayout.setRefreshing(false);
                if (bean != null && bean.size() > 0) {
                    noAlertLayout.setVisibility(View.GONE);
                    alertLayout.setVisibility(View.VISIBLE);
                    alertAdapter.setData(bean);
                } else {
                    noAlertLayout.setVisibility(View.VISIBLE);
                    alertLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int code, String msg) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }
}
