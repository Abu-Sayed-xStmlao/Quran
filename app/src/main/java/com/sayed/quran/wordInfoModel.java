package com.sayed.quran;

public class wordInfoModel {

    String sura_no;
    String ayah_no;
    String word_no;
    String count;
    String arabic1;
    String arabic2;
    String arabic3;
    String arabic4;
    String arabic5;
    String pos1;
    String pos2;
    String pos3;
    String pos4;
    String pos5;
    String root_ar;
    String lemma;

    public wordInfoModel(String sura_no, String ayah_no, String word_no, String count, String arabic1, String arabic2, String arabic3, String arabic4, String arabic5, String pos1, String pos2, String pos3, String pos4, String pos5, String root_ar, String lemma) {
        this.sura_no = sura_no;
        this.ayah_no = ayah_no;
        this.word_no = word_no;
        this.count = count;
        this.arabic1 = arabic1;
        this.arabic2 = arabic2;
        this.arabic3 = arabic3;
        this.arabic4 = arabic4;
        this.arabic5 = arabic5;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.pos4 = pos4;
        this.pos5 = pos5;
        this.root_ar = root_ar;
        this.lemma = lemma;
    }


}
