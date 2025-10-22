package com.sayed.quran;

import static com.sayed.quran.FontController.initialize_font_face;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class wordVerseSearchAdapter extends RecyclerView.Adapter<wordVerseSearchAdapter.ViewHolder> {
    Context context;
    ArrayList<VerseModel> versesArray;
    String finder;

    public wordVerseSearchAdapter(Context context, ArrayList<VerseModel> versesArray, String finder) {
        this.context = context;
        this.versesArray = versesArray;
        this.finder = finder;
    }

    @NonNull
    @Override
    public wordVerseSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.word_verse_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull wordVerseSearchAdapter.ViewHolder holder, int position) {
        dbConn dbConn = new dbConn(context);
        ArrayList<suraInfoModel> suraInfo = new ArrayList<>();
        suraInfo = dbConn.getSuraInfo(versesArray.get(position).sura);

        String sura_title = suraInfo.get(0).sura_title;


        // Set basic text views
        holder.ayah.setText(sura_title + "->" + versesArray.get(position).sura + ":" + versesArray.get(position).ayah);
        holder.translation.setText(versesArray.get(position).translation);

        // Split Arabic and translation words
        String arabic_text = versesArray.get(position).arabic.trim();
        String[] arabic_words = arabic_text.split(" ");

        String translation_str = versesArray.get(position).words.trim();
        translation_str = translation_str.replaceFirst("__#", "");
        String[] translation_words = translation_str.split("__#");

        // Setup Flexbox layout for words

        holder.words_container.removeAllViews();

        String sura_no = versesArray.get(position).sura;
        String ayah_no = versesArray.get(position).ayah;
        for (int i = 0; i < arabic_words.length; i++) {
            View word_content = LayoutInflater.from(context).inflate(R.layout.word_item, holder.words_container, false);
            TextView arabic_word = word_content.findViewById(R.id.arabic_word);
            TextView translation_word = word_content.findViewById(R.id.transition_word);

            arabic_word.setText(arabic_words[i]);
            translation_word.setText(translation_words[i]);

            String formatted_arabic = QuranController.removeArabicSigns(arabic_words[i]);
            String formatted_finder = QuranController.removeArabicSigns(finder);


            if (formatted_arabic.contains(formatted_finder)) {
                // Background tint সেট করা
                word_content.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000"))); // কালো

                // Text color সেট করা
                arabic_word.setTextColor(Color.parseColor("#FFFFFF")); // সাদা
            }


            String translation_word_srt = translation_words[i];

            initialize_font_face(context, translation_word);
            holder.words_container.addView(word_content);
            String word_no = (i + 1) + "";

            word_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View asrock) {


                    ArrayList<wordInfoModel> wordInfoArray = new ArrayList<>();
                    dbConn dbConn = new dbConn(context);

                    wordInfoArray = dbConn.getWordInfo(sura_no, ayah_no, word_no);

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
                    bottomSheetDialog.setContentView(LayoutInflater.from(context).inflate(R.layout.bottom_sheet_word_info, null));

                    FlexboxLayout pos_container = bottomSheetDialog.findViewById(R.id.pos_container);

                    pos_container.removeAllViews();

                    TextView word_title = bottomSheetDialog.findViewById(R.id.word_title);
                    TextView word_number = bottomSheetDialog.findViewById(R.id.word_number);
                    TextView word_meaning = bottomSheetDialog.findViewById(R.id.word_meaning);
                    TextView word_lemma = bottomSheetDialog.findViewById(R.id.word_lemma);
                    TextView word_root = bottomSheetDialog.findViewById(R.id.word_root);
                    TextView arabic_exp = bottomSheetDialog.findViewById(R.id.arabic_exp);

                    word_meaning.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "word_meaning", Toast.LENGTH_SHORT).show();
                        }
                    });

                    word_number.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "word_number", Toast.LENGTH_SHORT).show();
                        }
                    });

                    word_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "word_title", Toast.LENGTH_SHORT).show();
                        }
                    });

                    word_lemma.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "word_lemma", Toast.LENGTH_SHORT).show();
                        }
                    });

                    word_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "word_root", Toast.LENGTH_SHORT).show();
                        }
                    });


                    word_title.setText(sura_title + " -> ");
                    word_number.setText(sura_no + ":" + ayah_no + ":" + word_no);
                    initialize_font_face(context, word_title);


                    String exp = "___";
                    int exp_count = Integer.parseInt(wordInfoArray.get(0).count);

                    ArrayList<String> arabic_words_array = new ArrayList<>();
                    ArrayList<String> arabic_pos_array = new ArrayList<>();

                    arabic_words_array.add(wordInfoArray.get(0).arabic1);
                    arabic_words_array.add(wordInfoArray.get(0).arabic2);
                    arabic_words_array.add(wordInfoArray.get(0).arabic3);
                    arabic_words_array.add(wordInfoArray.get(0).arabic4);
                    arabic_words_array.add(wordInfoArray.get(0).arabic5);

                    arabic_pos_array.add(wordInfoArray.get(0).pos1);
                    arabic_pos_array.add(wordInfoArray.get(0).pos2);
                    arabic_pos_array.add(wordInfoArray.get(0).pos3);
                    arabic_pos_array.add(wordInfoArray.get(0).pos4);
                    arabic_pos_array.add(wordInfoArray.get(0).pos5);


                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

                    for (int i = 0; i < exp_count; i++) {


                        View pos_item = LayoutInflater.from(context).inflate(R.layout.pos_item, pos_container, false);
                        View pos_indicator = pos_item.findViewById(R.id.pos_indicator);
                        TextView pos_title = pos_item.findViewById(R.id.pos_title);


                        int start = spannableStringBuilder.length(); // শুরুর index
                        spannableStringBuilder.append(arabic_words_array.get(i));

                        int end = spannableStringBuilder.length(); // শেষ index


                        String pos_abbr = arabic_pos_array.get(i);
                        int abbr_color = QuranController.abrToColor(pos_abbr);

                        pos_abbr = QuranController.abrToFullword(pos_abbr);


                        pos_indicator.setBackgroundTintList(ColorStateList.valueOf(abbr_color));

                        pos_title.setText(pos_abbr);
                        pos_container.addView(pos_item);

                        int finali = i;

                        // "here" এ ক্লিক ইভেন্ট সেট করা
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {

                                Toast.makeText(widget.getContext(), arabic_words_array.get(finali), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, wordSearchActivity.class);
                                intent.putExtra("finder", arabic_words_array.get(finali));
                                context.startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false); // underline না চাইলে false দিন
                                ds.setColor(Color.RED);     // টেক্সটের রঙ পরিবর্তন
                            }
                        };


                        spannableStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        spannableStringBuilder.setSpan(new ForegroundColorSpan(abbr_color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                    }


                    // apply spannable
                    arabic_exp.setText(spannableStringBuilder);
                    arabic_exp.setMovementMethod(LinkMovementMethod.getInstance()); // <-- জরুরি
                    arabic_exp.setHighlightColor(Color.TRANSPARENT); // highlight remove


                    word_meaning.setText(translation_word_srt);
                    initialize_font_face(context, word_meaning);


                    word_lemma.setText(wordInfoArray.get(0).lemma);

                    if (wordInfoArray.get(0).root_ar.trim().isEmpty()) {
                        word_root.setText("...");
                    } else {
                        word_root.setText(wordInfoArray.get(0).root_ar);
                    }

                    if (wordInfoArray.get(0).lemma.trim().isEmpty()) {
                        word_lemma.setText("...");
                    } else {
                        word_lemma.setText(wordInfoArray.get(0).lemma);
                    }


                    Button exit_btn = bottomSheetDialog.findViewById(R.id.exit_button);
                    exit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });
                    bottomSheetDialog.show();

                }
            });


        }


        // Set animation and top margin
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));


        //initialize font faces here.
        initialize_font_face(context, holder.translation);


        holder.translation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbConn tafsirDbConn = new dbConn(context);
                String tafsir = tafsirDbConn.getTafsir(Integer.parseInt(versesArray.get(position).sura), Integer.parseInt(versesArray.get(position).ayah));

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_layout);
                TextView content = dialog.findViewById(R.id.content);
                tafsir = tafsir.replace("\n", "<br>");


                // Android Nougat (API 24) এবং তার উপরে:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    content.setText(Html.fromHtml(tafsir, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    // পুরনো ভার্সনের জন্য
                    content.setText(Html.fromHtml(tafsir));
                }
                initialize_font_face(context, content);

                dialog.show();
            }
        });


        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));

    }

    @Override
    public int getItemCount() {
        return versesArray.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ayah, translation;
        FlexboxLayout words_container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            ayah = itemView.findViewById(R.id.ayah);
            translation = itemView.findViewById(R.id.translation);
            words_container = itemView.findViewById(R.id.words_container);

        }
    }
}
