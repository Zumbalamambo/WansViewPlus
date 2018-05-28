package net.ajcloud.wansview.main.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.support.customview.ReplayTimeAxisView;
import net.ajcloud.wansview.support.tools.WLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mamengchao on 2018/05/15.
 * 我的
 */
public class MineFragment extends Fragment {

    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        toolbar.setOverflowIcon(null);
        toolbarLayout.setTitle("Me");
        toolbarLayout.setExpandedTitleColor(Color.BLACK);
        toolbarLayout.setExpandedTitleGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        toolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);


        final ReplayTimeAxisView test = view.findViewById(net.ajcloud.wansview.R.id.test);
        test.setOnSlideListener(new ReplayTimeAxisView.OnSlideListener() {
            @Override
            public void onSlide(long timeStamp) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
                String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));   // 时间戳转换成时间
                WLog.d("timeStamp", sd);
            }

            @Override
            public void onSelected(long startTime, long endTime) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
                String start = sdf.format(new Date(Long.parseLong(String.valueOf(startTime))));   // 时间戳转换成时间
                String end = sdf.format(new Date(Long.parseLong(String.valueOf(endTime))));   // 时间戳转换成时间
                WLog.d("timeStamp", "start: " + start + "      end: " + end);
            }
        });
        long currentTime = System.currentTimeMillis() / 1000;
        test.setMidTimeStamp(System.currentTimeMillis());
        List<Pair<Long, Long>> list = new ArrayList<>();
        Pair<Long, Long> pair1 = new Pair<>(currentTime - 3600, currentTime + 3600);
        Pair<Long, Long> pair2 = new Pair<>(currentTime - 7200, currentTime - 4000);
        Pair<Long, Long> pair3 = new Pair<>(currentTime + 4000, currentTime + 7200);
        list.add(pair1);
        list.add(pair2);
        list.add(pair3);
        test.setRecordList(list);


        view.findViewById(net.ajcloud.wansview.R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (test.getCurrentMode() == ReplayTimeAxisView.Mode.DownLoad) {
                    test.setCurrentMode(ReplayTimeAxisView.Mode.Play);
                } else {
                    test.setCurrentMode(ReplayTimeAxisView.Mode.DownLoad);
                }
            }
        });
    }

}
