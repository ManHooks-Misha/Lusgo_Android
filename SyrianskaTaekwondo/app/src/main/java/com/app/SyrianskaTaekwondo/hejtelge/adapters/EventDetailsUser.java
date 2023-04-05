package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Details;
import com.bumptech.glide.Glide;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class EventDetailsUser extends RecyclerView.Adapter<EventDetailsUser.MyViewHolder> {


    private List<Details> horizontalList;
    private Activity context;


    public EventDetailsUser(List<Details> horizontalList, Activity context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        AvatarView imageView;
        TextView txtview, pos;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.tv_name);

            imageView = view.findViewById(R.id.iv_profile);

        }
    }


    @NotNull
    @Override
    public EventDetailsUser.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txtview.setText(horizontalList.get(position).getName());

        if (horizontalList.get(position).getName().length() > 0) {
            try {
                String name = String.valueOf((horizontalList.get(position).getName().trim().charAt(0)));

                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(horizontalList.get(position).getProfile_image())
                        .fitCenter()
                        .placeholder(drawable)
                        .into(holder.imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = horizontalList.get(position).getUser_id();
                    context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user"));
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });
        }


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
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}

