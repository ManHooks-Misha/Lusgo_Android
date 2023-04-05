package com.app.SyrianskaTaekwondo.hejtelge;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateTeam;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class TeamUser extends MasterActivity implements SearchView.OnQueryTextListener {
    private String teamid, userid, teamname;
    private CommonMethods cmn;
    Teamlist user_data;
    private ArrayList<UserList> arr_user = new ArrayList<>();
    private ArrayList<String> arr_userid = new ArrayList<>();
    private ArrayList<UserList> arr = new ArrayList<>();
    RecyclerView list, user_group;
    private FloatingActionButton add;
    User_event_Adapter mAdapter;
    private ArrayList<UserList> filtedlist;
    UserAdapter user;
    boolean isCheckFromParent = true;
    SearchView searchView;
    private User_event_Adapter.FriendFilters friendFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_user);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadID();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
       // getSupportActionBar().setTitle("Lägg till användare");
        if (getIntent() != null && getIntent().hasExtra("id")) {

            user_data = (Teamlist) getObject(getIntent().getStringExtra(DATA), Teamlist.class);

            teamid = getIntent().getStringExtra("id");
            teamname = getIntent().getStringExtra("name");
        }
        cmn = new CommonMethods();

        userid = cmn.getPrefsData(context, "id", "");
       // add.setOnClickListener(view -> addTeamAPI(arr_userid, userid, teamid));
        if (cmn.isOnline(context)) {
            getUserAPI();
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
        }
        user = new UserAdapter(
                arr_user, context);
        user_group.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        user_group.setAdapter(user);
    }



    private void getUserAPI() {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", 0);
            object.put("user_id", userid);
            object.put("team_id", "");
            object.put("block_users", "1");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    JSONObject objarr = new JSONObject(result.getData());
                    if (objarr.length() > 0) {

                        JSONArray obj = objarr.getJSONArray("userList");


                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject object = obj.getJSONObject(i);
                            String id = object.getString("id");
                            if (!userid.equals(id)) {
                                arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                            }

                        }
                    }
                   /* for (int i = 0; i < obj.length(); i++) {
                        arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
    }

    public void loadID() {
        list = findViewById(R.id.list_group);
        user_group = findViewById(R.id.user_group);
        add = findViewById(R.id.add);
        mAdapter = new User_event_Adapter(arr, list, TeamUser.this);
        list.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            mAdapter.notifyItemInserted(arr.size() - 1);
            list.postDelayed(() -> {
                int end = arr.size() + 20;
                getRecursionAPI(String.valueOf(end));
            }, 2000);
        });
    }

    private void getRecursionAPI(String offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", offset);
            object.put("user_id", userid);
            object.put("team_id", "");

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                int position = arr.size() - 1;
                arr.remove(position);
                mAdapter.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONObject objarr = new JSONObject(result.getData());

                    JSONArray obj = objarr.getJSONArray("userList");

                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }

                    if (obj.length() > 0) {
                        mAdapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //  mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.update, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.done); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        mColorFullMenuBtn.setTitle(s);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();


        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(this);
        // searchView.setIconifiedByDefault(false);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return true;
    }


    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


        private List<UserList> horizontalList;
        List<UserList> friendFilters;
        private Context context;


        public UserAdapter(List<UserList> horizontalList, Context context) {
            this.horizontalList = horizontalList;
            this.friendFilters = horizontalList;
            this.context = context;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout ll_menu;
            AvatarView imageView;
            AppCompatImageView cancel;
            TextView txtview, pos;

            MyViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);
                imageView = view.findViewById(R.id.iv_profile);
                cancel = view.findViewById(R.id.iv_selected);
                //   pos = view.findViewById(R.id.pos);
//            ll_menu = view.findViewById(R.id.ll_menu);
            }
        }


        @NotNull
        @Override
        public UserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_user, parent, false);

            return new UserAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final UserAdapter.MyViewHolder holder, final int position) {

            holder.txtview.setText(horizontalList.get(position).getFirstname() + " " + horizontalList.get(position).getLastname());

            if (horizontalList.get(position).getFirstname().length() > 0) {
                String namea = String.valueOf(horizontalList.get(position).getFirstname().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(horizontalList.get(position).getProfile_image())
                        .centerCrop()
                        .placeholder(drawable)
                        .into(holder.imageView);
            }
          /*  Glide.with(context)
                    .load(horizontalList.get(position).getProfile_image())
                    .fitCenter()
                    .placeholder(R.drawable.user_diff)
                    .into(holder.imageView);*/
            holder.cancel.setOnClickListener(view -> {

                arr_user.remove(position);
                arr_userid.remove(position);
                mAdapter.notifyDataSetChanged();
                //EditGroup.arr.remove(position);
                notifyDataSetChanged();
            });

        }


        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }


    private void addTeamAPI(ArrayList<String> arr, String coachid, String teamid) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            UpdateTeam pojo = new UpdateTeam();
            pojo.access_key = ConsURL.accessKey;
            pojo.action = "Add";
            pojo.coach_id = coachid;
            pojo.user_id = userid;
            pojo.members = arr;
            pojo.team_id = teamid;
            Gson gson = new Gson();

            requestData = gson.toJson(pojo);

        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    for (UserList map : arr_user) {
                        EditTeam.arr.add(map);
                    }
                     startActivity(new Intent(TeamUser.this, Team_ListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    //finish();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_teamUser");
    }


    private class User_event_Adapter extends RecyclerView.Adapter implements Filterable {
        private final int VIEW_ITEM = 1;
        private ArrayList<UserList> horizontalList;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        public TeamUser activity;

        private User_event_Adapter(ArrayList<UserList> students, RecyclerView recyclerView, TeamUser activity) {
            horizontalList = students;
            filtedlist = students;
            this.activity = activity;

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
        public Filter getFilter() {
            if (friendFilter == null) {
                friendFilter = new FriendFilters();
            }

            return friendFilter;
        }


        public class FriendFilters extends Filter {

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

        @Override
        public int getItemViewType(int position) {
            int VIEW_PROG = 0;
            return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new User_event_Adapter.StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participate_user, parent, false));
            } else {
                return new User_event_Adapter.ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof User_event_Adapter.StudentViewHolder) {
                ((User_event_Adapter.StudentViewHolder) holder).setData(horizontalList.get(position));
            } else {
                ((User_event_Adapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
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

        private class StudentViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView iv_selected;
            private AvatarView img;
            private TextView txtview;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);

                img = view.findViewById(R.id.iv_profile);
                iv_selected = view.findViewById(R.id.iv_selected);

            }

            void setData(UserList data) {
                txtview.setText(data.getFirstname());
                iv_selected.setVisibility(arr_user.contains(data) ? View.VISIBLE : View.GONE);

                itemView.setOnClickListener(view -> {


                    // UserList item = arr_user1.get(position);

                   /* for (UserList map1 : EditTeam.arr) {
                        if (data.getId().equals(map1.getId())) {
                            Toast.makeText(context, "den här användaren finns redan i detta team", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                   for (UserList map1 : arr_user) {
                        if (data.getId().equals(map1.getId())) {
                            Toast.makeText(context, "den här användaren finns redan i detta team", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }*/
/*

                    if (data.getTeam_id().length() == 0) {
                        if (!arr_user.contains(data)) {
                            user_group.setVisibility(View.VISIBLE);
                            UserList map = new UserList();
                            map.setFirstname(data.getFirstname());
                            map.setImagepath(data.getProfile_image());
                            map.setId(data.getId());
                            map.setLastname(data.getLastname());
                            arr_user.add(map);
                            arr_userid.add(data.getId());
                            user.notifyDataSetChanged();
                        } else {
                            arr_user.remove(data);
                            user.notifyDataSetChanged();

                            //  showToast("Användaren finns redan");
                        }


                    } else {
                        Toast.makeText(context, "Användare har redan lagts till i teamet", Toast.LENGTH_SHORT).show();
                    }*/

                    for (UserList users : user_data.getUsers()) {
                        if ((data.getFirstname().equals(users.getFirstname()))) {

                          //  Toast.makeText(context, "Användare har redan lagts till i teamet", Toast.LENGTH_SHORT).show();
                            cmn.customDialogMsg(TeamUser.this,"Användare har redan lagts till i teamet");
                            return;

                        }
                    }
                    if (!arr_user.contains(data)) {
                        user_group.setVisibility(View.VISIBLE);
                        arr_userid.add(data.getId());

                        arr_user.add(data);
                        user.notifyDataSetChanged();

                        // showToast("Användaren
                    } else {
                        arr_user.remove(data);
                        arr_userid.remove(data.getId());

                        user.notifyDataSetChanged();

                        // showToast("Användaren finns redan");
                    }
                    notifyItemChanged(getAdapterPosition());
                    updateUI();
                });

                if (data.getFirstname().length() > 0) {
                    String namea = String.valueOf(data.getFirstname().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(itemView)
                            .load(data.getProfile_image())
                            .centerCrop()
                            .placeholder(drawable)
                            .into(img);
                }
                /*Glide.with(itemView)
                        .load(data.getProfile_image())
                        .fitCenter()
                        .placeholder(R.drawable.user_diff)
                        .into(img);*/
            }

        }

        class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }

        public ArrayList<UserList> getList() {
            return arr_user;
        }

        public void updateAllData(boolean toAdd) {
            arr_user.clear();
            if (toAdd) {
                arr_user.addAll(horizontalList);
            }
            notifyDataSetChanged();
            updateUI();
        }

        private void updateUI() {
            if (arr_user.containsAll(horizontalList)) {
                activity.isCheckFromParent = true;
                //se.setChecked(true);
            } else {
                activity.isCheckFromParent = false;
                //selectallUser.setChecked(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.done) {
            addTeamAPI(arr_userid, userid, teamid);
            // finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
