package net.ajcloud.wansviewplus.main.device;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.main.history.MyViewPager;
import net.ajcloud.wansviewplus.support.core.device.Camera;
import net.ajcloud.wansviewplus.support.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ReplayActivity extends BaseActivity {

    public static final int ITEM_CLOUD = 0;
    public static final int ITEM_TFCARD = 1;
    private TextView tv_date;
    private ImageView iv_arrow;
    private TabLayout tabLayout;
    private BottomNavigationBar bottomNavigationBar;

    private MyViewPager viewPager;
    private MyPageAdapter pageAdapter;
    private List<String> datas = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private String deviceId;
    private Camera camera;

    public static void start(Context context, String deviceId) {
        Intent intent = new Intent(context, ReplayActivity.class);
        intent.putExtra("deviceId", deviceId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_replay;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setLeftImg(R.mipmap.ic_back);
        getToolbar().setTittleCalendar(R.string.alert_detail);
        tv_date = getToolbar().getTextDate();
        iv_arrow = getToolbar().getImgArrow();
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.vp_content);

        tv_date.setText(DateUtil.getCurrentDate());
        datas.add("Cloud storage");
        datas.add("TF card");
        fragments.add(new ReplayCloudFragment());
        fragments.add(new ReplayTFFragment());
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.device_replay_cloud)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.device_replay_tf)));

        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), datas, fragments);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.clearAll();
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_console_color_, getString(R.string.wv_camera_ptz)).setInactiveIconResource(R.mipmap.ic_console_mid))
                .addItem(new BottomNavigationItem(R.mipmap.ic_viewangle_color, getString(R.string.wv_camera_angle)).setInactiveIconResource(R.mipmap.ic_viewangle_mid))
                .addItem(new BottomNavigationItem(R.mipmap.ic_motion_color, getString(R.string.wv_camera_dynamic)).setInactiveIconResource(R.mipmap.ic_motion_mid))
                .addItem(new BottomNavigationItem(R.mipmap.ic_replay_color, getString(R.string.wv_camera_replay)).setInactiveIconResource(R.mipmap.ic_replay_mid))
                .setActiveColor(R.color.colorPrimary)
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.selectTab(3, false);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 3) {
                    return;
                }
                Intent intent = new Intent();
                if (position == 0) {
                    intent.putExtra("selection", 0);
                } else if (position == 1) {
                    intent.putExtra("selection", 1);
                } else if (position == 2) {
                    intent.putExtra("selection", 2);
                }
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
            case R.id.iv_arrow:
                break;
        }
    }

    class MyPageAdapter extends FragmentPagerAdapter {

        List<String> list;
        List<Fragment> fragments;

        private MyPageAdapter(FragmentManager fm, List<String> list, List<Fragment> fragments) {
            super(fm);
            this.list = list;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments != null ? fragments.size() : 0;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position);
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public void setToolBarVisible(boolean visible) {
        super.setToolBarVisible(visible);
        if (visible) {
            tabLayout.setVisibility(View.VISIBLE);
            bottomNavigationBar.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
            bottomNavigationBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
