package net.ajcloud.wansviewplus.main.device.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.home.HomeActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.customview.dialog.ConfirmDialog;
import net.ajcloud.wansviewplus.support.event.DeviceDeleteEvent;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

public class MaintenanceActivity extends BaseActivity {

    private static final String LOADING = "LOADING";
    private RelativeLayout restartLayout, restoreLayout, deleteLayout;
    private ConfirmDialog restartDialog;
    private ConfirmDialog restoreDialog;
    private ConfirmDialog deleteDialog;
    private DeviceApiUnit deviceApiUnit;
    private String deviceId;
    private Camera camera;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, MaintenanceActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_maintenance;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle(getResources().getString(R.string.device_setting_maintenance));
        getToolbar().setLeftImg(R.mipmap.ic_back);

        restartLayout = findViewById(R.id.item_restart);
        restoreLayout = findViewById(R.id.item_restore);
        deleteLayout = findViewById(R.id.item_delete);
        restartDialog = new ConfirmDialog(this);
        restoreDialog = new ConfirmDialog(this);
        deleteDialog = new ConfirmDialog(this);
        restartDialog.setTittle(getResources().getString(R.string.dialog_restart_confirm));
        restoreDialog.setTittle(getResources().getString(R.string.dialog_restore_confirm));
        deleteDialog.setTittle(getResources().getString(R.string.dialog_delete_confirm));
    }

    @Override
    protected void initData() {
        deviceApiUnit = new DeviceApiUnit(this);
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
    }

    @Override
    protected void initListener() {
        restartLayout.setOnClickListener(this);
        restoreLayout.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);
        restartDialog.setDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
            @Override
            public void confirm() {
                doRestart();
            }
        });
        restoreDialog.setDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
            @Override
            public void confirm() {
                doRestore();
            }
        });
        deleteDialog.setDialogClickListener(new ConfirmDialog.OnDialogClickListener() {
            @Override
            public void confirm() {
                doDelete();
            }
        });
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_restart:
                if (!restartDialog.isShowing()) {
                    restartDialog.show();
                }
                break;
            case R.id.item_restore:
                if (!restoreDialog.isShowing()) {
                    restoreDialog.show();
                }
                break;
            case R.id.item_delete:
                if (!deleteDialog.isShowing()) {
                    deleteDialog.show();
                }
                break;
        }
    }

    private void doRestart() {
        progressDialogManager.showDialog(LOADING, this);
        deviceApiUnit.restart(camera.getGatewayUrl(), deviceId, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single(getResources().getString(R.string.common_success));
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single(msg);
            }
        });
    }

    private void doRestore() {
        progressDialogManager.showDialog(LOADING, this);
        deviceApiUnit.restore(camera.getGatewayUrl(), deviceId, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single(getResources().getString(R.string.common_success));
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single(msg);
            }
        });
    }

    private void doDelete() {
        progressDialogManager.showDialog(LOADING, this);
        deviceApiUnit.remove(camera.getGatewayUrl(), deviceId, new OkgoCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                progressDialogManager.dimissDialog(LOADING, 0);
                EventBus.getDefault().post(new DeviceDeleteEvent(deviceId));
                startActivity(new Intent(MaintenanceActivity.this, HomeActivity.class));
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(LOADING, 0);
                ToastUtil.single(msg);
            }
        });
    }
}
