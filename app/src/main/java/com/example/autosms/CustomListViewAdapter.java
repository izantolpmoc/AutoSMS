package com.example.autosms;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

// Dealing with the display of contacts (multiple choice)
public class CustomListViewAdapter extends ArrayAdapter {

    private ArrayList<ContactData> dataSet;
    private Context mContext;
    private ArrayList<ContactData> selectedContacts = new ArrayList<>();
    private SparseBooleanArray checkedItems = new SparseBooleanArray();

    private static class ViewHolder {
        TextView txtName;
        CheckBox checkBox;
    }

    public CustomListViewAdapter(ArrayList<ContactData> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public ContactData getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.txtName);
            viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        ContactData item = getItem(position);
        viewHolder.txtName.setText(item.getName()+ "\n📞 " + item.getPhoneNumber());

        // Get the state of the checkbox from the SparseBooleanArray
        boolean isChecked = checkedItems.get(position, false);
        viewHolder.checkBox.setChecked(isChecked);

        // Set a listener to update the SparseBooleanArray when the checkbox is clicked
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newState = !item.isChecked();
                item.setChecked(!item.isChecked());
                viewHolder.checkBox.setChecked(newState);
                checkedItems.put(position, newState);
                if (selectedContacts.contains(item) && !item.isChecked()) {
                    selectedContacts.remove(item);
                }
            }
        });

        return result;
    }

    public ArrayList<ContactData> getSelectedContacts() {

        for (int i = 0; i < dataSet.size(); i++) {
            if (checkedItems.get(i)) {
                ContactData contact = dataSet.get(i);
                if(!selectedContacts.contains(contact))
                    selectedContacts.add(contact);
            }
        }
        return selectedContacts;
    }

}




