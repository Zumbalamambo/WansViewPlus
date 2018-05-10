package com.ajcloud.wansview.home;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.application.BaseActivity;
import com.ajcloud.wansview.support.utils.ToastUtil;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class HomeActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private FrameLayout mContent;
    private BottomNavigationBar bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.activity_home);
    }

    @Override
    protected void initTittle() {
        super.initTittle();
        toolbar.setTittle("wansview");
        toolbar.setLeftImg(R.drawable.ic_all);
        toolbar.setRightImg(R.drawable.ic_search);
    }

    @Override
    protected void initView() {
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

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
