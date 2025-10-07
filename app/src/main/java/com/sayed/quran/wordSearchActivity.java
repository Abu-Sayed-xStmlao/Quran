package com.sayed.quran;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
        requested_word = requested_word.replace('\u0652', '\u06E1');

        dbConn dbConn = new dbConn(this);


        // Get context from the widget (safe inside span)

        // Copy text to clipboard
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Copied Text", requested_word);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "not", Toast.LENGTH_SHORT).show();
        }

        finder_word = findViewById(R.id.finder_word);
        verses_recyclerview = findViewById(R.id.verses_recyclerview);


        verseArray = dbConn.findWordVerses(requested_word.trim());
        finder_word.setText(requested_word + " found: " + verseArray.size());


        verses_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        verses_recyclerview.setAdapter(new wordVerseSearchAdapter(wordSearchActivity.this, verseArray, requested_word));

    }


}