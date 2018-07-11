package net.ajcloud.wansviewplus.support.utils;

/**
 * Created by mamengchao on 2018/07/11.
 * Function:
 */
public class DateUtil {

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
}
