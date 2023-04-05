package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Item;
import com.bumptech.glide.Glide;


import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;


/**
 * Created by cmj on 2016/3/29.
 */
public class RecyclerPopupWindowAdapter extends RecyclerView.Adapter<RecyclerPopupWindowAdapter.MyViewHolder> {


    /*RecyclerPopupWindowAdapter*/
    private List<Item> items;
    private int prePosition;
    private static final int TYPE_INACTIVE = 0;
    private static final int TYPE_ACTIVE = 1;
    Context context;
    public RecyclerPopupWindowAdapter(List<Item> items, Context context) {
        super();
        prePosition = 0;
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Item item = items.get(position);
        return item.isActive() ? TYPE_ACTIVE : TYPE_INACTIVE;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = viewType == TYPE_INACTIVE ? R.layout.item_active : R.layout.item_active;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MyViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = items.get(position);
        if(item.getTitle().length()>0) {
            holder.getTimeTV().setText(item.getTitle().trim());
            if(item.getRole()!=null) {
                holder.role.setVisibility(View.VISIBLE);

                holder.role.setText(item.getRole().trim());
            }else{

                holder.role.setVisibility(View.GONE);
            }
            if (item.getTitle().length() > 0) {
                String name =String.valueOf(item.getTitle().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(item.getProfile_image())
                        .placeholder(drawable)
                        .into(holder.profile);
            }

        }else{
            holder.getTimeTV().setText(item.getUsername().trim());
            if(item.getRole()!=null) {
                holder.role.setVisibility(View.VISIBLE);

                holder.role.setText(item.getRole().trim());
            }else{
                holder.role.setVisibility(View.GONE);

            }
            if (item.getUsername().length() > 0) {
                String name =String.valueOf(item.getUsername().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(item.getProfile_image())
                        .placeholder(drawable)
                        .into(holder.profile);
            }


        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 设置Item点击监听
     *
     * @param listener
     *
     */

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /*RecyclerPopupWindowAdapter*/


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView timeTV,role;
        AvatarView profile;
        private OnItemClickListener mListener;

        public MyViewHolder(View rootView, OnItemClickListener listener) {
            super(rootView);
            this.mListener = listener;
            timeTV = rootView.findViewById(R.id.tv_item_time);
            role = rootView.findViewById(R.id.role);
            profile = rootView.findViewById(R.id.profile_img);
            rootView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition());
            }
        }

        public TextView getTimeTV() {
            return timeTV;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
