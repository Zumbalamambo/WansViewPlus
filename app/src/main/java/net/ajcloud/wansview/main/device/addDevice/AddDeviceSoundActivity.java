package net.ajcloud.wansview.main.device.addDevice;

import android.content.Context;
import android.content.Intent;
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

public class AddDeviceSoundActivity extends BaseActivity {

    private Button sendButton;
    private Button nextButton;
    private TextView errorTextView;
    private CommonDialog noSoundDialog;
    private String type;

    public static void start(Context context, String type) {
        Intent intent = new Intent(context, AddDeviceSoundActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_add_device_sound;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Connect to network");
        getToolbar().setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
        sendButton = findViewById(net.ajcloud.wansview.R.id.btn_send);
        nextButton = findViewById(net.ajcloud.wansview.R.id.btn_next);
        errorTextView = findViewById(net.ajcloud.wansview.R.id.tv_error);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }
    }

    @Override
    protected void initListener() {
        sendButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        errorTextView.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.img_left:
                finish();
                break;
            case net.ajcloud.wansview.R.id.btn_send:
                break;
            case net.ajcloud.wansview.R.id.btn_next:
                startActivity(new Intent(AddDeviceSoundActivity.this, AddDeviceWaitingActivity.class));
                break;
            case net.ajcloud.wansview.R.id.tv_error:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        if (noSoundDialog == null) {
            noSoundDialog = new CommonDialog.Builder(this)
                    .canceledOnTouchOutside(false)
                    .view(net.ajcloud.wansview.R.layout.dialog_no_sound)
                    .height(DisplayUtil.dip2Pix(this, 130))
                    .width(DisplayUtil.dip2Pix(this, 100))
                    .addViewOnclickListener(net.ajcloud.wansview.R.id.iv_close, dialogClickListener)
                    .build();
        }
        if (!noSoundDialog.isShowing()) {
            noSoundDialog.show();
        }
    }

    private View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case net.ajcloud.wansview.R.id.iv_close:
                    if (noSoundDialog != null && noSoundDialog.isShowing()) {
                        noSoundDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
