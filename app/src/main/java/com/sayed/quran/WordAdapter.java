package com.sayed.quran;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    Context context;
    ArrayList<WordModel> wordArray;

    public WordAdapter(Context context, ArrayList<WordModel> wordArray) {
        this.context = context;
        this.wordArray = wordArray;
    }



    @NonNull
    @Override
    public WordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.word_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapter.ViewHolder holder, int position) {
        holder.arabic_word.setText(wordArray.get(position).arabic);
        holder.transition_word.setText(wordArray.get(position).translation);

    }

    @Override
    public int getItemCount() {
        return wordArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView arabic_word, transition_word;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            arabic_word = itemView.findViewById(R.id.arabic_word);
            transition_word = itemView.findViewById(R.id.transition_word);
        }
    }
}
