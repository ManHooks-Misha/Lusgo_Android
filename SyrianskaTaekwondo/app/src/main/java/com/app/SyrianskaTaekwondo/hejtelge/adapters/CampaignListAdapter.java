package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.EditCampaign;
import com.app.SyrianskaTaekwondo.hejtelge.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CampaignListAdapter extends RecyclerView.Adapter<CampaignListAdapter.MyViewHolder> {


    private List<HashMap<String, String>> horizontalList;
    private Context context;


    public CampaignListAdapter(List<HashMap<String, String>> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_menu;
        AppCompatImageView imageView;
        TextView txtview, pos;
        CircleImageView img;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            imageView = view.findViewById(R.id.img_edit);
            img = view.findViewById(R.id.pos);
//            ll_menu = view.findViewById(R.id.ll_menu);
        }
    }


    @NotNull
    @Override
    public CampaignListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sponserlist_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        // holder.txtview.setText(horizontalList.get(position).get("name"));
//        int i=position+1;
//
//        holder.pos.setText(""+i);
      /*  holder.ll_menu.setOnClickListener(v -> {


            if (position == 1) {
                Intent i = new Intent(context, GroupActivity.class);
                context.startActivity(i);
            }
        });
*/

        if (position == 0) {
            holder.txtview.setText("Business Campaign");
            holder.img.setImageResource(R.drawable.micro);
        }
        if (position == 1) {
            holder.txtview.setText("IT Campaign");
            holder.img.setImageResource(R.drawable.adidas);

        }
        if (position == 2) {
            holder.txtview.setText("Digital Marketing");
            holder.img.setImageResource(R.drawable.pepsi);


        }
        if (position == 3) {
            holder.txtview.setText("Visiting Card Campaign");
            holder.img.setImageResource(R.drawable.state);
        }
        if (position == 4) {
            holder.txtview.setText("Other Campaign");
            holder.img.setImageResource(R.drawable.ms);

        }

        holder.imageView.setOnClickListener(v -> {

            Intent i = new Intent(context, EditCampaign.class);
            context.startActivity(i);

        });
    }


    @Override
    public int getItemCount() {
        return 5;
    }
}

