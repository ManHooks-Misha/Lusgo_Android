package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Event;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;

import org.jetbrains.annotations.NotNull;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthDateAdapter extends RecyclerView.Adapter<MonthDateAdapter.MyViewHolder> {
    private ArrayList<HashMap<String, List<Event>>> horizontalList;
    private String usertype;
    private String userid;
    private Activity context;
    private long mLastClickTime = 0;
    private long mLastClickTime1 = 0;
    private long mLastClickTime2 = 0;

    public final DateTimeFormatter mTimeFormat = DateTimeFormat.forPattern(" h:mm a");
    private CommonMethods cmn;
    private ArrayList<HashMap<String, String>> arr_participateuser_attend = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_participateuser_notattend = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_participateuser_maybe = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_participateuser_notanswer = new ArrayList<>();
    private AlertDialog alertDialog;
    long stdate_, enddate_;
    private String title, description, place;

    public MonthDateAdapter(ArrayList<HashMap<String, List<Event>>> horizontalList, Activity context, String usertype, String userid) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.usertype = usertype;
        this.userid = userid;
        cmn = new CommonMethods();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView event;

        private AppCompatTextView date;

        MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.monthtxt);
            event = view.findViewById(R.id.calendar_event_list);
            ViewCompat.setNestedScrollingEnabled(event, false);
            //   pos = view.findViewById(R.id.pos);
//            ll_menu = view.findViewById(R.id.ll_menu);
        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {

        HashMap<String, List<Event>> map = horizontalList.get(position);
       /* if(!key.isEmpty()) {
            key.
        }*/
        for (Map.Entry<String,  List<Event>> pair : map.entrySet()) {
            holder.date.setText(pair.getKey());
            List<Event> arr_show = horizontalList.get(position).get(pair.getKey());
            CalendarAdapter calenderAdapter = new CalendarAdapter(
                    arr_show, context, usertype, userid);
            holder.event.setLayoutManager(new GridLayoutManager(context, 1));
            holder.event.setAdapter(calenderAdapter);
        }

/*
        holder.itemView.setOnClickListener(view -> {
            String event_id = horizontalList.get(position).getId();
            if (userid.length() > 0) {
                context.startActivity(new Intent(context, Eventshow.class).putExtra("DATA", Common.INSTANCE.getJSON(horizontalList.get(position))).putExtra("id", event_id));
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            } else {
                context.startActivity(new Intent(context, Eventshow.class).putExtra("DATA", Common.INSTANCE.getJSON(horizontalList.get(position))).putExtra("id", event_id));
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return horizontalList.size();

    }


}
