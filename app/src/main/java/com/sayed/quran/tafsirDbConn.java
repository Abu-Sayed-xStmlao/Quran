package com.sayed.quran;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class tafsirDbConn extends SQLiteOpenHelper {

    public static final String DB_PATH = "/storage/emulated/0/Android/Quran/";
    public static final int DB_VERSION = 1;
    public static String DB_NAME = "kathir.db";
    private Context context;
    private SQLiteDatabase database;

    public tafsirDbConn(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

        // Language অনুযায়ী DB নির্বাচন
        if (LanguagePref.getLanguage(context).equals("en")) {
            DB_NAME = "tafsir_en.db";
        } else if (LanguagePref.getLanguage(context).equals("bn")) {
            DB_NAME = "tafsir_bn.db";
        } else {
            DB_NAME = "tafsir_hn.db";
        }

        // External DB ওপেন
        String fullPath = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(fullPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Not used (pre-populated DB)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // কিছু না করলে ভালো (pre-populated DB এর জন্য)
    }

    public String getTafsir(String sura, String ayah) {
        String tafsir = "";
        String sql = "SELECT text FROM verses WHERE sura = ? AND ayah = ?";
        try (Cursor cursor = database.rawQuery(sql, new String[]{sura, ayah})) {
            if (cursor.moveToFirst()) {
                tafsir = cursor.getString(cursor.getColumnIndexOrThrow("text"));
            }
        }
        return tafsir;
    }
}
