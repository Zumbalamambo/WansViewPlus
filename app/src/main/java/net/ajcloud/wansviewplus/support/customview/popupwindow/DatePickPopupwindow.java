package net.ajcloud.wansviewplus.support.customview.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.ajcloud.wansviewplus.R;
import net.ajcloud.wansviewplus.support.utils.DateUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by mamengchao on 2018/07/20.
 * Function:
 */
public class DatePickPopupwindow {

    private static String[] days = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
    private PopupWindow popupWindow;
    private Context context;
    private View rootView;
    private LinearLayout ll_content;
    private TextView tv_month_left, tv_month_mid, tv_month_right;

    private String cdate;
    private String selectDate;
    private List<String> record;
    private OnPopClickListener listener;

    public DatePickPopupwindow(Context context, String selectDate) {
        this.context = context;
        this.cdate = DateUtil.getCurrentCDate();
        this.selectDate = selectDate;
        initPop();
        initView();
    }

    private void initPop() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
            popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setAnimationStyle(R.style.TopInOutAnim);
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setFocusable(true);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
        }
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_date_pick, null);
        popupWindow.setContentView(rootView);
    }

    private void initView() {
        ll_content = rootView.findViewById(R.id.ll_content);
        tv_month_left = rootView.findViewById(R.id.tv_month_left);
        tv_month_mid = rootView.findViewById(R.id.tv_month_mid);
        tv_month_right = rootView.findViewById(R.id.tv_month_right);
        if (TextUtils.isEmpty(cdate) || cdate.length() < 8) {
            return;
        }
        try {
            //tittle
            int year = Integer.parseInt(cdate.substring(0, 4));
            int month = Integer.parseInt(cdate.substring(4, 6));
            int day = Integer.parseInt(cdate.substring(6, 8));
            int offset = 0;
            if (day < 7) {
                tv_month_left.setVisibility(View.VISIBLE);
                tv_month_right.setVisibility(View.VISIBLE);
                tv_month_mid.setVisibility(View.GONE);
                tv_month_left.setText(DateUtil.getLastDateYM(cdate));
                tv_month_right.setText(DateUtil.getDateYM(cdate));
            } else {
                tv_month_left.setVisibility(View.GONE);
                tv_month_right.setVisibility(View.GONE);
                tv_month_mid.setVisibility(View.VISIBLE);
                tv_month_mid.setText(DateUtil.getDateYM(cdate));
            }
            for (int i = ll_content.getChildCount(); i > 0; i--) {
                if (ll_content.getChildAt(i - 1) instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) ll_content.getChildAt(i - 1);
                    int tmpDay = day - offset;
                    int tmpMonth = month;
                    int tmpYear = year;
                    if (tmpDay <= 0) {
                        tmpMonth = tmpMonth - 1;
                        if (tmpMonth == 0) {
                            tmpMonth = 12;
                            tmpYear = tmpYear - 1;
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(tmpYear, tmpMonth, 0);
                        tmpDay = calendar.getActualMaximum(Calendar.DATE) + tmpDay;
                    }
                    offset++;
                    for (int j = 0; j < viewGroup.getChildCount(); j++) {
                        View view = viewGroup.getChildAt(j);
                        if (TextUtils.equals(view.getTag().toString(), "date")) {
                            if (isToday(tmpYear, tmpMonth, tmpDay)) {
                                ((TextView) view).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            } else {
                                ((TextView) view).setTextColor(context.getResources().getColor(R.color.gray_first));
                            }
                            ((TextView) view).setText(String.valueOf(tmpDay));
                        }
                        if (TextUtils.equals(view.getTag().toString(), "day")) {
                            if (isToday(tmpYear, tmpMonth, tmpDay)) {
                                ((TextView) view).setTextColor(context.getResources().getColor(R.color.colorPrimary));
                            } else {
                                ((TextView) view).setTextColor(context.getResources().getColor(R.color.gray_second));
                            }
                            if (i == ll_content.getChildCount()) {
                                continue;
                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(tmpYear, tmpMonth - 1, tmpDay);
                            String date = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                            ((TextView) view).setText(date);
                        }
                        if (TextUtils.equals(view.getTag().toString(), "light")) {
                            if (hasRecord(tmpYear, tmpMonth, tmpDay)) {
                                view.setVisibility(View.VISIBLE);
                            } else {
                                view.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    final int finalYear = tmpYear;
                    final int finalMonth = tmpMonth;
                    final int finalDay = tmpDay;
                    viewGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(finalYear);
                            if (finalMonth < 10) {
                                stringBuilder.append("0");
                            }
                            stringBuilder.append(finalMonth);
                            if (finalDay < 10) {
                                stringBuilder.append("0");
                            }
                            stringBuilder.append(finalDay);
                            selectDate = stringBuilder.toString();
                            if (listener != null) {
                                listener.onClick(stringBuilder.toString());
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(View view) {
        initView();
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAsDropDown(view);
        }
    }

    private boolean hasRecord(int year, int month, int day) {
        try {
            for (String date : record
                    ) {
                int recordYear = Integer.parseInt(date.substring(0, 4));
                int recordMonth = Integer.parseInt(date.substring(4, 6));
                int recordDay = Integer.parseInt(date.substring(6, 8));
                if (recordYear == year &&
                        recordMonth == month &&
                        recordDay == day) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean isToday(int year, int month, int day) {
        try {
            int recordYear = Integer.parseInt(selectDate.substring(0, 4));
            int recordMonth = Integer.parseInt(selectDate.substring(4, 6));
            int recordDay = Integer.parseInt(selectDate.substring(6, 8));
            if (recordYear == year &&
                    recordMonth == month &&
                    recordDay == day) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void setRecord(List<String> record) {
        this.record = record;
    }

    public interface OnPopClickListener {
        void onClick(String date);
    }

    public void setPopClickListener(OnPopClickListener listener) {
        this.listener = listener;
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        if (popupWindow != null)
            popupWindow.setOnDismissListener(listener);
    }
}
