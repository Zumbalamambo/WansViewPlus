package net.ajcloud.wansview.main.device.setting;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.ajcloud.wansview.R;
import net.ajcloud.wansview.entity.TimezoneInfo;
import net.ajcloud.wansview.main.application.BaseActivity;
import net.ajcloud.wansview.main.device.adapter.TimezoneAdapter;
import net.ajcloud.wansview.main.device.adapter.TimezoneDecoration;
import net.ajcloud.wansview.support.utils.FileUtil;

import java.util.List;

public class TimeZoneActivity extends BaseActivity {

    private RecyclerView timezoneRecycler;
    private TimezoneAdapter timezoneAdapter;
    private List<TimezoneInfo> timezoneInfos;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_time_zone;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        getToolbar().setTittle("Camera time zone");
        getToolbar().setLeftImg(R.mipmap.icon_back);

        timezoneRecycler = findViewById(R.id.rv_timezone);
        timezoneAdapter = new TimezoneAdapter(this);
        timezoneRecycler.setLayoutManager(new LinearLayoutManager(this));
        timezoneRecycler.addItemDecoration(new TimezoneDecoration(this));
        timezoneRecycler.setAdapter(timezoneAdapter);
    }

    @Override
    protected void initData() {
        timezoneInfos = FileUtil.getTimeZoneDataFromJson(this);
        timezoneAdapter.setData(timezoneInfos);
    }

    @Override
    protected void initListener() {
        timezoneAdapter.setOnItemClickListener(new TimezoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, TimezoneInfo bean) {
                finish();
            }
        });
    }
}
