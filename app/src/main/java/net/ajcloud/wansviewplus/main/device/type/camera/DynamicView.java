package net.ajcloud.wansviewplus.main.device.type.camera;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.entity.camera.EventMessage;
import net.ajcloud.wansviewplus.entity.camera.PhoneImageListData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by HW on 2017/9/5.
 */

public class DynamicView /*implements ResponseListener*/ {
    private Context context;
    private String oid;
    private View view;
    private LinearLayout emptyLayout;
    private LinearLayout contentLayout;
    private ImageView imageViewOne;
    private ImageView imageViewTwo;
    private TextView textViewOne;
    private TextView textViewTwo;
    private ImageView more;
    private boolean isSharedCamera;
    private ArrayList<PhoneImageListData> list = new ArrayList<>();
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private ArrayList<TextView> textViews = new ArrayList<>();

    public DynamicView(Context context, String oid, boolean isSharedCamera) {
        this.context = context;
        this.oid = oid;
        this.isSharedCamera = isSharedCamera;
    }

    public View getView() {
        view = View.inflate(context, net.ajcloud.wansviewplus.R.layout.view_dynamic, null);
        emptyLayout = (LinearLayout) view.findViewById(net.ajcloud.wansviewplus.R.id.empty_layout);
        contentLayout = (LinearLayout) view.findViewById(net.ajcloud.wansviewplus.R.id.content_layout);
        imageViewOne = (ImageView) view.findViewById(net.ajcloud.wansviewplus.R.id.caution_img_one);
        imageViewTwo = (ImageView) view.findViewById(net.ajcloud.wansviewplus.R.id.caution_img_two);
        textViewOne = (TextView) view.findViewById(net.ajcloud.wansviewplus.R.id.time_one);
        textViewTwo = (TextView) view.findViewById(net.ajcloud.wansviewplus.R.id.time_two);
        more = (ImageView) view.findViewById(net.ajcloud.wansviewplus.R.id.more);
        imageViews.add(imageViewOne);
        imageViews.add(imageViewTwo);
        textViews.add(textViewOne);
        textViews.add(textViewTwo);
        imageViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, ImageViewer.class);
                intent.putExtra("type", "network");
                intent.putExtra("canDelete", false);
                intent.putExtra("index", 0);
                intent.putExtra("sources", list);
                context.startActivity(intent);*/
            }
        });

        imageViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, ImageViewer.class);
                intent.putExtra("type", "network");
                intent.putExtra("canDelete", false);
                intent.putExtra("index", 1);
                intent.putExtra("sources", list);
                context.startActivity(intent);*/
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, CautionActivity.class);
                intent.putExtra("cid", oid);
                intent.putExtra("isSharedCamera", isSharedCamera);
                context.startActivity(intent);*/
            }
        });
        refreshView();
        return view;
    }

    public void refreshView() {
        long startTs = getCurrentCamraTime();
        long endTs = startTs + 24 * 60 * 60 * 1000;

        /*HttpAdapterManger.getMessageRequest().listEmDetail(ODM.CAMERA, oid, 50, null, true, endTs, startTs, 2,
                new ZResponse(MessageRequest.ListEMCDetail, this));*/
    }

    private long getCurrentCamraTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.clear();
        try {
            /*String[] tz = TimeZone.getAvailableIDs(Integer.valueOf(timezoneOffset.get(oid)) * 1000);
            if (tz != null && tz.length > 0 && !TextUtils.isEmpty(tz[0])) {
                //calendar.setTimeZone(TimeZone.getTimeZone(TimeZone.getAvailableIDs(Integer.valueOf(lib.zte.homecare.utils.Utils.timezoneOffset.get(oid)) * 1000)[0]));
                calendar.setTimeZone(TimeZone.getTimeZone(tz[0]));
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    //@Override
    public void onSuccess(String api, Object object) {
        /*EventListMessages data = (EventListMessages) object;
        if (data != null && !data.getMessages().isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
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
            emptyLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        }*/
    }

    //@Override
    public void onError(String api, int errorcode) {
        emptyLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
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
