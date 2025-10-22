package com.sayed.quran;

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
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class VersesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    Context context;
    ArrayList<VerseModel> versesArray;

    public VersesAdapter(Context context, ArrayList<VerseModel> versesArray) {
        this.context = context;
        this.versesArray = versesArray;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;  // প্রথম position এ header
        } else {
            return TYPE_ITEM;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quran_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.verse_item, parent, false);
            return new ItemViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int p) {
        dbConn dbConn = new dbConn(context);
        ArrayList<suraInfoModel> suraInfo = new ArrayList<>();
        suraInfo = dbConn.getSuraInfo(versesArray.get(0).sura);

        String sura_title = suraInfo.get(0).sura_title;

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.title.setText(sura_title);
            headerViewHolder.meaning.setText(suraInfo.get(0).meaning);
            headerViewHolder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            initialize_font_face(headerViewHolder.title);

            headerViewHolder.verse_jumper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecyclerView recyclerView = QuranController.getRecyclerView(headerViewHolder.itemView);
                    QuranController.jumper_popup(context, Integer.parseInt(versesArray.get(0).sura), recyclerView);
                }
            });

        } else {
            int position = p - 1;
            ItemViewHolder itemViewHolder = ((ItemViewHolder) holder);
            // Set basic text views
            itemViewHolder.ayah.setText(versesArray.get(position).ayah);
            itemViewHolder.arabic.setText(versesArray.get(position).arabic);
            itemViewHolder.translation.setText(versesArray.get(position).translation);

            // Split Arabic and translation words
            String arabic_text = versesArray.get(position).arabic.trim();
            String[] arabic_words = arabic_text.split(" ");

            String translation_str = versesArray.get(position).words.trim();
            translation_str = translation_str.replaceFirst("__#", "");
            String[] translation_words = translation_str.split("__#");

            // Setup Flexbox layout for words

            itemViewHolder.words_container.removeAllViews();

            String sura_no = versesArray.get(position).sura;
            String ayah_no = versesArray.get(position).ayah;
            for (int i = 0; i < arabic_words.length; i++) {
                View word_content = LayoutInflater.from(context).inflate(R.layout.word_item, itemViewHolder.words_container, false);
                TextView arabic_word = word_content.findViewById(R.id.arabic_word);
                TextView translation_word = word_content.findViewById(R.id.transition_word);

                arabic_word.setText(arabic_words[i]);
                translation_word.setText(translation_words[i]);

                String translation_word_srt = translation_words[i];

                initialize_font_face(translation_word);
                itemViewHolder.words_container.addView(word_content);
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
                        initialize_font_face(word_title);


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
                        initialize_font_face(word_meaning);


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
            initialize_font_face(itemViewHolder.translation);


            itemViewHolder.translation.setOnClickListener(new View.OnClickListener() {
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
                    initialize_font_face(content);

                    dialog.show();
                }
            });
        }

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


    @Override
    public int getItemCount() {
        return versesArray.size() + 1; // header এর জন্য +1
    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title, meaning, verse_jumper;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            meaning = itemView.findViewById(R.id.meaning);
            verse_jumper = itemView.findViewById(R.id.verse_jumper);

        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView ayah, arabic, translation;
        FlexboxLayout words_container;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ayah = itemView.findViewById(R.id.ayah);
            arabic = itemView.findViewById(R.id.arabic);
            translation = itemView.findViewById(R.id.translation);
            words_container = itemView.findViewById(R.id.words_container);
        }
    }

}
