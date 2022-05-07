package com.techroof.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.techroof.nooninvest.R;

public class Services extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"notifyLemubit").
                setSmallIcon(R.drawable.logo).
                setContentTitle("Data Updated").
                setContentText("Your investment amount is updated").
                setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }
}
