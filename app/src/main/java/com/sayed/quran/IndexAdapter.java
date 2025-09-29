package com.sayed.quran;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.ViewHolder> {

    Context context;
    ArrayList<IndexModel> indexArray;

    public IndexAdapter(Context context, ArrayList<IndexModel> indexArray) {
        this.context = context;
        this.indexArray = indexArray;
    }

    @NonNull
    @Override
    public IndexAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.index_item, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull IndexAdapter.ViewHolder holder, int position) {
        holder.sura_no.setText(indexArray.get(position).sura);
        holder.title.setText(indexArray.get(position).title);
        holder.meaning.setText(indexArray.get(position).meaning);
        holder.ayah_count.setText(indexArray.get(position).ayah_count);

        FontController.initialize_font_face(context, holder.title);
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        if (position == 0) {
            // Convert 50dp to pixels
            int topMarginInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
            layoutParams.topMargin = topMarginInPx;
        } else {
            layoutParams.topMargin = 0; // reset for other positions to avoid wrong reuse
        }

        holder.itemView.setLayoutParams(layoutParams);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuranActivity.class);
                intent.putExtra("sura", indexArray.get(position).sura);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return indexArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, meaning, ayah_count, sura_no;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            meaning = itemView.findViewById(R.id.meaning);
            ayah_count = itemView.findViewById(R.id.ayah_count);
            sura_no = itemView.findViewById(R.id.sura_no);


        }
    }
}
