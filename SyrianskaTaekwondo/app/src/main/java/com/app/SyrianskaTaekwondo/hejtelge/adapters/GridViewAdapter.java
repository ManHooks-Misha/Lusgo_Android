package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.app.SyrianskaTaekwondo.hejtelge.AddCampaign;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private ArrayList<String> allElementDetails;

    private LayoutInflater mInflater;
    private List<HashMap<String, String>> arr;
    Context context;

    public GridViewAdapter(Context context, List<HashMap<String, String>> results) {
        arr = results;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return arr.size();
    }

    public Object getItem(int position) {
        return allElementDetails.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.grid, null);
        ImageView imageview = convertView.findViewById(R.id.iv_profile);
        ImageView cancel = convertView.findViewById(R.id.iv_selected);

        String image = arr.get(position).get("image");
        Glide.with(context)
                .load(image)
                .centerCrop()
                .into(imageview);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCampaign.img.remove(position);
                notifyDataSetChanged();
            }
        });

        // Bitmap theImage= BitmapFactory.decodeStream(imageStream,null,op);
        //imageview.setImageBitmap(theImage);
        return convertView;
    }
}
