// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;

import java.util.Calendar;

/**
 * Created by Denis on 24-Apr-15
 * <File Description>
 */
public class Calendars {
    public static Calendar now() {
        return Calendar.getInstance();
    }

    public static Calendar newInstance(int year, int month, int day) {
        Calendar instance = now();
        instance.set(year, month, day);
        return instance;
    }

    public static Calendar today() {
        return beginOfDay(now());
    }

    public static Calendar yesterday() {
        return addDays(today(), -1);
    }

    public static Calendar tomorrow() {
        return addDays(today(), 1);
    }

    public static Calendar thisMonth() {
        return beginOfMonth(now());
    }

    public static Calendar thisYear() {
        return beginOfYear(now());
    }

    public static Calendar addDays(Calendar calendar, long days) {
        return addUnits(calendar, days, Dates.MILLISECONDS_IN_DAY);
    }

    public static Calendar addHours(Calendar calendar, long hours) {
        return addUnits(calendar, hours, Dates.MILLISECONDS_IN_HOUR);
    }

    public static Calendar addMinutes(Calendar calendar, long minutes) {
        return addUnits(calendar, minutes, Dates.MILLISECONDS_IN_MINUTE);
    }

    public static Calendar beginOfYear(Calendar calendar) {
        return newInstance(calendar.get(Calendar.YEAR), 1, 1);
    }

    public static Calendar beginOfMonth(Calendar calendar) {
        return newInstance(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
    }

    public static Calendar beginOfDay(Calendar calendar) {
        return alignToUnits(calendar, Dates.MILLISECONDS_IN_DAY);
    }

    public static Calendar beginOfHour(Calendar calendar) {
        return alignToUnits(calendar, Dates.MILLISECONDS_IN_HOUR);
    }

    public static Calendar beginOfMinute(Calendar calendar) {
        return alignToUnits(calendar, Dates.MILLISECONDS_IN_MINUTE);
    }

    public static Calendar fromMilliseconds(long milliseconds) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(milliseconds);
        return instance;
    }

    private static Calendar alignToUnits(Calendar calendar, long millisecondsInUnit) {
        return fromMilliseconds(Dates.alignToUnit(calendar.getTimeInMillis(), millisecondsInUnit));
    }

    private static double unitsBetween(Calendar c1, Calendar c2, long millisecondsInUnit) {
        return Dates.unitsBetween(c1.getTimeInMillis(), c2.getTimeInMillis(), millisecondsInUnit);
    }

    private static Calendar addUnits(Calendar calendar, long units, long millisecondsInUnit) {
        return fromMilliseconds(Dates.addUnits(calendar.getTimeInMillis(), units, millisecondsInUnit));
    }
}
