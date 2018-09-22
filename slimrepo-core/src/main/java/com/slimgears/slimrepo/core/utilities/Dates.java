// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Denis on 24-Apr-15
 *
 */
public class Dates {
    static final long MILLISECONDS_IN_SECOND = 1000;
    static final long MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_SECOND * 60;
    static final long MILLISECONDS_IN_HOUR = MILLISECONDS_IN_MINUTE * 60;
    static final long MILLISECONDS_IN_DAY = MILLISECONDS_IN_HOUR * 24;

    public static Date fromDate(int year, int month, int day) {
        return fromCalendar(Calendars.newInstance(year, month, day));
    }

    public static Date fromCalendar(Calendar calendar) {
        return new Date(calendar.getTimeInMillis());
    }

    public static Date now() {
        return new Date();
    }

    public static Date today() {
        return beginOfDay(now());
    }

    public static Date beginOfDay(Date date) {
        return alignToUnit(date, MILLISECONDS_IN_DAY);
    }

    public static Date beginOfHour(Date date) {
        return alignToUnit(date, MILLISECONDS_IN_HOUR);
    }

    public static Date beginOfMinute(Date date) {
        return alignToUnit(date, MILLISECONDS_IN_MINUTE);
    }

    public static Date yesterday() {
        return addDays(today(), -1);
    }

    public static Date tomorrow() {
        return addDays(today(), 1);
    }

    public double daysBetween(Date date1, Date date2) {
        return unitsBetween(date1, date2, MILLISECONDS_IN_DAY);
    }

    public double hoursBetween(Date date1, Date date2) {
        return unitsBetween(date1, date2, MILLISECONDS_IN_HOUR);
    }

    public double minutesBetween(Date date1, Date date2) {
        return unitsBetween(date1, date2, MILLISECONDS_IN_MINUTE);
    }

    public double secondsBetween(Date date1, Date date2) {
        return unitsBetween(date1, date2, MILLISECONDS_IN_SECOND);
    }

    public static Date addDays(Date date, long days) {
        return addUnits(date, days, MILLISECONDS_IN_DAY);
    }

    public static Date addHours(Date date, long hours) {
        return addUnits(date, hours, MILLISECONDS_IN_HOUR);
    }

    public static Date addMinutes(Date date, long minutes) {
        return addUnits(date, minutes, MILLISECONDS_IN_MINUTE);
    }

    public static Date addSeconds(Date date, long seconds) {
        return addUnits(date, seconds, MILLISECONDS_IN_SECOND);
    }

    static long addUnits(long time, long units, long millisecondsInUnit) {
        return time + units * millisecondsInUnit;
    }

    static double unitsBetween(long time1, long time2, long millisecondsInUnit) {
        return (double)(time2 - time1) / millisecondsInUnit;
    }

    static long alignToUnit(long time, long millisecondsInUnit) {
        return (time / millisecondsInUnit) * millisecondsInUnit;
    }

    private static Date addUnits(Date date, long units, long millisecondsInUnit) {
        return new Date(addUnits(date.getTime(), units, millisecondsInUnit));
    }

    private static double unitsBetween(Date date1, Date date2, long millisecondsInUnit) {
        return unitsBetween(date1.getTime(), date2.getTime(), millisecondsInUnit);
    }

    private static Date alignToUnit(Date date, long millisecondsInUnit) {
        return new Date(alignToUnit(date.getTime(), millisecondsInUnit));
    }
}
