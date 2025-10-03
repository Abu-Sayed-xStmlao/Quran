package com.sayed.quran;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dbConn extends SQLiteOpenHelper {

    public static final String DB_PATH = "/storage/emulated/0/Android/Quran/";
    public static final String DB_NAME = "theQuran.db";
    public static final int DB_VERSION = 1;

    private final Context context;

    public dbConn(@Nullable Context context) {
        super(context, DB_PATH + DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Not needed for pre-populated databases
        // But if needed later, you can create tables here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS sura");
        onCreate(db);
    }


    public ArrayList<IndexModel> getIndex() {
        ArrayList<IndexModel> indexList = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        String lang = LanguagePref.getLanguage(context);
        // Use try-with-resources to ensure automatic resource cleanup
        try (Cursor cursor = database.rawQuery("SELECT * FROM sura ", null)) {
            if (cursor.moveToFirst()) {
                do {
                    String sura = cursor.getInt(cursor.getColumnIndexOrThrow("sura")) + "";
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("name_" + lang));
                    String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                    String ayah_count = cursor.getInt(cursor.getColumnIndexOrThrow("ayat_count")) + "";

                    IndexModel model = new IndexModel(sura, title, meaning, ayah_count);
                    indexList.add(model);
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return indexList;
    }

    public ArrayList<IndexModel> getIndex(String find) {
        ArrayList<IndexModel> indexList = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        String lang = LanguagePref.getLanguage(context);
        // Use try-with-resources to ensure automatic resource cleanup


        Cursor cursor = database.rawQuery(
                "SELECT * FROM sura WHERE name_en LIKE ? OR name_bn LIKE ? OR name_hn LIKE ? OR meaning LIKE ? OR sura LIKE ?",
                new String[]{"%" + find + "%", "%" + find + "%", "%" + find + "%", "%" + find + "%", "%" + find + "%"}
        );


        try (cursor) {
            if (cursor.moveToFirst()) {
                do {
                    String sura = cursor.getInt(cursor.getColumnIndexOrThrow("sura")) + "";
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("name_" + lang));
                    String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                    String ayah_count = cursor.getInt(cursor.getColumnIndexOrThrow("ayat_count")) + "";

                    IndexModel model = new IndexModel(sura, title, meaning, ayah_count);

                    indexList.add(model);
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return indexList;
    }

    public ArrayList<VerseModel> getVerses(int sura, String lang) {
        ArrayList<VerseModel> versesList = new ArrayList<>();
        Map<Integer, String> wordsMap = new HashMap<>();
        SQLiteDatabase database = this.getReadableDatabase();

        // Step 1: Preload all words for this sura into memory
        String wordsQuery = "SELECT ayah, " + lang + " FROM words WHERE sura = ?";
        try (Cursor cursor = database.rawQuery(wordsQuery, new String[]{String.valueOf(sura)})) {
            if (cursor.moveToFirst()) {
                int ayahCol = cursor.getColumnIndexOrThrow("ayah");
                int langCol = cursor.getColumnIndexOrThrow(lang);
                do {
                    int ayah = cursor.getInt(ayahCol);
                    String word = cursor.getString(langCol);

                    // Append multiple words per ayah
                    if (!wordsMap.containsKey(ayah)) {
                        wordsMap.put(ayah, "__#" + word);
                    } else {
                        wordsMap.put(ayah, wordsMap.get(ayah) + "__#" + word);
                    }
                } while (cursor.moveToNext());
            }
        }

        // Step 2: Load all verses with translations
        String verseQuery = "SELECT ar.ayah AS ayah, ar.content AS arabic, tr.content AS translation " +
                "FROM ar_verses ar " +
                "INNER JOIN " + lang + "_verses tr ON ar.id = tr.id " +
                "WHERE ar.sura = ?";
        try (Cursor cursor = database.rawQuery(verseQuery, new String[]{String.valueOf(sura)})) {
            if (cursor.moveToFirst()) {
                int ayahCol = cursor.getColumnIndexOrThrow("ayah");
                int arabicCol = cursor.getColumnIndexOrThrow("arabic");
                int transCol = cursor.getColumnIndexOrThrow("translation");

                do {
                    String ayah = cursor.getString(ayahCol);
                    String arabic = cursor.getString(arabicCol);
                    String translation = cursor.getString(transCol);

                    // Fast lookup from cached words map
                    String words = wordsMap.getOrDefault(Integer.parseInt(ayah), "");

                    versesList.add(new VerseModel(sura + "", ayah, arabic, translation, words));
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return versesList;
    }


    public ArrayList<suraInfoModel> getSuraInfo(String sura_no) {
        ArrayList<suraInfoModel> suraInfo = new ArrayList<>();
        String lang = LanguagePref.getLanguage(context);
        SQLiteDatabase database = this.getWritableDatabase();
        // Use try-with-resources to ensure automatic resource cleanup
        try (Cursor cursor = database.rawQuery("SELECT * FROM sura WHERE sura = ?", new String[]{sura_no})) {
            if (cursor.moveToFirst()) {
                do {
                    String sura_title = cursor.getString(cursor.getColumnIndexOrThrow("name_" + lang));
                    String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                    String ayat_count = cursor.getInt(cursor.getColumnIndexOrThrow("ayat_count")) + "";
                    suraInfoModel suraInfoModel = new suraInfoModel();
                    suraInfoModel.sura_title = sura_title;
                    suraInfoModel.meaning = meaning;
                    suraInfoModel.ayah_count = ayat_count;

                    suraInfo.add(suraInfoModel);

                } while (cursor.moveToNext());
            }
        }

        database.close();
        return suraInfo;
    }


    public ArrayList<VerseModel> findVerses(String finder) {
        ArrayList<VerseModel> versesList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        String lang = LanguagePref.getLanguage(context);

        // Step 2: Load all verses with translations
        String verseQuery = "SELECT ar.sura AS sura, ar.ayah AS ayah, ar.content AS arabic, tr.content AS translation " +
                "FROM arabic_verses ar " +
                "INNER JOIN " + lang + "_verses tr ON ar.id = tr.id " +
                "WHERE " +
                "ar.ayah LIKE ? OR " +
                "ar.content LIKE ? OR " +
                "tr.content LIKE ?";
        try (Cursor cursor = database.rawQuery(verseQuery, new String[]{"%" + finder + "%", "%" + finder + "%", "%" + finder + "%"})) {
            if (cursor.moveToFirst()) {
                int suraCol = cursor.getColumnIndexOrThrow("sura");
                int ayahCol = cursor.getColumnIndexOrThrow("ayah");
                int arabicCol = cursor.getColumnIndexOrThrow("arabic");
                int transCol = cursor.getColumnIndexOrThrow("translation");

                do {
                    String sura = cursor.getString(suraCol);
                    String ayah = cursor.getString(ayahCol);
                    String arabic = cursor.getString(arabicCol);
                    String translation = cursor.getString(transCol);

                    versesList.add(new VerseModel(sura, ayah, arabic, translation, ""));
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return versesList;
    }


    public ArrayList<VerseModel> loadVerses() {
        ArrayList<VerseModel> versesList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        String lang = LanguagePref.getLanguage(context);

        // Step 2: Load all verses with translations
        String verseQuery = "SELECT ar.sura AS sura, ar.ayah AS ayah, ar.content AS arabic, tr.content AS translation " +
                "FROM arabic_verses ar " +
                "INNER JOIN " + lang + "_verses tr ON ar.id = tr.id  LIMIT 20";
        try (Cursor cursor = database.rawQuery(verseQuery, new String[]{})) {
            if (cursor.moveToFirst()) {
                int suraCol = cursor.getColumnIndexOrThrow("sura");
                int ayahCol = cursor.getColumnIndexOrThrow("ayah");
                int arabicCol = cursor.getColumnIndexOrThrow("arabic");
                int transCol = cursor.getColumnIndexOrThrow("translation");

                do {
                    String sura = cursor.getString(suraCol);
                    String ayah = cursor.getString(ayahCol);
                    String arabic = cursor.getString(arabicCol);
                    String translation = cursor.getString(transCol);

                    versesList.add(new VerseModel(sura, ayah, arabic, translation, ""));
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return versesList;
    }


    public ArrayList<VerseModel> findWordVerses(String searchWord) {
        String lang = LanguagePref.getLanguage(context);

        ArrayList<VerseModel> versesList = new ArrayList<>();
        Map<String, String> wordsMap = new HashMap<>(); // key = "sura:ayah", value = concatenated words

        SQLiteDatabase database = this.getReadableDatabase();

        // Step 1: Load all matching verses
        String verseQuery = "SELECT ar.sura AS sura, ar.ayah AS ayah, ar.content AS arabic, tr.content AS translation " +
                "FROM arabic_verses ar " +
                "INNER JOIN " + lang + "_verses tr ON ar.id = tr.id " +
                "WHERE ar.ayah LIKE ? OR ar.content LIKE ? OR tr.content LIKE ?";

        String searchPattern = "%" + searchWord + "%";

        List<String> suraAyahPairs = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(verseQuery, new String[]{searchPattern, searchPattern, searchPattern})) {
            if (cursor.moveToFirst()) {
                int suraCol = cursor.getColumnIndexOrThrow("sura");
                int ayahCol = cursor.getColumnIndexOrThrow("ayah");
                int arabicCol = cursor.getColumnIndexOrThrow("arabic");
                int transCol = cursor.getColumnIndexOrThrow("translation");

                do {
                    String sura = cursor.getString(suraCol);
                    String ayah = cursor.getString(ayahCol);

                    suraAyahPairs.add(sura + ":" + ayah);

                    versesList.add(new VerseModel(
                            sura,
                            ayah,
                            cursor.getString(arabicCol),
                            cursor.getString(transCol),
                            "" // placeholder for words
                    ));
                } while (cursor.moveToNext());
            }
        }

        // Step 2: Preload all words for matching ayahs in one query
        if (!suraAyahPairs.isEmpty()) {
            StringBuilder placeholders = new StringBuilder();
            List<String> params = new ArrayList<>();
            for (String pair : suraAyahPairs) {
                String[] parts = pair.split(":");
                placeholders.append("(sura = ? AND ayah = ?) OR ");
                params.add(parts[0]);
                params.add(parts[1]);
            }
            placeholders.setLength(placeholders.length() - 4); // remove trailing " OR "

            String wordsQuery = "SELECT sura, ayah, " + lang + " FROM words WHERE " + placeholders;

            try (Cursor wCursor = database.rawQuery(wordsQuery, params.toArray(new String[0]))) {
                if (wCursor.moveToFirst()) {
                    int suraCol = wCursor.getColumnIndexOrThrow("sura");
                    int ayahCol = wCursor.getColumnIndexOrThrow("ayah");
                    int langCol = wCursor.getColumnIndexOrThrow(lang);

                    do {
                        String key = wCursor.getString(suraCol) + ":" + wCursor.getString(ayahCol);
                        String word = wCursor.getString(langCol);

                        wordsMap.put(key, wordsMap.getOrDefault(key, "") + "__#" + word);
                    } while (wCursor.moveToNext());
                }
            }
        }

        // Step 3: Attach words to verses
        for (VerseModel verse : versesList) {
            String key = verse.sura + ":" + verse.ayah;
            verse.words = wordsMap.getOrDefault(key, "");
        }

        database.close();
        return versesList;
    }


}
