package com.sayed.quran;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class QuranController {

    public static void jumper_popup(Context context, int sura, RecyclerView recyclerView) {


        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_warning_layout);

        EditText jumper_sura = dialog.findViewById(R.id.jumper_sura_no);
        EditText jumper_ayah = dialog.findViewById(R.id.jumper_ayah_no);
        Button button = dialog.findViewById(R.id.continue_button);

        jumper_sura.setText(sura + "");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suraText = jumper_sura.getText().toString();
                String ayahText = jumper_ayah.getText().toString();


                dbConn dbConn = new dbConn(context);
                int verses_count = dbConn.getVerses(Integer.parseInt(suraText)).size();
                int jumper_sura_no = 1;
                int jumper_ayah_no = 0;


                if (!suraText.equals("") && !suraText.equals("0")) {
                    jumper_sura_no = Integer.parseInt(suraText);
                }


                if (!ayahText.equals("") && !ayahText.equals("0")) {
                    jumper_ayah_no = Integer.parseInt(ayahText);
                }

                Toast.makeText(context, verses_count + "", Toast.LENGTH_SHORT).show();

                if (jumper_ayah_no > verses_count) {
                    jumper_ayah_no = verses_count;
                }

                if (jumper_sura_no > 114) {
                    jumper_sura_no = 114;
                }

                if (sura == jumper_sura_no) {
                    if (recyclerView != null) {
                        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                        if (lm instanceof LinearLayoutManager) {
                            ((LinearLayoutManager) lm).scrollToPositionWithOffset(jumper_ayah_no, 200);
                        } else {
                            recyclerView.scrollToPosition(jumper_ayah_no);
                        }
                    }
                } else {
                    Intent intent = new Intent(context, QuranActivity.class);
                    intent.putExtra("sura", jumper_sura_no + "");
                    intent.putExtra("ayah", jumper_ayah_no + "");
                    context.startActivity(intent);
                }

                dialog.dismiss();
            }

        });

        dialog.show();


    }

    public static void jumper_popup(Context context) {


        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_warning_layout);

        EditText jumper_sura = dialog.findViewById(R.id.jumper_sura_no);
        EditText jumper_ayah = dialog.findViewById(R.id.jumper_ayah_no);
        Button button = dialog.findViewById(R.id.continue_button);

        jumper_sura.setText("");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suraText = jumper_sura.getText().toString();
                String ayahText = jumper_ayah.getText().toString();


                dbConn dbConn = new dbConn(context);
                int verses_count = dbConn.getVerses(Integer.parseInt(suraText)).size();
                int jumper_sura_no = 1;
                int jumper_ayah_no = 0;


                if (!suraText.equals("") && !suraText.equals("0")) {
                    jumper_sura_no = Integer.parseInt(suraText);
                }


                if (!ayahText.equals("") && !ayahText.equals("0")) {
                    jumper_ayah_no = Integer.parseInt(ayahText);
                }

                Toast.makeText(context, verses_count + "", Toast.LENGTH_SHORT).show();

                if (jumper_ayah_no > verses_count) {
                    jumper_ayah_no = verses_count;
                }

                if (jumper_sura_no > 114) {
                    jumper_sura_no = 114;
                }


                Intent intent = new Intent(context, QuranActivity.class);
                intent.putExtra("sura", jumper_sura_no + "");
                intent.putExtra("ayah", jumper_ayah_no + "");
                context.startActivity(intent);

                dialog.dismiss();
            }

        });

        dialog.show();
    }

    public static RecyclerView getRecyclerView(View view) {
        View parent = (View) view.getParent();
        while (parent != null && !(parent instanceof RecyclerView)) {
            parent = (View) parent.getParent();
        }
        return (RecyclerView) parent;
    }

    public static void hideNavigationBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION   // শুধু navigation bar hide করবে
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }


    public static void popup_warning(Context context, String content) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_warning_container);
        TextView textContent = dialog.findViewById(R.id.textcontent);
        textContent.setText(content);
        dialog.show();
    }

    public static String removeArabicSigns(String arabic) {
        if (arabic == null) return null;

        // Remove all Arabic diacritics, harakat, tatweel, sukun, waqf signs, and Quranic marks
        return arabic.replaceAll("[\\u0610-\\u061A" +  // small high signs
                "\\u064B-\\u065F" +  // harakat and sukun
                "\\u0670" +          // small alef
                "\\u06D6-\\u06ED" +  // additional Quranic signs
                "\\u08D3-\\u08FF" +  // extended Arabic marks (Unicode 8+)
                "\\uFD3E\\uFD3F" +   // ornate parentheses
                "\\u06E5\\u06E6" +   // small yeh and waw
                "\\u0640" +          // tatweel (ـ)
                "]", "");
    }

    public static String abrToFullword(String abr) {
        if (abr == null) return "";

        switch (abr) {
            case "P":
                return "Preposition";
            case "PN":
                return "Proper Noun";
            case "DET":
                return "Determiner";
            case "N":
                return "Noun";
            case "PRON":
                return "Pronoun";
            case "V":
                return "Verb";
            case "CONJ":
                return "Conjunction";
            case "REL":
                return "Relative Pronoun";
            case "INL":
                return "Interjection / Inline";
            case "DEM":
                return "Demonstrative";
            case "NEG":
                return "Negation";
            case "REM":
                return "Remark / Relative Marker";
            case "ACC":
                return "Accusative";
            case "EQ":
                return "Equative";
            case "CIRC":
                return "Circumstantial";
            case "RES":
                return "Resultative";
            case "PRO":
                return "Pronoun";
            case "ATT":
                return "Attribute";
            case "SUP":
                return "Superlative";
            case "INTG":
                return "Integrative";
            case "LOC":
                return "Locative";
            case "T":
                return "Tense";
            case "EMPH":
                return "Emphasis";
            case "VOC":
                return "Vocative";
            case "RSLT":
                return "Result";
            case "COND":
                return "Conditional";
            case "SUB":
                return "Subordinate";
            case "EXP":
                return "Expletive";
            case "CAUS":
                return "Causative";
            case "CERT":
                return "Certainty";
            case "PRP":
                return "Purpose / Preposition";
            case "ANS":
                return "Answer";
            case "RET":
                return "Retrospective";
            case "EXH":
                return "Exhaustive";
            case "INT":
                return "Interrogative";
            case "FUT":
                return "Future";
            case "COM":
                return "Comparative";
            case "INC":
                return "Inclusive";
            case "AMD":
                return "Amendment";
            case "SUR":
                return "Surplus / Surround";
            case "NV":
                return "Non-Verb";
            case "EXL":
                return "Exclamation";
            case "AVR":
                return "Average";
            case "IMPV":
                return "Imperative";
            default:
                return abr; // fallback if not found
        }
    }

    public static int abrToColor(String abr) {
        if (abr == null) return Color.BLACK; // default color

        switch (abr) {
            case "P":
                return Color.parseColor("#1E88E5");   // Blue
            case "PN":
                return Color.parseColor("#8E24AA");  // Purple
            case "DET":
                return Color.parseColor("#ff3399"); // Green
            case "N":
                return Color.parseColor("#6200EA");   // sweet blue
            case "PRON":
                return Color.parseColor("#6D4C41");// Brown
            case "V":
                return Color.parseColor("#E53935");   // Red
            case "CONJ":
                return Color.parseColor("#00897B");// Teal
            case "REL":
                return Color.parseColor("#3949AB");// Indigo
            case "INL":
                return Color.parseColor("#FB8C00");// Deep Orange
            case "DEM":
                return Color.parseColor("#5E35B1");// Deep Purple
            case "NEG":
                return Color.parseColor("#C62828");// Dark Red
            case "REM":
                return Color.parseColor("#7CB342");// Light Green
            case "ACC":
                return Color.parseColor("#039BE5");// Light Blue
            case "EQ":
                return Color.parseColor("#8D6E63");// Light Brown
            case "CIRC":
                return Color.parseColor("#FDD835");// Yellow
            case "RES":
                return Color.parseColor("#00ACC1");// Cyan
            case "PRO":
                return Color.parseColor("#558B2F");// Olive Green
            case "ATT":
                return Color.parseColor("#FF7043");// Coral
            case "SUP":
                return Color.parseColor("#9C27B0");// Purple
            case "INTG":
                return Color.parseColor("#AD1457");// Pink
            case "LOC":
                return Color.parseColor("#00796B");// Teal Dark
            case "T":
                return Color.parseColor("#6A1B9A");// Violet
            case "EMPH":
                return Color.parseColor("#D81B60");// Pink Dark
            case "VOC":
                return Color.parseColor("#FFB300");// Amber
            case "RSLT":
                return Color.parseColor("#3949AB");// Indigo
            case "COND":
                return Color.parseColor("#1E88E5");// Blue
            case "SUB":
                return Color.parseColor("#8BC34A");// Light Green
            case "EXP":
                return Color.parseColor("#F4511E");// Orange
            case "CAUS":
                return Color.parseColor("#E53935");// Red
            case "CERT":
                return Color.parseColor("#43A047");// Green
            case "PRP":
                return Color.parseColor("#00897B");// Teal
            case "ANS":
                return Color.parseColor("#6D4C41");// Brown
            case "RET":
                return Color.parseColor("#C2185B");// Magenta
            case "EXH":
                return Color.parseColor("#7B1FA2");// Deep Purple
            case "INT":
                return Color.parseColor("#0097A7");// Cyan Dark
            case "FUT":
                return Color.parseColor("#FF5722");// Orange
            case "COM":
                return Color.parseColor("#5D4037");// Dark Brown
            case "INC":
                return Color.parseColor("#4CAF50");// Green
            case "AMD":
                return Color.parseColor("#3F51B5");// Indigo
            case "SUR":
                return Color.parseColor("#9E9D24");// Mustard
            case "NV":
                return Color.parseColor("#795548");// Coffee
            case "EXL":
                return Color.parseColor("#D32F2F");// Strong Red
            case "AVR":
                return Color.parseColor("#616161");// Gray
            case "IMPV":
                return Color.parseColor("#F44336");// Bright Red
            default:
                return Color.BLACK; // fallback color
        }
    }


}
