package me.huaisu.common.android.runtime;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import me.huaisu.common.android.App;
import me.huaisu.common.android.event.EventBus;
import me.huaisu.common.android.event.IEvent;
import me.huaisu.common.android.lifecycle.AppLifecycleCallback;
import me.huaisu.common.android.lifecycle.AppLifecycleManager;
import me.huaisu.common.android.utils.AppUtils;
import me.huaisu.common.android.utils.Logger;

import static me.huaisu.common.android.consts.AppRuntimeConsts.ScreenState.SCREEN_OFF;
import static me.huaisu.common.android.consts.AppRuntimeConsts.ScreenState.SCREEN_ON;
import static me.huaisu.common.android.consts.AppRuntimeConsts.ScreenState.SCREEN_USER_PRESENT;

public class AppRuntime implements AppLifecycleCallback {

    public static final String TAG = "AppRuntime";

    private static volatile AppRuntime sInstance;

    private boolean mIsScreenBroadcastRegistered;

    /**
     * 屏幕状态
     */
    private int mScreenState = SCREEN_USER_PRESENT;
    /**
     * 前后台状态
     */
    private boolean mForeground;
    /**
     * 是否正在充电
     */
    private boolean isCharging;

    public static AppRuntime get() {
        if (sInstance == null) {
            synchronized (AppRuntime.class) {
                if (sInstance == null) {
                    sInstance = new AppRuntime();
                }
            }
        }
        return sInstance;
    }

    private AppRuntime() {

    }

    public int getCurrentScreenState() {
        return mScreenState;
    }

    public boolean isScreenOn() {
        return getCurrentScreenState() != SCREEN_OFF;
    }

    public boolean isForeground() {
        return mForeground;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public void onProcessStart() {
        Context context = App.get();
        mForeground = AppUtils.isAppOnForeground(context);
        mScreenState = getCurrentScreenState(context);
        isCharging = isCharging(context);
        registerBroadcast(context);
        AppLifecycleManager.get().addCallback(this);
    }

    private void registerBroadcast(Context context) {
        if (mIsScreenBroadcastRegistered) {
            return;
        }
        InnerReceiver receiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        try {
            context.registerReceiver(receiver, intentFilter);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        mIsScreenBroadcastRegistered = true;
    }

    @Override
    public void onBackground() {
        onForegroundChanged(false);
    }

    @Override
    public void onForeground() {
        onForegroundChanged(true);
    }

    private void onForegroundChanged(boolean foreground) {
        boolean wasForeground = mForeground;
        mForeground = foreground;
        EventBus.instance().postEvent(new IEvent.ForegroundChange(mForeground));
        // app退到后台进程会被冻结，导致系统api不回调（如熄屏、网络变更广播），回到前台需要更新一下缓存的状态
        boolean returnFromBackground = !wasForeground && mForeground;
        if (returnFromBackground) {
            if (mScreenState != SCREEN_USER_PRESENT) {
                mScreenState = SCREEN_USER_PRESENT;
                EventBus.instance().postEvent(new IEvent.ScreenStateChange(mScreenState));
            }
        }
    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent != null ? intent.getAction() : null;
            if (action == null) {
                return;
            }
            switch (action) {
                case Intent.ACTION_USER_PRESENT:
                case Intent.ACTION_SCREEN_ON:
                case Intent.ACTION_SCREEN_OFF:
                    if (Intent.ACTION_USER_PRESENT.equals(action)) {
                        mScreenState = SCREEN_USER_PRESENT;
                    } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                        mScreenState = SCREEN_ON;
                    } else {
                        mScreenState = SCREEN_OFF;
                    }
                    EventBus.instance().postEvent(new IEvent.ScreenStateChange(mScreenState));
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    isCharging = true;
                    EventBus.instance().postEvent(new IEvent.BatteryStateChange(isCharging));
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    isCharging = false;
                    EventBus.instance().postEvent(new IEvent.BatteryStateChange(isCharging));
                    break;
                default:
                    break;
            }
        }
    }

    public static int getCurrentScreenState(@NonNull Context context) {
        if (isScreenOn(context)) {
            return isScreenLocked(context) ? SCREEN_ON : SCREEN_USER_PRESENT;
        } else {
            return SCREEN_OFF;
        }
    }

    private static boolean isScreenOn(@NonNull Context context) {
        boolean isScreenOn = false;
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                isScreenOn = pm.isScreenOn();
            }
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        return isScreenOn;
    }

    private static boolean isScreenLocked(@NonNull Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    return keyguardManager.isKeyguardLocked();
                } else {
                    return keyguardManager.inKeyguardRestrictedInputMode();
                }
            } catch (Throwable e) {
                Logger.e(TAG, e);
            }
        }
        return false;
    }

    public static boolean isCharging(Context context) {
        Intent batteryStatus = null;
        try {
            batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        if (batteryStatus == null) {
            return false;
        }
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
    }
}
