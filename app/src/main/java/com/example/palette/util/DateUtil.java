package com.example.palette.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String TYPE_ONE = "yyyy-MM-DD HH:mm:ss";

    /**
     * 获取当前时间
     * @param format
     * @return
     */
    public static String getNowTime(String format){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }

    /**
     * 将字符串日期转Date
     * @param dateString
     * @param format
     * @return
     */
    public static Date stringToDate(String dateString,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Date转字符串日期
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取指定日期对应星期
     * @param date
     * @return
     */
    public static String getWeekByDate(Date date){
        String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(week<0){
            week=0;
        }
        return weeks[week];
    }

    /**
     * 获取某月的天数
     * @param year
     * @param month
     * @return
     */
    public static int daysOfMonth(int year,int month){
        switch (month){
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
                if((year%4==0 && year%100==0)||year%400!=0){
                    return 29;
                }else {
                    return 28;
                }
            default:
                return 31;
        }
    }

    /**
     * 设置时间
     * @param year
     * @param month
     * @param day
     */
    public static void setCalendar(Calendar calendar,int year,int month,int day){
        calendar.set(year,month-1,day);
    }

    /**
     *设置前一天
     * @param cal
     * @return
     */
    public static Calendar setBefore(Calendar cal){
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE,day-1);
        return cal;
    }

    /**
     * 设置后一天
     * @param cal
     * @return
     */
    public static Calendar setAfter(Calendar cal){
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE,day+1);
        return cal;
    }

    /**
     * 获取年
     * @return
     */
    public static int getYear(Calendar calendar){
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取月
     * @return
     */
    public static int getMonth(Calendar calendar){
        return calendar.get(Calendar.MONTH)+1;
    }

    /**
     * 获取日
     * @return
     */
    public static int getDate(Calendar calendar){
        return calendar.get(Calendar.DATE);
    }

}
