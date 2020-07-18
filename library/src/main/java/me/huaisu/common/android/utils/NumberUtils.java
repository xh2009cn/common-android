package me.huaisu.common.android.utils;

import android.text.TextUtils;


public class NumberUtils {
    private static final String TAG = "NumberUtils";

    private NumberUtils() {
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static long parseLong(String s) {
        return parseLong(s, 0);
    }

    public static float parseFloat(String s) {
        return parseFloat(s, 0.0f);
    }

    public static double parseDouble(String s) {
        return parseDouble(s, 0.0);
    }

    public static int parseInt(String s, int defaultValue) {
        if (!TextUtils.isEmpty(s)) {
            if (!TextUtils.isEmpty(s)) {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    Logger.w(TAG, e);
                }
            }
        }
        return defaultValue;
    }

    public static long parseLong(String s, long defaultValue) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                Logger.w(TAG, e);
            }
        }
        return defaultValue;
    }

    public static float parseFloat(String s, float defaultValue) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                Logger.w(TAG, e);
            }
        }
        return defaultValue;
    }

    public static double parseDouble(String s, double defaultValue) {
        if (!TextUtils.isEmpty(s)) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                Logger.w(TAG, e);
            }
        }
        return defaultValue;
    }
}
