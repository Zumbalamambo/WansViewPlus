package com.ajcloud.wansview.home;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.application.BaseActivity;
import com.ajcloud.wansview.support.utils.ToastUtil;

public class HomeActivity extends BaseActivity {

    private FrameLayout mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.activity_home);
    }

    @Override
    protected void initTittle() {
        super.initTittle();
        toolbar.setTittle("wansview");
        toolbar.setLeftImg(R.drawable.ic_all);
        toolbar.setRightImg(R.drawable.ic_search);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                ToastUtil.single("哈哈哈哈");
                break;
            case R.id.img_right:
                ToastUtil.single("呵呵呵呵");
                break;
            default:
                break;
        }
    }
}
