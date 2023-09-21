package com.example.palette.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String TYPE_ONE = "yyyy-MM-dd HH:mm:ss";
    public static final String TYPE_TWO = "yyyy-MM-dd";

    /**
     * 获取当前时间yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getFullNowTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(TYPE_ONE);
        return sdf.format(date);
    }

    /**
     * 获取当前时间yyyy-MM-dd
     *
     * @return
     */
    public static String getSimpleNowTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(TYPE_TWO);
        return sdf.format(date);
    }

    /**
     * 获取自定义格式时间
     *
     * @param format
     * @return
     */
    public static String getCustomNowTime(String format) {
        if (TextUtils.isEmpty(format)) {
            return null;
        } else {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
    }

    /**
     * 将字符串日期转Date
     *
     * @param dateString
     * @param format
     * @return
     */
    public static Date getDataFromString(String dateString, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Date转字符串日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String getStringFromDate(Date date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定日期对应星期
     *
     * @param date
     * @return 1-7
     */
    public static int getWeekByDate(Date date) {
        int[] weeks = {7, 1, 2, 3, 4, 5, 6};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (week < 0) {
            week = 0;
        }
        return weeks[week];
    }

    /**
     * 获取某月第一天
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return cal.getTime();
    }

    /**
     * 获取某月最后一天
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return cal.getTime();
    }

    /**
     * 获取某月的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if ((year % 4 == 0 && year % 100 == 0) || year % 400 != 0) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return 31;
        }
    }

    /**
     * 设置时间
     *
     * @param year
     * @param month
     * @param day
     */
    public static void setCalendar(Calendar calendar, int year, int month, int day) {
        calendar.set(year, month - 1, day);
    }

    /**
     * 设置前一天
     *
     * @param cal
     * @return
     */
    public static Calendar setBefore(Calendar cal) {
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        return cal;
    }

    /**
     * 设置后一天
     *
     * @param cal
     * @return
     */
    public static Calendar setAfter(Calendar cal) {
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day + 1);
        return cal;
    }

    /**
     * 获取年
     *
     * @return
     */
    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取月
     *
     * @return
     */
    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日
     *
     * @return
     */
    public static int getDate(Calendar calendar) {
        return calendar.get(Calendar.DATE);
    }

}
