package com.example.standup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private ToggleButton alarmToggle;
    private NotificationManager mNotificationManager;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmToggle = findViewById(R.id.alarmToggle);

        createNotificationChannel();
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        //A boolean to see if the alarm has already been set up in previous application runs
        //This will define the starting state of the toggleButton (alarmToggle)
        //If a PendingIntent exists, that PendingIntent is returned; otherwise the call returns null
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent,
                PendingIntent.FLAG_NO_CREATE) != null);
        alarmToggle.setChecked(alarmUp);

        //Create the PendingIntent to be used for alarm (this will broadcast)
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmToggle.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String toastMessage;
                if (isChecked) {
                    //deliverNotification(MainActivity.this);
                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                triggerTime, repeatInterval, notifyPendingIntent);
                    }
                    toastMessage = "Stand Up Alarm On!";
                } else {
                    mNotificationManager.cancelAll();
                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntent);
                    }
                    toastMessage = "Stand Up Alarm Off!";
                }
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void deliverNotification(Context context) {
//        Intent contentIndent = new Intent(context, MainActivity.class);
//        PendingIntent contentPendingIntent = PendingIntent.getActivity(
//                context, NOTIFICATION_ID, contentIndent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_stand_up)
//                .setContentTitle("Stand Up Alert")
//                .setContentText("You should stand up and walk around now!")
//                .setContentIntent(contentPendingIntent)
//                .setAutoCancel(true) // closes the notification when user taps on it
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setDefaults(NotificationCompat.DEFAULT_ALL);
//
//        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
//    }

    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Stand up notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifies every 15 minutes to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


}