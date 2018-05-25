package net.ajcloud.wansview.support.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ajcloud.wansview.R;


public class MyToolbar extends Toolbar {

    private RelativeLayout toolbar_content;
    private ImageView img_left, img_right, img_tittle;
    private Button btn_left, btn_right;
    private TextView tv_tittle;

    public MyToolbar(Context context) {
        super(context);
    }

    public MyToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar_content = findViewById(R.id.toolbar_content);
        img_left = findViewById(R.id.img_left);
        btn_left = findViewById(R.id.btn_left);
        img_right = findViewById(R.id.img_right);
        btn_right = findViewById(R.id.btn_right);
        tv_tittle = findViewById(R.id.tv_tittle);
        img_tittle = findViewById(R.id.img_tittle);
    }

    public void registerClickListener(OnClickListener listener) {
        img_left.setOnClickListener(listener);
        btn_left.setOnClickListener(listener);
        img_right.setOnClickListener(listener);
        btn_right.setOnClickListener(listener);
    }

    public RelativeLayout getToolbarContent() {
        return toolbar_content;
    }

    public void setToolbarContent(RelativeLayout toolbar_content) {
        this.toolbar_content = toolbar_content;
    }

    /**
     * 设置中间标题
     */
    public void setTittle(String tittle) {
        tv_tittle.setVisibility(VISIBLE);
        img_tittle.setVisibility(GONE);
        tv_tittle.setText(tittle);
    }

    /**
     * 设置中间标题
     */
    public void setTittle(int resId) {
        tv_tittle.setVisibility(VISIBLE);
        img_tittle.setVisibility(GONE);
        tv_tittle.setText(resId);
    }

    /**
     * 设置中间图片
     */
    public void setTittleImg(int resId) {
        tv_tittle.setVisibility(GONE);
        img_tittle.setVisibility(VISIBLE);
        img_tittle.setImageResource(resId);
    }

    /**
     * 设置左边文字
     */
    public void setLeftText(String text) {
        btn_left.setVisibility(VISIBLE);
        img_left.setVisibility(GONE);
        btn_left.setText(text);
    }

    /**
     * 设置左边文字
     */
    public void setLeftText(int resId) {
        btn_left.setVisibility(VISIBLE);
        img_left.setVisibility(GONE);
        btn_left.setText(resId);
    }

    /**
     * 设置左边文字颜色
     */
    public void setLeftTextColor(int resId) {
        btn_left.setTextColor(resId);
    }

    /**
     * 设置左边图片
     */
    public void setLeftImg(int resId) {
        btn_left.setVisibility(GONE);
        img_left.setVisibility(VISIBLE);
        img_left.setImageResource(resId);
    }

    /**
     * 设置右边文字
     */
    public void setRightText(String text) {
        btn_right.setVisibility(VISIBLE);
        img_right.setVisibility(GONE);
        btn_right.setText(text);
    }

    /**
     * 设置右边文字
     */
    public void setRightText(int resId) {
        btn_right.setVisibility(VISIBLE);
        img_right.setVisibility(GONE);
        btn_right.setText(resId);
    }

    /**
     * 设置右边文字颜色
     */
    public void setRightTextColor(int resId) {
        btn_right.setTextColor(resId);
    }

    /**
     * 设置右边图片
     */
    public void setRightImg(int resId) {
        btn_right.setVisibility(GONE);
        img_right.setVisibility(VISIBLE);
        img_right.setImageResource(resId);
    }

    /**
     * 设置tool背景色
     */
    public void setToolbarBg(int color) {
        toolbar_content.setBackgroundColor(color);
    }
}
