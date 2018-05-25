package net.ajcloud.wansview.main.account;

import android.view.View;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;

import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.MyToolbar;

public class TermsActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansview.R.layout.activity_terms;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle("Terms of Use");
            toolbar.setLeftImg(net.ajcloud.wansview.R.mipmap.icon_back);
            toolbar.setRightText("Agree");
            toolbar.setRightTextColor(getResources().getColor(net.ajcloud.wansview.R.color.colorPrimary));
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case net.ajcloud.wansview.R.id.btn_right:
                finish();
                break;
            default:
                break;
        }
    }
}
