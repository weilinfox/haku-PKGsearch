package me.weilinfox.pkgsearch.searcher.mirrorSearcher.updateService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searcher.mirrorSearcher.LoongnixSearcher;
import me.weilinfox.pkgsearch.utils.Constraints;

public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    private static PendingIntent updateIntent;
    RemoteViews mView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: create UpdateService.");
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(getApplicationContext(), Constraints.notificationChannelId)
                    .setContentTitle(getResources().getString(R.string.update_title))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_baseline_refresh_white_24)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getResources().getString(R.string.update_title))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_baseline_refresh_white_24)
                    .build();
        }
        /*mView = new RemoteViews(getPackageName(), R.layout.notification_item);
        mView.setTextViewText(R.id.not_text, getResources().getString(R.string.update_title));
        mView.setProgressBar(R.id.not_progress, 100, 0, false);

        notification.contentView = mView;*/
        startForeground(Constraints.updateProcessId, notification);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: start UpdateService.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                if (! LoongnixSearcher.updateMirror(getApplicationContext())) {
                    Log.e(TAG, "onStartCommand: update Loongnix mirror error.");
                    flag = false;
                    // 失败通知 如果有多个，则由 updateFailBaseId 开始依次累加
                    showNotification(Constraints.updateFailBaseId, "Loongnix mirror update failed.");
                }

                if (flag) {
                    // 成功通知
                    showNotification(Constraints.updateSuccessId, null);
                }
                stopSelf();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void startRepeating(Context context) {
        AlarmManager mirrorAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateService.class);
        updateIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mirrorAlarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                                                AlarmManager.INTERVAL_HOUR, updateIntent);
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopRepeating(Context context) {
        AlarmManager mirrorAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mirrorAlarm.cancel(updateIntent);
    }

    private void showNotification(int code, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        switch (code) {
            case Constraints.updateSuccessId:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = new NotificationCompat.Builder(getApplicationContext(), Constraints.notificationChannelId)
                            .setContentText(getResources().getString(R.string.update_success))
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_baseline_done_24)
                            .build();
                } else {
                    notification = new NotificationCompat.Builder(getApplicationContext())
                            .setContentText(getResources().getString(R.string.update_success))
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_baseline_done_24)
                            .build();
                }
                break;
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = new NotificationCompat.Builder(getApplicationContext(), Constraints.notificationChannelId)
                            .setContentTitle(getResources().getString(R.string.update_failed))
                            .setSettingsText(message)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_baseline_close_24)
                            .build();
                } else {
                    notification = new NotificationCompat.Builder(getApplicationContext())
                            .setContentTitle(getResources().getString(R.string.update_failed))
                            .setSettingsText(message)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_baseline_close_24)
                            .build();
                }
                break;
        }
        notificationManager.notify(code, notification);
    }
}
