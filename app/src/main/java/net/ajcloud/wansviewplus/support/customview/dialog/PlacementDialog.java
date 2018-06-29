package net.ajcloud.wansviewplus.support.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;

/**
 * Created by mamengchao on 2018/06/29.
 * Function:
 */
public class PlacementDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ImageView iv_close;
    private ImageView iv_upright;
    private ImageView iv_inverted;
    private TextView tv_upright;
    private TextView tv_inverted;
    private Camera camera;
    private OnDialogClickListener listener;

    public PlacementDialog(Context context, Camera camera) {
        super(context);
        this.context = context;
        this.camera = camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_place);
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = DisplayUtil.dip2Pix(context, 327);
        window.setAttributes(lp);

        iv_close = findViewById(R.id.iv_close);
        iv_upright = findViewById(R.id.iv_upright);
        iv_inverted = findViewById(R.id.iv_inverted);
        tv_upright = findViewById(R.id.tv_upright);
        tv_inverted = findViewById(R.id.tv_inverted);
        iv_close.setOnClickListener(this);
        iv_upright.setOnClickListener(this);
        iv_inverted.setOnClickListener(this);

        refresh();
    }

    private void refresh() {
        if (camera != null) {
            if (TextUtils.isEmpty(camera.orientationValue) || TextUtils.equals("0", camera.orientationValue)) {
                iv_upright.setImageResource(R.mipmap.ic_setting_upright_selected);
                iv_inverted.setImageResource(R.mipmap.ic_setting_inverted_not_selected);
                tv_upright.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                tv_inverted.setTextColor(context.getResources().getColor(R.color.gray_first));
            } else {
                iv_upright.setImageResource(R.mipmap.ic_setting_upright_not_selected);
                iv_inverted.setImageResource(R.mipmap.ic_setting_inverted_selected);
                tv_upright.setTextColor(context.getResources().getColor(R.color.gray_first));
                tv_inverted.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_upright:
                camera.orientationValue = "0";
                listener.onClick(camera);
                break;
            case R.id.iv_inverted:
                camera.orientationValue = "3";
                listener.onClick(camera);
                break;
        }
    }

    public void show(Camera camera) {
        this.camera = camera;
        show();
    }

    @Override
    public void show() {
        super.show();
        refresh();
    }

    public interface OnDialogClickListener {
        void onClick(Camera camera);
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }
}
