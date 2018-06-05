package net.ajcloud.wansview.main.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.camera.EventMessage;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {
    public interface OnClickItemListener {
        void onClick(int position);
    }

    private Context mCtx;
    private List<EventMessage> mList;
    private OnClickItemListener mListener;

    private int checkedPos;

    public VideoAdapter(Context ctx, List<EventMessage> list, OnClickItemListener listener) {
        this.mCtx = ctx;
        this.mList = list;
        this.mListener = listener;

        this.checkedPos = 0;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        VideoHolder viewHolder;

        view = LayoutInflater.from(mCtx).inflate(R.layout.item_video, parent, false);
        viewHolder = new VideoHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {
        //CloudPkg pkg = mList.get(position);

//        框体
        holder.rlView.setActivated(position == checkedPos);
//        选中图标
        holder.checked.setVisibility(position == checkedPos ? View.VISIBLE : View.GONE);

        //holder.curamount.setText(pkg.getLabel());
        holder.curamount.setActivated(position == checkedPos);
        holder.curamount.setTextColor(position == checkedPos ? 0xff0099cc : 0xff404040);

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public int getCheckedPos() {
        return checkedPos;
    }

    public void setCheckedPos(int checkedPos) {
        this.checkedPos = checkedPos;
    }

}
