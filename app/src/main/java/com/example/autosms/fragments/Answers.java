package com.example.autosms.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autosms.CustomAdapter;
import com.example.autosms.R;
import com.example.autosms.SingleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Answers extends Fragment {
    private RecyclerView recyclerView;
    private SingleAdapter customAdapter;
    private ArrayList<String> answersList = new ArrayList(), defaultAnswers = new ArrayList<>();
    private Button button;
    private EditText input;
    SharedPreferences sharedPreferences;
    public Answers(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.answers, container, false);

        button = result.findViewById(R.id.button);
        input = result.findViewById(R.id.editText);
        recyclerView = (RecyclerView) result.findViewById(R.id.recyclerView);

        // define default answers
        defaultAnswers.add("Je ne suis pas disponible pour le moment.");
        defaultAnswers.add("Salut, ça va ?");
        defaultAnswers.add("Joyeux anniversaire !");
        answersList.addAll(defaultAnswers);

        // set adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        customAdapter = new SingleAdapter(getContext(), answersList);
        recyclerView.setAdapter(customAdapter);

        // get previously defined custom answers
        sharedPreferences = getContext().getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        Set<String> savedAnswers = sharedPreferences.getStringSet("SAVED_ANSWERS", null);
        if (savedAnswers != null) {
            answersList.addAll(savedAnswers);
            customAdapter.notifyDataSetChanged();
        }

        // Save the string value to the arguments bundle using setArguments()
        Bundle args = new Bundle();
        args.putString("SELECTED_ANSWER", customAdapter.getSelected());
        setArguments(args);

        // Send the bundle through the Fragment Manager
        getParentFragmentManager().setFragmentResult("ANSWER_VALUE_UPDATE",args);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answersList.add(input.getText().toString());
                customAdapter.notifyDataSetChanged();
                saveAnswers();
            }
        });

        customAdapter.setOnAnswerSelectedListener(new SingleAdapter.OnAnswerSelectedListener() {
            @Override
            public void onAnswerSelected(String answer) {
                // Save the string value to the arguments bundle using setArguments()
                Bundle args = new Bundle();
                args.putString("SELECTED_ANSWER", answer);
                setArguments(args);

                // Send the bundle through the Fragment Manager
                getParentFragmentManager().setFragmentResult("ANSWER_VALUE_UPDATE",args);
            }
        });
        return result;
    }

    private void saveAnswers() {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(answersList);
        list.removeAll(defaultAnswers);
        Set<String> answers = new HashSet<>(list);
        sharedPreferences.edit().putStringSet("SAVED_ANSWERS",  answers).apply();

    }
}
