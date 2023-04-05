package com.app.SyrianskaTaekwondo.hejtelge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.mipl.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SliderViewDemo extends SliderViewAdapter<SliderViewDemo.SliderAdapterVH> {

    private Context context;
    private int mCount;
    private ArrayList<HashMap<String, String>> image;

    public SliderViewDemo(Context context, ArrayList<HashMap<String, String>> image) {
        this.context = context;
        this.image = image;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagesslide, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        viewHolder.itemView.setOnClickListener(v -> {
            if(image.get(position).get("link").contains("http")){
                Uri uri = Uri.parse(image.get(position).get("link")); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }else {
                Uri uri = Uri.parse("http://"+image.get(position).get("link")); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
          //  context.startActivity(new Intent(context, show_Messege.class).putExtra("Url", image.get(position).get("link")));
        });
        try {
            String imagepath = image.get(position).get("image");
           /* Glide.with(viewHolder.itemView)
                    .load(imagepath)
                    .into(viewHolder.imageViewBackground);*/
            Glide.with(context).load(imagepath).into(viewHolder.imageViewBackground);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return image.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        AppCompatTextView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }


}