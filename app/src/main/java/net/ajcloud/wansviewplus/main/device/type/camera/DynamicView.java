package net.ajcloud.wansviewplus.main.device.type.camera;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.camera.EventMessage;
import net.ajcloud.wansviewplus.support.core.bean.AlarmBean;
import net.ajcloud.wansviewplus.support.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by HW on 2017/9/5.
 */

public class DynamicView {
    private Context context;
    private String oid;
    private View view;
    private LinearLayout ll_date;
    private LinearLayout ll_empty;
    private LinearLayout ll_content;
    private RelativeLayout rl_caution_one;
    private RelativeLayout rl_caution_two;
    private ImageView imageViewOne;
    private ImageView imageViewTwo;
    private ImageView iv_state_one;
    private ImageView iv_state_two;
    private ImageView iv_more;
    private TextView tv_date;
    private TextView tv_date_one;
    private TextView tv_date_two;
    private TextView tv_time_one;
    private TextView tv_time_two;
    private List<AlarmBean> alarms;
    private boolean isSharedCamera;

    public DynamicView(Context context, String oid, List<AlarmBean> alarms, boolean isSharedCamera) {
        this.context = context;
        this.oid = oid;
        this.alarms = alarms;
        this.isSharedCamera = isSharedCamera;
    }

    public View getView() {
        view = View.inflate(context, net.ajcloud.wansviewplus.R.layout.view_dynamic, null);
        ll_date = view.findViewById(net.ajcloud.wansviewplus.R.id.ll_date);
        ll_empty = view.findViewById(net.ajcloud.wansviewplus.R.id.ll_empty);
        ll_content = view.findViewById(net.ajcloud.wansviewplus.R.id.ll_content);
        rl_caution_one = view.findViewById(R.id.rl_caution_one);
        rl_caution_two = view.findViewById(R.id.rl_caution_two);

        iv_more = view.findViewById(net.ajcloud.wansviewplus.R.id.iv_more);
        imageViewOne = view.findViewById(net.ajcloud.wansviewplus.R.id.caution_img_one);
        imageViewTwo = view.findViewById(net.ajcloud.wansviewplus.R.id.caution_img_two);
        iv_state_one = view.findViewById(R.id.iv_state_one);
        iv_state_two = view.findViewById(R.id.iv_state_two);
        tv_date_one = view.findViewById(R.id.tv_date_one);
        tv_date_two = view.findViewById(R.id.tv_date_two);
        tv_time_one = view.findViewById(R.id.tv_time_one);
        tv_time_two = view.findViewById(R.id.tv_time_two);
        tv_date = view.findViewById(R.id.tv_date);

        ll_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rl_caution_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rl_caution_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        refreshView();
        return view;
    }

    private void refreshView() {
        tv_date.setText(DateUtil.getCurrentDate());
        if (alarms == null || alarms.size() == 0) {
            ll_empty.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
        } else {
            ll_empty.setVisibility(View.GONE);
            ll_content.setVisibility(View.VISIBLE);
            for (int i = 0; i < alarms.size(); i++) {
                AlarmBean bean = alarms.get(i);
                if (i == 0) {
                    rl_caution_one.setVisibility(View.VISIBLE);
                    tv_date_one.setText(DateUtil.getSimpleFormatDate(bean.cdate));
                    tv_time_one.setText(DateUtil.getFormatTime(bean.ctime));
                    if (bean.images != null && bean.images.size() > 0) {
                        for (int j = 0; j < bean.images.size(); j++) {
                            AlarmBean.ItemInfoBean imgInfo = bean.images.get(j);
                            if (TextUtils.equals(imgInfo.tags, "thumbnail")) {
                                Glide.with(context).load(imgInfo.url)
                                        .placeholder(R.mipmap.figure_big)
                                        .error(R.mipmap.figure_big)
                                        .into(imageViewOne);
                            }
                        }
                    }
                    if (bean.avs == null || bean.avs.size() == 0) {
                        iv_state_one.setVisibility(View.GONE);
                    }
                }
                if (i == 1) {
                    rl_caution_two.setVisibility(View.VISIBLE);
                    tv_date_two.setText(DateUtil.getSimpleFormatDate(bean.cdate));
                    tv_time_two.setText(DateUtil.getFormatTime(bean.ctime));
                    if (bean.images != null && bean.images.size() > 0) {
                        for (int j = 0; j < bean.images.size(); j++) {
                            AlarmBean.ItemInfoBean imgInfo = bean.images.get(j);
                            if (TextUtils.equals(imgInfo.tags, "thumbnail")) {
                                Glide.with(context).load(imgInfo.url)
                                        .placeholder(R.mipmap.figure_big)
                                        .error(R.mipmap.figure_big)
                                        .into(imageViewTwo);
                            }
                        }
                    }
                    if (bean.avs == null || bean.avs.size() == 0) {
                        iv_state_two.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    //@Override
    public void onSuccess(String api, Object object) {
        /*EventListMessages data = (EventListMessages) object;
        if (data != null && !data.getMessages().isEmpty()) {
            ll_empty.setVisibility(View.GONE);
            ll_content.setVisibility(View.VISIBLE);
            list.clear();
            for (int i = 0; i < data.getMessages().size() && i < 2; i++) {
                EventMessage message = data.getMessages().get(i);
                String hour = CameraUtils.getAssignTimezoneDate(oid, message.getCtime(), "HH") + ":00";
                PhoneImageListData item = new PhoneImageListData();
                item.setRestype(5);
                item.setHeadTime(hour);
                item.setDetailTime(CameraUtils.getAssignTimezoneDate(oid, message.getCtime(), "yyyy-MM-dd HH:mm"));
                item.setMinute(CameraUtils.getAssignTimezoneDate(oid, message.getCtime(), "HH:mm"));
                item.setImagePath(getImageUrl(message));
                item.setThumbUrl(message.getThumbUrl());
                item.setEmid(message.getId());
                list.add(item);
                Glide.with(context).load(data.getMessages().get(i).getImageUrl()).placeholder(R.drawable.figure_big).transform(new GlideRoundTransform(context, 10)).into(imageViews.get(i));
                textViews.get(i).setText(CameraUtils.getAssignTimezoneDate(oid, data.getMessages().get(i).getCtime(), "HH:mm"));
            }
        } else {
            ll_empty.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
        }*/
    }

    //@Override
    public void onError(String api, int errorcode) {
        ll_empty.setVisibility(View.VISIBLE);
        ll_content.setVisibility(View.GONE);
    }

    private String getImageUrl(EventMessage mesage) {
        String imgPath = "";
        if (mesage.getParams().containsKey("ext")) {
            try {
                JSONObject jo = new JSONObject(mesage.getParams().get("ext"));
                JSONArray ja = jo.getJSONArray("images");
                if (ja != null && ja.length() > 0) {
                    imgPath = ja.get(0).toString().trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imgPath;
    }
}
