package com.example.autosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;

public class ScheduledSMSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the phone numbers and message from the intent's extras
        ArrayList<String> phoneNumbers = intent.getStringArrayListExtra("PHONE_NUMBERS");
        String message = intent.getStringExtra("MESSAGE");

        for (String number : phoneNumbers) {
            sendSMS(context, number, message);
        }
    }

    private void sendSMS(Context context, String phoneNumber, String message) {
        // Envoyer le SMS
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
    }
}
