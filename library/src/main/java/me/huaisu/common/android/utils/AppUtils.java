package me.huaisu.common.android.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

public class AppUtils {

    public static boolean isAppOnForeground(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        try {
            appProcesses = activityManager.getRunningAppProcesses();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (appProcesses == null || appProcesses.isEmpty()) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            String processName = appProcess != null ? appProcess.processName : null;
            if (!TextUtils.isEmpty(processName)
                    && processName.startsWith(context.getPackageName())
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
