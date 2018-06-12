package net.ajcloud.wansviewplus.main.device.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.type.DeviceHomeActivity;
import net.ajcloud.wansviewplus.main.device.type.camera.MainCameraFragment;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.device.DeviceInfoDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备列表Adapter
 */
public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Camera> mData;
    private Context context;

    public DeviceListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }

    public void setData(List<Camera> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public void add(Camera camera) {
        mData.add(camera);
        notifyItemInserted(mData.size());
    }

    public void update(Camera camera) {
        for (int i = 0; i < mData.size(); i++) {
            if (TextUtils.equals(camera.deviceId, mData.get(i).deviceId)) {
                mData.set(i, camera);
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_device_list, parent, false);
        return new DeviceListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DeviceListHolder) holder).deviceName.setText(DeviceInfoDictionary.getNameByDevice(mData.get(position)));
        ((DeviceListHolder) holder).cloudLayout.setVisibility(View.VISIBLE);
        ((DeviceListHolder) holder).stateLayout.setVisibility(View.GONE);

        ((DeviceListHolder) holder).thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Camera> cameras = new ArrayList<>(MainApplication.getApplication().getDeviceCache().getDevices());
                Intent intent = new Intent(context, DeviceHomeActivity.class);
                intent.putExtra("class", MainCameraFragment.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class DeviceListHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private TextView deviceName;
        private LinearLayout cloudLayout;
        private LinearLayout stateLayout;
        private TextView cloudTextView;
        private TextView connectButton;

        public DeviceListHolder(View itemView) {
            super(itemView);

            thumbnailImageView = itemView.findViewById(R.id.iv_thumbnail);
            deviceName = itemView.findViewById(R.id.tv_device_name);
            cloudLayout = itemView.findViewById(R.id.ll_cloud_storage);
            stateLayout = itemView.findViewById(R.id.ll_camera_state);
            cloudTextView = itemView.findViewById(R.id.tv_cloud_storage);
            connectButton = itemView.findViewById(R.id.tv_connect);
        }
    }
}
