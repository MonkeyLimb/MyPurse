package com.example.zachmarcelo.mypurse;

/**
 * Created by Zach Marcelo on 12/8/2018.
 */
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static com.example.zachmarcelo.mypurse.App.CHANNEL_1_ID;

public class MyBroadcastReceiver extends BroadcastReceiver {
    NotificationManagerCompat notificationManager;
    private final long[] pattern = {100, 300, 300, 300};
    protected static Ringtone r;


    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);


        notificationManager = NotificationManagerCompat.from(context);
        String message = intent.getStringExtra("Alarm");

        Intent activityIntent = new Intent(context, StopRingtone.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                24444, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//
//        Intent broadcastIntent = new Intent(context, AddAlarmActivity.class);
//        broadcastIntent.putExtra("toastMessage", "Alarm stopped");
//        PendingIntent actionIntent = PendingIntent.getBroadcast(context,
//                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        assert alarmManager != null;
//        alarmManager.cancel(contentIntent);
        Uri noti  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Intent startIntent = new Intent(context, RingtonePlayingService.class);
        startIntent.putExtra("ringtone-uri", noti.toString());
        context.startService(startIntent);



        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setContentTitle("Important Reminder:")
                .setContentText(message)
                .setSmallIcon(R.drawable.logo2)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setVibrate(pattern)
                .addAction(R.mipmap.ic_launcher, "Stop alarm", contentIntent)
                .build();
        notificationManager.notify(1, notification);
//
//        Intent intent1 = new Intent(context, StopRingtone.class);
//        intent.putExtra("notifID", "1");
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(StopRingtone.class);
//
//        stackBuilder.addNextIntent(intent1);







    }
}