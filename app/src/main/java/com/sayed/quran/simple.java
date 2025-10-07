package com.sayed.quran;

public class simple {

    public static void main(String[] args) {
        String a = "بِسۡمِ ٱللَّهِ ٱلرَّحْمَٰنِ";
        String b = "سْمِ";
        b = b.replace('\u0652', '\u06E1'); // Normal sukun → extended sukun


        System.out.println(a.contains(b)); // দেখো true না false আসে

        for (char c : b.toCharArray()) {
            System.out.println(String.format("U+%04X ", (int) c));
        }

    }
}
