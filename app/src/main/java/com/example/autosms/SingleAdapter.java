package com.example.autosms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//Dealing with the display of answers (single choice)
public class SingleAdapter extends RecyclerView.Adapter<SingleAdapter.SingleViewHolder> {

    private Context context;
    private ArrayList<String> answersList;
    private int checkedPosition = 0;
    private OnAnswerSelectedListener onAnswerSelectedListener;

    public SingleAdapter(Context context, ArrayList<String> answersList) {
        this.context = context;
        this.answersList = answersList;
    }

    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_list_item, viewGroup, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder singleViewHolder, int position) {
        singleViewHolder.bind(answersList.get(position));
    }

    @Override
    public int getItemCount() {
        return answersList.size();
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final String answer) {
            if (checkedPosition == -1) {
                imageView.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            textView.setText(answer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageView.setVisibility(View.VISIBLE);
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                        if (onAnswerSelectedListener != null) {
                            onAnswerSelectedListener.onAnswerSelected(answersList.get(checkedPosition));
                        }
                    }
                }
            });
        }
    }

    public String getSelected() {
        if (checkedPosition != -1) {
            return answersList.get(checkedPosition);
        }
        return null;
    }

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(String answer);
    }
    public void setOnAnswerSelectedListener(OnAnswerSelectedListener onAnswerSelectedListener) {
        this.onAnswerSelectedListener = onAnswerSelectedListener;
    }
}
