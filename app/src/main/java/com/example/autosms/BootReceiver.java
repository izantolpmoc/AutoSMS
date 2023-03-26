package com.example.autosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                // redémarre le service en même temps que le téléphone
                Intent serviceIntent = new Intent(context, ScheduledSMSService.class);
                context.startService(serviceIntent);
            }
        }
    }
