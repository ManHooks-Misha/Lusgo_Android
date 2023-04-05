package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.Team_ListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class Team_list_alertbox extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_ITEM = 1;
    private FriendFilters friendFilter;

    private ArrayList<Teamlist> horizontalList = new ArrayList<>(), filtedlist = new ArrayList<>();
    private AlertDialog alertDialog;
    private String inviteteamid;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public Team_ListActivity activity;
    private String userid, teamid, teamname, id_invite;
    Context context;
    RecyclerView recycleview;
    private AppCompatEditText textView;
    int row_index;
    String flag;
    int index=0;

    public Team_list_alertbox(RecyclerView recyclerView, String userid, Context context, AppCompatEditText textView, String inviteteamid, String flag) {

        this.userid = userid;
        this.context = context;
        this.textView = textView;
        this.flag = flag;
        this.inviteteamid = inviteteamid;
        this.recycleview = recyclerView;
        notifyDataSetChanged();
        id_invite = new CommonMethods().getPrefsData(context, "team_id_invite", "");
        CommonMethods cmn = new CommonMethods();
//        role = cmn.getPrefsData(context, "usertype", "");

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setUpdatedData(ArrayList<Teamlist> tempList) {
        horizontalList.clear();
        filtedlist.clear();
        for (Teamlist teamlist : tempList) {
            if (!horizontalList.contains(teamlist)) {
                horizontalList.add(teamlist);
            }
            if (!filtedlist.contains(teamlist)) {
                filtedlist.add(teamlist);
            }
        }
        this.notifyDataSetChanged();
    }

    public void setUpdatedData(ArrayList<Teamlist> tempList, String charSequence) {
        if (charSequence.isEmpty()) {
            setUpdatedData(tempList);
        } else {
            horizontalList.clear();
            filtedlist.clear();


            for (Teamlist user : tempList) {
                if (!filtedlist.contains(user)) {
                    filtedlist.add(user);
                }

                if (user == null || user.getName().toLowerCase().trim().contains(charSequence.toLowerCase())) {
                    if (!horizontalList.contains(user)) {
                        horizontalList.add(user);
                    }
                }
            }
            this.notifyDataSetChanged();
            setLoaded();
        }
    }

    public void addItem() {
        horizontalList.add(null);
        filtedlist.add(null);
        this.notifyItemInserted(horizontalList.size() - 1);
    }

    public void removeLastData() {
        horizontalList.remove(horizontalList.size() - 1);
        filtedlist.remove(filtedlist.size() - 1);
        notifyItemRemoved(horizontalList.size() - 1);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_adapter, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            ((StudentViewHolder) holder).setData(horizontalList.get(position), position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private AvatarView img;
        private TextView txtview;
        private LinearLayout layout;
        private CardView card_view, pre_cardview;

        StudentViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            card_view = view.findViewById(R.id.card_view);
            layout = view.findViewById(R.id.layout);

            img = view.findViewById(R.id.pos);

        }

        void setData(Teamlist data, int position) {
            txtview.setText(data.getName());
            itemView.setOnClickListener(view -> {


                if (flag.equals("1")) {
                    row_index = position;
                    setTeamID(data.getId());
                    setTeamName(data.getName());
                    notifyDataSetChanged();
                    refreshRecyclerView(recycleview);
                } else {
                    row_index = position;
                    setTeamID(data.getId());
                    setTeamName(data.getName());
                    if (textView != null) {
                        textView.setText(data.getName());
                        textView.setSelection(data.getName().length());
                        recycleview.setVisibility(View.GONE);
                    }

                    notifyDataSetChanged();
                    refreshRecyclerView(recycleview);

                }
            });
            if(index==0) {
                if (horizontalList.get(position).getId().equals(id_invite)) {
                    row_index = position;
                    index++;
                    if (row_index == position) {
                        setTeamID(data.getId());
                        setTeamName(data.getName());
                        card_view.setCardBackgroundColor(Color.parseColor("#c0c0c0"));
                    } else {
                        card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
                    }
                }
            }else{
                if (row_index == position) {
                    setTeamID(data.getId());
                    setTeamName(data.getName());
                    card_view.setCardBackgroundColor(Color.parseColor("#c0c0c0"));
                } else {
                    card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
                }
            }

           /* itemView.setOnClickListener(view -> {
                setTeamID(data.getId());
                setTeamName(data.getName());

                card_view.setCardBackgroundColor(Color.parseColor("#c0c0c0"));
                pre_cardview=card_view;
//                if(textView!=null) {
//                    textView.setText(data.getName());
//                    textView.setSelection(data.getName().length());
//                    recycleview.setVisibility(View.GONE);
//                }
            });*/
            if (data.getName().length() > 0) {
                String name = String.valueOf(data.getName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(itemView)
                        .load(data.getLogo())
                        .fitCenter()
                        .placeholder(drawable)
                        .into(img);
            }


        }


    }

    public String getTeamID() {
        return teamid;
    }

    public String getTeamName() {
        return teamname;
    }

    public String setTeamID(String id) {
        teamid = id;
        return teamid;
    }

    public String setTeamName(String name) {
        teamname = name;
        return teamname;
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }


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
                ArrayList<Teamlist> tempList = new ArrayList<>();
                // search content in friend list
                for (Teamlist user : filtedlist) {
                    if (user == null || user.getName().toLowerCase().trim().contains(constraint.toString().toLowerCase().trim())) {
                        tempList.add(user);
                    }
                }
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
            //   horizontalList = (ArrayList<Teamlist>) results.values;
            if (results.values != null) {
                horizontalList.clear();
                horizontalList.addAll((ArrayList<Teamlist>) results.values);
            }

            notifyDataSetChanged();
            recycleview.invalidate();
        }
    }

    void refreshRecyclerView(RecyclerView recyclerView) {
        RecyclerView.Adapter adapterRef = recyclerView.getAdapter();
        recyclerView.setAdapter(null);
        recyclerView.setAdapter(adapterRef);
    }
}