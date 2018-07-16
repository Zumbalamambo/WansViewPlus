package net.ajcloud.wansviewplus.main.alert.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nineoldandroids.animation.ObjectAnimator;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.utils.DateUtil;

import java.util.List;

/**
 * Created by mamengchao on 2018/07/12.
 * Function:    报警消息详情页
 */
public class AlertListDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static int TYPE_NORMAL = 0;
    public static int TYPE_FOOT = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_END = 3;
    public static final int STATE_NORMAL = 4;
    private Context context;
    private LayoutInflater inflater;
    public LoadMoreHolder loadMoreHolder;
    private List<AlarmBean> mInfos;
    private int selected;
    private OnItemClickListener listener;

    public AlertListDetailAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void addData(List<AlarmBean> newInfo) {
        if (mInfos == null) {
            mInfos = newInfo;
            notifyDataSetChanged();
        } else {
            int lastIndex = mInfos.size();
            mInfos.addAll(newInfo);
            notifyItemRangeInserted(lastIndex, newInfo.size());
        }
    }

    public void setSelector(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_NORMAL) {
            itemView = inflater.inflate(R.layout.layout_item_alarm, parent, false);
            return new AlarmDetailHolder(itemView);
        } else {
            itemView = inflater.inflate(R.layout.item_load_more, parent, false);
            loadMoreHolder = new LoadMoreHolder(itemView);
            return loadMoreHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof AlarmDetailHolder) {
            ((AlarmDetailHolder) holder).tv_date.setText(DateUtil.getSimpleFormatDate(mInfos.get(position).cdate));
            ((AlarmDetailHolder) holder).tv_time.setText(DateUtil.getFormatTime(mInfos.get(position).ctime));

            AlarmBean.ItemInfoBean imageItemInfo = mInfos.get(position).images.get(0);
            Glide.with(context).load(imageItemInfo.url)
                    .placeholder(R.mipmap.figure_big)
                    .error(R.mipmap.figure_big)
                    .into(((AlarmDetailHolder) holder).iv_thumbnail);

            if (selected == position) {
                ((AlarmDetailHolder) holder).iv_state.setImageResource(R.mipmap.ic_playing);
            } else {
                ((AlarmDetailHolder) holder).iv_state.setImageResource(R.mipmap.ic_play_white);
            }

            ((AlarmDetailHolder) holder).iv_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if (mInfos.get(position).avs.size() > 0) {
                            if (mInfos.get(position).avs != null && mInfos.get(position).avs.size() > 0) {
                                AlarmBean.ItemInfoBean videoItemInfo = mInfos.get(position).avs.get(0);
                                listener.OnItemClick(position, imageItemInfo.url, videoItemInfo.url);
                            } else {
                                listener.OnItemClick(position, imageItemInfo.url, null);
                            }
                            selected = position;
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mInfos.size())
            return TYPE_FOOT;
        else
            return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mInfos == null ? 0 : mInfos.size() + 1;
    }


    private class AlarmDetailHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumbnail;
        private ImageView iv_state;
        private TextView tv_date;
        private TextView tv_time;

        private AlarmDetailHolder(View itemView) {
            super(itemView);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            iv_state = itemView.findViewById(R.id.iv_state);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_loading;
        private LinearLayout ll_end;
        private ImageView iv_loading;

        private LoadMoreHolder(View itemView) {
            super(itemView);
            ll_loading = itemView.findViewById(R.id.ll_loading);
            ll_end = itemView.findViewById(R.id.ll_end);
            iv_loading = itemView.findViewById(R.id.iv_loading);
        }

        //根据传过来的status控制哪个状态可见
        public void setData(int status) {
            switch (status) {
                case STATE_NORMAL:
                    setAllGone();
                    break;
                case STATE_LOADING:
                    setAllGone();
                    ll_loading.setVisibility(View.VISIBLE);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(iv_loading, "rotation", 0f, 360f);
                    animator.setDuration(2000).setRepeatCount(-1);
                    animator.start();
                    break;
                case STATE_END:
                    setAllGone();
                    ll_end.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        //全部不可见
        void setAllGone() {
            if (ll_loading != null) {
                ll_loading.setVisibility(View.GONE);
            }
            if (ll_end != null) {
                ll_end.setVisibility(View.GONE);
            }
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position, String imgUrl, String videoUrl);
    }
}
