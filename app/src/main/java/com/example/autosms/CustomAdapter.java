package com.example.autosms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//dealing with the display of contacts (multiple choice)
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<String> answersList;

    public CustomAdapter(ArrayList<String> answersList) {
        this.answersList = answersList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.txtName);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String currentLineText = answersList.get(position);
        holder.text.setText(currentLineText);
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }
}
