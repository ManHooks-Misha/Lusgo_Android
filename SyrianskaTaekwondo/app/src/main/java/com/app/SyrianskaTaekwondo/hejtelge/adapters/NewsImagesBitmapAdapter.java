package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.CreateNews;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class NewsImagesBitmapAdapter extends RecyclerView.Adapter<NewsImagesBitmapAdapter.MyViewHolder> {


    private ArrayList<File> horizontalList;
    private Context context;
    private String flag;


    public NewsImagesBitmapAdapter(ArrayList<File> horizontalList, Context context, String flag) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.flag = flag;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imageView, delete;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_news);
            delete = view.findViewById(R.id.iv_selected);

        }
    }


    @NotNull
    @Override
    public NewsImagesBitmapAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_image_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        File imagepath = horizontalList.get(position);
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(imagepath.getAbsolutePath(), bmOptions);


        Glide.with(context)
                .load(imagepath)
                .placeholder(R.drawable.spinningwheel)
                .into(holder.imageView);
        holder.delete.setOnClickListener(view -> {
            if (flag.equals("news")) {
                CreateNews.images.remove(position);
                notifyDataSetChanged();
            } else {
                CreateMessage.images_path.remove(position);
                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}

