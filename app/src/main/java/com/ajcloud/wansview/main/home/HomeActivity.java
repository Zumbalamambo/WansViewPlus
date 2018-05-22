package com.ajcloud.wansview.main.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.main.device.DeviceFragment;
import com.ajcloud.wansview.main.message.MessageFragment;
import com.ajcloud.wansview.main.mine.MineFragment;
import com.ajcloud.wansview.support.utils.ToastUtil;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private BottomNavigationBar bottomNavigationBar;
    private FragmentManager fragmentManager;
    private DeviceFragment deviceFragment;
    private MessageFragment messageFragment;
    private MineFragment mineFragment;
    private ArrayList<Fragment> fragments;

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
        deviceFragment = new DeviceFragment();
        messageFragment = new MessageFragment();
        mineFragment = new MineFragment();
        fragments = getFragments();
        fragmentManager = getSupportFragmentManager();
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        int currentSelectedPosition = bottomNavigationBar.getCurrentSelectedPosition();
        bottomNavigationBar.clearAll();
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_music, getString(R.string.home)).setInactiveIconResource(R.drawable.ic_music))
                .addItem(new BottomNavigationItem(R.drawable.ic_music, getString(R.string.message)).setInactiveIconResource(R.drawable.ic_music))
                .addItem(new BottomNavigationItem(R.drawable.ic_music, getString(R.string.mine)).setInactiveIconResource(R.drawable.ic_music))
                .setActiveColor(R.color.colorPrimary)
                .setFirstSelectedPosition(0)
                .initialise();
        if (currentSelectedPosition > 0) {
            bottomNavigationBar.selectTab(currentSelectedPosition, false);
        }
        setDefaultFragment();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        bottomNavigationBar.setTabSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                break;
            case R.id.img_right:
                break;
            default:
                break;
        }
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
                    if (fragment == messageFragment) {
                        tag = "message";
                    } else if (fragment == mineFragment) {
                        tag = "mine";
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
        transaction.add(R.id.content, deviceFragment, "home");
        transaction.commit();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(deviceFragment);
        fragments.add(messageFragment);
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
