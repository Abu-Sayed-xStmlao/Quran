package com.sayed.quran;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
                int verses_count = dbConn.getVerses(Integer.parseInt(suraText), "en").size();
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

    public static RecyclerView getRecyclerView(View view) {
        View parent = (View) view.getParent();
        while (parent != null && !(parent instanceof RecyclerView)) {
            parent = (View) parent.getParent();
        }
        return (RecyclerView) parent;
    }


    public static void popup_warning(Context context, String content) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_warning_container);
        TextView textContent = dialog.findViewById(R.id.textcontent);
        textContent.setText(content);
        dialog.show();
    }

}
