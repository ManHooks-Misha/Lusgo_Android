package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.EditGroup;
import com.app.SyrianskaTaekwondo.hejtelge.GroupListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.Common;
import vk.help.views.TextDrawable;

public class GroupListAdapter extends RecyclerView.Adapter implements Filterable {
    private final int VIEW_ITEM = 1;

    private ArrayList<GroupList> horizontalList;
    private ArrayList<GroupList> filtedlist;
    private AlertDialog alertDialog;
    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public GroupListActivity activity;
    private String userid;
    private AppCompatTextView record;
    private FriendFilter friendFilter;

    public GroupListAdapter(ArrayList<GroupList> students, RecyclerView recyclerView, AppCompatTextView record, String userid, GroupListActivity activity) {
        horizontalList = students;
        this.filtedlist = students;
        this.activity = activity;
        this.userid = userid;
        this.record = record;
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

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grouplist_adapter, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {

            ((StudentViewHolder) holder).setData(activity, horizontalList.get(position));
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        try {
            return horizontalList.size();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private AvatarView img;
        private TextView txtview, edit, img_del;

        StudentViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            edit = view.findViewById(R.id.img_edit);
            img_del = view.findViewById(R.id.img_del);
            img = view.findViewById(R.id.pos);
        }

        void setData(Activity activity, GroupList data) {
            txtview.setText(data.getName());
            String id = data.getGroup_id();
            if (data.getChatstarted().equals("True") && userid.equals(data.getInsertby())) {
                edit.setVisibility(View.VISIBLE);
                edit.setText("Öppen chatt");
                edit.setBackgroundResource(R.drawable.create_button_green);

            } else if (data.getChatstarted().equals("False")) {
                edit.setVisibility(View.VISIBLE);
                edit.setText("Stängd chatt");
                edit.setBackgroundResource(R.drawable.closechat);
            }

            itemView.setOnClickListener(view -> {
                activity.startActivity(new Intent(activity, EditGroup.class)
                        .putExtra("data", Common.INSTANCE.getJSON(data))
                        .putExtra("id", id)
                        .putExtra("name", data.getName())
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //activity.finish();
            });
            img_del.setOnClickListener(view -> {
                showCustomDialog(data.getName(), data.getGroup_id(), data);
            });
            String name = String.valueOf(data.getName().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(itemView)
                    .load(data.getImage())
                    .placeholder(drawable)
                    .into(img);
        }

    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    public void showCustomDialog(String group_name, String groupid, GroupList pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setMessage("Vill du säkert radera " + group_name + "' group?");


        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (new CommonMethods().isOnline(activity)) {
                addAddAPI(groupid, pos);
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();

        alertDialog.show();
    }

    private void addAddAPI(String groupid, GroupList pos) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("group_id", groupid);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(activity, result -> {
            try {
                if (result.isStatus()) {
                    activity.arr.remove(pos);
                    if (activity.arr.size() == 0) {
                        record.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_group");
    }

    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilter();
        }

        return friendFilter;
    }

    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<GroupList> tempList = new ArrayList<>();

                // search content in friend list
                for (GroupList user : filtedlist) {
                    if (user.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            horizontalList = (ArrayList<GroupList>) results.values;
            notifyDataSetChanged();
        }
    }

}
