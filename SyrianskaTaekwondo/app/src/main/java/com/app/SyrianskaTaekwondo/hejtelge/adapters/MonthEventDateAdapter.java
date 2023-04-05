package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.Eventshow;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.MonthlyEventResponse;

import java.util.ArrayList;


public class MonthEventDateAdapter extends RecyclerView.Adapter<MonthEventDateAdapter.ViewHolder> {
    Context context;
    ArrayList<MonthlyEventResponse.EventDetail> arListChildEvent;

    public MonthEventDateAdapter(Context context, ArrayList<MonthlyEventResponse.EventDetail> arrayList) {
        this.context = context;
        this.arListChildEvent = arrayList;
    }

    @NonNull
    @Override
    public MonthEventDateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_eventdate_custom_layout, parent, false);
        return new MonthEventDateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MonthEventDateAdapter.ViewHolder holder, final int position) {
        String title = arListChildEvent.get(position).title.trim();
           title = title.replaceFirst("<p dir=\"ltr\">", "");
         title = title.replaceAll("</p>", "");

        holder.txt_event_title.setText(Html.fromHtml(title));
//
//        SpannableString spannableString = new SpannableString(title);
//
//// Remove the underline span
//        UnderlineSpan[] underlineSpans = spannableString.getSpans(0, spannableString.length(), UnderlineSpan.class);
//        for (UnderlineSpan span : underlineSpans) {
//            spannableString.removeSpan(span);
//        }
//
//// Set the modified SpannableString back to the TextView
//        holder.txt_event_title.setText(spannableString);
        String stdate = arListChildEvent.get(position).startDate;
        if (!arListChildEvent.get(position).teamName.equals("")) {
            holder.txtEvntTeamName.setVisibility(View.VISIBLE);
            holder.teamIMg.setVisibility(View.VISIBLE);
            holder.txtEvntTeamName.setText(arListChildEvent.get(position).teamName);
        }

        String enddate = arListChildEvent.get(position).endDate;
        String[] arrstart = stdate.split(" ");
        String txt_sttime = arrstart[1];
        String[] arrend = enddate.split(" ");
        String endtime = arrend[1];

        String[] sttime_ = txt_sttime.split(":");
        txt_sttime = sttime_[0] + ":" + sttime_[1];
        String[] endtime_ = endtime.split(":");
        endtime = endtime_[0] + ":" + endtime_[1];


        holder.txt_event_date.setText(txt_sttime + " - " + endtime);

        holder.txt_event_place.setText(Html.fromHtml(arListChildEvent.get(position).place));

        holder.itemView.setOnClickListener(view -> {
            String event_id = arListChildEvent.get(position).id;
            context.startActivity(new Intent(context, Eventshow.class).putExtra("id", event_id));


        });

    }


    @Override
    public int getItemCount() {
        return arListChildEvent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_event_title, txt_event_date, txt_event_place, txtEvntTeamName;
        ImageView teamIMg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_event_title = itemView.findViewById(R.id.txt_event_title);
            txt_event_date = itemView.findViewById(R.id.txt_event_date);
            txt_event_place = itemView.findViewById(R.id.txt_event_place);
            teamIMg = itemView.findViewById(R.id.img_team);
            txtEvntTeamName = itemView.findViewById(R.id.txt_event_teamname);
        }
    }
}
