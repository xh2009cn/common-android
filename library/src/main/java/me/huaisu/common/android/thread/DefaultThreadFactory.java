package me.huaisu.common.android.thread;

import android.support.annotation.NonNull;
import java.util.concurrent.ThreadFactory;

public class DefaultThreadFactory implements ThreadFactory {
    private int threadNumber = 1;
    private String prefix;

    public DefaultThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    public DefaultThreadFactory() {
        this.prefix = "ThreadPool-";
    }

    public Thread newThread(@NonNull Runnable r) {
        return new Thread(r, this.prefix + this.threadNumber++);
    }
}
