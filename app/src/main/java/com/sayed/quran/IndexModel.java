package com.sayed.quran;

public class IndexModel {
    String sura, name_en, name_bn, name_hn, meaning, ayat_count;

    public IndexModel(String sura, String name_en, String name_bn, String name_hn, String meaning, String ayat_count) {
        this.sura = sura;
        this.name_en = name_en;
        this.name_bn = name_bn;
        this.name_hn = name_hn;
        this.meaning = meaning;
        this.ayat_count = ayat_count;
    }

}
