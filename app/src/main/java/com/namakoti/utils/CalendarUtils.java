package com.namakoti.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by anusha on 3/8/2018.
 */

public class CalendarUtils {

    public static String getStringFromCalendar(String format, Calendar calendar){
        SimpleDateFormat sd = new SimpleDateFormat(format);
        String deviceStr = sd.format(calendar.getTime());

        return deviceStr;
    }

    /**
     * Convert the given String to Date object.
     */
    public static Date ConvertStingtoDate(String date, String dateFormat) {
        SimpleDateFormat _simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        try {
            System.out.println(date);
            return _simpleDateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String convertDateToStringObject(Date originalDate, String formattedString){
        DateFormat dateFormat = new SimpleDateFormat(formattedString);
        //to convert Date to String, use format method of SimpleDateFormat class.
        String strDate = dateFormat.format(originalDate);
        return strDate;

    }
    //Convert Calendar to Date
    public static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

    public static long milliseconds(String date,String dateFormat)//"yyyy-MM-dd"
    {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
    public static int countDifference(Date selectedDate, Date pillStartDate) {
        long differenceMillis = selectedDate.getTime() - pillStartDate.getTime();
        int daysDifference = (int) (differenceMillis / (1000 * 60 * 60 * 24));
        return daysDifference;
    }
    /**
     * This method is used to convert required date stringobject
     * @param dateString original date string
     * @return dateFormat required format date string
     */
    public static String getDateFormattedString(String originalFormat,String dateString,String targetDateFormat) {
        SimpleDateFormat mTargetFormat = new SimpleDateFormat(targetDateFormat, Locale.ENGLISH);
        SimpleDateFormat mOriginalFormat = new SimpleDateFormat(originalFormat, Locale.ENGLISH);

        String reqstring = null;
        try {
            reqstring = mTargetFormat.format(mOriginalFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reqstring;
    }

    public static String getTodayString(String format) {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedDate = df.format(c);

        return formattedDate;

    }

    public static int calculateDifference(String end_datetime) {
//        Date startDate = CalendarUtils.ConvertStingtoDate(details.start_datetime, Constants.DATE_TIME_FORMAT);
        Date endDate = CalendarUtils.ConvertStingtoDate(end_datetime, Constants.DATE_TIME_FORMAT);
        int daysDifference = CalendarUtils.countDifference(endDate,new Date());
        return  daysDifference;

    }

    /**
     * Convert the given String to Date object.
     */
    public static Date ConvertStingToDate(String date, String dateFormat) {
        SimpleDateFormat _simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        try {
            System.out.println(date);
            return _simpleDateFormat.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String updateTime(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
//        mTimeTv.setText(aTime);
        return aTime;
    }

    public static String getCurrentTime(String timeFormat) {
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);

        String time = simpleDateFormat.format(calander.getTime());
        return time;
    }
}
