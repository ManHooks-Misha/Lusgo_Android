package com.app.SyrianskaTaekwondo.hejtelge.adapters;

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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.EditTeam;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.Team_ListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;

import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.Common;
import vk.help.views.TextDrawable;

public class TeamListAdapter extends RecyclerView.Adapter implements Filterable {
    private final int VIEW_ITEM = 1;

    private ArrayList<Teamlist> horizontalList = new ArrayList<>();
    private ArrayList<Teamlist> filtedlist = new ArrayList<>();
    private AlertDialog alertDialog;
    // The minimum amount of items to have below your current scroll position
// before loading more.

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public Team_ListActivity activity;
    private String userid, role;
    private AppCompatTextView record;
    private FriendFilters friendFilter;

    public TeamListAdapter(RecyclerView recyclerView, AppCompatTextView record, String userid, Team_ListActivity activity) {
        this.activity = activity;
        this.userid = userid;
        this.record = record;
        CommonMethods cmn;
        cmn = new CommonMethods();
        role = cmn.getPrefsData(activity, "usertype", "");

/*
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
*/
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

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
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
        }
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
            ((StudentViewHolder) holder).setData(horizontalList.get(position));
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
        private AppCompatImageView img_view;
        private AvatarView img;
        private TextView txtview, edit, img_del;

        StudentViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            edit = view.findViewById(R.id.img_edit);
            img_del = view.findViewById(R.id.img_del);
            img_view = view.findViewById(R.id.img_view);

            img = view.findViewById(R.id.pos);

        }

        void setData(Teamlist data) {
            txtview.setText(data.getName());
            edit.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(view -> {
                String id = data.getId();
                String coachid = data.getCoach_id();

                if (!data.getName().isEmpty()) {
                    activity.startActivity(new Intent(activity, EditTeam.class)
                            .putExtra("data", Common.INSTANCE.getJSON(data))
                            .putExtra("id", id)
                            .putExtra("name",data.getName())
                            .putExtra("coachid", coachid));

                } else {
                    activity.startActivity(new Intent(activity, EditTeam.class).putExtra("data", Common.INSTANCE.getJSON(data)).putExtra("id", id).putExtra("name", data.getUsername()).putExtra("coachid", coachid));

                }
                //activity.finish();
            });
            img_del.setOnClickListener(view -> {
                showCustomDialog(data.getName(), data.getCoach_id(), data);
            });
            if (data.getChatstarted().equals("True") && userid.equals(data.getCoach_id())) {
                edit.setVisibility(View.VISIBLE);
                edit.setText("Öppen chatt");
                edit.setBackgroundResource(R.drawable.create_button_green);
            } else if (data.getChatstarted().equals("False")) {
                edit.setVisibility(View.VISIBLE);
                edit.setText("Stängd chatt");
                edit.setBackgroundResource(R.drawable.closechat);
            }

            if (data.getName().length() > 0) {
                String name = String.valueOf(data.getName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(itemView)
                        .load(data.getLogo())
                        .centerCrop()
                        .placeholder(drawable)
                        .into(img);
            }

            if (role.equals("2") || role.equals("5")) {
                img_del.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                img_view.setVisibility(View.GONE);
                itemView.setOnClickListener(view -> {
                    String id = data.getId();
                    String coachid = data.getCoach_id();
                    ArrayList<UserList> arr_user = new ArrayList<>(data.getUsers());
                    if (arr_user.size() > 0) {
                        activity.startActivity(new Intent(activity, EditTeam.class).putExtra("data",
                                Common.INSTANCE.getJSON(data)).putExtra("id", id)
                                .putExtra("name",data.getName())
                                .putExtra("coachid", coachid));

                        //   showCustomDialogUser(arr_user);
                     //   showBottomDialogeUser(arr_user);

                    } else {
                        Common.INSTANCE.showToast(activity, "Ingen användare hittades");
                    }

                });
            } else {
                img_del.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                img_view.setVisibility(View.GONE);

            }
        }


    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }


    private void showCustomDialog(String group_name, String groupid, Teamlist pos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setMessage("Vill du säkert ta bort detta '" + group_name + "'?");


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

    private void addAddAPI(String groupid, Teamlist pos) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("coach_id", groupid);
            object.put("team_id", pos.getId());
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
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_team");
    }


    private void showCustomDialogUser(ArrayList<UserList> arr) {
        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.activity_group_list, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setView(dialogView);
        RecyclerView email_text = dialogView.findViewById(R.id.list_user);
        AppCompatImageView cancel = dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        if (arr.size() > 0) {
            UserListAdapter adapterteam = new UserListAdapter(arr, activity);
            email_text.setAdapter(adapterteam);
        } else {
            Common.INSTANCE.showToast(activity, "Ingen användare hittades");
        }


        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showBottomDialogeUser(ArrayList<UserList> arr) {
        final BottomSheetDialog dialog = new BottomSheetDialog(activity.context, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.userlist_b_s_d);
        dialog.setCanceledOnTouchOutside(false);
        AppCompatImageView img_dialog = dialog.findViewById(R.id.CustomBottomDialog_ivCancel);
        RecyclerView recyclerView = dialog.findViewById(R.id.recycerUsers);
        if (arr.size() > 0) {
            UserListAdapter adapterteam = new UserListAdapter(arr, activity);
            recyclerView.setAdapter(adapterteam);
        } else {
            Common.INSTANCE.showToast(activity, "Ingen användare hittades");
        }
        img_dialog.setOnClickListener(view -> dialog.dismiss());
        dialog.show();


    }

}
