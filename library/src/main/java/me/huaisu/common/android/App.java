package me.huaisu.common.android;

import android.app.Application;

public class App {

    private static Application sApp;

    public static Application get() {
        return sApp;
    }

    public static void set(Application app) {
        sApp = app;
    }
}
