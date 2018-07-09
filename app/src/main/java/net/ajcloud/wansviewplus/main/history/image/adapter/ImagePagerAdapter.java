package net.ajcloud.wansviewplus.main.history.image.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import net.ajcloud.wansviewplus.main.history.entity.ImageInfo;
import net.ajcloud.wansviewplus.main.history.image.ui.ImageDetailFragment;
import net.ajcloud.wansviewplus.main.history.image.widget.DragViewPager;

import java.util.ArrayList;
import java.util.List;


public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private DragViewPager mPager;
    private ArrayList<Fragment> mFragmentList;

    public ImagePagerAdapter(FragmentManager fm, List<ImageInfo> datas, DragViewPager pager) {
        super(fm);
        mPager=pager;
        mPager.setAdapter(this);
        updateData(datas);
    }

    public void updateData(List<ImageInfo> dataList) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0, size = dataList.size(); i < size; i++) {
            final ImageDetailFragment fragment = ImageDetailFragment.newInstance(dataList.get(i).getImagePath());
            fragment.setOnImageListener(() -> {
                View view = fragment.getView();
                mPager.setCurrentShowView(view);
            });
            fragments.add(fragment);
        }
        setViewList(fragments);
    }

    private void setViewList(ArrayList<Fragment> fragmentList) {
        if (mFragmentList != null) {
            mFragmentList.clear();
        }
        mFragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFragmentList==null?0:mFragmentList.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


}