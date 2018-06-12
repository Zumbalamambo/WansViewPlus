package net.ajcloud.wansviewplus.main.video.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;

/**
 * Created by guopeirong on 18-6-2.
 */

class VideoHolder extends RecyclerView.ViewHolder {
    RelativeLayout rlView;
    ImageView checked;
    TextView curamount;
    ImageView ivThumb;
    public VideoHolder(View itemView) {
        super(itemView);
        rlView = (RelativeLayout) itemView.findViewById(R.id.rl_view);
//            选中图标
        checked = (ImageView) itemView.findViewById(R.id.item_checked);
//            折后价格
        curamount = (TextView) itemView.findViewById(R.id.item_payment_curamount);

        ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
    }
}