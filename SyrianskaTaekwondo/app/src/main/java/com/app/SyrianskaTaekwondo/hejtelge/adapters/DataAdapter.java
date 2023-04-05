package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.UserListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Team_choose;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Team_search;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class DataAdapter extends RecyclerView.Adapter implements Filterable {
    private final int VIEW_ITEM = 1;
    private AlertDialog alertDialog;
    private ArrayList<UserList> horizontalList=new ArrayList<>();
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private FriendFilter friendFilter;
    private ArrayList<UserList> filtedlist;
    List<HashMap<String, String>> arr_output = new ArrayList<>();
    private UserListActivity context;
    private String news = "0", events = "0", message = "0", userid, role_txt;
    private CommonMethods cmn;
    UserTeamList_member adapter;
    private String teams = "";
    List<HashMap<String, String>> arr_team = new ArrayList<>();

    public DataAdapter(ArrayList<UserList> students, RecyclerView recyclerView, UserListActivity context) {
        horizontalList = students;
        cmn = new CommonMethods();
        filtedlist = students;
        this.context = context;
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
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_adapter, parent, false));
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
        private AvatarView img;
        private AppCompatTextView img_permission;
        private AppCompatTextView txtview, role, email, date_time, txt_mob;
        private String event_ischecked = "", news_ischecked = "", message_ischecked = "";

        StudentViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.txt_name);
            role = view.findViewById(R.id.txt_role);
            email = view.findViewById(R.id.txt_email);
            img = view.findViewById(R.id.pos);
            txt_mob = view.findViewById(R.id.txt_mob);
            date_time = view.findViewById(R.id.date_time);
            img_permission = view.findViewById(R.id.img_permission);
        }

        void setData(UserList data) {
            role_txt = cmn.getPrefsData(context, "usertype", "");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = null;
            if (data.getCreated() != null) {
                long now = System.currentTimeMillis() - 1000;

                try {
                    date = dateFormat.parse(data.getCreated());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                assert date != null;
                long result = TimeUnit.DAYS.convert((now - date.getTime()), TimeUnit.MILLISECONDS);
                updateLabel(date_time, result);
            }
            /*if (data.getPermissions() != null) {
                news = data.getPermissions().getNews();
                message = data.getPermissions().getMessage();
                events = data.getPermissions().getEvent();
            }*/
            if (data.getStatus().equals("2")) {
                img_permission.setText("Blockerad");
                img_permission.setTextColor(Color.RED);
                img_permission.setBackground(null);
                img_permission.setVisibility(View.GONE);
            } else {
                try {
                    if (data.getRole().trim().equals(ConsURL.rolename)) {
                        img_permission.setVisibility(View.VISIBLE);

                    } else {
                        img_permission.setVisibility(View.VISIBLE);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            if (role_txt.equals(ConsURL.sub_coach)) {
                if (data.getStatus().equals("2")) {
                    img_permission.setVisibility(View.VISIBLE);

                } else {
                    img_permission.setVisibility(View.VISIBLE);
                }

            }
            if (!data.getStatus().equals("2")) {
                img_permission.setOnClickListener(view -> {
                    String uid = data.getId();

                    getadd_teamnAPI(uid);
                    //   showCustomDialogUser(uid, data);
                });
            }


            userid = cmn.getPrefsData(context, "id", "");
            if (data.getFirstname().trim().length() > 0) {
                txtview.setText(String.format("%s %s", data.getFirstname().trim(), data.getLastname().trim()));
            } else {
                txtview.setText(data.getUsername());

            }
            if (data.getEmail().length() > 0) {
                email.setText(data.getEmail());
                email.setVisibility(View.VISIBLE);

            } else {
                email.setVisibility(View.GONE);
            }
            if (role_txt.equals("2") || role_txt.equals("5")) {

                if (data.getStatus().equals("2")) {
                    img_permission.setText("Blockerad");
                    img_permission.setTextColor(Color.RED);
                    img_permission.setVisibility(View.VISIBLE);
                    img_permission.setBackground(null);

                } else {
                    img_permission.setVisibility(View.VISIBLE);

                }
            }
            if (data.getPhone() != null) {
                txt_mob.setText(data.getPhone());
                txt_mob.setVisibility(View.VISIBLE);


            } else {
                txt_mob.setVisibility(View.GONE);
            }
            if (data.getRole_id().equals("2") || data.getRole_id().equals("5")) {
                role.setText(Html.fromHtml(data.getRole()));

            } else {
                teams = "";
                List<Team_search> arrteam = data.getTeams();
                try {
                    for (int i = 0; i < arrteam.size(); i++) {

                        String name = arrteam.get(i).name;
                        teams = teams + "," + name;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                if (teams.length() > 0) {
                    teams = teams.replaceFirst(",", "");

                }

                String text = "<font color=#177ff0>" + " (" + teams + ")" + "</font>";
                if (!teams.equals("")) {
                    role.setText(Html.fromHtml(data.getRole() + text));

                } else {
                    role.setText(data.getRole());
                }
            }
            if (data.getFirstname().trim().length() > 0) {

                String name = String.valueOf(data.getFirstname().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));

                Glide.with(itemView)
                        .load(data.getProfile_image())
                        .centerCrop()
                        .placeholder(drawable)
                        .into(img);
            } else {
                String name = String.valueOf(data.getUsername().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));

                Glide.with(itemView)
                        .load(data.getProfile_image())
                        .centerCrop()
                        .placeholder(drawable)
                        .into(img);
            }
          /*  String name =String.valueOf(data.getFirstname().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(itemView)
                    .load(data.getProfile_image())
                    .placeholder(drawable)
                    .fitCenter()
                    .into(img);
*/
            itemView.setOnClickListener(view -> {
                String id = data.getId();
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user"));
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            });

        }

    }

    private void updateLabel(AppCompatTextView txt, long result) {
        if (result == 0) {
            txt.setText(String.format("%s", "Idag"));

        } else if (result == 1) {
            txt.setText(String.format("%s", "Igår"));

        } else {

            txt.setText(result + String.format("%s", " dagar sedan"));

        }

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
            friendFilter = new FriendFilter();
        }

        return friendFilter;
    }
    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<UserList> tempList = new ArrayList<>();

                // search content in friend list
                for (UserList user : filtedlist) {
                    if (user.getFirstname().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            horizontalList = (ArrayList<UserList>) results.values;
            if (horizontalList == null) {
                horizontalList = new ArrayList<>();
            }
            notifyDataSetChanged();
        }
    }

/*
    private class FriendFilters extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<UserList> tempList = new ArrayList<>();

                // search content in friend list
                for (UserList user : filtedlist) {
                    if (user.getFirstname().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }
                for (UserList user : filtedlist) {
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
            horizontalList = (ArrayList<UserList>) results.values;
            if (horizontalList == null) {
                horizontalList = new ArrayList<>();
            }

            notifyDataSetChanged();

        }
    }
*/


    private void showCustomDialogUser(String uid, UserList data) {
        if (data.getPermissions() != null) {
            news = data.getPermissions().getNews();
            message = data.getPermissions().getMessage();
            events = data.getPermissions().getEvent();
        }

        ViewGroup viewGroup = context.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.show_userrole, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatCheckBox create_news = dialogView.findViewById(R.id.per_news);
        AppCompatCheckBox create_event = dialogView.findViewById(R.id.per_event);
        AppCompatCheckBox create_msg = dialogView.findViewById(R.id.per_message);
        AppCompatCheckBox create_all = dialogView.findViewById(R.id.per_all);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        if (message.equals("1")) {
            create_msg.setChecked(true);
        }
        if (news.equals("1")) {
            create_news.setChecked(true);
        }
        if (events.equals("1")) {
            create_event.setChecked(true);

        }
        if (message.equals("1") && news.equals("1") && events.equals("1")) {
            create_event.setChecked(true);
            create_news.setChecked(true);
            create_msg.setChecked(true);
            create_all.setChecked(true);

        }
        create_news.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                create_all.setChecked(false);

                news = "1";

              /*  if (message.equals("1") && events.equals("1")) {
                    create_all.setChecked(true);

                }*/

            } else {
                news = "0";

            }
        });
        create_event.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                create_all.setChecked(false);
                events = "1";

               /* if (message.equals("1") && news.equals("1")) {
                    create_all.setChecked(true);

                }*/
            } else {
                events = "0";

            }
        });
        create_msg.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                create_all.setChecked(false);
                message = "1";

             /*   if (events.equals("1") && news.equals("1")) {
                    create_all.setChecked(true);

                }*/
            } else {
                message = "0";

            }
        });
        create_all.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                create_event.setChecked(false);
                create_msg.setChecked(false);
                create_news.setChecked(false);
                create_all.setChecked(true);

                message = "1";
                events = "1";
                news = "1";
            } else {
                message = "0";
                events = "0";
                news = "0";
            }
        });
        ok.setOnClickListener(view -> {
            String per = "";
            if (cmn.isOnline(context)) {
                if (create_all.isChecked()) {
                    per = "1";
                } else {
                    per = "0";

                }
                //  getadd_permissionAPI(uid, per);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialogUserTeam(String uid, List<HashMap<String, String>> data) {
      /*  if (data.getPermissions() != null) {
            news = data.getPermissions().getNews();
            message = data.getPermissions().getMessage();
            events = data.getPermissions().getEvent();
        }*/

        ViewGroup viewGroup = context.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.user_previleges, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        RecyclerView create_news = dialogView.findViewById(R.id.user_team);

        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);

        adapter = new UserTeamList_member(
                data, context);
        create_news.setLayoutManager(new GridLayoutManager(context, 1));
        create_news.setAdapter(adapter);


        ok.setOnClickListener(view -> {
            arr_output.clear();
            List<HashMap<String, String>> arr = adapter.getList();
            for (HashMap map : arr) {
                HashMap map1 = new HashMap();
                map1.put("team_id", map.get("team_id").toString());
                map1.put("u_id", uid);
                if (map.get("checked").equals("true")) {
                    map1.put("role", "6");

                } else {
                    map1.put("role", "4");

                }
                arr_output.add(map1);
            }
            if (cmn.isOnline(context)) {
                getadd_permissionAPI(arr_output);

            } else {
                cmn.showAlert(context.getResources().getString(R.string.internet),context);
              //  Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void getadd_permissionAPI(List<HashMap<String, String>> per) {
        String requestData;
        try {
            Gson gson = new Gson();
            Team_choose team = new Team_choose();
            team.access_key = ConsURL.accessKey;
            team.user_id = userid;
            team.teams = per;
            requestData = gson.toJson(team);

        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    alertDialog.dismiss();
                    context.startActivity(new Intent(context, UserListActivity.class));
                    context.finish();

                } else {
                    alertDialog.dismiss();
                    cmn.showAlert("Detta team har bara en medlem.", context);
                    //  Common.INSTANCE.showToast(context, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "add_permission");
    }

    private void getadd_teamnAPI(String uid) {
        arr_team.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("u_id", uid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    JSONArray arr = new JSONArray(result.getData());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object = arr.getJSONObject(i);
                        HashMap map = new HashMap();
                        map.put("team_id", object.getString("team_id"));
                        map.put("coach_id", object.getString("coach_id"));
                        map.put("name", object.getString("name"));
                        map.put("logo", object.getString("logo"));
                        if (object.getString("role").equals("Teamleader hjälpare")) {
                            map.put("checked", "true");
                        } else {
                            map.put("checked", "false");

                        }
                        map.put("logo", object.getString("logo"));
                        arr_team.add(map);
                    }
                    showCustomDialogUserTeam(uid, arr_team);
//                    context.startActivity(new Intent(context, UserListActivity.class));
//                    context.finish();


                } else {
                    cmn.showAlert(result.getMessage(),context);
                   // Common.INSTANCE.showToast(context, result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userTeams");
    }


}

