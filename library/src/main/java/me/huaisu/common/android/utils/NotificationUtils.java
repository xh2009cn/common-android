package me.huaisu.common.android.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationUtils {

    private static final String TAG = "ForegroundUtils";

    private static final String CHANNEL_ID = "normal";
    private static final String CHANNEL_NAME = "普通通道";

    public static void startForegroundService(Context context, Intent service) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service);
            } else {
                context.startService(service);
            }
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }

    public static void startForeground(Service service, int notificationId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        try {
            ensureNotificationChannel(service);
            Notification.Builder builder = new Notification.Builder(service, CHANNEL_ID);
            builder.setContentTitle("常驻通知标题");
            builder.setContentText("常驻通知内容");
            builder.setSmallIcon(android.R.drawable.star_on);
            Notification notification = builder.build();
            service.startForeground(notificationId, notification);
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void ensureNotificationChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            createNotificationChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static NotificationChannel createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);
        return channel;
    }
}
