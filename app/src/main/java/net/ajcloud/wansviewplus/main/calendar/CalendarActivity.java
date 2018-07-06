package net.ajcloud.wansviewplus.main.calendar;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;

import com.example.library_calendar.CalendarController;
import com.example.library_calendar.MonthByWeekFragment;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.main.application.BaseActivity;
import net.ajcloud.wansviewplus.support.customview.MyToolbar;

import java.util.HashMap;

public class CalendarActivity extends BaseActivity implements CalendarController.EventHandler {

    private CalendarController mController;
    MonthByWeekFragment monthFrag;
    HashMap<String, Boolean> recordArray;
    boolean isShowNextDay = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_calendar;
    }

    @Override
    protected boolean hasTittle() {
        return true;
    }

    @Override
    protected void initView() {
        mController = CalendarController.getInstance(this);
        MyToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTittle("Calendar");
            toolbar.setLeftImg(R.mipmap.ic_back);
        }

        isShowNextDay = getIntent().getBooleanExtra("isShowNextDay", false);
        long ctime = System.currentTimeMillis();
        /* get last time
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date d = formatter.parse("20150506");
            ctime = d.getTime();
        } catch (ParseException ex) {

        } */
//        try {
//            monthFrag = new MonthByWeekFragment(CameraUtils.getCurrentTime(getIntent().getStringExtra("oid")), true);
//            monthFrag.setTimezone(AppApplication.getInstance(), CameraUtils.getTimezoneId(getIntent().getStringExtra("oid")));
//        } catch (Exception ex) {
//            ex.printStackTrace();
        monthFrag = new MonthByWeekFragment(ctime, true);
//        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recordArray = (HashMap<String, Boolean>) bundle.getSerializable("serializable");
            monthFrag.setRecordMap(recordArray);
        }

        //Log.e("lgwell", "calendar time id:" + TimeZone.getDefault().getID());

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.cal_frame, monthFrag).commit();
        mController.registerEventHandler(R.id.cal_frame, monthFrag);

        mController.registerFirstEventHandler(0, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public long getSupportedEventTypes() {
        return CalendarController.EventType.GO_TO |
                CalendarController.EventType.VIEW_EVENT |
                CalendarController.EventType.UPDATE_TITLE;
    }

    @Override
    public void handleEvent(CalendarController.EventInfo event) {
        if (event.eventType == CalendarController.EventType.GO_TO) {
            Time curtime = new Time();
            curtime.setToNow();
            if (isShowNextDay) {
                if (curtime.yearDay + 1 < event.selectedTime.yearDay) {
                    return;
                }
            } else if (curtime.before(event.selectedTime)) {
                return;
            }

            Intent intent = new Intent();
            Time time = event.selectedTime;
            intent.putExtra("calendar", String.format("%04d-", time.year) +
                    String.format("%02d-", time.month + 1) +
                    String.format("%02d", time.monthDay));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void finish() {
        mController.removeInstance(this);
        super.finish();
    }

    @Override
    public void eventsChanged() {

    }
}
