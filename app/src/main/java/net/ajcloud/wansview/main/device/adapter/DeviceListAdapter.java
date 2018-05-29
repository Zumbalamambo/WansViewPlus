package net.ajcloud.wansview.main.device.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.camera.Camera;
import net.ajcloud.wansview.main.device.type.DeviceHomeActivity;
import net.ajcloud.wansview.main.device.type.camera.MainCameraFragment;

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_device_list, parent, false);
        return new DeviceListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DeviceListHolder) holder).deviceName.setText(mData.get(position).getName());
        ((DeviceListHolder) holder).cloudLayout.setVisibility(View.VISIBLE);
        ((DeviceListHolder) holder).stateLayout.setVisibility(View.GONE);

        ((DeviceListHolder) holder).thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
