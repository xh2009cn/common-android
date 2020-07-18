package me.huaisu.common.android.lifecycle;

/**
 * 应用前后台切换及启动首个activity和退出最后一个activity的活动
 *
 */
public interface AppLifecycleCallback {

    void onBackground();

    void onForeground();
}
