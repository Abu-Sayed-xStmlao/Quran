package com.sayed.quran;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ThemesAdapter extends RecyclerView.Adapter<ThemesAdapter.ViewHolder> {

    Context context;
    ArrayList<ThemeModel> themes;

    public ThemesAdapter(Context context, ArrayList<ThemeModel> themes) {
        this.context = context;
        this.themes = themes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.theme_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(themes.get(position).myBackground)
                .into(holder.theme_image);
        holder.theme_name.setText(themes.get(position).name);

        int bgColor = ContextCompat.getColor(context, themes.get(position).myColor);
        int textColor = ContextCompat.getColor(context, themes.get(position).myTextColor);


        // If you want to set background color instead of drawable, use:
        // holder.theme_name.setBackgroundColor(bgColor);

        // Set text color
        holder.theme_name.setBackgroundColor(bgColor);
        holder.theme_name.setTextColor(textColor);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeHelper.saveTheme(context, themes.get(position).myTheme);

                // Safely recreate the activity
                if (context instanceof Activity) {
                    ((Activity) context).recreate();
                }


                context.startActivity(new Intent(context, MainActivity.class));


            }
        });


        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));

    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView theme_image;
        public TextView theme_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            theme_image = itemView.findViewById(R.id.theme_image);
            theme_name = itemView.findViewById(R.id.theme_name);
        }
    }
}
