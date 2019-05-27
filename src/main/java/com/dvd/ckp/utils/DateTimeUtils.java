package com.dvd.ckp.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class DateTimeUtils {

    private static final Logger logger = Logger.getLogger(DateTimeUtils.class);

    /*
	 * @todo: Action chuyen tu dang date sang string
	 * 
	 * @param: pstrDate
	 * 
	 * @param: pstrPattern
     */
    public static String convertDateToString(Date pstrDate, String pstrPattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pstrPattern);
        try {
            if (pstrDate == null) {
                return "";
            }
            return dateFormat.format(pstrDate);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /*
	 * @todo: Action chuyen tu dang string sang date
	 * 
	 * @param: pstrDate
	 * 
	 * @param: pstrPattern
     */
    public static Date convertStringToTime(String pstrDate, String pstrPattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pstrPattern);
        try {
            return dateFormat.parse(pstrDate);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static Long getDifferenceDay(Date startDate, Date endDate) {
        Long diff = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    public static int getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static java.sql.Date convertStringToDateSql(String pstrDate, String pstrPattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pstrPattern);
        try {
            Date date = dateFormat.parse(pstrDate);
            java.sql.Date dateSql = new java.sql.Date(date.getTime());
            return dateSql;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean compareDate(Date startDate, Date endDate) {
        return startDate.after(endDate);
    }

    public static boolean compareMonth(Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        int monthStartDate = cal.get(Calendar.MONTH);

        cal.setTime(endDate);

        int monthEndDate = cal.get(Calendar.MONTH);

        return (monthStartDate == monthEndDate);
    }

    public static int getTime(Date date, int type) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int value = 0;
        switch (type) {
            case 1:
                value = cal.get(Calendar.DATE);
                break;
            case 2:
                value = cal.get(Calendar.MONTH) + 1;
                break;
            case 3:
                value = cal.get(Calendar.YEAR);
                break;
            default:
                value = 0;
                break;
        }
        return value;

    }

    public static String formatDate(String value) {
        if (!StringUtils.isValidString(value)) {
            return "";
        }
        String[] array = value.split("/");

        return array[2] + array[1] + array[0];

    }

    public static Date addMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static String getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.DATE));
    }

    public static List<Date> getAllDay(String fromDate, String toDate) {
        Date vFromdate = convertStringToTime(fromDate, "yyyyMMdd");
        Date vTodate = convertStringToTime(toDate, "yyyyMMdd");
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(vFromdate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(vTodate);

        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    public static void main(String[] arg) {

        String value = formatDate("20/04/2018");
        System.out.println(getDay(convertStringToTime(value, "yyyyMMdd")));

    }
}
