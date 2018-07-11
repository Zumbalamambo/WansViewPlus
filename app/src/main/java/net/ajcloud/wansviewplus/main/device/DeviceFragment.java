package net.ajcloud.wansviewplus.main.device;

import android.content.Intent;
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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.adapter.DeviceListAdapter;
import net.ajcloud.wansviewplus.main.device.addDevice.AddDeviceSelectActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.event.DeviceBindSuccessEvent;
import net.ajcloud.wansviewplus.support.event.DeviceDeleteEvent;
import net.ajcloud.wansviewplus.support.event.DeviceRefreshEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/15.
 * 设备页
 */
public class DeviceFragment extends Fragment implements View.OnClickListener {

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView deviceLayout;
    private LinearLayout noDeviceLayout;
    private ImageView addDeviceImageView;
    private Button addDeviceButton;
    private RecyclerView deviceListRecycleView;
    private DeviceListAdapter deviceListAdapter;
    private DeviceApiUnit deviceApiUnit;
    private boolean isfirst = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        toolbar.setOverflowIcon(null);
        toolbarLayout.setTitle("Camera");
        toolbarLayout.setExpandedTitleColor(Color.BLACK);
        toolbarLayout.setExpandedTitleGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        toolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);

        deviceLayout = view.findViewById(R.id.ll_device_list);
        noDeviceLayout = view.findViewById(R.id.ll_device_none);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        addDeviceImageView = view.findViewById(R.id.iv_add_device);
        addDeviceButton = view.findViewById(R.id.btn_add_device);
        deviceListRecycleView = view.findViewById(R.id.rv_device_list);
        deviceListRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        deviceListAdapter = new DeviceListAdapter(getActivity());
        deviceListRecycleView.setAdapter(deviceListAdapter);
        deviceListRecycleView.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator) deviceListRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        Animation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(200);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation, 0.5F);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        deviceListRecycleView.setLayoutAnimation(layoutAnimationController);

        initData();
        initListener();
    }

    private void initData() {
        deviceApiUnit = new DeviceApiUnit(getActivity());
        getDeviceList();
    }

    private void initListener() {
        addDeviceImageView.setOnClickListener(this);
        addDeviceButton.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeviceList();
            }
        });
    }

    private void getDeviceList() {
        refreshLayout.setRefreshing(true);
        deviceApiUnit.getDeviceList(new OkgoCommonListener<List<Camera>>() {
            @Override
            public void onSuccess(List<Camera> bean) {
                refreshLayout.setRefreshing(false);
                List<Camera> devices = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
                if (devices.size() == 0) {
                    deviceLayout.setVisibility(View.GONE);
                    noDeviceLayout.setVisibility(View.VISIBLE);
                } else {
                    deviceLayout.setVisibility(View.VISIBLE);
                    noDeviceLayout.setVisibility(View.GONE);
                    deviceListAdapter.setData(devices);
                    if (isfirst) {
                        isfirst = false;
                        pushSetting();
                    }

                }
            }

            @Override
            public void onFail(int code, String msg) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void pushSetting() {
        new UserApiUnit(getActivity()).pushSetting("upsert", null, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_device:
            case R.id.iv_add_device:
                startActivity(new Intent(getContext(), AddDeviceSelectActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && deviceListAdapter != null) {
            List<Camera> list = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
            deviceListAdapter.setData(list);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceRefresh(DeviceRefreshEvent event) {
        if (!TextUtils.isEmpty(event.deviceId)) {
            Camera camera = MainApplication.getApplication().getDeviceCache().get(event.deviceId);
            deviceListAdapter.update(camera);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceDelete(DeviceDeleteEvent event) {
        if (!TextUtils.isEmpty(event.deviceId)) {
            deviceListAdapter.remove(event.deviceId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceBindSuccess(DeviceBindSuccessEvent event) {
        if (!TextUtils.isEmpty(event.deviceId)) {
            Camera camera = MainApplication.getApplication().getDeviceCache().get(event.deviceId);
            deviceListAdapter.add(camera);
        }
    }
}
