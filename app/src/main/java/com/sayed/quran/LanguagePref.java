package com.sayed.quran;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguagePref {
    private static final String PREF_NAME = "app_settings";
    private static final String KEY_LANG = "selected_lang";

    public static void setLanguage(Context context, String langCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANG, langCode).apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANG, "en"); // default "en"
    }
}
