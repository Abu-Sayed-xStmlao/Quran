package com.sayed.quran;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VerseSearchActivity extends AppCompatActivity {


    EditText search_input;
    RecyclerView verses_recyclerview;
    TextView finder_count;

    ArrayList<VerseModel> verseArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getTheme(this)); // Apply saved theme before UI is drawn
        ThemeHelper.makeStatusBarTransparent(VerseSearchActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse_search);
        dbConn dbConn = new dbConn(this);

        search_input = findViewById(R.id.search_input);
        verses_recyclerview = findViewById(R.id.verses_recyclerview);
        finder_count = findViewById(R.id.finder_count);

        String requested_language = LanguagePref.getLanguage(this);

        Intent intent = getIntent();
        String search_type = (intent != null) ? intent.getStringExtra("search_type") : null;

        if ("arabic".equals(search_type)) {
            search_input.setHint("البحث بالعربية...");
            search_input.setTextDirection(View.TEXT_DIRECTION_RTL);
        } else {
            search_input.setTextDirection(View.TEXT_DIRECTION_LTR);

            if (requested_language.equals("en")) {
                search_input.setHint("Translation search..");
                search_input.setTypeface(ResourcesCompat.getFont(this, R.font.poppins));
            } else if (requested_language.equals("bn")) {
                search_input.setHint("অনুবাদ সার্চ..");
                search_input.setTypeface(ResourcesCompat.getFont(this, R.font.anek_bangla));
            } else {
                search_input.setHint("अनुवाद खोजें..");
                search_input.setTypeface(ResourcesCompat.getFont(this, R.font.poppins));
            }
        }


        verseArray = dbConn.loadVerses();


        verses_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        verses_recyclerview.setAdapter(new VerseSearchAdapter(VerseSearchActivity.this, verseArray, "", search_type));

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verseArray = dbConn.findVerses(s.toString());
                verses_recyclerview.setAdapter(new VerseSearchAdapter(VerseSearchActivity.this, verseArray, s.toString(), search_type));
                finder_counter_update();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void finder_counter_update() {
        finder_count.setText(verseArray.size() + " founded!");
    }
}