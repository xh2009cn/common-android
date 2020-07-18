package me.huaisu.common.android.event;

import android.support.annotation.NonNull;

public interface IEvent<T> {

    @NonNull String name();

    T data();

    class ForegroundChange implements IEvent<Boolean> {

        public static final String NAME = "foreground_change";

        private final boolean foreground;

        public ForegroundChange(boolean foreground) {
            this.foreground = foreground;
        }

        @Override
        public String name() {
            return NAME;
        }

        @NonNull
        @Override
        public Boolean data() {
            return foreground;
        }
    }

    class ScreenStateChange implements IEvent<Integer> {

        public static final String NAME = "screen_state_change";

        private final int screenState;

        public ScreenStateChange(int screenState) {
            this.screenState = screenState;
        }

        @Override
        public String name() {
            return NAME;
        }

        @NonNull
        @Override
        public Integer data() {
            return screenState;
        }
    }

    class BatteryStateChange implements IEvent<Boolean> {

        public static final String NAME = "battery_state_change";

        private final boolean isCharging;

        public BatteryStateChange(boolean isCharging) {
            this.isCharging = isCharging;
        }

        @Override
        public String name() {
            return NAME;
        }

        @NonNull
        @Override
        public Boolean data() {
            return isCharging;
        }
    }
}
