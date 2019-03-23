package com.example.android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.permissionrequest.ChatActivity;
import com.example.android.permissionrequest.MainActivity;
import com.example.android.permissionrequest.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMMessasingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {

            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(chatIntent);

            String titleString = remoteMessage.getNotification().getTitle();
            String messageString = remoteMessage.getNotification().getBody();

            Intent it = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), it, 0);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            int ico_notification = R.drawable.ic_launcher;
            int color = ContextCompat.getColor(this, R.color.cardview_dark_background);

            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            String CHANNEL_ID = "FCM_channel_01";
            CharSequence name = "Channel fCM";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(ico_notification)
                            .setContentTitle(titleString)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(messageString))
                            .setSound(soundUri)
                            .setColor(color)
                            .setAutoCancel(true)
                            .setVibrate(new long[]{1000, 1000})
                            .setContentText(messageString);

            mBuilder.setContentIntent(contentIntent);
            Notification notification = mBuilder.build();

            mNotificationManager.notify(0, notification);
        }
    }
}
