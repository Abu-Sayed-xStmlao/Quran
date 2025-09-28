package com.sayed.quran;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VerseSearchAdapter extends RecyclerView.Adapter<VerseSearchAdapter.ViewHolder> {
    Context context;
    ArrayList<VerseModel> versesArray;
    String finder, search_type;

    public VerseSearchAdapter(Context context, ArrayList<VerseModel> versesArray, String finder, String search_type) {
        this.context = context;
        this.versesArray = versesArray;
        this.finder = finder;
        this.search_type = search_type;
    }

    @NonNull
    @Override
    public VerseSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.verse_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VerseSearchAdapter.ViewHolder holder, int position) {
        holder.info.setText("SN" + versesArray.get(position).sura + "VN" + versesArray.get(position).ayah);

        if (search_type.equals("arabic")) {
            highlightText(holder.arabic, versesArray.get(position).arabic, finder);
            holder.translation.setVisibility(View.GONE);
            holder.arabic.setVisibility(View.VISIBLE);
        } else {
            holder.arabic.setVisibility(View.GONE);
            holder.translation.setVisibility(View.VISIBLE);
            highlightText(holder.translation, versesArray.get(position).translation, finder);
            initialize_font_face(holder.translation);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuranActivity.class);
                intent.putExtra("sura", versesArray.get(position).sura);
                intent.putExtra("ayah", versesArray.get(position).ayah);
                context.startActivity(intent);
            }
        });

        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));

    }

    @Override
    public int getItemCount() {
        return versesArray.size();
    }


    private void highlightText(TextView textView, String fullText, String targetWord) {
        if (targetWord == null || targetWord.isEmpty()) {
            textView.setText(fullText); // শুধু সাধারণ টেক্সট দেখাবে
            return;
        }

        SpannableString spannable = new SpannableString(fullText);

        int startIndex = fullText.indexOf(targetWord);
        while (startIndex >= 0) {
            int endIndex = startIndex + targetWord.length();

            int textColor = getThemeColor(context, R.attr.myTextColor);
            int bgColor = getThemeColor(context, R.attr.myColor);

            spannable.setSpan(
                    new ForegroundColorSpan(textColor),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            spannable.setSpan(
                    new BackgroundColorSpan(bgColor),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );


            // Next occurrence
            startIndex = fullText.indexOf(targetWord, endIndex);
        }

        textView.setText(spannable);

    }

    private int getThemeColor(Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrResId, typedValue, true);
        return typedValue.data;
    }

    public void initialize_font_face(TextView textView) {
        // Set translation font based on language
        String lang = LanguagePref.getLanguage(context);
        if (lang.equals("en")) {
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.poppins));
        } else if (lang.equals("bn")) {
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.anek_bangla));
        } else {
            textView.setTypeface(ResourcesCompat.getFont(context, R.font.poppins)); // default
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView info, arabic, translation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            info = itemView.findViewById(R.id.info);
            arabic = itemView.findViewById(R.id.arabic);
            translation = itemView.findViewById(R.id.translation);


        }
    }
}
