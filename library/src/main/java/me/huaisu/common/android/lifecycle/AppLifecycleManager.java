package me.huaisu.common.android.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.concurrent.CopyOnWriteArrayList;

public class AppLifecycleManager implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "AppLifecycleManager";

    private static volatile AppLifecycleManager instance;
    private int topActivityCnt = 0;
    private final CopyOnWriteArrayList<AppLifecycleCallback> callbacks;

    private AppLifecycleManager() {
        callbacks = new CopyOnWriteArrayList<>();
    }

    public static AppLifecycleManager get() {
        if (instance == null) {
            synchronized (AppLifecycleManager.class) {
                if (instance == null) {
                    instance = new AppLifecycleManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        topActivityCnt++;
        if (topActivityCnt == 1) {
            dispatchForeground();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        topActivityCnt--;
        if (topActivityCnt == 0) {
            dispatchBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private void dispatchForeground() {
        for (AppLifecycleCallback callback : callbacks) {
            callback.onForeground();
        }
    }

    private void dispatchBackground() {
        for (AppLifecycleCallback callback : callbacks) {
            callback.onBackground();
        }
    }

    public void addCallback(AppLifecycleCallback callback) {
        if (callback != null) {
            callbacks.add(callback);
        }
    }

    public void remove(AppLifecycleCallback callback) {
        if (callback != null) {
            callbacks.remove(callback);
        }
    }

    public void clear() {
        callbacks.clear();
    }
}
