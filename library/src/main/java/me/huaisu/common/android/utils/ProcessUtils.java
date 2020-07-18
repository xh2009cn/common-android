package me.huaisu.common.android.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import me.huaisu.common.android.App;
import me.huaisu.common.android.thread.WorkThreadPool;

public class ProcessUtils {

    /**
     * 判断进程是否存在
     */
    public static boolean isProcessAlive(Context context, String processName) {
        boolean isSelfProcess = !TextUtils.isEmpty(processName) && processName.startsWith(context.getPackageName());
        if (isSelfProcess) {
            return isProcessAliveByRunningApps(context, processName);
        } else {
            boolean alive = isProcessAliveByRunningService(context, processName);
            if (!alive) {
                alive = isProcessAliveByShell(context, processName);
            }
            return alive;
        }
    }

    /**
     * RunningApps判断进程是否存在
     * 最快，8.0手机耗时2ms左右
     * 只能拿到自己的进程
     */
    public static boolean isProcessAliveByRunningApps(Context context, String processName) {
        if (context == null || TextUtils.isEmpty(processName)) {
            return false;
        }
        boolean isSelfProcess = processName.startsWith(context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isSelfProcess) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> runningApps = null;
        try {
            runningApps = activityManager.getRunningAppProcesses();
        } catch (Throwable e) {
            // ignore
        }
        if (runningApps == null || runningApps.isEmpty()) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo runningApp : runningApps) {
            if (TextUtils.equals(runningApp.processName, processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * RunningService判断进程是否存在
     * 比较快，8.0手机上耗时6ms左右
     * 8.0及以上只能拿到自己的进程
     */
    public static boolean isProcessAliveByRunningService(Context context, String processName) {
        if (context == null || TextUtils.isEmpty(processName)) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> runningServices = null;
        try {
            runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        } catch (Throwable e) {
            //ignore
        }
        if (runningServices == null || runningServices.isEmpty()) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (TextUtils.equals(runningServiceInfo.process, processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ps判断进程是否存在
     * 较慢，8.0手机耗时30ms左右
     * 8.0以上只能拿到自己的进程
     */
    public static boolean isProcessAliveByShell(Context context, final String processName) {
        if (TextUtils.isEmpty(processName)) {
            return false;
        }
        boolean isSelfProcess = processName.startsWith(context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isSelfProcess) {
            return false;
        }
        String[] cmd = new String[]{"/system/bin/sh", "-c", "ps | grep \"" + processName + "\""};
        BufferedReader in = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);

            flushErrorStream(p);

            in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                int last = line.lastIndexOf(" ");
                if (last != -1) {
                    String lastStr = line.substring(last);
                    if (lastStr != null) {
                        lastStr = lastStr.trim();
                    }
                    if (TextUtils.equals(processName, lastStr)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static int getPid(Context context, String processName) {
        if (context == null || TextUtils.isEmpty(processName)) {
            return 0;
        }
        boolean isSelfProcess = processName.startsWith(context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isSelfProcess) {
            return 0;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return 0;
        }
        List<ActivityManager.RunningServiceInfo> runningServices = null;
        try {
            runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        } catch (Throwable e) {
            //ignore
        }
        if (runningServices == null || runningServices.isEmpty()) {
            return 0;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (TextUtils.equals(runningServiceInfo.process, processName)) {
                return runningServiceInfo.pid;
            }
        }
        return 0;
    }

    /**
     * 为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞
     */
    private static void flushErrorStream(final Process p) {
        try {
            BufferedReader bReader = null;
            InputStream is = p.getErrorStream();
            if (is != null) {
                bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), "UTF-8"));
            }
            final BufferedReader finalBReader = bReader;
            WorkThreadPool.get().post(() -> {
                if (finalBReader == null)
                    return;
                try {
                    while (finalBReader.readLine() != null) {
                        //Thread.sleep(200);
                    }
                } catch (Exception ex) {
                } finally {
                    try {
                        finalBReader.close();
                    } catch (IOException e) {
                    }
                }
            });

        } catch (Throwable t) {
        }
    }

    private static final int INVALID_OOM_ADJ = -1;

    @WorkerThread
    public static int getOomAdj() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return INVALID_OOM_ADJ;
        }
        return getOomAdj(android.os.Process.myPid());
    }

    @WorkerThread
    public static int getOomAdj(String processName) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return INVALID_OOM_ADJ;
        }
        int pid = getPid(App.get(), processName);
        if (pid == 0) {
            return INVALID_OOM_ADJ;
        }
        return getOomAdj(pid);
    }

    @WorkerThread
    public static int getOomAdj(int pid) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return INVALID_OOM_ADJ;
        }
        return readInt("/proc/" + pid + "/oom_adj");
    }

    @WorkerThread
    public static int getOomScore() {
        return getOomScore(android.os.Process.myPid());
    }

    @WorkerThread
    public static int getOomScore(String processName) {
        int pid = getPid(App.get(), processName);
        if (pid == 0) {
            return INVALID_OOM_ADJ;
        }
        return getOomScore(pid);
    }

    @WorkerThread
    public static int getOomScore(int pid) {
        return readInt("/proc/" + pid + "/oom_score");
    }

    private static int readInt(String filePath) {
        try {
            File file = new File(filePath);
            String ret = IOUtils.readToString(file);
            if (!TextUtils.isEmpty(ret)) {
                return Integer.parseInt(ret);
            }
        } catch (Exception ignore) {

        }
        return INVALID_OOM_ADJ;
    }

    @Nullable
    private static Boolean is64Bit = null;

    public static boolean is64Process() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return android.os.Process.is64Bit();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return checkAndroid5();
        }
        return false;
    }

    private static boolean checkAndroid5() {
        if (is64Bit == null) {
            try {
                Class<?> clazz = Class.forName("dalvik.system.VMRuntime");
                Method method = clazz.getMethod("getRuntime");
                Object vmRuntime = method.invoke(null);
                Method methodIs64Bit = clazz.getMethod("is64Bit");
                Object is64 = methodIs64Bit.invoke(vmRuntime);
                if (is64 instanceof Boolean) {
                    is64Bit = (Boolean) is64;
                }
            } catch (Exception e) {
                is64Bit = false;
            }
        } // end reflect
        if (is64Bit == null) {
            is64Bit = false;
        }
        return is64Bit;
    }
}
