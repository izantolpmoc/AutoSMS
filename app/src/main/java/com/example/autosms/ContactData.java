package com.example.autosms;

import java.io.Serializable;

public class ContactData implements Serializable {

    private String name;
    private String phoneNumber;
    private boolean checked;

    public ContactData(String name, String phoneNumber, boolean checked) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
