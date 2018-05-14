package com.ajcloud.wansview.main.home;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseActivity;
import com.ajcloud.wansview.support.utils.ToastUtil;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.firebase.analytics.FirebaseAnalytics;

public class HomeActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private BottomNavigationBar bottomNavigationBar;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void initView() {
        homeFragment = new HomeFragment();
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
    protected void initListener() {
        bottomNavigationBar.setTabSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_left:
                ToastUtil.single("哈哈哈哈");
                break;
            case R.id.img_right:
                ToastUtil.single("呵呵呵呵");
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabSelected(int position) {
        //test
        if (position == 0){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (homeFragment.isAdded()){
                ft.show(homeFragment);
            }else {
                ft.add(R.id.content, homeFragment, "home");
            }
            ft.commitAllowingStateLoss();
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
        transaction.add(R.id.content, homeFragment, "home");
        transaction.commit();
    }
}
