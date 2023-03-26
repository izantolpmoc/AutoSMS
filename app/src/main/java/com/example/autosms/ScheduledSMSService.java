package com.example.autosms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class ScheduledSMSService extends Service {
    private ScheduledSMSBroadcastReceiver smsReceiver;
    private PendingIntent pendingIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retrieve the phone numbers and message from the intent's extras
        ArrayList<String> phoneNumbers = intent.getStringArrayListExtra("PHONE_NUMBERS");
        String message = intent.getStringExtra("MESSAGE");

        // Create an intent that will be fired when the alarm goes off
        Intent smsIntent = new Intent(this, ScheduledSMSBroadcastReceiver.class);
        smsIntent.putExtra("PHONE_NUMBERS", phoneNumbers);
        smsIntent.putExtra("MESSAGE", message);

        // Create a PendingIntent that will launch the Broadcast Receiver
        pendingIntent = PendingIntent.getBroadcast(this, 0, smsIntent, PendingIntent.FLAG_MUTABLE);

        // Get the time at which the SMS should be sent
        long timeInMillis = intent.getLongExtra("TIME_IN_MILLIS", 0);

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to fire at the specified time
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }

        // Lancer le service en arrière-plan
        // Création du cannal de notification
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("2", "scheduledSMS", NotificationManager.IMPORTANCE_DEFAULT);
        }
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        // Créer une notification pour le service en cours d'exécution
        Notification notification = new NotificationCompat.Builder(this, "2")
                .setContentTitle("ScheduledSMSService en cours d'exécution")
                .setContentText("Envoyer un SMS plannifié aux contacts sélectionnés.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        // Démarrer le service en mode "Foreground"
        startForeground(2, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
