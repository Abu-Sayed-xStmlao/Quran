package com.sayed.quran;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.view.Window;

public class ThemeHelper {
    private static final String PREF_NAME = "theme_pref";
    private static final String KEY_THEME = "selected_theme";

    public static void saveTheme(Context context, int themeId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME, themeId);
        editor.apply();
    }

    public static int getTheme(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_THEME, R.style.theme10); // Default theme
    }

    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        }
        hideNavigationBar(activity);
    }


    public static void hideNavigationBar(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }


}