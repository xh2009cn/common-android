package me.huaisu.common.android.hook;

import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;

import me.huaisu.common.android.utils.Logger;
import me.huaisu.common.android.utils.ReflectUtils;

public class ActivityThreadHook {

    private static final String TAG = "ActivityThreadHook";

    private static Object sActivityThread;
    private static Handler sH;

    public static Object getActivityThread() {
        if (sActivityThread != null) {
            return sActivityThread;
        }
        try {
            Class<?> clz = Class.forName("android.app.ActivityThread");
            sActivityThread = ReflectUtils.invokeStaticMethod(clz, "currentActivityThread");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sActivityThread;
    }

    public static void hookActivityThread() {
        try {
            Handler mH = getHandler();
            Field field = Handler.class.getDeclaredField("mCallback");
            field.setAccessible(true);
            Handler.Callback origin = (Handler.Callback) field.get(mH);
            field.set(mH, new HackCallback(origin));
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    private static Handler getHandler() {
        if (sH == null) {
            Object activityThread = getActivityThread();
            sH = ReflectUtils.getField(activityThread, "mH");
        }
        return sH;
    }

    public static class HackCallback implements Handler.Callback {

        private Handler.Callback origin;

        public HackCallback(Handler.Callback origin) {
            this.origin = origin;
        }

        @Override
        public boolean handleMessage(Message msg) {
            Logger.i(TAG, "handleMessage " + (msg != null ? msg.toString() : null));
            if (msg == null) {
                return true;
            }
            if (origin != null && origin.handleMessage(msg)) {
                return true;
            }
            try {
                sH.handleMessage(msg);
            } catch (Throwable e) {
                Logger.e(TAG, e);
            }
            return true;
        }
    }
}
