package com.sayed.quran;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView index_recyclerview;
    EditText search_input;
    ArrayList<IndexModel> indexArray = new ArrayList<>();
    Boolean is_searchbar_active = false;
    TextView longer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getTheme(this)); // Apply saved theme before UI is drawn
        ThemeHelper.makeStatusBarTransparent(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkStoragePermission();
        copyDatabaseToExternalStorage(this);


        dbConn dbConn = new dbConn(this);
        indexArray = dbConn.getIndex();


        index_recyclerview = findViewById(R.id.index_recyclerview);
        index_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        index_recyclerview.setAdapter(new IndexAdapter(this, indexArray));


        longer = findViewById(R.id.longer);
        longer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                startActivity(new Intent(MainActivity.this, QuranSearchTypeActivity.class));

                return true;
            }
        });
        search_input = findViewById(R.id.search_input);

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // live search as user types

                String finder = search_input.getText().toString().trim();
                if (finder.length() > 0) {
                    indexArray = dbConn.getIndex(finder);
                    index_recyclerview.setAdapter(new IndexAdapter(MainActivity.this, indexArray));
                } else {
                    indexArray = dbConn.getIndex();
                    index_recyclerview.setAdapter(new IndexAdapter(MainActivity.this, indexArray));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // optional
            }
        });

    }

    public void goTheme(View view) {
        startActivity(new Intent(MainActivity.this, ThemesActivity.class));
    }


    public void copyDatabaseToExternalStorage(Context context) {
        String folderPath = Environment.getExternalStorageDirectory() + "/Android/Quran";
        String outFileName = folderPath + "/theQuran.db";

        File dir = new File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File outFile = new File(outFileName);
        if (outFile.exists()) {
            return;
        }

        try {
            InputStream is = context.getAssets().open("theQuran.db");
            OutputStream os = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            os.flush();
            os.close();
            is.close();

            Toast.makeText(context, "Database copied to Quran folder!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to copy DB: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permission granted
            } else {
                // Ask for the MANAGE_EXTERNAL_STORAGE permission
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 101);
            } else {
                // Permission granted

            }
        }
    }


    public void show_seachbar(View view) {
        if (is_searchbar_active) {
            Animation animOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_fade_out);
            search_input.startAnimation(animOut);

            // animation শেষে GONE করে দিচ্ছি
            animOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    search_input.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            is_searchbar_active = false;

        } else {
            search_input.setVisibility(View.VISIBLE);
            Animation animIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_fade_in);
            search_input.startAnimation(animIn);

            is_searchbar_active = true;
        }
    }

}