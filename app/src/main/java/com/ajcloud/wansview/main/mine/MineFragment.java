package com.ajcloud.wansview.main.mine;

import android.util.Pair;
import android.view.View;

import com.ajcloud.wansview.R;
import com.ajcloud.wansview.main.application.BaseFragment;
import com.ajcloud.wansview.support.customview.ReplayTimeAxisView;
import com.ajcloud.wansview.support.tools.WLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/15.
 * 我的
 */
public class MineFragment extends BaseFragment {

    @Override
    protected int layoutResID() {
        return R.layout.fragment_mine;
    }

    @Override
    protected boolean hasTittle() {
        return false;
    }

    @Override
    protected void initTittle() {

    }

    @Override
    protected void initView(View rootView) {
        ReplayTimeAxisView test = rootView.findViewById(R.id.test);
        test.setOnSlideListener(new ReplayTimeAxisView.OnSlideListener() {
            @Override
            public void onSlide(long timeStamp) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
                String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));   // 时间戳转换成时间
                WLog.d("timeStamp", sd);
            }
        });
        long currentTime = System.currentTimeMillis()/1000;
        test.setMidTimeStamp(System.currentTimeMillis());
        List<Pair<Long, Long>> list = new ArrayList<>();
        Pair<Long, Long> pair1 = new Pair<>(currentTime - 3600 , currentTime + 3600);
        Pair<Long, Long> pair2 = new Pair<>(currentTime - 7200 , currentTime - 4000);
        Pair<Long, Long> pair3 = new Pair<>(currentTime +4000 , currentTime + 7200);
        list.add(pair1);
        list.add(pair2);
        list.add(pair3);
        test.setRecordList(list);
    }
}
