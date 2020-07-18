package me.huaisu.common.android.event;

import android.support.annotation.NonNull;

public interface Subscriber {

    void onEvent(@NonNull IEvent event);
}
