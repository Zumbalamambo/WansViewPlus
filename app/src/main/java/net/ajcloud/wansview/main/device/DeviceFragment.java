package net.ajcloud.wansview.main.device;

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
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.MainApplication;
import net.ajcloud.wansview.main.device.adapter.DeviceListAdapter;
import net.ajcloud.wansview.main.device.addDevice.AddDeviceSelectActivity;
import net.ajcloud.wansview.support.core.api.DeviceApiUnit;
import net.ajcloud.wansview.support.core.api.OkgoCommonListener;
import net.ajcloud.wansview.support.core.bean.DeviceListBean;
import net.ajcloud.wansview.support.core.device.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/15.
 * 设备页
 */
public class DeviceFragment extends Fragment implements View.OnClickListener {

    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView deviceLayout;
    private LinearLayout noDeviceLayout;
    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private ImageView addDeviceImageView;
    private Button addDeviceButton;
    private RecyclerView deviceListRecycleView;
    private DeviceListAdapter deviceListAdapter;
    private DeviceApiUnit deviceApiUnit;

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
        toolbarLayout.setTitle("Devices");
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

        initData();
        initListener();
    }

    private void initData() {
        refreshLayout.setRefreshing(true);
        deviceApiUnit = new DeviceApiUnit(getActivity());
//        List<Camera> list = new ArrayList<>();
//        Camera camera1 = new Camera();
//        camera1.setName("客厅");
//        Camera camera2 = new Camera();
//        camera2.setName("厨房");
//        Camera camera3 = new Camera();
//        camera3.setName("卧室");
//        Camera camera4 = new Camera();
//        camera4.setName("餐厅");
//        Camera camera5 = new Camera();
//        camera5.setName("书房");
//        Camera camera6 = new Camera();
//        camera6.setName("阳台");
//        list.add(camera1);
//        list.add(camera2);
//        list.add(camera3);
//        list.add(camera4);
//        list.add(camera5);
//        list.add(camera6);
//        deviceListAdapter.setData(list);
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
        deviceApiUnit.getDeviceList(new OkgoCommonListener<DeviceListBean>() {
            @Override
            public void onSuccess(DeviceListBean bean) {
                refreshLayout.setRefreshing(false);
                List<Camera> devices = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
                if (devices.size() == 0) {
                    deviceLayout.setVisibility(View.GONE);
                    noDeviceLayout.setVisibility(View.VISIBLE);
                } else {
                    deviceLayout.setVisibility(View.VISIBLE);
                    noDeviceLayout.setVisibility(View.GONE);
                    deviceListAdapter.setData(devices);
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
        switch (v.getId()) {
            case R.id.btn_add_device:
            case R.id.iv_add_device:
                startActivity(new Intent(getContext(), AddDeviceSelectActivity.class));
//                startActivity(new Intent(getContext(), TestActivity.class));
                break;
            default:
                break;
        }
    }
}
