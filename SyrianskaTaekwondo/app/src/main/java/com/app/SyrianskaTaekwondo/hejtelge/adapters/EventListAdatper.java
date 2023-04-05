package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.MonthlyEventResponse;

import java.util.ArrayList;

    public class EventListAdatper extends RecyclerView.Adapter<EventListAdatper.ViewHolder> {
        Context context;
        private int selectedPos=0;
        ArrayList<MonthlyEventResponse.Datum> arList;
      //  private GetMonth getMonth;
        public EventListAdatper(Context context, ArrayList<MonthlyEventResponse.Datum> arrayList) {
            this.context = context;
            this.arList = arrayList;
            this.selectedPos = selectedPos;
            //this.getMonth = getMonth;


        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
         //   holder.itemView.setSelected(selectedPos == position);
           holder.txtMonths.setText(arList.get(position).months);

//            holder.calendar_event_list.setLayoutManager(new GridLayoutManager(context, 1));
//            holder.calendar_event_list.setHasFixedSize(true);
//            MonthEventChildAdapter monthEventChildAdapter = new MonthEventChildAdapter(context, arList.get(position).monthlyDateVM);
//            holder.calendar_event_list.setAdapter(monthEventChildAdapter);


        }


        @Override
        public int getItemCount() {
            return arList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtMonths;
            RecyclerView calendar_event_list;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                txtMonths = itemView.findViewById(R.id.monthtxt);
                calendar_event_list = itemView.findViewById(R.id.calendar_event_list);

            }
        }
//        public interface GetMonth{
//            void monthName(String name);
//        }
    }
