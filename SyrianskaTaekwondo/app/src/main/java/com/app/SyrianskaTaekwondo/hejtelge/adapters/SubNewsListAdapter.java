package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.ImageShowDetails;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SubNewsListAdapter extends RecyclerView.Adapter<SubNewsListAdapter.MyViewHolder> {


    private Activity context;
    ImageLoader imageLoader;
    private ArrayList<HashMap<String, String>> image;
    private ArrayList<HashMap<String, String>> image1;
    CommonMethods cmn = new CommonMethods();
    private long mLastClickTime = 0;


    public SubNewsListAdapter(ArrayList<HashMap<String, String>> horizontalList, Activity context,ArrayList<HashMap<String, String>> horizontalList1) {
        this.image = horizontalList;
        this.image1 = horizontalList1;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        AppCompatTextView imageGifContainer;

        MyViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
            imageGifContainer = view.findViewById(R.id.iv_gif_container_text);

        }
    }


    @NotNull
    @Override
    public SubNewsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_for_sub_news_list_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Glide.with(context)
                .load(image.get(position).get("location"))
                .centerCrop()
                .thumbnail(Glide.with(context).load(R.drawable.loader__))
                .into(holder.image);

        int pos = position + 1;
        if (image.size() > 1) {
            holder.imageGifContainer.setVisibility(View.VISIBLE);
            holder.imageGifContainer.setText(pos + "/" + image.size());
        }
        holder.itemView.setOnClickListener(view -> {
            ArrayList<String> arrimg = new ArrayList<>();

            for (HashMap map : image) {
                arrimg.add(Objects.requireNonNull(map.get("location")).toString());
            }
//            Animation StoryAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
//            viewHolder.itemView.startAnimation(StoryAnimation);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            context.startActivity(new Intent(context, ImageShowDetails.class).putExtra("image", arrimg).putExtra("position", "" + position));
            //  context.startActivity(new Intent(context, ImageShowDetails.class).putExtra("image", Common.INSTANCE.getBytes(arrimg)));
            // context.overridePendingTransition(R.anim.fade_in, 0);
        });




    }


    @Override
    public int getItemCount() {
        return image.size();
    }

}

