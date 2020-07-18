package me.huaisu.common.android.utils;

import android.util.Log;

public class Logger {

    public static void i(String tag, String log) {
        Log.i(tag, log);
    }

    public static void e(String tag, Throwable e) {
        Log.e(tag, Log.getStackTraceString(e));
    }

    public static void w(String tag, Throwable e) {
        Log.w(tag, Log.getStackTraceString(e));
    }
}
