package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {


    private List<UserList> horizontalList;
    private Context context;


    public UserListAdapter(ArrayList<UserList> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        AvatarView img;
        TextView txtview,group_line;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            img = view.findViewById(R.id.pos);
            group_line = view.findViewById(R.id.group_line);

        }
    }


    @NotNull
    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        holder.group_line.setVisibility(View.VISIBLE);
        String name = horizontalList.get(position).getFirstname();
        String username = horizontalList.get(position).getUsername();
        String lastname = horizontalList.get(position).getLastname();
        String imagepath = horizontalList.get(position).getImagepath();
        if(name.length()>0) {
            holder.txtview.setText(String.format("%s %s", name, lastname));
        }else{
            holder.txtview.setText(horizontalList.get(position).getUsername());

        }
        holder.group_line.setText(horizontalList.get(position).getRole_name());

        holder.itemView.setOnClickListener(view -> {
            String id = horizontalList.get(position).getId();
            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen","team"));

        });
        if(name.length()>0) {
            String namea = String.valueOf(horizontalList.get(position).getFirstname().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));

            Glide.with(context)
                    .load(imagepath)
                    .placeholder(drawable)
                    .centerCrop()
                    .into(holder.img);
        }
        if (username.length() > 0) {
            String namea = String.valueOf(horizontalList.get(position).getUsername().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));

            Glide.with(context)
                    .load(imagepath)
                    .placeholder(drawable)
                    .centerCrop()
                    .into(holder.img);
        }
        /* if (position == 0) {
            holder.txtview.setText("Sweta Gupta");
            holder.role.setText("Admin");
            holder.email.setText("sweta@gmail.com");
            holder.img.setImageResource(R.drawable.group_color);
        }
        if (position == 1) {
            holder.txtview.setText("Hemant Vijay");
            holder.email.setText("hemant@gmail.com");


            holder.role.setText("Coach");


            holder.img.setImageResource(R.drawable.group_list);

        }
        if (position == 2) {
            holder.txtview.setText("Rajat Gupta");
            holder.email.setText("rajat@gmail.com");

            holder.role.setText("User");

            holder.img.setImageResource(R.drawable.user_diff);


        }
        if (position == 3) {
            holder.txtview.setText("Vikrant");
            holder.email.setText("vicky@gmail.com");

            holder.role.setText("Coach");

            holder.img.setImageResource(R.drawable.group_list);
        }
        if (position == 4) {
            holder.txtview.setText("Shefali");
            holder.role.setText("User");
            holder.email.setText("shefali@gmail.com");


            holder.img.setImageResource(R.drawable.user_diff);

        }*/

    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}

