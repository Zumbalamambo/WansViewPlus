package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.tools.zxing.encoding.EncodingUtils;

import java.io.ByteArrayOutputStream;

public class AddDeviceScanQRActivity extends BaseActivity {

    private ImageView qrcodeImageView;
    private Button waitButton;
    private TextView soundTextView;
    private String type;
    private String ssid;
    private String pwd;

    public static void start(Context context, String type, String ssid, String pwd) {
        Intent intent = new Intent(context, AddDeviceScanQRActivity.class);
        intent.putExtra("type", type);
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
        getToolbar().setTittle("Connect to network");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        qrcodeImageView = findViewById(R.id.iv_qr_code);
        waitButton = findViewById(R.id.btn_sure);
        soundTextView = findViewById(R.id.tv_sound);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
            ssid = getIntent().getStringExtra("ssid");
            pwd = getIntent().getStringExtra("pwd");
        }
        final ViewTreeObserver viewTreeObserver = qrcodeImageView.getViewTreeObserver();
        viewTreeObserver.addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            @Override
            public void onWindowFocusChanged(final boolean hasFocus) {
                if (hasFocus) {
                    showQRImage();
                }
            }
        });
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
                startActivity(new Intent(AddDeviceScanQRActivity.this, AddDeviceWaitingActivity.class));
                break;
            case R.id.tv_sound:
                AddDeviceSoundActivity.start(AddDeviceScanQRActivity.this, type, ssid, pwd);
                break;
            default:
                break;
        }
    }

    private void showQRImage() {
        if (ssid == null || pwd == null) {
            return;
        }
        int width = qrcodeImageView.getMeasuredWidth();
        int height = qrcodeImageView.getMeasuredHeight();
        //TODO
        String content = "{" + ssid + "/" + pwd + "}";
        Bitmap bitmap = EncodingUtils.createQRCode(content, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(AddDeviceScanQRActivity.this)
                .load(bytes)
                .into(qrcodeImageView);
    }
}
