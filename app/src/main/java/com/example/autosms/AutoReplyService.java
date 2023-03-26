package com.example.autosms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsManager;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class AutoReplyService extends Service {
    private SmsBroadcastReceiver smsBroadcastReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> selectedContacts = intent.getStringArrayListExtra("SELECTED_PHONE_NUMBERS");
        String autoReplyText = intent.getStringExtra("SELECTED_AUTO_REPLY");
        smsBroadcastReceiver = new SmsBroadcastReceiver(selectedContacts);
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsBroadcastReceiver, filter);

        smsBroadcastReceiver.setListener(new SmsBroadcastReceiver.Listener() {
            @Override
            public void onTextReceived(String number, String text) {
                sendSMS(number, autoReplyText);
            }
        });

        // Lancer le service en arrière-plan
        // Création du cannal de notification
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("1", "autoReply", NotificationManager.IMPORTANCE_DEFAULT);
        }
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        // Créer une notification pour le service en cours d'exécution
        Notification notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("AutoReplyService en cours d'exécution")
                .setContentText("Répondre automatiquement aux SMS des contacts sélectionnés")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        // Démarrer le service en mode "Foreground"
        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendSMS(ArrayList<String> phoneNumbers, String message) {
        // Envoyer les SMS
        for (String phoneNumber : phoneNumbers) {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
        }

    }
    private void sendSMS(String phoneNumber, String message) {
        // Envoyer le SMS
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
    }
}
