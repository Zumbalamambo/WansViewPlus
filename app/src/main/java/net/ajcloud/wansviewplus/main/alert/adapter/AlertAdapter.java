package net.ajcloud.wansviewplus.main.alert.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.MessageInfo;

import java.util.List;

/**
 * Created by mamengchao on 2018/06/04.
 * Function: 消息中心adapter
 */
public class AlertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ALARM = 0x20;
    private static final int TYPE_NOTIFICATION = 0x21;
    private static final int TYPE_ADVERTISEMENT = 0x22;
    private Context context;
    private LayoutInflater inflater;
    private List<MessageInfo> mInfos;

    public AlertAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (mInfos != null) {
            if (mInfos.get(position).getType() == TYPE_ALARM) {
                return TYPE_ALARM;
            } else if (mInfos.get(position).getType() == TYPE_NOTIFICATION) {
                return TYPE_NOTIFICATION;
            } else if (mInfos.get(position).getType() == TYPE_ADVERTISEMENT) {
                return TYPE_ADVERTISEMENT;
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == TYPE_ALARM) {
            itemView = inflater.inflate(R.layout.layout_inbox_alarm, parent, false);
            return new AlarmHolder(itemView);
        } else if (viewType == TYPE_NOTIFICATION) {
            itemView = inflater.inflate(R.layout.layout_inbox_notifycation, parent, false);
            return new NotificationHolder(itemView);
        } else if (viewType == TYPE_ADVERTISEMENT) {
            itemView = inflater.inflate(R.layout.layout_inbox_advertisemeent, parent, false);
            return new AdvertisementHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mInfos == null ? 0 : mInfos.size();
    }


    private class AlarmHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumbnail;
        private TextView tv_deviceName;
        private TextView tv_date;

        public AlarmHolder(View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tv_deviceName = itemView.findViewById(R.id.tv_tittle);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

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

    private class AdvertisementHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumbnail;

        public AdvertisementHolder(View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }
    }
}
