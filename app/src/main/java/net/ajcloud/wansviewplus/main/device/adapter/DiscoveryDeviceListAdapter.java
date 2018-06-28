package net.ajcloud.wansviewplus.main.device.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.device.addDevice.cable.AddDeviceCableWaitingActivity;
import net.ajcloud.wansviewplus.support.core.bean.DeviceSearchBean;
import net.ajcloud.wansviewplus.support.core.device.DeviceInfoDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/25.
 * 发现设备列表adapter
 */
public class DiscoveryDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<DeviceSearchBean> mData;

    public DiscoveryDeviceListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>();
    }

    public void setData(List<DeviceSearchBean> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_discovery_device_list, parent, false);
        return new DeviceListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DeviceListHolder) holder).deviceName.setText(mData.get(position).szDevName);
        ((DeviceListHolder) holder).deviceIcon.setImageResource(DeviceInfoDictionary.getIconByType(mData.get(position).deviceModel));
        ((DeviceListHolder) holder).deviceId.setText(String.format(context.getResources().getString(R.string.device_discovery_id), mData.get(position).getSzIpAddr()));
        final int finalPosition = position;
        ((DeviceListHolder) holder).selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDeviceCableWaitingActivity.startBind(context, mData.get(finalPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class DeviceListHolder extends RecyclerView.ViewHolder {
        private ImageView deviceIcon;
        private TextView deviceName;
        private TextView deviceId;
        private Button selectButton;

        public DeviceListHolder(View itemView) {
            super(itemView);

            deviceIcon = itemView.findViewById(R.id.iv_device_icon);
            deviceName = itemView.findViewById(R.id.tv_device_name);
            deviceId = itemView.findViewById(R.id.tv_device_id);
            selectButton = itemView.findViewById(R.id.btn_select);
        }
    }
}
