package net.ajcloud.wansviewplus.main.device.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.core.bean.ViewAnglesBean;
import net.ajcloud.wansviewplus.support.core.device.Camera;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/05.
 * Function:    视角
 */
public class ViewAnglesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<ViewAnglesBean.ViewAngle> mData;
    private Context context;
    private boolean isEdit;
    private OnItemClickListener listener;

    public ViewAnglesAdapter(Context context, List<ViewAnglesBean.ViewAngle> mData) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<ViewAnglesBean.ViewAngle> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    public void remove(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public void add(Camera camera) {
        if (camera != null && camera.viewAnglesConfig != null) {
            mData.clear();
            for (ViewAnglesBean.ViewAngle angle :
                    camera.viewAnglesConfig.viewAngles) {
                if (!TextUtils.isEmpty(angle.url)) {
                    mData.add(angle);
                }
            }
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_viewangles, parent, false);
        return new ViewAnglesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewAnglesBean.ViewAngle bean = mData.get(holder.getAdapterPosition());

        Glide.with(context).load(bean.url)
                .placeholder(R.mipmap.figure_big)
                .error(R.mipmap.ic_device_default)
                .into(((ViewAnglesHolder) holder).iv_viewangle);

        ((ViewAnglesHolder) holder).tv_viewangle.setText(String.format(context.getResources().getString(R.string.device_angle_view_num), bean.viewAngle + ""));

        if (isEdit) {
            ((ViewAnglesHolder) holder).iv_delete.setVisibility(View.VISIBLE);
            ((ViewAnglesHolder) holder).iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDelete(bean.viewAngle);
                    }
                }
            });
        } else {
            ((ViewAnglesHolder) holder).iv_delete.setVisibility(View.GONE);
            ((ViewAnglesHolder) holder).iv_delete.setOnClickListener(null);
        }
        ((ViewAnglesHolder) holder).content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTurn(bean.viewAngle);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            //视角规定只能四个
            return mData.size() > 4 ? 4 : mData.size();
        }
    }

    private class ViewAnglesHolder extends RecyclerView.ViewHolder {

        private View content;
        private ImageView iv_viewangle;
        private TextView tv_viewangle;
        private ImageView iv_delete;

        private ViewAnglesHolder(View itemView) {
            super(itemView);
            content = itemView;
            iv_viewangle = itemView.findViewById(R.id.iv_viewangle);
            tv_viewangle = itemView.findViewById(R.id.tv_viewangle);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }
    }

    public interface OnItemClickListener {
        void onTurn(int angle);

        void onDelete(int angle);
    }
}
