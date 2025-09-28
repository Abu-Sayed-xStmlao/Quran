package com.sayed.quran;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class wordDbConn extends SQLiteOpenHelper {

    public static final String DB_PATH = "/storage/emulated/0/Android/Quran/";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "corpus.db";

    private SQLiteDatabase database;

    public wordDbConn(@Nullable Context context) {
        // আমরা আসলে internal DB বানাচ্ছি না, তাই null পাঠাচ্ছি
        super(context, null, null, DB_VERSION);

        // External DB ওপেন
        String fullPath = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(fullPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Pre-populated DB, তাই কিছু করার দরকার নাই
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Pre-populated DB update handle করার দরকার নাই
    }

    public ArrayList<wordInfoModel> getWordInfo(String sura, String ayah, String word) {
        ArrayList<wordInfoModel> wordInfoArray = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(
                "SELECT * FROM corpus WHERE sura = ? AND ayah = ? AND word = ?",
                new String[]{sura, ayah, word})) {

            if (cursor.moveToFirst()) {
                do {
                    String sura_no = cursor.getInt(cursor.getColumnIndexOrThrow("sura")) + "";
                    String ayah_no = cursor.getInt(cursor.getColumnIndexOrThrow("ayah")) + "";
                    String word_no = cursor.getString(cursor.getColumnIndexOrThrow("word"));
                    String count = cursor.getInt(cursor.getColumnIndexOrThrow("count")) + "";
                    String arabic = cursor.getString(cursor.getColumnIndexOrThrow("arabic"));
                    String position = cursor.getString(cursor.getColumnIndexOrThrow("position"));
                    String root_ar = cursor.getString(cursor.getColumnIndexOrThrow("root_ar"));
                    String lemma = cursor.getString(cursor.getColumnIndexOrThrow("lemma"));

                    wordInfoModel wim = new wordInfoModel(
                            sura_no, ayah_no, word_no, count, arabic, position, root_ar, lemma
                    );
                    wordInfoArray.add(wim);
                } while (cursor.moveToNext());
            }
        }

        return wordInfoArray;
    }

    // Helper properly close করার method
    @Override
    public synchronized void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
        super.close();
    }
}
