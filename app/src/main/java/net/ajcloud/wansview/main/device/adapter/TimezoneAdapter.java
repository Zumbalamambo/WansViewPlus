package net.ajcloud.wansview.main.device.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.TimezoneInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/04.
 * Function:时区adapter
 */
public class TimezoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<TimezoneInfo> mData;
    private Context context;
    private int selectionPos = -1;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, TimezoneInfo bean);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public TimezoneAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }

    public void setData(List<TimezoneInfo> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_timezone, parent, false);
        return new TimezoneHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TimezoneInfo bean = mData.get(holder.getAdapterPosition());
        final int finalPosition = position;
        ((TimezoneHolder) holder).timezoneName.setText(bean.en);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, finalPosition, bean);
                    selectionPos = finalPosition;
                    notifyItemChanged(selectionPos);
                }
            });
        }
        if (selectionPos == finalPosition) {
            ((TimezoneHolder) holder).selectedImage.setVisibility(View.VISIBLE);
        } else {
            ((TimezoneHolder) holder).selectedImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class TimezoneHolder extends RecyclerView.ViewHolder {

        private TextView timezoneName;
        private TextView offsetTime;
        private ImageView selectedImage;

        public TimezoneHolder(View itemView) {
            super(itemView);
            timezoneName = itemView.findViewById(R.id.tv_name);
            offsetTime = itemView.findViewById(R.id.tv_offset_time);
            selectedImage = itemView.findViewById(R.id.iv_selected);
        }
    }
}
