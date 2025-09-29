package com.sayed.quran;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuranActivity extends AppCompatActivity {

    RecyclerView quran_recyclerview;
    ArrayList<VerseModel> versesArray = new ArrayList<>();
    TextView title, jumper_counter;
    VersesAdapter versesAdapter;
    dbConn dbConn;
    String requested_title, requested_sura, requested_ayah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getTheme(this)); // Apply saved theme before UI is drawn
        ThemeHelper.makeStatusBarTransparent(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran);


        dbConn = new dbConn(this);
        title = findViewById(R.id.title);
        quran_recyclerview = findViewById(R.id.quran_recyclerview);
        jumper_counter = findViewById(R.id.jumper_counter);


        Intent intent = getIntent();
        requested_sura = intent.getStringExtra("sura");
        requested_ayah = (intent != null) ? intent.getStringExtra("ayah") : null;
        requested_title = dbConn.getSuraInfo(requested_sura).get(0).sura_title;


        title.setText(requested_title);
        FontController.initialize_font_face(QuranActivity.this, title);
        Toast.makeText(this, requested_sura, Toast.LENGTH_SHORT).show();

        int requested_sura_int = Integer.parseInt(requested_sura);
        versesArray = dbConn.getVerses(requested_sura_int, LanguagePref.getLanguage(this));

        versesAdapter = new VersesAdapter(QuranActivity.this, versesArray);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        quran_recyclerview.setLayoutManager(layoutManager);
        quran_recyclerview.setAdapter(versesAdapter);
        quran_recyclerview.setHasFixedSize(true);

        if (requested_ayah != null) {
            int ayahPosition = Integer.parseInt(requested_ayah);
            layoutManager.scrollToPositionWithOffset(ayahPosition, 200); // offset 200px
        }


        jumper_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuranController.jumper_popup(QuranActivity.this, requested_sura_int, quran_recyclerview);
            }
        });

        quran_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (lm != null) {
                    int topEdgeItem = lm.findFirstCompletelyVisibleItemPosition();
                    if (topEdgeItem == RecyclerView.NO_POSITION) {
                        topEdgeItem = lm.findFirstVisibleItemPosition();
                    }

                    if (topEdgeItem >= 0 && topEdgeItem < versesArray.size() + 1) {
                        jumper_counter.setText(requested_sura + " : " + topEdgeItem);
                    }
                }
            }
        });


        title.setOnClickListener(c -> showLanguageDialog(requested_sura_int));
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                quran_recyclerview.smoothScrollToPosition(0);
                return true;
            }
        });
    }

    private void showLanguageDialog(int suraNumber) {
        Dialog dialog = new Dialog(QuranActivity.this);
        dialog.setContentView(R.layout.popup_language_chooser);
        dialog.setCancelable(true);

        TextView btnEnglish = dialog.findViewById(R.id.btn_english);
        TextView btnBangla = dialog.findViewById(R.id.btn_bangla);
        TextView btnHindi = dialog.findViewById(R.id.btn_hindi);

        View.OnClickListener languageClickListener = v -> {
            String lang = "en";
            if (v.getId() == R.id.btn_bangla) {
                lang = "bn";
            } else if (v.getId() == R.id.btn_hindi) {
                lang = "hn";
            }

            LanguagePref.setLanguage(QuranActivity.this, lang);
            versesArray.clear();
            versesArray.addAll(dbConn.getVerses(suraNumber, lang));
            versesAdapter.notifyDataSetChanged();
            requested_title = dbConn.getSuraInfo(requested_sura).get(0).sura_title;
            title.setText(requested_title);
            FontController.initialize_font_face(QuranActivity.this, title);
            dialog.dismiss();
        };

        btnEnglish.setOnClickListener(languageClickListener);
        btnBangla.setOnClickListener(languageClickListener);
        btnHindi.setOnClickListener(languageClickListener);

        dialog.show();
    }


}
