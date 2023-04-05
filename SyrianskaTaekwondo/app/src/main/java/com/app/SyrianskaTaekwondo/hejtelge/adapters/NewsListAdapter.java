package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyViewHolder> {


    private Activity context;
    private List<HashMap<String, String>> horizontalList;

    public NewsListAdapter(List<HashMap<String, String>> horizontalList, Activity context) {
        this.context = context;
        this.horizontalList = horizontalList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView name, email, mobile, role;
        private LinearLayout llname, llmobile, llemail;
        private AvatarView user_profile;
        private AppCompatImageView delete, edit;



        MyViewHolder(View view) {
            super(view);
            //   txtview = view.findViewById(R.id.group_name);
            // imageView = view.findViewById(R.id.img_edit);

            //   pos = view.findViewById(R.id.pos);
//
            name = view.findViewById(R.id.name);
            mobile = view.findViewById(R.id.mobile);
            email = view.findViewById(R.id.email);
            llemail = view.findViewById(R.id.llemail);
            llmobile = view.findViewById(R.id.llmobile);
            llname = view.findViewById(R.id.llname);
            user_profile = view.findViewById(R.id.user_profile);
            role = view.findViewById(R.id.role);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
        }
    }


    @NotNull
    @Override
    public NewsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.newslist_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //holder.txtview.setText("Sweta Gupta");
        // holder.txtview.setText(horizontalList.get(position).get("name"));
//        int i=position+1;
//
//        holder.pos.setText(""+i);

        if (horizontalList.get(position).get("name").length() > 0) {
            String name_txt = String.valueOf(horizontalList.get(position).get("name").toString().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(name_txt.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(context)
                    .load(horizontalList.get(position).get("image"))
                    .centerCrop()
                    .placeholder(drawable)
                    .into(holder.user_profile);
        }else{

                String namea = String.valueOf(horizontalList.get(position).get("role").trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(context)
                    .load(horizontalList.get(position).get("image"))
                    .centerCrop()
                    .placeholder(drawable)
                    .into(holder.user_profile);

        }

        if (!horizontalList.get(position).get("mobile_no").equals("null") && !horizontalList.get(position).get("mobile_no").equals("")) {
            holder.mobile.setText(horizontalList.get(position).get("mobile_no"));
            holder.llmobile.setVisibility(View.VISIBLE);

        }else {
            holder.llmobile.setVisibility(View.GONE);
        }if (!horizontalList.get(position).get("name").equals("null") && !horizontalList.get(position).get("name").equals("")) {
            holder.name.setText(horizontalList.get(position).get("name"));
            holder.name.setVisibility(View.VISIBLE);

        } else {
            holder.role.setTextSize(18);
            holder.role.setTypeface(holder.role.getTypeface(), Typeface.BOLD);
            holder.name.setVisibility(View.GONE);
        }
        if (!horizontalList.get(position).get("email").equals("null") && !horizontalList.get(position).get("email").equals("")) {
            holder.email.setText(horizontalList.get(position).get("email"));
            holder.llemail.setVisibility(View.VISIBLE);

        } else {
            holder.llemail.setVisibility(View.GONE);
        }
        if (!horizontalList.get(position).get("role").equals("null") && !horizontalList.get(position).get("role").equals("")) {
            holder.role.setText(horizontalList.get(position).get("role"));
            holder.role.setVisibility(View.VISIBLE);

        } else {
            holder.role.setVisibility(View.GONE);
        }
        holder.email.setText(horizontalList.get(position).get("email"));
//        holder.name.setText(horizontalList.get(position).get("name"));
        holder.email.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{horizontalList.get(position).get("email")});
            intent.putExtra(Intent.EXTRA_SUBJECT, "");


            context.startActivity(Intent.createChooser(intent, "Email via..."));
        });

        holder.mobile.setOnClickListener(arg0 -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + horizontalList.get(position).get("mobile_no")));//change the number
                context.startActivity(callIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 101);
                }
            }
        });

        holder.edit.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}

