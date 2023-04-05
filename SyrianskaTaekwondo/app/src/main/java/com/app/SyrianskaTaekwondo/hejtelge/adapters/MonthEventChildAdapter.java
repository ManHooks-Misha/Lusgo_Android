package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.model.MonthlyVMDateModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class MonthEventChildAdapter extends RecyclerView.Adapter<MonthEventChildAdapter.ViewHolder> {
        Context context;
        ArrayList<MonthlyVMDateModel> arListChildEvent;

        public MonthEventChildAdapter(Context context, ArrayList<MonthlyVMDateModel> arrayList) {
            this.context = context;
            this.arListChildEvent = arrayList;
        }

        @NonNull
        @Override
        public MonthEventChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_child_custom_layout, parent, false);
            return new MonthEventChildAdapter.ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull final MonthEventChildAdapter.ViewHolder holder, final int position) {
           // Locale locale = new Locale("sv", "SE"); // ( language code, country code );
          //  Locale.setDefault(locale);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault());
            LocalDate ld = LocalDate.parse(arListChildEvent.get(position).date, dtf);
            String month_name = dtf2.format(ld);
          //  Log.d("fasdjlk",month_name);
            holder.txt_event_mnthdate.setText(month_name);


            holder.recycer_monthdate.setLayoutManager(new GridLayoutManager(context, 1));
            holder.recycer_monthdate.setHasFixedSize(true);
            MonthEventDateAdapter monthEventDateAdapter = new MonthEventDateAdapter(context, arListChildEvent.get(position).eventDetails);
            holder.recycer_monthdate.setAdapter(monthEventDateAdapter);

        }


        @Override
        public int getItemCount() {
            return arListChildEvent.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_event_mnthdate;
            RecyclerView recycer_monthdate;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                txt_event_mnthdate = itemView.findViewById(R.id.txt_event_mnthdate);
                recycer_monthdate = itemView.findViewById(R.id.recycer_monthdate);

            }
        }
    }

