package com.example.autosms.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.autosms.CustomListViewAdapter;
import com.example.autosms.ContactData;
import com.example.autosms.R;

import java.util.ArrayList;

public class Contacts extends Fragment {

    private ArrayList<ContactData> contactDataArrayList = new ArrayList<>();
    private CustomListViewAdapter adapter;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contacts, container, false);
        listView = view.findViewById(R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Load contacts from phone and populate dataModel
        if(contactDataArrayList.size() == 0)
            loadContacts();

        // Setting the adapter
        adapter = new CustomListViewAdapter(contactDataArrayList, requireContext());
        listView.setAdapter(adapter);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a bundle to be sent to the other fragment
                Bundle bundle = new Bundle();
                ArrayList<ContactData> contacts = adapter.getSelectedContacts();
                bundle.putSerializable("SELECTED_PHONE_NUMBERS", contacts);

                // Send the bundle through the Fragment Manager
                getParentFragmentManager().setFragmentResult("PHONE_NUMBERS_UPDATE", bundle);
            }
        });

        return view;
    }

    @SuppressLint("Range")
    private void loadContacts() {
        // Obtenir un ContentResolver pour accéder aux données du carnet d'adresses
        ContentResolver cr = requireContext().getContentResolver();

        // Récupérer les données des contacts, triés par nom
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                // Récupérer le nom et l'ID du contact
                @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                @SuppressLint("Range") String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                // Vérifier si le contact a des numéros de téléphone
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    // Récupérer les numéros de téléphone du contact
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            // Récupérer le numéro de téléphone du contact
                            String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            ContactData contact = new ContactData(name, phone, false);

                            // Ajouter le contact à la liste
                            contactDataArrayList.add(contact);
                        }
                        pCur.close();
                    }
                }
            }
            cur.close();
        }
    }
}
