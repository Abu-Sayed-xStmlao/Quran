package com.sayed.quran;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ThemesActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    ArrayList<ThemeModel> themes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.getTheme(this)); // Apply saved theme before UI is drawn
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        makeStatusBarTransparent();
        checkStoragePermission();


        themes = new ArrayList<>();
        themes.add(new ThemeModel("Int Plant", R.color.int_plant_color, R.drawable.int_plant, R.color.int_plant_text_color, R.style.int_plant));
        themes.add(new ThemeModel("Rose", R.color.rose_color, R.drawable.rose, R.color.rose_text_color, R.style.rose));
        themes.add(new ThemeModel("Butterfly", R.color.butterfly_color, R.drawable.butterfly, R.color.butterfly_text_color, R.style.butterfly));
        themes.add(new ThemeModel("Lotus", R.color.lotus_color, R.drawable.lotus, R.color.lotus_text_color, R.style.lotus));
        themes.add(new ThemeModel("Beach Life", R.color.beach_life_color, R.drawable.beach_life, R.color.beach_life_text_color, R.style.beach_life));
        themes.add(new ThemeModel("Little World", R.color.little_world_color, R.drawable.little_world, R.color.little_world_text_color, R.style.little_world));
        themes.add(new ThemeModel("Cilantro", R.color.cilantro_color, R.drawable.cilantro, R.color.cilantro_text_color, R.style.cilantro));
        themes.add(new ThemeModel("Green Sprout", R.color.green_sprout_color, R.drawable.green_sprout, R.color.green_sprout_text_color, R.style.green_sprout));
        themes.add(new ThemeModel("Momagic", R.color.momagic_color, R.drawable.momagic, R.color.momagic_text_color, R.style.momagic));
        themes.add(new ThemeModel("Pants", R.color.pants_color, R.drawable.pants, R.color.pants_text_color, R.style.pants));
        themes.add(new ThemeModel("Pink Rose", R.color.pink_rose_color, R.drawable.pink_rose, R.color.pink_rose_text_color, R.style.pink_rose));
        themes.add(new ThemeModel("Pomegranate", R.color.pomegranate_color, R.drawable.pomegranate, R.color.pomegranate_text_color, R.style.pomegranate));


        themes.add(new ThemeModel("Cherry", R.color.cherry_color, R.drawable.cherry, R.color.cherry_text_color, R.style.cherry));
        themes.add(new ThemeModel("Fruits Green", R.color.fruits_green_color, R.drawable.fruits_green, R.color.fruits_green_text_color, R.style.fruits_green));
        themes.add(new ThemeModel("Green Perfume", R.color.green_perfume_color, R.drawable.green_perfume, R.color.green_perfume_text_color, R.style.green_perfume));
        themes.add(new ThemeModel("Lychee", R.color.lychee_color, R.drawable.lychee, R.color.lychee_text_color, R.style.lychee));
        themes.add(new ThemeModel("Olive Cake", R.color.olive_cake_color, R.drawable.olive_cake, R.color.olive_cake_text_color, R.style.olive_cake));
        themes.add(new ThemeModel("Olive", R.color.olive_color, R.drawable.olive, R.color.olive_text_color, R.style.olive));
        themes.add(new ThemeModel("Omelette", R.color.omelette_color, R.drawable.omelette, R.color.omelette_text_color, R.style.omelette));
        themes.add(new ThemeModel("Orange Juice", R.color.orange_juice_color, R.drawable.orange_juice, R.color.orange_juice_text_color, R.style.orange_juice));
        themes.add(new ThemeModel("Orange", R.color.orange_color, R.drawable.orange, R.color.orange_text_color, R.style.orange));
        themes.add(new ThemeModel("Strawberry", R.color.strawberry_color, R.drawable.strawberry, R.color.strawberry_text_color, R.style.strawberry));

        themes.add(new ThemeModel(
                "theme1",
                R.color.theme1_color,
                R.drawable.theme1,
                R.color.theme1_text_color,
                R.style.theme1
        ));

        themes.add(new ThemeModel(
                "theme2",
                R.color.theme2_color,
                R.drawable.theme2,
                R.color.theme2_text_color,
                R.style.theme2
        ));

        themes.add(new ThemeModel(
                "theme3",
                R.color.theme3_color,
                R.drawable.theme3,
                R.color.theme3_text_color,
                R.style.theme3
        ));
        themes.add(new ThemeModel(
                "theme4",
                R.color.theme4_color,
                R.drawable.theme4,
                R.color.theme4_text_color,
                R.style.theme4
        ));
        themes.add(new ThemeModel(
                "theme5",
                R.color.theme5_color,
                R.drawable.theme5,
                R.color.theme5_text_color,
                R.style.theme5
        ));
        ;
        themes.add(new ThemeModel(
                "theme6",
                R.color.theme6_color,
                R.drawable.theme6,
                R.color.theme6_text_color,
                R.style.theme6
        ));
        ;
        themes.add(new ThemeModel(
                "theme7",
                R.color.theme7_color,
                R.drawable.theme7,
                R.color.theme7_text_color,
                R.style.theme7
        ));
        themes.add(new ThemeModel(
                "theme8",
                R.color.theme8_color,
                R.drawable.theme8,
                R.color.theme8_text_color,
                R.style.theme8
        ));
        themes.add(new ThemeModel(
                "theme9",
                R.color.theme9_color,
                R.drawable.theme9,
                R.color.theme9_text_color,
                R.style.theme9
        ));
        themes.add(new ThemeModel(
                "theme10",
                R.color.theme10_color,
                R.drawable.theme10,
                R.color.theme10_text_color,
                R.style.theme10
        ));
        themes.add(new ThemeModel(
                "theme11",
                R.color.theme11_color,
                R.drawable.theme11,
                R.color.theme11_text_color,
                R.style.theme11
        ));
        themes.add(new ThemeModel(
                "theme12",
                R.color.theme12_color,
                R.drawable.theme12,
                R.color.theme12_text_color,
                R.style.theme12
        ));

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new ThemesAdapter(this, themes));


    }


    public void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permission granted
            } else {
                // Ask for the MANAGE_EXTERNAL_STORAGE permission
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 111);
            } else {
                // Permission granted
            }
        }
    }


}