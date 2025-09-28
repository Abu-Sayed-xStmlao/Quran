package com.sayed.quran;

public class wordInfoModel {

    String sura_no, ayah_no, word_no, count, arabic, position, root_ar, lemma;

    public wordInfoModel(String sura_no, String ayah_no, String word_no, String count, String arabic, String position, String root_ar, String lemma) {
        this.sura_no = sura_no;
        this.ayah_no = ayah_no;
        this.word_no = word_no;
        this.count = count;
        this.arabic = arabic;
        this.position = position;
        this.root_ar = root_ar;
        this.lemma = lemma;
    }
}
