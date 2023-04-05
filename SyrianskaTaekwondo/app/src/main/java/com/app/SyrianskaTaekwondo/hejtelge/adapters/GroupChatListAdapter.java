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

import com.app.SyrianskaTaekwondo.hejtelge.GroupChatActivity;
import com.app.SyrianskaTaekwondo.hejtelge.GroupChatList;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.database.ChatPush;
import com.app.SyrianskaTaekwondo.hejtelge.database.MuteNotify;
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

public class GroupChatListAdapter extends RecyclerView.Adapter implements Filterable {
    private final int VIEW_ITEM = 1;

    private ArrayList<GroupList> horizontalList;
    private ArrayList<HashMap<String, String>> arr_badge;
    private ArrayList<GroupList> filtedlist;
    private AlertDialog alertDialog;
    private CommonMethods cmn;
    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public GroupChatList activity;
    ChatPush chattable;
    MuteNotify muteTable;
    private String userid, usertype;
    private AppCompatTextView record;
    private FriendFilter friendFilter;

    public GroupChatListAdapter(ArrayList<GroupList> students, RecyclerView recyclerView, AppCompatTextView record, String userid, GroupChatList activity, String usertype, ArrayList<HashMap<String, String>> arr_badge) {
        horizontalList = students;
        this.filtedlist = students;
        this.activity = activity;

        this.userid = userid;
        this.record = record;
        this.usertype = usertype;
        this.arr_badge = arr_badge;
        chattable = new ChatPush(activity).getInstance(activity);
        muteTable = new MuteNotify(activity).getInstance(activity);
        cmn = new CommonMethods();
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
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chatlist, parent, false));
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
        return horizontalList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private AvatarView img, img1;
        private TextView txtview, edit, img_del, badge;

        StudentViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            edit = view.findViewById(R.id.img_edit);
            img_del = view.findViewById(R.id.img_del);
            img = view.findViewById(R.id.pos);
            img1 = view.findViewById(R.id.pos1);
            badge = view.findViewById(R.id.badge);
        }

        void setData(Activity activity, GroupList data) {
            String id = data.getGroup_id();
            if (data.getChatstarted().equals("False") && userid.equals(data.getInsertby())) {
                edit.setVisibility(View.GONE);
            } else {
                edit.setVisibility(View.GONE);

            }
            if (usertype.equals(ConsURL.members)) {
                edit.setVisibility(View.GONE);

            }
            if (!muteTable.CheckIsDataAlreadyInDBorNot(data.getGroup_id())) {
                muteTable.insertdata(activity, data.getGroup_id(), userid, "true");

            }

            ArrayList<String> arr = new ArrayList();
            if (arr_badge.size() > 0) {

                for (int i = 0; i < arr_badge.size(); i++) {

                    if (arr_badge.get(i).get("groupid").equals(data.getGroup_id())) {

                        arr.add(arr_badge.get(i).get("groupid"));
                    }

                }

            }
            if (arr.size() > 0) {
                badge.setText("" + arr.size());
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);

            }

            // edit.setOnClickListener(v -> addStartChatAPI(data.getGroup_id(), data));
            itemView.setOnClickListener(view -> {

                //  if (data.getChatstarted().equals("True")) {
                chattable.deleteAll(id);
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("countDelete","1");
//                editor.apply();
                activity.startActivity(new Intent(activity, GroupChatActivity.class)
                        .putExtra("isopen",data.getChatstarted())
                        .putExtra("data", Common.INSTANCE.getJSON(data))
                        .putExtra("id", id)
                        .putExtra("name", data.getName())
                        .putExtra("image", data.getImage())
                        .putExtra("chatfor", data.getChat_for())
                        .putExtra("backValue", "0")
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                activity.finish();
                // }
                //   activity.finish();
            });
            img_del.setOnClickListener(view -> {
                showCustomDialog(data.getName(), data.getGroup_id(), data);
            });
            String name = String.valueOf(data.getName().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(itemView)
                    .load(data.getImage())
                    .fitCenter()
                    .placeholder(drawable)
                    .into(img);
            String name_chat =(data.getChat_for().trim());
            if(name_chat.trim().equals("team")){
                txtview.setText(data.getName()+" - Teamchatt");

            }else{
                txtview.setText(data.getName()+" - Gruppchatt");

            }
            img1.setVisibility(View.GONE);
            TextDrawable drawable1 = TextDrawable.builder()
                    .buildRect(name_chat.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(itemView)
                    .load("")
                    .fitCenter()
                    .placeholder(drawable1)
                    .into(img1);
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

//                    startActivity(new Intent(EditGroup.this, EditGroup.class).putExtra("data", Common.INSTANCE.getJSON(data)).putExtra("id", id).putExtra("name", data.getName()));
//                    finish();

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
