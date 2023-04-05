package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.ImageShowDetails;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.bumptech.glide.Glide;
import com.mipl.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SliderImageNews extends
        SliderViewAdapter<SliderImageNews.SliderAdapterVH> {

    private Activity context;
    private int mCount;
    private ArrayList<HashMap<String, String>> image;
    private long mLastClickTime = 0;

    public SliderImageNews(Activity context, ArrayList<HashMap<String, String>> image) {
        this.context = context;
        this.image = image;
        setCount(image.size());
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    @Override
    public SliderImageNews.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslide_news, null);
        return new SliderImageNews.SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderImageNews.SliderAdapterVH viewHolder, final int position) {


        String imagepath = image.get(position).get("location");
        int pos = position + 1;
        if (image.size() > 1) {
            viewHolder.imageGifContainer.setVisibility(View.VISIBLE);
            viewHolder.imageGifContainer.setText(pos + "/" + image.size());
        }
        if (imagepath != null) {
            Glide.with(viewHolder.itemView)
                    .load(imagepath)
                    .thumbnail(Glide.with(context).load(R.drawable.spin))


                    .into(viewHolder.imageViewBackground);
        } else {
            Glide.with(viewHolder.itemView)
                    .load(image.get(position).get("name"))
                    .thumbnail(Glide.with(context).load(R.drawable.spin))


                    .into(viewHolder.imageViewBackground);
        }
        viewHolder.itemView.setOnClickListener(view -> {
            ArrayList<String> arrimg = new ArrayList<>();
    /*        for(HashMap<String, String> arr:image){
                arrimg.add(arr.get("location"));

            }
*/
            //  String arrim=image.get(position).get("location");
            //   arrimg.add(Objects.requireNonNull(arrim));

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
    public int getCount() {
        //slider view count could be dynamic size
        return mCount;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        AppCompatTextView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container_text);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }


}