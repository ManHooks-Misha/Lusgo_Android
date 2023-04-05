package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.bumptech.glide.Glide;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class Profile_Add_team_Member extends RecyclerView.Adapter<Profile_Add_team_Member.MyViewHolder> {


    private List<Teamlist> horizontalList;
    ArrayList<Integer> list_pos = new ArrayList<>();
    private Context context;
    private String userid;

    public Profile_Add_team_Member(List<Teamlist> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        CommonMethods cmn = new CommonMethods();
        this.context = context;
        userid = cmn.getPrefsData(context, "id", "");

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtview;
        AvatarView img;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            img = view.findViewById(R.id.pos);
        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_team_add_profile, parent, false);

        return new MyViewHolder(itemView);
    }

    public List<Teamlist> getList() {
        return horizontalList;
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {

        String name = horizontalList.get(position).getName();
        String image = horizontalList.get(position).getLogo();

     /*   String created_at = horizontalList.get(position).get("created_at");
        String[] time_arr = Objects.requireNonNull(created_at).split(" ");
        created_at = time_arr[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long now = System.currentTimeMillis() - 1000;

        long result = TimeUnit.DAYS.convert((now - date.getTime()), TimeUnit.MILLISECONDS);

        updateLabel(holder.time_txt, result);*/
        holder.txtview.setText(name);

        if (horizontalList.get(position).getName().length() > 0) {
            String namea =String.valueOf(horizontalList.get(position).getName().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(context)
                    .load(image)
                    .fitCenter()
                    .placeholder(R.drawable.team)
                    .into(holder.img);
        }


    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

}

