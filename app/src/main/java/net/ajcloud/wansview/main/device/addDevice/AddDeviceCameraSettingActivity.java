package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.dialog.CommonDialog;
import net.ajcloud.wansview.support.utils.DisplayUtil;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.dialog.CommonDialog;
import net.ajcloud.wansview.support.utils.DisplayUtil;

public class AddDeviceCameraSettingActivity extends BaseActivity {

    private Button flashButton;
    private TextView noFlashTextView;
    private String type;
    private CommonDialog notFlashDialog;

    public static void start(Context context, String type) {
        Intent intent = new Intent(context, AddDeviceCameraSettingActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_add_device_camera_setting;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Camera setting");
        getToolbar().setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        flashButton = findViewById(net.ajcloud.wansview.R.id.btn_flash);
        noFlashTextView = findViewById(net.ajcloud.wansview.R.id.tv_no_flash);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }
    }

    @Override
    protected void initListener() {
        flashButton.setOnClickListener(this);
        noFlashTextView.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansview.R.id.btn_flash:
                if (!TextUtils.isEmpty(type)) {
                    AddDeviceWifiSettingActivity.start(AddDeviceCameraSettingActivity.this, type);
                }
                break;
            case net.ajcloud.wansview.R.id.tv_no_flash:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        if (notFlashDialog == null) {
            notFlashDialog = new CommonDialog.Builder(this)
                    .canceledOnTouchOutside(false)
                    .view(net.ajcloud.wansview.R.layout.dialog_not_flash)
                    .height(DisplayUtil.dip2Pix(this, 100))
                    .width(DisplayUtil.dip2Pix(this, 100))
                    .addViewOnclickListener(net.ajcloud.wansview.R.id.iv_close, dialogClickListener)
                    .build();
        }
        if (!notFlashDialog.isShowing()) {
            notFlashDialog.show();
        }
    }

    private View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case net.ajcloud.wansview.R.id.iv_close:
                    if (notFlashDialog != null && notFlashDialog.isShowing()) {
                        notFlashDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
