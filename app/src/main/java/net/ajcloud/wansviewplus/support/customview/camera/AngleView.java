package net.ajcloud.wansviewplus.support.customview.camera;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.entity.camera.CameraStatus;
import net.ajcloud.wansviewplus.entity.camera.ViewSetting;
import net.ajcloud.wansviewplus.main.device.type.camera.VirtualCamera;
import net.ajcloud.wansviewplus.support.core.bean.ViewAnglesBean;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by HW on 2017/9/5.
 */

public class AngleView implements View.OnClickListener {
    private Context context;
    private View view;
    private LinearLayout editLayout;
    private ImageView editImg;
    private LinearLayout selectLayout;
    private TextView cancelTv;
    private TextView selectAllTv;
    private GridView myGridview;
    private LinearLayout contentLayout;
    private LinearLayout emptyLayout;
    private ArrayList<ViewAnglesBean.ViewAngle> list = new ArrayList<>();
    private String oid;
    private AngleAdapter adapter;
    private View normalView;
    private View deleteView;
    // private TipDialog tip;
    private boolean isEidt;
    private VirtualCamera virtualCamera;

    public AngleView(final Context context, final String oid, final List<ViewAnglesBean.ViewAngle> list, View v1, View v2, VirtualCamera virtualCamera) {
        this.context = context;
        this.oid = oid;
        normalView = v1;
        deleteView = v2;
        this.virtualCamera = virtualCamera;
        this.list.addAll(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansviewplus.R.id.edit_angle:
                isEidt = true;
                editLayout.setVisibility(View.GONE);
                selectLayout.setVisibility(View.VISIBLE);
                normalView.setVisibility(View.GONE);
                deleteView.setVisibility(View.VISIBLE);

                break;
            case net.ajcloud.wansviewplus.R.id.cancel:
                isEidt = false;
                for (ViewAnglesBean.ViewAngle data : list) {
                    data.setSelect(false);
                }
                refreshView();
                adapter.notifyDataSetChanged();

                break;
            case net.ajcloud.wansviewplus.R.id.select_all:
                for (ViewAnglesBean.ViewAngle data : list) {
                    data.setSelect(true);
                }
                adapter.notifyDataSetChanged();

                break;
        }
    }

    private void refreshView() {
        if (this.list.isEmpty()) {
            contentLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
            selectLayout.setVisibility(View.GONE);
        }
        normalView.setVisibility(View.VISIBLE);
        deleteView.setVisibility(View.GONE);
    }


    private class AngleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(context, net.ajcloud.wansviewplus.R.layout.item_select_angle, null);
            ImageView angleView = (ImageView) convertView.findViewById(net.ajcloud.wansviewplus.R.id.angle_img);
            ImageView selectImg = (ImageView) convertView.findViewById(net.ajcloud.wansviewplus.R.id.select_img);
            ViewAnglesBean.ViewAngle data = list.get(position);
            Glide.with(context).load(data.url).placeholder(net.ajcloud.wansviewplus.R.mipmap.figure_big).into(angleView);
            if (isEidt && data.isSelect()) {
                selectImg.setVisibility(View.VISIBLE);
            } else {
                selectImg.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    public View getView() {
        view = View.inflate(context, net.ajcloud.wansviewplus.R.layout.view_angle, null);
        editLayout = (LinearLayout) view.findViewById(net.ajcloud.wansviewplus.R.id.edit_angle_layout);
        editImg = (ImageView) view.findViewById(net.ajcloud.wansviewplus.R.id.edit_angle);
        selectLayout = (LinearLayout) view.findViewById(net.ajcloud.wansviewplus.R.id.select_layout);
        cancelTv = (TextView) view.findViewById(net.ajcloud.wansviewplus.R.id.cancel);
        selectAllTv = (TextView) view.findViewById(net.ajcloud.wansviewplus.R.id.select_all);
        myGridview = (GridView) view.findViewById(net.ajcloud.wansviewplus.R.id.angle_gridview);
        contentLayout = (LinearLayout) view.findViewById(net.ajcloud.wansviewplus.R.id.content_layout);
        emptyLayout = (LinearLayout) view.findViewById(net.ajcloud.wansviewplus.R.id.empty_layout);
        editImg.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        selectAllTv.setOnClickListener(this);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seqs = "";
                for (ViewAnglesBean.ViewAngle data : list) {
                    if (data.isSelect()) {
                        seqs = seqs + "," + data.viewAngle;
                    }
                }
                if (TextUtils.isEmpty(seqs)) {
                    ToastUtil.show(context.getString(net.ajcloud.wansviewplus.R.string.wv_not_select_angles));
                    return;
                }
                //tip.show();
                /*HttpAdapterManger.getCameraRequest().removeCameraViewAngles(AppApplication.devHostPresenter.getDevHost(oid),
                        seqs.substring(1),
                        new ZResponse(CameraRequest.RemoveCameraViewAngles, new ResponseListener() {
                            @Override
                            public void onSuccess(String api, Object object) {
                                normalView.performClick();
                                isEidt = false;
                                for (int i = list.size() - 1; i >= 0; i--) {
                                    if (list.get(i).isSelect()) {
                                        list.remove(i);
                                    }
                                }
                                refreshView();
                                adapter.notifyDataSetChanged();
                                tip.dismiss();
                            }

                            @Override
                            public void onError(String api, int errorcode) {
                                isEidt = false;
                                for (ViewSetting data : list) {
                                    data.setSelect(false);
                                }
                                refreshView();
                                adapter.notifyDataSetChanged();
                                tip.dismiss();
                            }
                        }));*/
            }
        });

        adapter = new AngleAdapter();
        myGridview.setAdapter(adapter);
        myGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isEidt) {
                    for (int i = 0; i < list.size(); i++) {
                        if (i == position) {
                            list.get(i).setSelect(!list.get(i).isSelect());
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (virtualCamera.state == CameraStatus.OFFLINE) {
                        ToastUtil.show(context.getString(net.ajcloud.wansviewplus.R.string.wv_device_offline));
                    } else {
                        virtualCamera.onTurnView(list.get(position).viewAngle);
                    }
                }
            }
        });
        refreshView();
        return view;
    }
}
