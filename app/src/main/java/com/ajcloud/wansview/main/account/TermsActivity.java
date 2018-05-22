package com.ajcloud.wansview.main.account;

import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.customview.MyToolbar;

public class TermsActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_terms;
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
            toolbar.setLeftImg(R.mipmap.icon_back);
            toolbar.setRightText("Agree");
            toolbar.setRightTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                finish();
                break;
            default:
                break;
        }
    }
}
