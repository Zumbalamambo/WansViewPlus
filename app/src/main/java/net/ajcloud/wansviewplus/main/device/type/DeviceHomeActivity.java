package net.ajcloud.wansviewplus.main.device.type;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.BaseFragment;
import net.ajcloud.wansviewplus.main.device.setting.DeviceSettingActivity;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.core.device.DeviceInfoDictionary;

public class DeviceHomeActivity extends BaseActivity {
    BaseFragment baseFragment;
    String oid;
    Class<? extends BaseFragment> baseClass;

    /**
     * @param oid 设备序列号
     */
    public static void startCamerHomeActivity(Context context, String oid,
                                              Class<? extends BaseFragment> baseFragment) {
        Intent intent = new Intent(context, DeviceHomeActivity.class);
        intent.putExtra("oid", oid);
        intent.putExtra("class", baseFragment);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.ajcloud.wansviewplus.R.layout.activity_ws_device_home;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        oid = getIntent().getStringExtra("oid");
        baseClass = (Class) getIntent().getSerializableExtra("class");

        createBaseFragment();
        updataFragment(baseFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 创建fragment
     *
     * @return true-创建成功 false-创建失败
     */
    private boolean createBaseFragment() {
        try {
            baseFragment = baseClass.newInstance();
            return true;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * 更新fragment
     *
     * @return true-替换成功 false-替换失败
     */
    private boolean updataFragment(BaseFragment baseFragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(net.ajcloud.wansviewplus.R.id.cameraFragment, baseFragment, "fragment")
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commitAllowingStateLoss();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (baseFragment.canBackPressed()) {
            finish();
        }
    }

    @Override
    protected boolean hasStateBar() {
        return false;
    }

    public String getOid() {
        return oid;
    }
}
