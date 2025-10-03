package com.sayed.quran;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class wordSearchActivity extends AppCompatActivity {


    public String requested_word;
    RecyclerView verses_recyclerview;
    TextView finder_word;
    ArrayList<VerseModel> verseArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getTheme(this)); // Apply saved theme before UI is drawn
        ThemeHelper.makeStatusBarTransparent(wordSearchActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search);

        Intent intent = getIntent();
        requested_word = (intent != null) ? intent.getStringExtra("finder") : null;
        requested_word = QuranController.removeArabicSigns(requested_word);
        dbConn dbConn = new dbConn(this);

        finder_word = findViewById(R.id.finder_word);
        verses_recyclerview = findViewById(R.id.verses_recyclerview);
        finder_word.setText(requested_word);


        verseArray = dbConn.findWordVerses("البحر");


        verses_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        verses_recyclerview.setAdapter(new wordVerseSearchAdapter(wordSearchActivity.this, verseArray, requested_word));

    }


}