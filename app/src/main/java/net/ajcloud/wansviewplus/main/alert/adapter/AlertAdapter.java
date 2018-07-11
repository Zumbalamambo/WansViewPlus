package net.ajcloud.wansviewplus.main.alert.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.alert.AlertDetailActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.device.DeviceInfoDictionary;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/04.
 * Function: 消息中心adapter
 */
public class AlertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<AlarmBean> mInfos;

    public AlertAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<AlarmBean> mInfos) {
        this.mInfos = mInfos;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.layout_inbox_alarm, parent, false);
        return new AlarmHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Camera camera = MainApplication.getApplication().getDeviceCache().get(mInfos.get(position).did);
        ((AlarmHolder) holder).tv_deviceName.setText(DeviceInfoDictionary.getNameByDevice(camera));
        ((AlarmHolder) holder).tv_date.setText(mInfos.get(position).cdate);
        boolean unread = MainApplication.getApplication().getAlarmCountCache().hasUnread(mInfos.get(position).did);
        if (unread) {
            ((AlarmHolder) holder).iv_dot.setVisibility(View.VISIBLE);
        } else {
            ((AlarmHolder) holder).iv_dot.setVisibility(View.GONE);
        }

        if (mInfos.get(position).images.size() > 0) {
            AlarmBean.ItemInfoBean imageItemInfo = mInfos.get(position).images.get(0);
            Glide.with(context).load(imageItemInfo.url)
                    .placeholder(R.mipmap.figure_big)
                    .error(R.mipmap.figure_big)
                    .into(((AlarmHolder) holder).iv_thumbnail);
        }
        ((AlarmHolder) holder).iv_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getApplication().getAlarmCountCache().clearDeviceUnread(mInfos.get(position).did);
                AlertDetailActivity.start(context,mInfos.get(position).did, mInfos.get(position).cdate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInfos == null ? 0 : mInfos.size();
    }


    /**
     * 报警
     */
    private class AlarmHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumbnail;
        private ImageView iv_dot;
        private TextView tv_deviceName;
        private TextView tv_date;

        public AlarmHolder(View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            iv_dot = itemView.findViewById(R.id.iv_dot);
            tv_deviceName = itemView.findViewById(R.id.tv_tittle);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    /**
     * 通知
     */
    private class NotificationHolder extends RecyclerView.ViewHolder {
        private TextView tv_tittle;
        private TextView tv_message;
        private TextView tv_date;

        public NotificationHolder(View itemView) {
            super(itemView);
            tv_tittle = itemView.findViewById(R.id.tv_tittle);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    /**
     * 广告
     */
    private class AdvertisementHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumbnail;

        public AdvertisementHolder(View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }
    }
}
