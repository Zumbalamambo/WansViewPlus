package net.ajcloud.wansviewplus.main.mine;

import android.view.View;
import android.widget.RelativeLayout;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;

public class AboutActivity extends BaseActivity {

    private RelativeLayout emailLayout;
    private RelativeLayout serviceLayout;
    private RelativeLayout termsLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("About");
        getToolbar().setLeftImg(R.mipmap.ic_back);

        emailLayout = findViewById(R.id.rl_email);
        serviceLayout = findViewById(R.id.rl_service);
        termsLayout = findViewById(R.id.rl_terms);
    }

    @Override
    protected void initListener() {
        emailLayout.setOnClickListener(this);
        serviceLayout.setOnClickListener(this);
        termsLayout.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.rl_email:
                break;
            case R.id.rl_service:
                break;
            case R.id.rl_terms:
                break;
            default:
                break;
        }
    }
}
