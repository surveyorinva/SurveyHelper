package com.survlogic.surveyhelper.utils;

import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendarUtils {

    /**
            * Transforms Calendar to ISO 8601 string.
     **/
    public static String fromCalendar(final java.util.Calendar calendar) {
        // TODO: move this method to DateUtils
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Gets current date and time formatted as ISO 8601 string.
     **/
    public static String now() {
        // TODO: move this method to DateUtils
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Transforms ISO 8601 string to Calendar.
     **/
    public static Calendar toCalendar(final String iso8601string) throws ParseException {
        // TODO: move this method to DateUtils
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);
        } catch (IndexOutOfBoundsException e) {
            // throw new org.apache.http.ParseException();
            e.printStackTrace();
        }

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param time ISO formatted time when the event occurred in local time zone.
     * @deprecated Totally bloated code.
     * Calculates the elapsed time after the given parameter date.
     **/
    public static String getElapsedTime(String time) {
        TimeZone defaultTimeZone = TimeZone.getDefault();

        // TODO: its advisable not to use this method as it changes the
        // timezone.
        // Change it at some time in future.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Date eventTime = DateUtils.parseDate(time);

        Date currentDate = new Date();

        long diffInSeconds = (currentDate.getTime() - eventTime.getTime()) / 1000;
        String elapsed = "";
        long seconds = diffInSeconds;
        long mins = diffInSeconds / 60;
        long hours = diffInSeconds / (60 * 60);
        long days = diffInSeconds / 86400;
        long weeks = diffInSeconds / 604800;
        long months = diffInSeconds / 2592000;

        // Log.v( TAG, "#getElapsedTime seconds: " + seconds + " mins: " + mins
        // + " hours: " + hours + " days: " + days );

        if (seconds < 120) {
            elapsed = "a min ago";
        } else if (mins < 60) {
            elapsed = mins + " mins ago";
        } else if (hours < 24) {
            elapsed = hours + " " + (hours > 1 ? "hrs" : "hr") + " ago";
        } else if (hours < 48) {
            elapsed = "a day ago";
        } else if (days < 7) {
            elapsed = days + " days ago";
        } else if (weeks < 5) {
            elapsed = weeks + " " + (weeks > 1 ? "weeks" : "week") + " ago";
        } else if (months < 12) {
            elapsed = months + " " + (months > 1 ? "months" : "months") + " ago";
        } else {
            elapsed = "more than a year ago";
        }

        TimeZone.setDefault(defaultTimeZone);

        return elapsed;
    }

    /**
     * Gets the name of the day of the week.
     *
     * @param date ISO format date
     * @return The name of the day of the week
     **/
    public static String getDayOfWeek(String date) {
        // TODO: move to DateUtils
        Date dateDT = DateUtils.parseDate(date);

        if (dateDT == null) {
            return null;
        }

        // Get current date
        Calendar c = Calendar.getInstance();
        // it is very important to
        // set the date of
        // the calendar.
        c.setTime(dateDT);
        int day = c.get(Calendar.DAY_OF_WEEK);

        String dayStr = null;

        switch (day) {

            case Calendar.SUNDAY:
                dayStr = "Sunday";
                break;

            case Calendar.MONDAY:
                dayStr = "Monday";
                break;

            case Calendar.TUESDAY:
                dayStr = "Tuesday";
                break;

            case Calendar.WEDNESDAY:
                dayStr = "Wednesday";
                break;

            case Calendar.THURSDAY:
                dayStr = "Thursday";
                break;

            case Calendar.FRIDAY:
                dayStr = "Friday";
                break;

            case Calendar.SATURDAY:
                dayStr = "Saturday";
                break;
        }

        return dayStr;
    }


}
