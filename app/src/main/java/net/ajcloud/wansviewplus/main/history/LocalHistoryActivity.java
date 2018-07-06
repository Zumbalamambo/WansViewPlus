package net.ajcloud.wansviewplus.main.history;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.utils.DisplayUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LocalHistoryActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    public static final int ITEM_VIDEO = 0;
    public static final int ITEM_IMAGE = 1;
    public static final int ITEM_DOWNLOAD = 2;
    private ImageView iv_back;
    //normal
    private TabLayout tabLayout;
    private ImageView iv_edit;
    //edit
    private TextView tv_tittle;
    private TextView tv_selectAll;

    private MyViewPager viewPager;
    private MyPageAdapter pageAdapter;
    private List<String> datas = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    private boolean isEdit;
    private int currentItam;

    public static void start(Context context, int currentItam) {
        Intent intent = new Intent(context, LocalHistoryActivity.class);
        intent.putExtra("item", currentItam);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_local_history;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initView() {
        iv_back = findViewById(R.id.iv_back);
        tabLayout = findViewById(R.id.tab_layout);
        iv_edit = findViewById(R.id.iv_edit);
        tv_tittle = findViewById(R.id.tv_tittle);
        tv_selectAll = findViewById(R.id.tv_selectAll);
        viewPager = findViewById(R.id.vp_content);

        datas.add("Videos");
        datas.add("Images");
        datas.add("Downloading");
        fragments.add(new VideoListFragment());
        fragments.add(new ImageListFragment());
        fragments.add(new DownloadListFragment());
        tabLayout.addTab(tabLayout.newTab().setText("Videos"));
        tabLayout.addTab(tabLayout.newTab().setText("Images"));
        tabLayout.addTab(tabLayout.newTab().setText("Downloading"));

        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), datas, fragments);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.post(() -> {
            try {
                //拿到tabLayout的mTabStrip属性
                Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                mTabStripField.setAccessible(true);

                LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);

                int dp10 = DisplayUtil.dip2Pix(this, 10);

                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View tabView = mTabStrip.getChildAt(i);

                    //拿到tabView的mTextView属性
                    Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);

                    TextView mTextView = (TextView) mTextViewField.get(tabView);

                    tabView.setPadding(0, 0, 0, 0);

                    //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                    int width = 0;
                    width = mTextView.getWidth();
                    if (width == 0) {
                        mTextView.measure(0, 0);
                        width = mTextView.getMeasuredWidth();
                    }

                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = width;
                    params.leftMargin = dp10;
                    params.rightMargin = dp10;
                    tabView.setLayoutParams(params);

                    tabView.invalidate();
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        refreshUI(false);
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            currentItam = getIntent().getIntExtra("item", 0);
        }
        viewPager.setCurrentItem(currentItam);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        tv_selectAll.setOnClickListener(this);
        tabLayout.addOnTabSelectedListener(this);
    }

    private void refreshUI(boolean isEdit) {
        this.isEdit = isEdit;
        if (isEdit) {
            tabLayout.setVisibility(View.GONE);
            iv_edit.setVisibility(View.GONE);
            tv_tittle.setVisibility(View.VISIBLE);
            tv_selectAll.setVisibility(View.VISIBLE);
            viewPager.setScanScroll(false);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
            iv_edit.setVisibility(View.VISIBLE);
            tv_tittle.setVisibility(View.GONE);
            tv_selectAll.setVisibility(View.GONE);
            viewPager.setScanScroll(true);
        }
    }

    @Override
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_edit:
                refreshUI(!isEdit);
                break;
            case R.id.tv_selectAll:
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            refreshUI(!isEdit);
        } else {
            finish();
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
}
