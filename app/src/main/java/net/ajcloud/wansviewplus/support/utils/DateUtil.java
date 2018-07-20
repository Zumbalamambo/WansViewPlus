package net.ajcloud.wansviewplus.support.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mamengchao on 2018/07/11.
 * Function:
 */
public class DateUtil {

    private static SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
    private static String[] dates = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};

    /**
     * @param cdata 20180507--->May.07.2018
     */
    public static String getFormatDate(String cdata) {
        if (cdata.length() != 8) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String year = cdata.substring(0, 4);
        String month = cdata.substring(4, 6);
        String day = cdata.substring(6, 8);

        result.append(dates[Integer.parseInt(month) - 1]);
        result.append(".");
        result.append(day);
        result.append(".");
        result.append(year);
        return result.toString();
    }

    /**
     * @param cdata 20180507--->May.07
     */
    public static String getSimpleFormatDate(String cdata) {
        if (cdata.length() != 8) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String year = cdata.substring(0, 4);
        String month = cdata.substring(4, 6);
        String day = cdata.substring(6, 8);

        result.append(dates[Integer.parseInt(month) - 1]);
        result.append(".");
        result.append(day);
        return result.toString();
    }

    /**
     * @param ctime 20180710183636--->18:36
     */
    public static String getFormatTime(String ctime) {
        if (ctime.length() != 14) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        String year = ctime.substring(0, 4);
        String month = ctime.substring(4, 6);
        String day = ctime.substring(6, 8);
        String hour = ctime.substring(8, 10);
        String min = ctime.substring(10, 12);
        String second = ctime.substring(12, 14);

        result.append(hour);
        result.append(":");
        result.append(min);
        return result.toString();
    }

    /**
     * 20180507--->May.07.2018
     */
    public static String getCurrentDate() {
        Date date = new Date();
        String cdata = sd.format(date);
        if (cdata.length() != 8) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String year = cdata.substring(0, 4);
        String month = cdata.substring(4, 6);
        String day = cdata.substring(6, 8);

        result.append(dates[Integer.parseInt(month) - 1]);
        result.append(".");
        result.append(day);
        result.append(".");
        result.append(year);
        return result.toString();
    }

    /**
     * 20180507
     */
    public static String getCurrentCDate() {
        Date date = new Date();
        return sd.format(date);
    }

    //获得当天0点时间
    public static long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //获得当天24点时间
    public static long getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 20180507--->May.2018
     */
    public static String getDateYM(String cdata) {
        if (cdata.length() != 8) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String year = cdata.substring(0, 4);
        String month = cdata.substring(4, 6);

        result.append(dates[Integer.parseInt(month) - 1]);
        result.append(".");
        result.append(year);
        return result.toString();
    }

    /**
     * 20180507--->May.2018 获取上个月
     */
    public static String getLastDateYM(String cdata) {
        if (cdata.length() != 8) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String year = cdata.substring(0, 4);
        String month = cdata.substring(4, 6);

        int monthInt = Integer.parseInt(month);
        int yearInt = Integer.parseInt(year);
        if (monthInt == 1) {
            result.append(dates[11]);
            result.append(".");
            result.append(yearInt - 1);
        } else {
            result.append(dates[Integer.parseInt(month) - 1 - 1]);
            result.append(".");
            result.append(year);
        }
        return result.toString();
    }
}
