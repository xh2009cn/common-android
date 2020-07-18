package me.huaisu.common.android.thread;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;

public class WorkThreadPool {

    private final ThreadPoolExecutor executor;
    private final Handler handler;
    private Map<Runnable, Runnable> mapping = new ConcurrentHashMap<>();
    private static final WorkThreadPool INSTANCE = new WorkThreadPool();

    private WorkThreadPool() {
        executor = new ThreadPoolExecutor(8,
                24,
                60L,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<>(11),
                new DefaultThreadFactory(),
                new DiscardOldestPolicy());
        HandlerThread thread = new HandlerThread("WorkThread", 10);
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public static WorkThreadPool get() {
        return INSTANCE;
    }

    public void post(Runnable runnable) {
        if (runnable != null) {
            if (executor.isShutdown()) {
                executor.prestartAllCoreThreads();
            }
            executor.execute(runnable);
        }
    }

    public void postDelayed(final Runnable runnable, long delayMillis) {
        if (runnable != null) {
            Runnable realRunnable = () -> {
                mapping.remove(runnable);
                post(runnable);
            };
            mapping.put(runnable, realRunnable);
            handler.postDelayed(realRunnable, delayMillis);
        }
    }

    public void removePendingTask(Runnable runnable) {
        if (runnable != null) {
            Runnable realRunnable = mapping.remove(runnable);
            if (realRunnable != null) {
                handler.removeCallbacks(realRunnable);
            }
        }
    }
}
