package com.sayed.quran;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class dbConn extends SQLiteOpenHelper {

    public static final String DB_PATH = "/storage/emulated/0/Android/Quran/";
    public static final String DB_NAME = "theQuran.db"; // main db (db1)
    public static final String DB2_NAME = "corpus.db";  // secondary db (db2)
    public static final int DB_VERSION = 1;

    private final Context context;

    public dbConn(@Nullable Context context) {
        // Pass only main DB name to SQLiteOpenHelper
        super(context, DB_PATH + DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Not needed for pre-populated databases
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Example for update
        db.execSQL("DROP TABLE IF EXISTS sura");
        onCreate(db);
    }

    /**
     * Get a readable database and attach the second one
     */
    public SQLiteDatabase getJoinedDatabase() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DB_PATH + DB_NAME,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );

        // Attach second DB
        File db2File = new File(DB_PATH + DB2_NAME);
        if (db2File.exists()) {
            String attachQuery = "ATTACH DATABASE '" + db2File.getPath() + "' AS corpusDB;";
            db.execSQL(attachQuery);
            Log.d("DB_ATTACH", "corpus.db attached successfully!");
        } else {
            Log.e("DB_ATTACH", "corpus.db not found!");
        }

        return db;
    }

    /**
     * Example: run an INNER JOIN between theQuran.db and corpus.db
     */
    public void testJoin() {
        SQLiteDatabase db = getJoinedDatabase();

        String query = "SELECT q.arabic_text, c.word_translation " +
                "FROM verses q " +
                "INNER JOIN corpusDB.words c ON q.id = c.verse_id " +
                "WHERE q.sura = 1 LIMIT 10;";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String arabic = cursor.getString(0);
            String translation = cursor.getString(1);
            Log.d("JOIN_RESULT", arabic + " — " + translation);
        }

        cursor.close();
        db.close();
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


    public ArrayList<VerseModel> findWordVerses(String search_word) {
        String lang = LanguagePref.getLanguage(context);
        //  String cleared_search_word = QuranController.removeArabicSigns(search_word);
        ArrayList<VerseModel> versesList = new ArrayList<>();
        Map<Integer, String> wordsMap = new HashMap<>();
        SQLiteDatabase database = getJoinedDatabase();

        // Step 1: Preload all words for this sura into memory

        String wordsQuery = "SELECT w.sura, w.ayah, w.word, w." + lang +
                " FROM words w " +
                "INNER JOIN corpusDB.corpus cp " +
                "ON w.sura = cp.sura " +
                "AND w.ayah = cp.ayah " +
                "WHERE cp.arabic LIKE ? ";

        String full_finder_pattern = "%" + search_word + "%";

        //String cleared_finder_pattern = "%" + cleared_search_word + "%";

        try (Cursor cursor = database.rawQuery(wordsQuery, new String[]{full_finder_pattern})) {
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
        String verseQuery = "SELECT " +
                "ar.sura AS sura, ar.ayah AS ayah, ar.content AS arabic, tr.content AS translation " +
                "FROM ar_verses ar " +
                "INNER JOIN " + lang + "_verses tr " +
                "ON ar.id = tr.id " +

                "INNER JOIN corpusDB.corpus cp " +
                "ON ar.sura = cp.sura " +
                "AND ar.ayah = cp.ayah " +

                "WHERE " +
                "cp.arabic LIKE ? ";

        try (Cursor cursor = database.rawQuery(verseQuery, new String[]{full_finder_pattern})) {
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

                    // Fast lookup from cached words map
                    String words = wordsMap.getOrDefault(Integer.parseInt(ayah), "");

                    versesList.add(new VerseModel(sura, ayah, arabic, translation, words));
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return versesList;
    }


}
