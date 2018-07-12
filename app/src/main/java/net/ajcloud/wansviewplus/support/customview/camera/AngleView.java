package net.ajcloud.wansviewplus.support.customview.camera;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.entity.camera.CameraStatus;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.device.adapter.ViewAnglesAdapter;
import net.ajcloud.wansviewplus.main.device.type.camera.VirtualCamera;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.ViewAnglesBean;
import net.ajcloud.wansviewplus.support.tools.VideoItemDecoration;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;
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
    private TextView count;
    private LinearLayout emptyLayout;
    private RecyclerView rv_angle;

    private ArrayList<ViewAnglesBean.ViewAngle> list = new ArrayList<>();
    private String oid;
    private ViewAnglesAdapter viewAnglesAdapter;
    // private TipDialog tip;
    private VirtualCamera virtualCamera;
    private DeviceApiUnit deviceApiUnit;
    private View.OnClickListener addAngleListener;

    public AngleView(final Context context, final String oid, final List<ViewAnglesBean.ViewAngle> list, VirtualCamera virtualCamera, View.OnClickListener addAngleListener) {
        this.context = context;
        this.addAngleListener = addAngleListener;
        this.oid = oid;
        this.virtualCamera = virtualCamera;
        deviceApiUnit = new DeviceApiUnit(context);
        for (ViewAnglesBean.ViewAngle angle : list
                ) {
            if (!TextUtils.isEmpty(angle.url)) {
                this.list.add(angle);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansviewplus.R.id.edit_angle:
                if (!this.list.isEmpty()) {
                    editLayout.setVisibility(View.GONE);
                    selectLayout.setVisibility(View.VISIBLE);
                    viewAnglesAdapter.setEdit(true);
                }
                break;
            case R.id.select_layout:
                refreshView();
                viewAnglesAdapter.setEdit(false);
                break;
        }
    }

    private void refreshView() {
        if (this.list.isEmpty()) {
            rv_angle.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            rv_angle.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
            selectLayout.setVisibility(View.GONE);
        }
    }

    public View getView() {
        view = View.inflate(context, net.ajcloud.wansviewplus.R.layout.view_angle, null);
        editLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.edit_angle_layout);
        editImg = view.findViewById(net.ajcloud.wansviewplus.R.id.edit_angle);
        selectLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.select_layout);
        rv_angle = view.findViewById(net.ajcloud.wansviewplus.R.id.rv_angle);
        emptyLayout = view.findViewById(net.ajcloud.wansviewplus.R.id.empty_layout);
        count = view.findViewById(net.ajcloud.wansviewplus.R.id.tv_angles_count);
        view.findViewById(net.ajcloud.wansviewplus.R.id.add_angle).setOnClickListener(addAngleListener);
        editImg.setOnClickListener(this);
        selectLayout.setOnClickListener(this);

        count.setText(String.format(context.getResources().getString(R.string.device_angles_count), list.size() + ""));

        viewAnglesAdapter = new ViewAnglesAdapter(context, list);
        rv_angle.setLayoutManager(new GridLayoutManager(context, 2));
        rv_angle.addItemDecoration(new VideoItemDecoration(context));
        rv_angle.setAdapter(viewAnglesAdapter);
        rv_angle.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator) rv_angle.getItemAnimator()).setSupportsChangeAnimations(false);
        Animation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(200);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation, 0.5F);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        rv_angle.setLayoutAnimation(layoutAnimationController);

        viewAnglesAdapter.setListener(new ViewAnglesAdapter.OnItemClickListener() {
            @Override
            public void onTurn(int angle) {
                if (virtualCamera.state == CameraStatus.OFFLINE) {
                    ToastUtil.show(context.getString(net.ajcloud.wansviewplus.R.string.wv_device_offline));
                } else {
                    deviceApiUnit.turnToAngles(oid, angle, new OkgoCommonListener<Object>() {
                        @Override
                        public void onSuccess(Object bean) {
                            ToastUtil.single("success");
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            ToastUtil.single(msg);
                        }
                    });
                }
            }

            @Override
            public void onDelete(int angle) {
                List<Integer> deleteAngles = new ArrayList<>();
                deleteAngles.add(angle);
                if (deleteAngles.size() == 0) {
                    ToastUtil.show(context.getString(net.ajcloud.wansviewplus.R.string.wv_not_select_angles));
                    return;
                }

                deviceApiUnit.deleteAngles(oid, deleteAngles, new OkgoCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {
                        ToastUtil.single("success");
                        for (int i = list.size() - 1; i >= 0; i--) {
                            if (list.get(i).viewAngle == angle) {
                                list.remove(i);
                                viewAnglesAdapter.remove(i);
                                count.setText(String.format(context.getResources().getString(R.string.device_angles_count), list.size() + ""));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        ToastUtil.single(msg);
                    }
                });
            }
        });
        refreshView();
        return view;
    }

    public void addAngle() {
        net.ajcloud.wansviewplus.support.core.device.Camera camera = MainApplication.getApplication().getDeviceCache().get(oid);
        if (viewAnglesAdapter != null) {
            viewAnglesAdapter.add(camera);
        }
    }

}
