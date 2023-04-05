package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.AddTeam_member;
import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;

import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.Common;
import vk.help.views.TextDrawable;

public class Addteammember_adapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;

    private ArrayList<Teamlist> horizontalList;
    private AlertDialog alertDialog;
    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    CommonMethods cmn = new CommonMethods();
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public AddTeam_member activity;
    private String userid, role, uid, coachid, roleid, flag;
    private AppCompatTextView record;

    public Addteammember_adapter(ArrayList<Teamlist> students, RecyclerView recyclerView, AppCompatTextView record, String userid, AddTeam_member activity, String coachid, String uid, String roleid, String flag) {
        horizontalList = students;
        this.activity = activity;
        this.userid = userid;
        this.flag = flag;
        this.record = record;
        this.coachid = coachid;
        this.roleid = roleid;
        this.uid = uid;
        CommonMethods cmn = new CommonMethods();
        role = cmn.getPrefsData(activity, "usertype", "");

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
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_team_member, parent, false));
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
        private TextView txtview, imgadd;

        StudentViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            img_view = view.findViewById(R.id.img_view);
            imgadd = view.findViewById(R.id.img_del);

            img = view.findViewById(R.id.pos);

        }

        void setData(Teamlist data) {
            txtview.setText(data.getName());
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

            if (role.equals("2") || role.equals("5")) {
                img_view.setVisibility(View.GONE);
                itemView.setOnClickListener(view -> {
                    ArrayList<UserList> arr_user = new ArrayList<>(data.getUsers());
                    if (arr_user.size() > 0) {
                        showCustomDialogUser(arr_user);

                    } else {
                        Common.INSTANCE.showToast(activity, "Ingen användare hittades");
                    }

                });
                imgadd.setOnClickListener(view -> {

                    String teamid = horizontalList.get(getPosition()).getId();
                    addAddTeamAPI(coachid, teamid, uid, roleid);
                });
            } else {

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

    private void addAddTeamAPI(String coachid, String teamid, String uid, String roleid) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );

        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("coach_id", coachid);
            object.put("team_id", teamid);
            object.put("user_id", userid);
            object.put("member_id", uid);
            object.put("role_id", roleid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(activity, result -> {
            try {
                if (result.isStatus()) {
                    if (flag.equals("block")) {
                        getBlockprofile();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setCancelable(false);
                        builder.setMessage("Medlem har lagts till i teamet");
                        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                            activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", uid).putExtra("flag", "user").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            alertDialog.dismiss();
                        });
                        alertDialog = builder.create();
                        alertDialog.show();
                        //    Toast.makeText(activity, "Medlem har" +" lagts till i teamet", Toast.LENGTH_SHORT).show();
                        // activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", uid).putExtra("flag", "user").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                    }


                } else {
                    //JSONObject object = new JSONObject(result.getData());

                    new CommonMethods().showAlert(result.getMessage(), activity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "add_member");
    }


    private void showCustomDialogUser(ArrayList<UserList> arr) {

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.activity_group_list, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setView(dialogView);
        RecyclerView email_text = dialogView.findViewById(R.id.list_user);
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


    private void getBlockprofile() {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("uStatus", "0");
            object.put("user_id", userid);
            object.put("u_id", uid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {

                if (result.isStatus()) {
                 //   Toast.makeText(activity, "Medlem har" + " lagts till i teamet", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setMessage("Medlem har lagts till i teamet");
                    builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                        activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", uid).putExtra("flag", "user").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        alertDialog.dismiss();
                    });
                    alertDialog = builder.create();
                    alertDialog.show();

                  //  activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", uid).putExtra("flag", "user").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "block_user");
    }
}
