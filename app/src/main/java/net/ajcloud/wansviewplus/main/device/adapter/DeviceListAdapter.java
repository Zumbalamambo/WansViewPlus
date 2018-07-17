package net.ajcloud.wansviewplus.main.device.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.device.type.DeviceHomeActivity;
import net.ajcloud.wansviewplus.main.device.type.camera.MainCameraFragment;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
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
    private DeviceApiUnit deviceApiUnit;

    public DeviceListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        deviceApiUnit = new DeviceApiUnit(context);
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

    public void remove(String deviceId) {
        for (int i = 0; i < mData.size(); i++) {
            if (TextUtils.equals(deviceId, mData.get(i).deviceId)) {
                mData.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mData.size());
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
        final Camera camera = mData.get(position);
        final int finalPosition = position;
        ((DeviceListHolder) holder).tv_deviceName.setText(DeviceInfoDictionary.getNameByDevice(camera));

        if (camera.onlineStatus == 2) {
            ((DeviceListHolder) holder).tv_status.setText(context.getResources().getString(R.string.device_online));
        } else {
            ((DeviceListHolder) holder).tv_status.setText(context.getResources().getString(R.string.device_offline));
        }

        if (camera.refreshStatus == 1) {
            ((DeviceListHolder) holder).iv_refresh.setVisibility(View.GONE);
        } else {
            ((DeviceListHolder) holder).iv_refresh.setVisibility(View.VISIBLE);
            ((DeviceListHolder) holder).tv_status.setText(context.getResources().getString(R.string.device_no_data));
        }

        if (camera.cloudStorPlan == null ||
                TextUtils.isEmpty(camera.cloudStorPlan.sku) ||
                System.currentTimeMillis() > Long.parseLong(camera.cloudStorPlan.validTsEnd)) {//关闭
            ((DeviceListHolder) holder).tv_cloud.setTextColor(context.getResources().getColor(R.color.gray_second));
            ((DeviceListHolder) holder).tv_cloudState.setText(" " + context.getResources().getString(R.string.device_cloud_not_subscribed));
            ((DeviceListHolder) holder).tv_cloud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 云存储购买页
                }
            });
        } else if (TextUtils.equals(camera.cloudStorConfig.enable, "1")) {//启用
            ((DeviceListHolder) holder).tv_cloud.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            ((DeviceListHolder) holder).tv_cloudState.setText(" is in service");
            ((DeviceListHolder) holder).tv_cloud.setOnClickListener(null);
        }

        final RecyclerView.ViewHolder finalHolder = holder;
        if (TextUtils.isEmpty(camera.snapshotUrl)) {
            if (camera.isOnline()) {
                ((DeviceListHolder) holder).iv_thumbnail.setImageResource(R.mipmap.figure_big);
                deviceApiUnit.doSnapshot(camera.getGatewayUrl(), camera.deviceId, new OkgoCommonListener<String>() {
                    @Override
                    public void onSuccess(String bean) {
                        Glide.with(context).load(bean)
                                .placeholder(R.mipmap.figure_big)
                                .error(R.mipmap.ic_device_default)
                                .into(((DeviceListHolder) finalHolder).iv_thumbnail);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Glide.with(context).load(R.mipmap.ic_device_default)
                                .into(((DeviceListHolder) finalHolder).iv_thumbnail);
                    }
                });
            } else {
                Glide.with(context).load(R.mipmap.ic_device_default)
                        .into(((DeviceListHolder) holder).iv_thumbnail);
            }
        } else {
            Glide.with(context).load(camera.snapshotUrl)
                    .placeholder(R.mipmap.figure_big)
                    .error(R.mipmap.ic_device_default)
                    .into(((DeviceListHolder) holder).iv_thumbnail);
        }

        ((DeviceListHolder) holder).iv_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceHomeActivity.startCamerHomeActivity(context, mData.get(finalPosition).deviceId, MainCameraFragment.class);
            }
        });
        ((DeviceListHolder) holder).iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Camera> devices = new ArrayList<>();
                devices.add(camera);
                deviceApiUnit.doGetDeviceList(devices);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class DeviceListHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumbnail;
        private TextView tv_deviceName;
        private TextView tv_cloud;
        private TextView tv_cloudState;
        private TextView tv_status;
        private ImageView iv_refresh;

        public DeviceListHolder(View itemView) {
            super(itemView);

            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tv_deviceName = itemView.findViewById(R.id.tv_tittle);
            tv_cloud = itemView.findViewById(R.id.tv_cloud);
            tv_cloudState = itemView.findViewById(R.id.tv_cloud_state);
            tv_status = itemView.findViewById(R.id.tv_device_status);
            iv_refresh = itemView.findViewById(R.id.iv_refresh);
        }
    }
}
