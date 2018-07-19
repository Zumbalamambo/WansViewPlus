package net.ajcloud.wansviewplus.support.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;

/**
 * Created by mamengchao on 2018/07/19.
 * Function:
 */
public class CameraRefreshDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private Button btn_close;
    private Button btn_refresh;
    private OnDialogClickListener listener;

    public CameraRefreshDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_camera_refresh);
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.width = DisplayUtil.dip2Pix(context, 327);
        window.setAttributes(lp);

        btn_close = findViewById(R.id.btn_close);
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_close.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_close:
                listener.onClose();
                break;
            case R.id.btn_refresh:
                listener.onRefresh();
                break;
        }
    }

    public interface OnDialogClickListener {
        void onClose();

        void onRefresh();
    }

    public void setDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }
}
