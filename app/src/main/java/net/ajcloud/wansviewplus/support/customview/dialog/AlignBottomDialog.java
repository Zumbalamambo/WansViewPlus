package net.ajcloud.wansviewplus.support.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.ajcloud.wansviewplus.R;


/**
 * Created
 */
public class AlignBottomDialog {
    private Context context;
    private int resId;
    private Dialog dia;
    private View view;
    private RelativeLayout dialogLayout;
    private LinearLayout popLayout;
    private CancelListener cancelListener;

    public AlignBottomDialog(Context context, int resId) {
        this.context = context;
        this.resId = resId;
        init();
    }

    private void init() {
        dia = new Dialog(context, R.style.accessdevice_dialog);
        view = View.inflate(context, resId, null);
        dia.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window win = dia.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        WindowManager.LayoutParams param = win.getAttributes();
        param.width = context.getResources().getDisplayMetrics().widthPixels;
        param.y = 0;
        win.setAttributes(param);
        dia.setCanceledOnTouchOutside(false);

        dialogLayout = (RelativeLayout) view.findViewById(R.id.dialog_layout);
        popLayout = (LinearLayout) view.findViewById(R.id.pop_layout);
        if (dialogLayout != null) {
            dialogLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        if (popLayout != null) {
            popLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
        }

        dia.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (getCancelListener() != null) {
                        getCancelListener().cancel();
                    }
                }
                return false;
            }
        });
    }

    public void show() {
        if (null != dia) {
            dia.show();
        }
    }

    public void dismiss() {
        if (null != dia && dia.isShowing()) {
            dia.dismiss();
        }
    }

    public View getContentView() {
        return view;
    }

    public CancelListener getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public interface CancelListener {
        void cancel();
    }

}
