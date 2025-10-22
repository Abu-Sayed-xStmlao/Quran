package com.sayed.quran;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class dbConn extends SQLiteOpenHelper {
    public static final String DB_PATH = "/storage/emulated/0/Android/fts/";
    public static final String INDEX_DB = "index.db"; //index db file
    public static final String QURAN_DB = "quran.db"; //main db file
    public static final String WORDS_DB = "words.db"; //words db file
    public static final String CORPUS_DB = "corpus.db"; //words db file
    public static final int DB_VERSION = 1;
    public static String TRANSLATION_DB = "en_sahih.db"; //translation db file
    public static String TAFSIR_DB = "kathir.db"; //tafsir db file
    public final Context context;
    public SQLiteDatabase database;

    public dbConn(@Nullable Context context) {
        // Pass only main DB name to SQLiteOpenHelper
        super(context, DB_PATH + INDEX_DB, null, DB_VERSION);
        this.context = context;
    }


    //define index database
    public SQLiteDatabase getIndexDb() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DB_PATH + INDEX_DB,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );
        return db;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Not needed for pre-populated databases
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Example for update
    }


    public ArrayList<IndexModel> getIndex() {
        ArrayList<IndexModel> indexList = new ArrayList<>();
        SQLiteDatabase database = getIndexDb();
        String lang = LanguagePref.getLanguage(context);
        // Use try-with-resources to ensure automatic resource cleanup
        try (Cursor cursor = database.rawQuery("SELECT * FROM sura ", null)) {
            if (cursor.moveToFirst()) {
                do {
                    String sura = cursor.getInt(cursor.getColumnIndexOrThrow("sura")) + "";
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("name_" + lang));
                    String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                    String ayah_count = cursor.getInt(cursor.getColumnIndexOrThrow("ayah_count")) + "";

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
        SQLiteDatabase database = getIndexDb();
        String lang = LanguagePref.getLanguage(context);
        // Use try-with-resources to ensure automatic resource cleanup


        Cursor cursor = database.rawQuery(
                "SELECT * FROM sura WHERE name_en LIKE ? OR name_bn LIKE ? OR name_hi LIKE ? OR meaning LIKE ? OR sura LIKE ?",
                new String[]{"%" + find + "%", "%" + find + "%", "%" + find + "%", "%" + find + "%", "%" + find + "%"}
        );


        try (cursor) {
            if (cursor.moveToFirst()) {
                do {
                    String sura = cursor.getInt(cursor.getColumnIndexOrThrow("sura")) + "";
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("name_" + lang));
                    String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                    String ayah_count = cursor.getInt(cursor.getColumnIndexOrThrow("ayah_count")) + "";

                    IndexModel model = new IndexModel(sura, title, meaning, ayah_count);

                    indexList.add(model);
                } while (cursor.moveToNext());
            }
        }

        database.close();
        return indexList;
    }


    //define arabic, translation, words database
    public SQLiteDatabase getATWDatabase() {
        String language = LanguagePref.getLanguage(context);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DB_PATH + QURAN_DB,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );


        switch (language) {
            case "bn":
                TRANSLATION_DB = "translation_bn.db";
                break;
            case "hi":
                TRANSLATION_DB = "translation_hi.db";
                break;
            default:
                TRANSLATION_DB = "translation_en.db";
                break;
        }


        // db.execSQL("ATTACH DATABASE '" + DB_PATH + QURAN_DB + "' AS arDB");
        db.execSQL("ATTACH DATABASE '" + DB_PATH + TRANSLATION_DB + "' AS trDB");
        db.execSQL("ATTACH DATABASE '" + DB_PATH + WORDS_DB + "' AS wordsDB");
        return db;
    }

    //define arabic, translation, words database
    public SQLiteDatabase getATWCDatabase() {
        String language = LanguagePref.getLanguage(context);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DB_PATH + QURAN_DB,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );


        switch (language) {
            case "bn":
                TRANSLATION_DB = "translation_bn.db";
                break;
            case "hi":
                TRANSLATION_DB = "translation_hi.db";
                break;
            default:
                TRANSLATION_DB = "translation_en.db";
                break;
        }


        db.execSQL("ATTACH DATABASE '" + DB_PATH + TRANSLATION_DB + "' AS trDB");
        db.execSQL("ATTACH DATABASE '" + DB_PATH + WORDS_DB + "' AS wordsDB");
        db.execSQL("ATTACH DATABASE '" + DB_PATH + CORPUS_DB + "' AS corpusDB");
        return db;
    }


    public ArrayList<VerseModel> getVerses(int sura) {
        String language = LanguagePref.getLanguage(context);
        ArrayList<VerseModel> versesList = new ArrayList<>();
        SQLiteDatabase database = getATWDatabase();
        String exploder = "__#";
        String verseQuery =
                "SELECT " +
                        "q.ayah AS ayah, q.content AS arabic, " +
                        "t.content AS translation, " +
                        "GROUP_CONCAT('" + exploder + "' || wd." + language + ", '') AS words " +
                        "FROM verses q " +
                        "INNER JOIN trDB.verses t ON " +
                        "q.sura = t.sura AND " +
                        "q.ayah = t.ayah " +
                        "INNER JOIN wordsDB.words wd ON " +
                        "q.sura = wd.sura AND q.ayah = wd.ayah " +
                        "WHERE " +
                        "q.sura = " + sura + " " +
                        "GROUP BY q.sura, q.ayah " +
                        "ORDER BY q.sura, q.ayah";


        try (Cursor cursor = database.rawQuery(verseQuery, null)) {
            if (cursor.moveToFirst()) {
                int ayahCol = cursor.getColumnIndexOrThrow("ayah");
                int arabicCol = cursor.getColumnIndexOrThrow("arabic");
                int transCol = cursor.getColumnIndexOrThrow("translation");
                int wordsCol = cursor.getColumnIndexOrThrow("words");

                do {
                    String ayah = cursor.getString(ayahCol);
                    String arabic = cursor.getString(arabicCol);
                    String translation = cursor.getString(transCol);
                    String words = cursor.getString(wordsCol);

                    // Fast lookup from cached words map
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
        SQLiteDatabase database = getIndexDb();
        // Use try-with-resources to ensure automatic resource cleanup
        try (Cursor cursor = database.rawQuery("SELECT * FROM sura WHERE sura = " + Integer.parseInt(sura_no) + " ", null)) {
            if (cursor.moveToFirst()) {
                do {
                    String sura_title = cursor.getString(cursor.getColumnIndexOrThrow("name_" + lang));
                    String meaning = cursor.getString(cursor.getColumnIndexOrThrow("meaning"));
                    String ayah_count = cursor.getInt(cursor.getColumnIndexOrThrow("ayah_count")) + "";
                    suraInfoModel suraInfoModel = new suraInfoModel();
                    suraInfoModel.sura_title = sura_title;
                    suraInfoModel.meaning = meaning;
                    suraInfoModel.ayah_count = ayah_count;

                    suraInfo.add(suraInfoModel);

                } while (cursor.moveToNext());
            }
        }

        database.close();
        return suraInfo;
    }


    public ArrayList<VerseModel> findVerses(String finder) {
        ArrayList<VerseModel> versesList = new ArrayList<>();
        SQLiteDatabase database = getATDatabase();

        String lang = LanguagePref.getLanguage(context);

        // Step 2: Load all verses with translations
        // Step 2: Load all verses with translations
        String verseQuery = "SELECT " +
                "vc.c0sura AS sura, " +
                "vc.c1ayah AS ayah, " +
                "vc.c2text AS arabic, " +
                "tr.c2text AS translation " +
                "FROM verses_content vc " +
                "INNER JOIN trDB.verses_content tr " +
                "ON tr.c0sura = vc.c0sura AND " +
                "tr.c1ayah = vc.c1ayah " +
                "INNER JOIN arDB.verses_content ar " +
                "ON tr.c0sura = ar.c0sura AND " +
                "tr.c1ayah = ar.c1ayah  " +
                "WHERE " +
                "vc.c2text LIKE ? OR " +
                "tr.c2text LIKE ? OR " +
                "ar.c2text LIKE ?  ";
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


    public SQLiteDatabase getATDatabase() {
        String language = LanguagePref.getLanguage(context);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DB_PATH + QURAN_DB,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );

        switch (language) {
            case "bn":
                TRANSLATION_DB = "translation_bn.db";
                break;
            case "hi":
                TRANSLATION_DB = "translation_hi.db";
                break;
            default:
                TRANSLATION_DB = "translation_en.db";
                break;
        }


        db.execSQL("ATTACH DATABASE '" + DB_PATH + "quran_ar.db" + "' AS arDB");
        db.execSQL("ATTACH DATABASE '" + DB_PATH + TRANSLATION_DB + "' AS trDB");
        db.execSQL("ATTACH DATABASE '" + DB_PATH + WORDS_DB + "' AS wordsDB");
        return db;
    }


    public ArrayList<VerseModel> loadVerses() {
        ArrayList<VerseModel> versesList = new ArrayList<>();
        SQLiteDatabase database = getATDatabase();

        String lang = LanguagePref.getLanguage(context);

        // Step 2: Load all verses with translations
        String verseQuery = "SELECT " +
                "vc.c0sura AS sura, " +
                "vc.c1ayah AS ayah, " +
                "vc.c2text AS arabic, " +
                "tr.c2text AS translation " +
                "FROM verses_content vc " +
                "INNER JOIN trDB.verses_content tr " +
                "ON tr.c0sura = vc.c0sura AND " +
                "tr.c1ayah = vc.c1ayah " +
                "INNER JOIN arDB.verses_content ar " +
                "ON tr.c0sura = ar.c0sura AND " +
                "tr.c1ayah = ar.c1ayah  " +
                "LIMIT 100";
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

    public String getTafsir(int sura, int ayah) {


        String language = LanguagePref.getLanguage(context);


        switch (language) {
            case "bn":
                TAFSIR_DB = "bn_tafsir_kathir.db";
                break;
            case "hn":
                TAFSIR_DB = "ur_maududi.db";
                break;
            default:
                TAFSIR_DB = "kathir.db";
                break;
        }


        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DB_PATH + TAFSIR_DB,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );


        String tafsir = "";
        String sql = "SELECT c2text FROM verses_content WHERE c0sura = " + sura + " AND c1ayah = " + ayah;
        try (Cursor cursor = db.rawQuery(sql, null)) {
            if (cursor.moveToFirst()) {
                tafsir = cursor.getString(cursor.getColumnIndexOrThrow("c2text"));
            }
        }
        return tafsir;
    }


    //define index database
    public SQLiteDatabase getCorpusDb() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                DB_PATH + CORPUS_DB,
                null,
                SQLiteDatabase.OPEN_READWRITE
        );
        return db;
    }


    public ArrayList<wordInfoModel> getWordInfo(String sura, String ayah, String word) {
        SQLiteDatabase database = getCorpusDb();
        ArrayList<wordInfoModel> wordInfoArray = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(
                "SELECT * FROM corpus WHERE sura = ? AND ayah = ? AND word = ?",
                new String[]{sura, ayah, word})) {

            if (cursor.moveToFirst()) {
                do {
                    String sura_no = cursor.getString(cursor.getColumnIndexOrThrow("sura"));
                    String ayah_no = cursor.getString(cursor.getColumnIndexOrThrow("ayah"));
                    String word_no = cursor.getString(cursor.getColumnIndexOrThrow("word"));
                    String count = cursor.getString(cursor.getColumnIndexOrThrow("count"));

                    String arabic1 = cursor.getString(cursor.getColumnIndexOrThrow("ar1"));
                    String arabic2 = cursor.getString(cursor.getColumnIndexOrThrow("ar2"));
                    String arabic3 = cursor.getString(cursor.getColumnIndexOrThrow("ar3"));
                    String arabic4 = cursor.getString(cursor.getColumnIndexOrThrow("ar4"));
                    String arabic5 = cursor.getString(cursor.getColumnIndexOrThrow("ar5"));

                    String pos1 = cursor.getString(cursor.getColumnIndexOrThrow("pos1"));
                    String pos2 = cursor.getString(cursor.getColumnIndexOrThrow("pos2"));
                    String pos3 = cursor.getString(cursor.getColumnIndexOrThrow("pos3"));
                    String pos4 = cursor.getString(cursor.getColumnIndexOrThrow("pos4"));
                    String pos5 = cursor.getString(cursor.getColumnIndexOrThrow("pos5"));

                    String root_ar = cursor.getString(cursor.getColumnIndexOrThrow("root_ar"));
                    String lemma = cursor.getString(cursor.getColumnIndexOrThrow("lemma"));

                    wordInfoModel wim = new wordInfoModel(
                            sura_no, ayah_no, word_no, count, arabic1, arabic2, arabic3, arabic4, arabic5, pos1, pos2, pos3, pos4, pos5, root_ar, lemma
                    );
                    wordInfoArray.add(wim);
                } while (cursor.moveToNext());
            }
        }

        return wordInfoArray;
    }


}
