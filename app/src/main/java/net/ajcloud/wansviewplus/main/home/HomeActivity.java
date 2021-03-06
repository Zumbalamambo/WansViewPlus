package net.ajcloud.wansviewplus.main.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.alert.AlertFragment;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.cloud.CloudFragment;
import net.ajcloud.wansviewplus.main.device.DeviceFragment;
import net.ajcloud.wansviewplus.main.mine.MineFragment;
import net.ajcloud.wansviewplus.support.core.api.UserApiUnit;
import net.ajcloud.wansviewplus.support.utils.ToastUtil;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private BottomNavigationBar bottomNavigationBar;
    private FragmentManager fragmentManager;
    private DeviceFragment deviceFragment;
    private AlertFragment alertFragment;
    private CloudFragment cloudFragment;
    private MineFragment mineFragment;
    private ArrayList<Fragment> fragments;
    private UserApiUnit userApiUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        userApiUnit = new UserApiUnit(this);
        deviceFragment = new DeviceFragment();
        alertFragment = new AlertFragment();
        cloudFragment = new CloudFragment();
        mineFragment = new MineFragment();
        fragments = getFragments();
        fragmentManager = getSupportFragmentManager();
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        int currentSelectedPosition = bottomNavigationBar.getCurrentSelectedPosition();
        bottomNavigationBar.clearAll();
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_device_color, getString(R.string.camera)).setInactiveIconResource(R.mipmap.ic_device_mid))
                .addItem(new BottomNavigationItem(R.mipmap.ic_inbox_color, getString(R.string.alert)).setInactiveIconResource(R.mipmap.ic_inbox_mid))
                .addItem(new BottomNavigationItem(R.mipmap.ic_cloud_color, getString(R.string.cloud)).setInactiveIconResource(R.mipmap.ic_cloud_mid))
                .addItem(new BottomNavigationItem(R.mipmap.ic_me_color, getString(R.string.me)).setInactiveIconResource(R.mipmap.ic_me_mid))
                .setActiveColor(R.color.colorPrimary)
                .setFirstSelectedPosition(0)
                .initialise();
        if (currentSelectedPosition > 0) {
            bottomNavigationBar.selectTab(currentSelectedPosition, false);
        }
        setDefaultFragment();
    }

    @Override
    protected void initListener() {
        bottomNavigationBar.setTabSelectedListener(this);
    }

    private int saveLastPosition = -1;

    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.show(fragment);
                } else {
                    String tag = fragment.getClass().getSimpleName();
                    if (fragment == alertFragment) {
                        tag = "alert";
                    } else if (fragment == cloudFragment) {
                        tag = "cloud";
                    } else if (fragment == mineFragment) {
                        tag = "me";
                    }
                    ft.add(R.id.content, fragment, tag);
                }
                if (saveLastPosition != -1 && saveLastPosition != position) {
                    ft.hide(fragments.get(saveLastPosition));
                }
                saveLastPosition = position;
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, deviceFragment, "devices");
        transaction.commitAllowingStateLoss();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(deviceFragment);
        fragments.add(alertFragment);
        fragments.add(cloudFragment);
        fragments.add(mineFragment);
        return fragments;
    }

    private long saveLastBackPressTime;
    private static final long BackPressTimeGap = 1800;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        long currentTime = System.currentTimeMillis();
        if (currentTime - saveLastBackPressTime > BackPressTimeGap) {
            saveLastBackPressTime = currentTime;
            ToastUtil.show(R.string.exit_twotwice);
            return;
        }
        this.moveTaskToBack(true);
    }
}
