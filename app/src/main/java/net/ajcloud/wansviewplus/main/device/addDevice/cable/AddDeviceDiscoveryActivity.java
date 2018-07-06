package net.ajcloud.wansviewplus.main.device.addDevice.cable;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.device.adapter.DiscoveryDeviceListAdapter;
import net.ajcloud.wansviewplus.main.device.adapter.TimezoneDecoration;
import net.ajcloud.wansviewplus.support.core.bean.DeviceSearchBean;
import net.ajcloud.wansviewplus.support.core.socket.CableConnectionUnit;

import java.util.List;

public class AddDeviceDiscoveryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private TextView wifiNameTextView;
    private TextView deviceNumTextView;
    private RecyclerView deviceListView;
    private LinearLayout noDeviceLayout;

    private WifiManager wifiManager;
    private DiscoveryDeviceListAdapter adapter;
    private CableConnectionUnit unit;
    private Handler handler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_discovery;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Discovering device");
        getToolbar().setLeftImg(R.mipmap.ic_back);
        refreshLayout = findViewById(R.id.refresh_layout);
        wifiNameTextView = findViewById(R.id.tv_wifi_name);
        deviceNumTextView = findViewById(R.id.tv_device_num);
        deviceListView = findViewById(R.id.rv_device_list);
        noDeviceLayout = findViewById(R.id.ll_no_device);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);

        adapter = new DiscoveryDeviceListAdapter(this);
        deviceListView.setLayoutManager(new LinearLayoutManager(this));
        deviceListView.addItemDecoration(new TimezoneDecoration(this));
        deviceListView.setAdapter(adapter);
        Animation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(200);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation, 0.5F);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        deviceListView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.setRefreshing(true);
    }

    @Override
    protected void initData() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                wifiNameTextView.setText(wifiInfo.getSSID().replace("\"", ""));
            }
        }
        doRefresh();
    }

    @Override
    protected void initListener() {
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        doRefresh();
    }

    private void doRefresh() {
        if (unit == null) {
            unit = new CableConnectionUnit();
        }
        unit.startSearch(new CableConnectionUnit.SearchDeviceCallback() {
            @Override
            public void result(final List<DeviceSearchBean> list) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        deviceNumTextView.setText(String.format(getResources().getString(R.string.add_device_discovery_num), list.size() + ""));
                        if (list.size() == 0) {
                            noDeviceLayout.setVisibility(View.VISIBLE);
                            deviceListView.setVisibility(View.GONE);
                        } else {
                            noDeviceLayout.setVisibility(View.GONE);
                            deviceListView.setVisibility(View.VISIBLE);
                            adapter.setData(list);
                        }
                    }
                });
            }
        });
    }
}
