package com.example.autosms;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.autosms.fragments.Answers;
import com.example.autosms.fragments.Contacts;
import com.example.autosms.fragments.SendText;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Contacts();
            case 1:
                return new Answers();
            case 2:
                return new SendText();
            default:
                return null;

        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Contacts";
            case 1 :
                return "Réponses";
            case 2:
                return "Envoyer un message";
            default:
                return "";

        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}






