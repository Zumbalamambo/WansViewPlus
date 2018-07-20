package net.ajcloud.wansviewplus.support.customview.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.HashMap;

/**
 * Created by mamengchao on 2018/07/20.
 * Function:
 */
public class DatePickPopupwindow extends PopupWindow {

    private static String[] dates = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
    private static String[] days = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
    private Context context;
    private View rootView;
    private LinearLayout ll_content;
    private TextView tv_month_left, tv_month_mid, tv_month_right;

    private String cdate;
    private HashMap<String, Boolean> record;
    private OnPopClickListener listener;

    public DatePickPopupwindow(Context context, String cdate) {
        super(context);
        this.context = context;
        this.cdate = cdate;
        initPop();
        initView();
    }

    private void initPop() {
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//        setAnimationStyle(R.style.TopInOutAnim);
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(false);
        setTouchable(true);
        setOutsideTouchable(false);
    }

    private void initView() {
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_date_pick, null);
        setContentView(rootView);
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
            if (day < 7) {
                tv_month_left.setVisibility(View.VISIBLE);
                tv_month_right.setVisibility(View.VISIBLE);
                tv_month_mid.setVisibility(View.GONE);
                tv_month_left.setText(DateUtil.getLastDateYM(cdate));
                tv_month_right.setText(DateUtil.getDateYM(cdate));
                int offset = 0;
                boolean isLastMonth = false;
                for (int i = ll_content.getChildCount(); i > 0; i--) {
                    if (ll_content.getChildAt(i - 1) instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) ll_content.getChildAt(i - 1);
                        int tmpDay = day - offset;
                        if (tmpDay <= 0) {
                            isLastMonth = true;
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month - 1, 0);
                            tmpDay = calendar.getActualMaximum(Calendar.DATE) + tmpDay;
                        }
                        offset++;
                        for (int j = 0; j < viewGroup.getChildCount(); j++) {
                            View view = viewGroup.getChildAt(j);
                            if (TextUtils.equals(view.getTag().toString(), "date")) {
                                ((TextView) view).setText(String.valueOf(tmpDay));
                            }
                            if (TextUtils.equals(view.getTag().toString(), "day")) {
                                if (i == ll_content.getChildCount()) {
                                    continue;
                                }
                                if (isLastMonth) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(year, month - 2, tmpDay);
                                    String date = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                                    ((TextView) view).setText(date);
                                } else {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(year, month - 1, tmpDay);
                                    String date = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                                    ((TextView) view).setText(date);
                                }
                            }
                            if (TextUtils.equals(view.getTag().toString(), "light")) {
                                //TODO
                            }
                        }
                    }
                }
            } else {
                tv_month_left.setVisibility(View.GONE);
                tv_month_right.setVisibility(View.GONE);
                tv_month_mid.setVisibility(View.VISIBLE);
                tv_month_mid.setText(DateUtil.getDateYM(cdate));
                int offset = 0;
                for (int i = ll_content.getChildCount(); i > 0; i--) {
                    if (ll_content.getChildAt(i - 1) instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup) ll_content.getChildAt(i - 1);
                        int tmpDay = day - offset;
                        offset++;
                        for (int j = 0; j < viewGroup.getChildCount(); j++) {
                            View view = viewGroup.getChildAt(j);
                            if (TextUtils.equals(view.getTag().toString(), "date")) {
                                ((TextView) view).setText(String.valueOf(tmpDay));
                            }
                            if (TextUtils.equals(view.getTag().toString(), "day")) {
                                if (i == ll_content.getChildCount()) {
                                    continue;
                                }
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month - 1, tmpDay);
                                String date = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                                ((TextView) view).setText(date);
                            }
                            if (TextUtils.equals(view.getTag().toString(), "light")) {
                                //TODO
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = ll_content.getChildCount(); i > 0; i--) {
            if (ll_content.getChildAt(i - 1) instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) ll_content.getChildAt(i - 1);
                for (int j = 0; j < viewGroup.getChildCount(); j++) {
                    View view = viewGroup.getChildAt(j);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                }
            }
        }
    }

    public interface OnPopClickListener {
        void onClick();
    }

    public void setPopClickListener(OnPopClickListener listener) {
        this.listener = listener;
    }
}
