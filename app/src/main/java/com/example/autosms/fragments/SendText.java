package com.example.autosms.fragments;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.autosms.ContactData;
import com.example.autosms.AutoReplyService;
import com.example.autosms.R;
import com.example.autosms.ScheduledSMSService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class SendText extends Fragment {
    private TextView contacts, message, currentServices;
    private Button sendButton, autoReplyButton, endServiceButton, sendLaterButton;
    private ArrayList<String> selectedContactsNames, selectedContactsNumbers;
    private ArrayList<ContactData> selectedContacts;
    private String selectedText;
    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sendtext, container, false);

        contacts = view.findViewById(R.id.selectedContacts);
        message = view.findViewById(R.id.selectedMessage);
        sendButton = view.findViewById(R.id.sendButton);
        autoReplyButton = view.findViewById(R.id.autoReplyButton);
        currentServices = view.findViewById(R.id.currentServices);
        endServiceButton = view.findViewById(R.id.endServiceButton);
        endServiceButton.setVisibility(View.INVISIBLE);
        datePicker = view.findViewById(R.id.datePicker);
        datePicker.setVisibility(View.INVISIBLE);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setVisibility(View.INVISIBLE);
        sendLaterButton = view.findViewById(R.id.sendLaterButton);

        // Set the text value to the previously saved value for contacts and selected answer, or to default.
        if (getArguments() != null){
            ArrayList<ContactData> checkedContacts = (ArrayList<ContactData>) getArguments().getSerializable("SELECTED_PHONE_NUMBERS");
            if (checkedContacts != null) {
                String[] contactNames = new String[checkedContacts.size()];
                String[] contactNumbers = new String[checkedContacts.size()];
                for (int i = 0; i < checkedContacts.size(); i++) {
                    contactNames[i] = checkedContacts.get(i).getName();
                    contactNumbers[i] = checkedContacts.get(i).getPhoneNumber();
                }
                contacts.setText("Contact(s): " + TextUtils.join(", ", contactNames));
                selectedContactsNames = new ArrayList<>(Arrays.asList(contactNames));
                selectedContactsNumbers = new ArrayList<>(Arrays.asList(contactNumbers));
            } else {
                contacts.setText("No contacts selected.");
            }
            selectedText = getArguments().getString("SELECTED_ANSWER", "Vous n'avez pas choisi de message.");
            message.setText("Message: " + selectedText);
        }

        if (isMyServiceRunning(AutoReplyService.class)) {
            currentServices.setText("Les réponses automatiques sont activées pour certains contacts. Cliquez sur le bouton ci-dessous pour les arrêter.");
            endServiceButton.setVisibility(View.VISIBLE);
        }

        // Listener for the Fragment modification
        getParentFragmentManager().setFragmentResultListener("PHONE_NUMBERS_UPDATE", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // Get the string from the bundle received by the FragmentResult call
                selectedContacts = (ArrayList<ContactData>) bundle.getSerializable("SELECTED_PHONE_NUMBERS");

                // Set the text for contacts list
                if (selectedContacts != null && selectedContacts.size() > 0) {
                    String[] contactNames = new String[selectedContacts.size()];
                    String[] contactNumbers = new String[selectedContacts.size()];
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        contactNames[i] = selectedContacts.get(i).getName();
                        contactNumbers[i] = selectedContacts.get(i).getPhoneNumber();
                    }
                    contacts.setText("Contact(s): " + TextUtils.join(", ", contactNames));
                    selectedContactsNames = new ArrayList<>(Arrays.asList(contactNames));
                    selectedContactsNumbers = new ArrayList<>(Arrays.asList(contactNumbers));
                } else {
                    contacts.setText("No contacts selected.");
                }

                if (isMyServiceRunning(AutoReplyService.class)) {
                    currentServices.setText("Les réponses automatiques sont activées pour certains contacts. Cliquez sur le bouton ci-dessous pour les arrêter.");
                    endServiceButton.setVisibility(View.VISIBLE);
                }
            }
        });

        getParentFragmentManager().setFragmentResultListener("ANSWER_VALUE_UPDATE", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // Get the string from the bundle received by the FragmentResult call
                selectedText = bundle.getString("SELECTED_ANSWER");

                // Set the text for selected answer
                message.setText("Message: " + selectedText);

                if (isMyServiceRunning(AutoReplyService.class)) {
                    currentServices.setText("Les réponses automatiques sont activées pour certains contacts. Cliquez sur le bouton ci-dessous pour les arrêter.");
                    endServiceButton.setVisibility(View.VISIBLE);
                }
            }
        });

        sendLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePicker.getVisibility() == View.INVISIBLE && timePicker.getVisibility() == View.INVISIBLE) {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                    sendLaterButton.setText("Annuler");
                } else {
                    datePicker.setVisibility(View.INVISIBLE);
                    timePicker.setVisibility(View.INVISIBLE);
                    sendLaterButton.setText("Plannifer un envoi futur");
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(datePicker.getVisibility() == View.INVISIBLE && timePicker.getVisibility() == View.INVISIBLE) {
                    sendSMS(selectedContactsNumbers, selectedText);
                } else {
                    // Get the selected date and time
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth();
                    int dayOfMonth = datePicker.getDayOfMonth();
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();

                    // Create a Calendar object with the selected date and time
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth, hour, minute);
                    scheduleSMS(selectedContactsNumbers, selectedText, calendar.getTimeInMillis());

                    datePicker.setVisibility(View.INVISIBLE);
                    timePicker.setVisibility(View.INVISIBLE);
                }
            }
        });

        autoReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Vérifier que la permission d'envoi de SMS est accordée
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    // Demander la permission d'envoi de SMS
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_SMS}, 22);
                } else {
                    Intent serviceIntent = new Intent(requireContext().getApplicationContext(), AutoReplyService.class);
                    serviceIntent = serviceIntent.putStringArrayListExtra("SELECTED_PHONE_NUMBERS", selectedContactsNumbers);
                    serviceIntent = serviceIntent.putExtra("SELECTED_AUTO_REPLY", selectedText);
                    requireActivity().startService(serviceIntent);
                    if (isMyServiceRunning(AutoReplyService.class)) {
                        currentServices.setText("Les réponses automatiques sont activées pour certains contacts. Cliquez sur le bouton ci-dessous pour les arrêter.");
                        endServiceButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // stop automatic replies
        endServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(requireContext().getApplicationContext(), AutoReplyService.class);
                requireActivity().stopService(serviceIntent);
                if (!isMyServiceRunning(AutoReplyService.class)) {
                    currentServices.setText("Aucune réponse automatique activée pour l'instant.");
                    endServiceButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                }
            });
        }

        return view;
    }

    private void sendSMS(ArrayList<String> phoneNumbers, String message) {
        // Vérifier que la permission d'envoi de SMS est accordée
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission d'envoi de SMS
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            // Envoyer les SMS
            for (String phoneNumber : phoneNumbers) {
                SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void scheduleSMS(ArrayList<String> phoneNumbers, String message, long timestamp) {
        // Create an intent that will be used to launch the service
        Intent intent = new Intent(getContext(), ScheduledSMSService.class);
        intent.putExtra("PHONE_NUMBERS", phoneNumbers);
        intent.putExtra("MESSAGE", message);
        intent.putExtra("TIME_IN_MILLIS", timestamp);
        requireActivity().startService(intent);
    }
}
