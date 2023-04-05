package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.GroupActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


    private ArrayList<UserList> horizontalList;
    private Context context;


    public UserAdapter(ArrayList<UserList> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_menu;
        CircleImageView imageView;
        AppCompatImageView cancel;
        TextView txtview,pos;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.tv_name);
            imageView = view.findViewById(R.id.iv_profile);
            cancel = view.findViewById(R.id.iv_selected);
            //   pos = view.findViewById(R.id.pos);
//            ll_menu = view.findViewById(R.id.ll_menu);
        }
    }


    @NotNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(horizontalList.get(position).getFirstname().length()>0) {
            holder.txtview.setText(horizontalList.get(position).getFirstname() + " " + horizontalList.get(position).getLastname());
        }else {
            holder.txtview.setText(horizontalList.get(position).getUsername());

        }
        Glide.with(context)
                .load(horizontalList.get(position).getProfile_image())
                .fitCenter()
                .placeholder(R.drawable.user_diff)
                .into(holder.imageView);
        holder.cancel.setOnClickListener(view -> {
            GroupActivity.arr_user_participate.remove(position);

            notifyDataSetChanged();
        });
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

