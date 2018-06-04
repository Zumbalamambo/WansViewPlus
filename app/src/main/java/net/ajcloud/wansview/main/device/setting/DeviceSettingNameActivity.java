package net.ajcloud.wansview.main.device.setting;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.support.customview.materialEditText.METValidator;
import net.ajcloud.wansview.support.customview.materialEditText.MaterialEditText;

import static net.ajcloud.wansview.main.device.setting.DeviceSettingActivity.RENAME;

public class DeviceSettingNameActivity extends BaseActivity {

    private MaterialEditText nameEditText;
    private String name;

    public static void startForResult(Activity activity, String name) {
        Intent intent = new Intent(activity, DeviceSettingNameActivity.class);
        intent.putExtra("name", name);
        activity.startActivityForResult(intent, RENAME);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting_name;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Camera name");
        getToolbar().setLeftImg(R.mipmap.icon_back);
        getToolbar().setRightText("Save");
        getToolbar().setRightTextColor(getResources().getColor(R.color.colorPrimary));

        nameEditText = findViewById(R.id.et_name);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            if (!TextUtils.isEmpty(name)) {
                nameEditText.setText(name);
                nameEditText.setSelection(name.length());
            }
        }
        nameEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClickView(View v) {
        super.onClickView(v);
        switch (v.getId()) {
            case R.id.btn_right:
                Intent intent = new Intent();
                intent.putExtra("name", nameEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
