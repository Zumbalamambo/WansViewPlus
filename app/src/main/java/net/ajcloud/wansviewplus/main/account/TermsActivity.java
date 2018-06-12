package net.ajcloud.wansviewplus.main.account;

import android.view.View;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;

import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;

public class TermsActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansviewplus.R.layout.activity_terms;
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
            toolbar.setLeftImg(net.ajcloud.wansviewplus.R.mipmap.icon_back);
            toolbar.setRightText("Agree");
            toolbar.setRightTextColor(getResources().getColor(net.ajcloud.wansviewplus.R.color.colorPrimary));
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                setResult(RESULT_OK);
                finish();
                break;
            default:
                break;
        }
    }
}
