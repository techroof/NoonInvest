package com.techroof.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techroof.nooninvest.R;

public class PushNotifications extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();
        final String CHANNEL_ID="HEADS_UP_NOTOFICATIONS";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"MyNotification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
        Notification.Builder notificaton=new Notification.Builder(this,CHANNEL_ID).
                setContentTitle(title).setContentText(body).setSmallIcon(R.drawable.logo).setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1,notificaton.build());
    }
}
