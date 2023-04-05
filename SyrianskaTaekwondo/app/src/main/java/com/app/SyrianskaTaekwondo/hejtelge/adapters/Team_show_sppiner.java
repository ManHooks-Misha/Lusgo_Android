package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Team_show_sppiner extends RecyclerView.Adapter<Team_show_sppiner.MyViewHolder> implements Filterable {

    private ArrayList<HashMap<String, String>> dataSet;
    private ArrayList<HashMap<String, String>> filtedlist;
    Context mContext;
    String usertype;
    AppCompatEditText textView;
    RecyclerView coach;
    private FriendFilters friendFilter;


    class MyViewHolder extends RecyclerView.ViewHolder {
        //  AppCompatImageView flBottomRight, flTopLeft, flBottomLeft, flTopRight;

        AppCompatTextView team_name, select, role;

        MyViewHolder(View view) {
            super(view);

            team_name = view.findViewById(R.id.txt_name);


        }
    }

    public Team_show_sppiner(ArrayList<HashMap<String, String>> data, Context context, String usertype, AppCompatEditText textView, RecyclerView coach) {
        super();
        this.dataSet = data;
        this.filtedlist = data;
        this.mContext = context;
        this.usertype = usertype;
        this.textView = textView;
        this.coach = coach;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        HashMap dataModel = dataSet.get(i);

        holder.team_name.setText(Objects.requireNonNull(dataModel.get("name")).toString());
        holder.itemView.setOnClickListener(v -> {
            String value = dataSet.get(i).get("name");
            String team_id = dataSet.get(i).get("team_id");
            new CommonMethods().setPrefsData(mContext, "team_id", team_id);
            new CommonMethods().setPrefsData(mContext, "team_name", value);
            if (usertype.equals(ConsURL.members)) {
                String coach_name = dataSet.get(i).get("coach_name");
                new CommonMethods().setPrefsData(mContext, "coach_name", coach_name);


            }

            textView.setText(dataSet.get(i).get("name"));
            textView.setSelection(dataSet.get(i).get("name").length());

            coach.setVisibility(View.GONE);
        });
    }

    @Override
    public long getItemId(int i) {
        return dataSet.size();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

   /* @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        // Get the data item for this position
        HashMap dataModel = dataSet.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);

            convertView = inflater.inflate(R.layout.row_item, parent, false);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        viewHolder.txtName.setText(Objects.requireNonNull(dataModel.get("name")).toString());

        // Return the completed view to render on screen
        return convertView;
    }*/



    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilters();
        }

        return friendFilter;
    }

    private class FriendFilters extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<HashMap<String,String>> tempList = new ArrayList<>();

                // search content in friend list
                for (HashMap user : filtedlist) {
                    if (user.get("name").toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }
               /* for (UserList user : filtedlist) {
                    if (user.getRole().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }
                for (UserList user : filtedlist) {
                    for (Team_search team : user.getTeams()) {
                        if (team.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            tempList.add(user);
                        }
                    }
                }*/

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = filtedlist.size();
                filterResults.values = filtedlist;
            }

            return filterResults;
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataSet = (ArrayList<HashMap<String, String>>) results.values;
            if (dataSet == null) {
                dataSet = new ArrayList<>();
            }

            notifyDataSetChanged();

        }
    }
}