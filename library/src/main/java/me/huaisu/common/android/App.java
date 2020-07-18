package me.huaisu.common.android;

import android.app.Application;

import me.huaisu.common.android.runtime.AppRuntime;

public class App {

    private static Application sApp;

    public static Application get() {
        return sApp;
    }

    public static void onAttachBaseContext(Application app) {
        sApp = app;
        AppBuildConfig.APPLICATION_ID = app.getPackageName();
        AppRuntime.get().onProcessStart();
    }
}
