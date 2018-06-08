package net.ajcloud.wansview.main.test;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

public class TestActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Test");
        getToolbar().setLeftImg(R.mipmap.icon_back);
    }
}
