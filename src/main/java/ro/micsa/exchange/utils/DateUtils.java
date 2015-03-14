/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author georgian
 */
public class DateUtils {

    /**
     * Romania time zone
     */
    public static String RO_TZ = "GMT+2";
    /**
     * Use this for logging application activities in Romania time zone.
     */
    public static SimpleDateFormat RO_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    /**
     * Use this for last change date for dates that are already in Romania time zone.
     */
    public static SimpleDateFormat SDF_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * Use this for last change date. It adds the Romania time zone.
     */
    public static SimpleDateFormat RO_SDF_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * Use this for date representation inside this project.
     */
    public static SimpleDateFormat DATE_SDF = new SimpleDateFormat("yyyy-MM-dd");

    static {
        RO_SDF.setTimeZone(TimeZone.getTimeZone(DateUtils.RO_TZ));
        RO_SDF_HH_mm.setTimeZone(TimeZone.getTimeZone(DateUtils.RO_TZ));
        DATE_SDF.setTimeZone(TimeZone.getTimeZone(DateUtils.RO_TZ));
    }

    /**
     * Check if the date is Saturday or Sunday.
     * @param date
     * @return 
     */
    public static boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(RO_TZ));
        calendar.setTime(date);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }

    public static boolean currentDateIsWeekend() {
        return isWeekend(new Date());
    }

    /**
     * 
     * Returns the last working day. For Monday it returns last Friday.
     * 
     * @param date
     * @return 
     */
    public static Date getPreviousBusinessDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(RO_TZ));
        calendar.setTime(date);
        int daysOffset = 1;
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            daysOffset = 2;
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            daysOffset = 3;
        }
        calendar.add(Calendar.DAY_OF_YEAR, -daysOffset);
        return calendar.getTime();
    }

    public static List<String> getLastNDays(String day, int count){
        ArrayList<String> days = new ArrayList<String>();
        Date date;
        try {
            date = DATE_SDF.parse(day);
        } catch (ParseException ex) {
            return days;
        }
        Calendar calendar = getCalendarInRoTimeZone(date);
        for(int index = 0; index < count; index ++){
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, -2);
            }else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
            Date previousdate = calendar.getTime();
            days.add(DATE_SDF.format(previousdate));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        Collections.reverse(days);
        return days;
    }
    
    public static String getCurrentYearAsString() {
        Calendar calendar = getCalendarInRoTimeZone(new Date());        
        return "" + calendar.get(Calendar.YEAR);
    }
    
    public static String getCurrentDateInSDFFormat() {
        return DATE_SDF.format(getCalendarInRoTimeZone(new Date()).getTime());
    }

    public static String getCurrentTimeInSDFFormat() {
        return RO_SDF_HH_mm.format(getCalendarInRoTimeZone(new Date()).getTime());
    }
    
    public static Calendar getCalendarInRoTimeZone(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(RO_TZ));
        calendar.setTime(date);
        
        return calendar;
    }
}
