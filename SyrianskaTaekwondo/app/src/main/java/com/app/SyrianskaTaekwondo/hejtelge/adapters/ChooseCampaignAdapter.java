package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.AddCampaign;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseCampaignAdapter extends RecyclerView.Adapter<ChooseCampaignAdapter.MyViewHolder> {


    private ArrayList<HashMap<String, String>> horizontalList;
    private Context context;


    public ChooseCampaignAdapter(ArrayList<HashMap<String, String>> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,del;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_news);
            del = view.findViewById(R.id.iv_selected);

        }
    }


    @NotNull
    @Override
    public ChooseCampaignAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_image_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        String imagepath = horizontalList.get(position).get("image");

        Glide.with(context)
                .load(imagepath)
                .centerCrop()
                .into(holder.imageView);
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCampaign.img.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}

