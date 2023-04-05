package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import com.app.SyrianskaTaekwondo.hejtelge.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class Show_coachInProfileAdapter extends RecyclerView.Adapter<Show_coachInProfileAdapter.MyViewHolder> {


    private List<HashMap<String, String>> horizontalList;
    private Context context;


    public Show_coachInProfileAdapter(List<HashMap<String, String>> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView coach, team;

        MyViewHolder(View view) {
            super(view);
            coach = view.findViewById(R.id.txt_coach);
            team = view.findViewById(R.id.txt_team);

        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_coach, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        String coachname = horizontalList.get(position).get("coach_name");
        String teamname = horizontalList.get(position).get("team_name");


        holder.coach.setText(coachname);
        holder.team.setText(teamname);
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}
