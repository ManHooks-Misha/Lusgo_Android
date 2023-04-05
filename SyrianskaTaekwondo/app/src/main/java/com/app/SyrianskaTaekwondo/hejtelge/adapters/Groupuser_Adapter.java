package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class Groupuser_Adapter extends RecyclerView.Adapter<Groupuser_Adapter.MyViewHolder> {


    private ArrayList<UserList> horizontalList;
    private Context context;


    public Groupuser_Adapter(ArrayList<UserList> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_menu;
        AvatarView imageView;
        TextView txtview, pos;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.tv_name);
            imageView = view.findViewById(R.id.iv_profile);
//            ll_menu = view.findViewById(R.id.ll_menu);
        }
    }


    @NotNull
    @Override
    public Groupuser_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.participent_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String name;
        if (horizontalList.get(position).getFirstname().length() > 0) {
            holder.txtview.setText(horizontalList.get(position).getFirstname() + " " + horizontalList.get(position).getLastname());
             name =String.valueOf(horizontalList.get(position).getFirstname().trim().charAt(0));

        } else {
             name =String.valueOf(horizontalList.get(position).getUsername().trim().charAt(0));

            holder.txtview.setText(horizontalList.get(position).getUsername());

        }

        TextDrawable drawable = TextDrawable.builder()
                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
        Glide.with(context)
                .load(horizontalList.get(position).getProfile_image())
                .fitCenter()
                .placeholder(drawable)
                .into(holder.imageView);

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

