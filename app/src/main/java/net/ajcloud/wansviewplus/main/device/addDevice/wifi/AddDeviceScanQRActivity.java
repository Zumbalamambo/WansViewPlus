package net.ajcloud.wansviewplus.main.device.addDevice.wifi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.home.HomeActivity;
import net.ajcloud.wansviewplus.support.core.api.DeviceApiUnit;
import net.ajcloud.wansviewplus.support.core.api.OkgoCommonListener;
import net.ajcloud.wansviewplus.support.core.bean.PreBindBean;
import net.ajcloud.wansviewplus.support.tools.zxing.encoding.EncodingUtils;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.io.ByteArrayOutputStream;

public class AddDeviceScanQRActivity extends BaseActivity {

    private static final String PRE_BIND = "PRE_BIND";
    private ImageView qrcodeImageView;
    private Button waitButton;
    private TextView soundTextView;
    private String token;
    private String ssid;
    private String pwd;

    public static void start(Context context, String ssid, String pwd) {
        Intent intent = new Intent(context, AddDeviceScanQRActivity.class);
        intent.putExtra("ssid", ssid);
        intent.putExtra("pwd", pwd);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device_scan_qr;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle(getResources().getString(R.string.add_device_connect_network));
        getToolbar().setLeftImg(R.mipmap.ic_back);
        qrcodeImageView = findViewById(R.id.iv_qr_code);
        waitButton = findViewById(R.id.btn_sure);
        soundTextView = findViewById(R.id.tv_sound);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            ssid = getIntent().getStringExtra("ssid");
            pwd = getIntent().getStringExtra("pwd");
        }
        preBind();
    }

    @Override
    protected void initListener() {
        waitButton.setOnClickListener(this);
        soundTextView.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                AddDeviceWifiWaitingActivity.startBind(AddDeviceScanQRActivity.this);
                break;
            case R.id.tv_sound:
                AddDeviceSoundActivity.start(AddDeviceScanQRActivity.this, token, ssid, pwd);
                break;
            default:
                break;
        }
    }

    private void preBind() {
        progressDialogManager.showDialog(PRE_BIND, this);
        new DeviceApiUnit(this).preBind(new OkgoCommonListener<PreBindBean>() {
            @Override
            public void onSuccess(PreBindBean bean) {
                progressDialogManager.dimissDialog(PRE_BIND, 0);
                if (bean != null && !TextUtils.isEmpty(bean.token)) {
                    token = bean.token;
                    showQRImage();
                }
            }

            @Override
            public void onFail(int code, String msg) {
                progressDialogManager.dimissDialog(PRE_BIND, 0);
                ToastUtil.single(msg);
                if (code == 1201){
                    startActivity(new Intent(AddDeviceScanQRActivity.this, HomeActivity.class));
                }
            }
        });
    }

    private void showQRImage() {
        if (ssid == null || pwd == null) {
            return;
        }
        int width = qrcodeImageView.getMeasuredWidth();
        int height = qrcodeImageView.getMeasuredHeight();
        StringBuilder content = new StringBuilder();
        content.append("s=");
        content.append(ssid);
        content.append("\n");
        content.append("p=");
        content.append(pwd);
        content.append("\n");
        content.append("c=");
        content.append(token);
        content.append("\n");
        Bitmap bitmap = EncodingUtils.createQRCode(content.toString(), width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(AddDeviceScanQRActivity.this)
                .load(bytes)
                .into(qrcodeImageView);
    }
}
