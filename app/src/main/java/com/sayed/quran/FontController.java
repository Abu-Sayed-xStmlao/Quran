package com.sayed.quran;

import android.content.Context;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class FontController {

    public static void initialize_font_face(Context context, TextView textView) {
        // Set translation font based on language
        String lang = LanguagePref.getLanguage(context);

        if ("en".equals(lang)) {
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.poppins));
        } else if ("bn".equals(lang)) {
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.anek_bangla));
        } else {
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.poppins)); // default
        }
    }
}
