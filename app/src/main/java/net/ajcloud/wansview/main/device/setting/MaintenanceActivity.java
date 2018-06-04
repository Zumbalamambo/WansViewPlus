package net.ajcloud.wansview.main.device.setting;

import android.view.View;
import android.widget.RelativeLayout;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;

public class MaintenanceActivity extends BaseActivity {

    private RelativeLayout restartLayout, restoreLayout, deleteLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_maintenance;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Device maintenance");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        restartLayout = findViewById(R.id.item_restart);
        restoreLayout = findViewById(R.id.item_restore);
        deleteLayout = findViewById(R.id.item_delete);
    }

    @Override
    protected void initListener() {
        restartLayout.setOnClickListener(this);
        restoreLayout.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.item_restart:
                break;
            case R.id.item_restore:
                break;
            case R.id.item_delete:
                break;
        }
    }
}
