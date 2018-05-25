package net.ajcloud.wansview.support.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import net.ajcloud.wansview.support.utils.DisplayUtil;

/**
 * Created by mamengchao on 2018/05/14.
 * 通用自定义dialog
 */
public class CommonDialog extends Dialog {

    private Context context;
    private boolean cancelable;
    private int height, width;
    private View view;

    private CommonDialog(Builder builder) {
        super(builder.context);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        cancelable = builder.canceledOnTouchOutside;
        view = builder.view;
    }

    private CommonDialog(Builder builder, int themeResId) {
        super(builder.context, themeResId);
        context = builder.context;
        height = builder.height;
        width = builder.width;
        cancelable = builder.canceledOnTouchOutside;
        view = builder.view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        setCanceledOnTouchOutside(cancelable);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.height = height;
        lp.width = width;
        window.setAttributes(lp);
    }

    public static final class Builder {
        private Context context;
        private int height, width;
        private boolean canceledOnTouchOutside;
        private View view;
        private int resStyle = -1;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder view(int resView) {
            view = LayoutInflater.from(context).inflate(resView, null);
            return this;
        }

        public Builder height(int height_dp) {
            height = DisplayUtil.dip2Pix(context, height_dp);
            return this;
        }

        public Builder width(int width_dp) {
            width = DisplayUtil.dip2Pix(context, width_dp);
            return this;
        }

        public Builder heightDimension(int height_dimen) {
            height = (int) context.getResources().getDimension(height_dimen);
            return this;
        }

        public Builder widthDimension(int width_dimen) {
            width = (int) context.getResources().getDimension(width_dimen);
            return this;
        }

        public Builder style(int resStyle) {
            this.resStyle = resStyle;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean val) {
            canceledOnTouchOutside = val;
            return this;
        }

        public Builder addViewOnclickListener(int viewRes, View.OnClickListener listener) {
            view.findViewById(viewRes).setOnClickListener(listener);
            return this;
        }

        public CommonDialog build() {
            if (resStyle != -1) {
                return new CommonDialog(this, resStyle);
            } else {
                return new CommonDialog(this);
            }
        }
    }
}
