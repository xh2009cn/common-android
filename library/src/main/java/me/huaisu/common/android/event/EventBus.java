package me.huaisu.common.android.event;


import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.huaisu.common.android.runtime.AppRuntime;
import me.huaisu.common.android.utils.Logger;

public class EventBus {

    private static volatile EventBus sInstance;

    public static EventBus instance() {
        if (sInstance == null) {
            synchronized (EventBus.class) {
                if (sInstance == null) {
                    sInstance = new EventBus();
                }
            }
        }
        return sInstance;
    }

    private EventBus() {

    }

    private final Object lock = new Object();
    private Map<String, Set<Subscriber>> subscribers = new ConcurrentHashMap<>();

    public void register(@NonNull String name, Subscriber subscriber) {
        if (subscriber == null) {
            return;
        }
        Set<Subscriber> set = null;
        synchronized (lock) {
            set = subscribers.get(name);
            if (set == null) {
                set = Collections.synchronizedSet(new LinkedHashSet<>(1));
                subscribers.put(name, set);
            }
            set.add(subscriber);
        }
    }

    public void register(@NonNull List<String> names, Subscriber subscriber) {
        if (subscriber != null) {
            for (String name : names) {
                register(name, subscriber);
            }
        }
    }

    public void unregister(Subscriber subscriber) {
        if (subscriber != null) {
            synchronized (lock) {
                for (Set<Subscriber> set : subscribers.values()) {
                    if (set != null) {
                        set.remove(subscriber);
                    }
                }
            }
        }
    }

    public void postEvent(IEvent event) {
        if (event == null || event.name() == null) {
            return;
        }
        Logger.i(AppRuntime.TAG, "post event " + event.name() + (event.data() != null ? " data:" + event.data() : ""));
        Set<Subscriber> copySet = null;
        synchronized (lock) {
            Set<Subscriber> set = subscribers.get(event.name());
            if (set != null) {
                copySet = Collections.synchronizedSet(new LinkedHashSet<>(1));
                copySet.addAll(set);
            }
        }
        if (copySet != null) {
            for (Subscriber subscriber : copySet) {
                subscriber.onEvent(event);
            }
        }
    }
}
