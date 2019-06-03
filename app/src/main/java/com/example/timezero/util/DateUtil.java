package com.example.timezero.util;

import com.example.timezero.model.Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static Calendar increaseDate(Calendar calendar) {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static Calendar decreaseDate(Calendar calendar) {
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar;
    }

    //for database query
    public static Date getDateFromString(String stringDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy HH:mm");
        Date date = new Date();
        try {
            date = formatter.parse(stringDate);
        } catch (Exception e) {
            //if stringDate can't be parsed, then will return the current date
            //but parse shouldn't throw any exception before it is standard text
        }
        return date;
    }

    //for database insertion
    public static String getStringFromDate(Date date) {
        String stringDate;
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy HH:mm");
        try {
            stringDate = formatter.format(date);
        } catch (Exception e) {
            stringDate = null;
        }
        return stringDate;
    }

    public static String getStringDateFromDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd yyyy");
        String dateInString;

        try {
            dateInString = formatter.format(date);

        } catch (Exception e) {
            Date date1 = new Date();
            dateInString = formatter.format(date1);
        }
        return dateInString;
    }

    public static String getSimpleDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        String dateInString;

        try {
            dateInString = formatter.format(date);

        } catch (Exception e) {
            Date date1 = new Date();
            dateInString = formatter.format(date1);
        }
        return dateInString;
    }

    public static String getStringTimeFromDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String timeInString;
        try {
            timeInString = formatter.format(date);
        } catch (Exception e) {
            timeInString = "Time format doesn't match";
        }
        return timeInString;
    }

    public static String getTodayString(Calendar calendar) {
        Date date = calendar.getTime();
        if (isToday(calendar)) {
            return "TODAY";
        } else {
            return DateUtil.getSimpleDateString(date);
        }
    }

    public static String getHourFromDate(Date date) {
        String formattedTime;
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
            formattedTime = simpleDateFormat.format(date);
        } else {
            formattedTime = null;
        }
        return formattedTime;

    }

    public static String getMinuteFromDate(Date date) {
        String formattedTime;
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");
            formattedTime = simpleDateFormat.format(date);
        } else {
            formattedTime = null;
        }
        return formattedTime;
    }

    public static Date getDate(String stringDate) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        try {
            date = formatter.parse(stringDate);
        } catch (Exception e) {
        }
        return date;
    }

    public static long getMilisTillEvent(Activity activity){
        Date activityDate = activity.getStartDate();
        return activityDate.getTime() - System.currentTimeMillis();
    }
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getSimpleDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String stringDate = "";
        try {
            stringDate = formatter.format(date);
        } catch (Exception e) {

        }
        return stringDate;
    }

    public static String getDayOfWeek(int day) {
        switch (day) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "What happened with normal days?";
        }
    }

    public static String getDayOfWeek(long day){
        switch ((int) day) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "What happened with normal days?";
        }
    }
}
