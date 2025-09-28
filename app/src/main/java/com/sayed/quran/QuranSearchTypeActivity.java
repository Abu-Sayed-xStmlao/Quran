package com.sayed.quran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class QuranSearchTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(ThemeHelper.getTheme(this)); // Apply saved theme before UI is drawn
        ThemeHelper.makeStatusBarTransparent(QuranSearchTypeActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_search_type);

    }

    public void goArabicSearch(View view) {
        Intent intent = new Intent(QuranSearchTypeActivity.this, VerseSearchActivity.class);
        intent.putExtra("search_type", "arabic");
        startActivity(intent);
    }

    public void goTransilationSearch(View view) {
        Intent intent = new Intent(QuranSearchTypeActivity.this, VerseSearchActivity.class);
        intent.putExtra("search_type", "translation");
        startActivity(intent);
    }

    public void goTafsirSearch(View view) {
        Intent intent = new Intent(QuranSearchTypeActivity.this, WebViewActivity.class);
        startActivity(intent);
    }
}