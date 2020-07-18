package me.huaisu.common.android.utils;

import java.util.TimeZone;

public class DateUtil {

    private final static long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    public static long getZeroClockTime(long timeInMillis) {
        return timeInMillis - ((timeInMillis + TimeZone.getDefault().getRawOffset()) % DAY_IN_MILLIS);
    }
}
