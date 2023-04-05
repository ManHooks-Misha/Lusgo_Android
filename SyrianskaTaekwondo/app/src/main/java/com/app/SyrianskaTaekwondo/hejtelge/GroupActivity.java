package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class GroupActivity extends MasterActivity implements SearchView.OnQueryTextListener {
    private FloatingActionButton add;
    private RecyclerView list, usergroup;
    private AppCompatTextView user_record;
    private String userid, value_flag, role, teamid, teamname;
    private Handler handler;
    private ArrayList<UserList> arr_user1 = new ArrayList<>();
    public static ArrayList<UserList> arr_user_participate = new ArrayList<>();
    private User_event_Adapter mAdapter;
    private UserAdapter user;
    private ArrayList<UserList> filtedlist;
    private SearchView searchView;

    private CommonMethods cmn = new CommonMethods();
    private AppCompatTextView save, cancel;
    private AlertDialog alertDialog;
    public boolean isCheckFromParentUser = true;

    private User_event_Adapter.FriendFilters friendFilter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.next) {
            if (value_flag.equals("1")) {
                if (arr_user_participate.size() > 0) {

                    startActivity(new Intent(context, CreateGroup.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                } else {
                    cmn.showAlert("Välj minst en deltagare",this);
                   // Toast.makeText(context, "Välj minst en deltagare", Toast.LENGTH_SHORT).show();
                }
            } else {

                startActivity(new Intent(context, AddTeam.class).putExtra("flag", ""));
                //  finish();


                // ll_group.setVisibility(View.VISIBLE);

            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_list, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.next); // extract the menu item here

//        String title = mColorFullMenuBtn.getTitle().toString();
//        SpannableString s = new SpannableString(title);
//        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
//        mColorFullMenuBtn.setTitle(s);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Sök");
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Användare");
        loadID();
        add.setBackgroundColor(Color.parseColor("#ffffff"));
        userid = cmn.getPrefsData(context, "id", "");
        role = cmn.getPrefsData(context, "usertype", "");
        teamid = cmn.getPrefsData(context, "team_id", "");
        teamname = cmn.getPrefsData(context, "team_name", "");
        handler = new Handler();
        getUserAPI();
        arr_user_participate.clear();
        if (getIntent() != null) {
            value_flag = getIntent().getStringExtra("flag");
        }
        if (arr_user_participate.size() == 0) {
            usergroup.setVisibility(View.GONE);
        }
        user = new UserAdapter(arr_user_participate, GroupActivity.this);
        usergroup.setLayoutManager(new LinearLayoutManager(GroupActivity.this, LinearLayoutManager.HORIZONTAL, false));
        usergroup.setAdapter(user);
        if (value_flag.equals("1")) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("    Skapa grupp");

        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("    Skapa team");
        }

        /*
         */
/*

        list.addOnItemTouchListener(new RecyclerTouchListener(context,
                list, (view, position) -> {

            UserList item = arr_user1.get(position);
            if (value_flag.equals("2")) {

                if (item.getTeam_id().length() == 0) {
                    if (!arr_user_participate.contains(item)) {
                        usergroup.setVisibility(View.VISIBLE);

                        arr_user_participate.add(item);
                        user.notifyDataSetChanged();
                    } else {
                        showToast("Användaren finns redan");
                    }


                } else {
                    Toast.makeText(context, "Användare har redan lagts till i teamet", Toast.LENGTH_SHORT).show();
                }
            } else {

                if (!arr_user_participate.contains(item)) {
                    usergroup.setVisibility(View.VISIBLE);

                    arr_user_participate.add(item);
                    user.notifyDataSetChanged();
                } else {
                    showToast("Användaren finns redan");
                }

//                if (arr_user_participate.size() > 0) {
//                    for (UserList arr : arr_user_participate) {
//                            if (item.equals(arr)) {
//                                showToast("Användaren finns redan");
//                            } else {
//                                arr_user_participate.add(item);
//                                user.notifyDataSetChanged();
//                            }
//                    }
//                } else {
//                    arr_user_participate.add(item);
//                    user.notifyDataSetChanged();
//
//                }
            }
            //                        iv_selected.setVisibility(View.VISIBLE);
        }));

*/

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
            object.put("limit", 1000);
            object.put("offset", 0);
            object.put("user_id", userid);
            object.put("block_users", "0");

            object.put("team_id", teamid);


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
                                arr_user1.add((UserList) (getObject(obj.getString(i), UserList.class)));

                            }

                        }
                        if (obj.length() > 0) {
                            mAdapter.setLoaded();
                        }

                        if (arr_user1.size() == 0) {
                            user_record.setVisibility(View.VISIBLE);
                            usergroup.setVisibility(View.GONE);
                            list.setVisibility(View.GONE);

                        } else {
                            user_record.setVisibility(View.GONE);
                            // usergroup.setVisibility(View.VISIBLE);
                            list.setVisibility(View.VISIBLE);

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (arr_user1.size() == 0) {
                    showCustomDialog(teamid, teamname);
                }
                mAdapter.notifyDataSetChanged();
                //  mAdapter.setLoaded();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
    }

    public void loadID() {
        add = findViewById(R.id.add);
        usergroup = findViewById(R.id.user_group);
        list = findViewById(R.id.list_group);
        user_record = findViewById(R.id.user_record);

        mAdapter = new User_event_Adapter(arr_user1, list, GroupActivity.this);
        list.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(() -> {
            arr_user1.add(null);
            mAdapter.notifyItemInserted(arr_user1.size() - 1);
            handler.postDelayed(() -> {
                int end = arr_user1.size() + 20;
                getRecursionAPI((end));
            }, 2000);
        });


    }

    private void getRecursionAPI(int offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", offset);
            object.put("user_id", userid);
            object.put("block_users", "0");

            if (role.equals(ConsURL.sub_coach)) {
                object.put("team_id", teamid);

            }
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                int position = arr_user1.size() - 1;
                arr_user1.remove(position);
                mAdapter.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONObject objarr = new JSONObject(result.getData());
                    JSONArray obj = objarr.getJSONArray("userList");

                    for (int i = 0; i < obj.length(); i++) {

                        arr_user1.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }

                    if (obj.length() > 0) {
                        mAdapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//              for(UserList user:arr_user) {
//                  if (userid.equals(user.getId())) {
//                    arr_user.remove(user);
//                  }
//              }
                //  mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
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


    public interface ClickListener {
        //    void onClick(View view,int position);
        void onLongClick(View view, int position);
    }

    private void showCustomDialog(String teamid, String teamname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Du har ingen användare. Lägg till användare");
        //setting the view of the builder to our custom view that we already inflated
        // builder.setView(dialogView);
        builder.setPositiveButton("Lägg till användare", (dialogInterface, i) -> {
            context.startActivity(new Intent(context, InviteActivity.class).putExtra("Teamid", teamid).putExtra("name", teamname));
            finish();
        });

        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {

            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private class User_event_Adapter extends RecyclerView.Adapter implements Filterable {
        private final int VIEW_ITEM = 1;
        private ArrayList<UserList> horizontalList;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        public GroupActivity activity;

        private User_event_Adapter(ArrayList<UserList> students, RecyclerView recyclerView, GroupActivity activity) {
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
                iv_selected.setVisibility(arr_user_participate.contains(data) ? View.VISIBLE : View.GONE);

                itemView.setOnClickListener(view -> {

                    searchView.setQuery("", false);
                    cmn.hideKeyboard((Activity) context);
                    // UserList item = arr_user1.get(position);
                    if (value_flag.equals("2")) {

                        // if (data.getTeam_id().length() == 0) {
                        if (!arr_user_participate.contains(data)) {
                            usergroup.setVisibility(View.VISIBLE);

                            arr_user_participate.add(data);
                            user.notifyDataSetChanged();
                            searchView.setQuery("", false);
                            cmn.hideKeyboard((Activity) context);
                        } else {
                            arr_user_participate.remove(data);
                            user.notifyDataSetChanged();
                            searchView.setQuery("", false);
                            cmn.hideKeyboard((Activity) context);

                        }



                    } else {

                        if (!arr_user_participate.contains(data)) {
                            usergroup.setVisibility(View.VISIBLE);

                            arr_user_participate.add(data);
                            user.notifyDataSetChanged();
                            searchView.setQuery("", false);
                            cmn.hideKeyboard((Activity) context);
                        } else {
                            arr_user_participate.remove(data);
                            user.notifyDataSetChanged();
                            searchView.setQuery("", false);
                            cmn.hideKeyboard((Activity) context);

                        }

                    }

                    notifyItemChanged(getAdapterPosition());
                    searchView.setQuery("", false);
                    updateUI();
                });

                if(data.getFirstname().length()>0) {
                    txtview.setText(data.getFirstname());

                    String name = String.valueOf(data.getFirstname().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(itemView)
                            .load(data.getProfile_image())
                            .fitCenter()
                            .placeholder(drawable)
                            .into(img);
                }else{
                    txtview.setText(data.getUsername());

                    String name = String.valueOf(data.getUsername().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(itemView)
                            .load(data.getProfile_image())
                            .fitCenter()
                            .placeholder(drawable)
                            .into(img);
                }

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
            return arr_user_participate;
        }

        public void updateAllData(boolean toAdd) {
            arr_user_participate.clear();
            if (toAdd) {
                arr_user_participate.addAll(horizontalList);
            }
            notifyDataSetChanged();
            updateUI();
        }

        private void updateUI() {
            if (arr_user_participate.containsAll(horizontalList)) {
                activity.isCheckFromParentUser = true;
                //se.setChecked(true);
            } else {
                activity.isCheckFromParentUser = false;
                //selectallUser.setChecked(false);
            }
        }
    }


    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


        private ArrayList<UserList> horizontalList;
        private Context context;


        public UserAdapter(ArrayList<UserList> horizontalList, Context context) {
            this.horizontalList = horizontalList;
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
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_user, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (horizontalList.get(position).getFirstname().length() > 0) {
                holder.txtview.setText(horizontalList.get(position).getFirstname() + " " + horizontalList.get(position).getLastname());
                String name = String.valueOf(horizontalList.get(position).getFirstname().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(horizontalList.get(position).getProfile_image())
                        .fitCenter()
                        .placeholder(drawable)
                        .into(holder.imageView);
            } else {
                holder.txtview.setText(horizontalList.get(position).getUsername());
                String name = String.valueOf(horizontalList.get(position).getUsername().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(horizontalList.get(position).getProfile_image())
                        .fitCenter()
                        .placeholder(drawable)
                        .into(holder.imageView);
            }

//            Glide.with(context)
//                    .load(horizontalList.get(position).getProfile_image())
//                    .fitCenter()
//                    .placeholder(R.drawable.user_diff)
//                    .into(holder.imageView);
            holder.cancel.setOnClickListener(view -> {
                GroupActivity.arr_user_participate.remove(position);
                mAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
            });
//        int i=position+1;
//
//        holder.pos.setText(""+i);
      /*  holder.ll_menu.setOnClickListener(v -> {


            if (position == 1) {
                Intent i = new Intent(context, GroupActivity.class);
                context.startActivity(i);
            }
        });
*/
        }


        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }


}
