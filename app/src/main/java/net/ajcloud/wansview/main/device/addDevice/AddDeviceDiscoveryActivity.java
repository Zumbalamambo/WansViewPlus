package net.ajcloud.wansview.main.device.addDevice;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.DiscoveryDeviceInfo;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.device.adapter.DiscoveryDeviceListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceDiscoveryActivity extends BaseActivity {

    private TextView wifiNameTextView;
    private TextView deviceNumTextView;
    private RecyclerView deviceListView;
    private LinearLayout noDeviceLayout;
    private DiscoveryDeviceListAdapter adapter;

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
        getToolbar().setLeftImg(R.mipmap.icon_back);
        wifiNameTextView = findViewById(R.id.tv_wifi_name);
        deviceNumTextView = findViewById(R.id.tv_device_num);
        deviceListView = findViewById(R.id.rv_device_list);
        noDeviceLayout = findViewById(R.id.ll_no_device);
    }

    @Override
    protected void initData() {
        adapter = new DiscoveryDeviceListAdapter(this);
        deviceListView.setLayoutManager(new LinearLayoutManager(this));
        deviceListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, 1);
            }
        });
        deviceListView.setAdapter(adapter);
        DiscoveryDeviceInfo info1 = new DiscoveryDeviceInfo();
        info1.deviceName = "钢铁侠";
        info1.deviceId = "1001sadsadasdasdasdasddsdasdas";
        DiscoveryDeviceInfo info2 = new DiscoveryDeviceInfo();
        info2.deviceName = "蜘蛛侠";
        info2.deviceId = "100eqwe1234213213123213231231232";
        DiscoveryDeviceInfo info3 = new DiscoveryDeviceInfo();
        info3.deviceName = "雷神";
        info3.deviceId = "10321321312321312321312303";
        DiscoveryDeviceInfo info4 = new DiscoveryDeviceInfo();
        info4.deviceName = "绿巨人";
        info4.deviceId = "10032312312321312312314";
        DiscoveryDeviceInfo info5 = new DiscoveryDeviceInfo();
        info5.deviceName = "奇异博士";
        info5.deviceId = "100wedasdasdasdasdasd5";
        DiscoveryDeviceInfo info6 = new DiscoveryDeviceInfo();
        info6.deviceName = "死侍";
        info6.deviceId = "100321326";
        List<DiscoveryDeviceInfo> list = new ArrayList<>();
        list.add(info1);
        list.add(info2);
        list.add(info3);
        list.add(info4);
        list.add(info5);
        list.add(info6);
        adapter.setData(list);
    }

    @Override
    protected void initListener() {

    }
}
