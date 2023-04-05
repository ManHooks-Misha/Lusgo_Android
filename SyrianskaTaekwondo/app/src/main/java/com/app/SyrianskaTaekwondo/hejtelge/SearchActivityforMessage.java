package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivitySearchActivityforMessageBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Team_search;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.utils.ProgressViewHolder;
import com.bumptech.glide.Glide;
import com.mipl.autoimageslider.IndicatorView.draw.drawer.Drawer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class SearchActivityforMessage extends MasterActivity implements SearchView.OnQueryTextListener  {
    ActivitySearchActivityforMessageBinding binding;
    String userid, role, teamid;
    //    private ArrayList<UserList> selectedUserListSearchSearch = new ArrayList<>();
    //    private ArrayList<UserList> selectedUserListSearchSearch = new ArrayList<>();
    private UserListAdapter userListAdapter;
    private UserAdapter user;
    SearchView searchView;
    CommonMethods cmn = new CommonMethods();
    private Boolean loadMoreForUser = true;
    private UserListAdapter.FriendFilters friendFilters;
    private static final int VIEW_ITEM = 1, VIEW_PROGRESS = 0, LIMIT = 20;
    public static boolean isSearch = false;
    public ArrayList<UserList> userArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchActivityforMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //  getSupportActionBar().setTitle("Sök användare");
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        binding.txt.setOnClickListener(v -> {
//            binding.search.setVisibility(View.VISIBLE);
//            binding.txt.setVisibility(View.GONE);
//        });
        user = new UserAdapter(CreateMessage.selectedUserListSearch, SearchActivityforMessage.this);
        binding.userSelected.setLayoutManager(new LinearLayoutManager(SearchActivityforMessage.this, LinearLayoutManager.HORIZONTAL, false));
        binding.userSelected.setAdapter(user);

        role = cmn.getPrefsData(context, "usertype", "");
        userid = cmn.getPrefsData(context, "id", "");
        teamid = cmn.getPrefsData(context, "team_id", "");
        getNameAPI(teamid);

        if (cmn.isOnline(context)) {

            userListAdapter = new UserListAdapter(userArrayList);
            binding.searchList.setAdapter(userListAdapter);

        } else {
            showToast(getResources().getString(R.string.internet));
        }

        binding.selectAllUser.setOnClickListener(view -> {
            if (role.equals("3")) {
                if (binding.selectAllUser.isChecked()) {
                    for (UserList temp : userArrayList) {
                        if (temp != null) {
                            if (!CreateMessage.selectedUserListSearch.contains(temp)) {
                                CreateMessage.selectedUserListSearch.add(temp);
                                user.notifyDataSetChanged();
                            }
                        }
                    }
                } else {

                    CreateMessage.selectedUserListSearch.clear();
                    user.notifyDataSetChanged();

                }
                userListAdapter.notifyDataSetChanged();
                updateUI();
            } else {
                if (binding.selectAllUser.isChecked()) {// Click change checked states already
                    for (UserList temp : userArrayList) {
                        if (!CreateMessage.selectedUserListSearch.contains(temp)) {
                            CreateMessage.selectedUserListSearch.add(temp);
                            // user.notifyDataSetChanged();

                        }
                    }
                } else {
                    CreateMessage.selectedUserListSearch.clear();
                    // user.notifyDataSetChanged();

                }
                userListAdapter.notifyDataSetChanged();
                user.notifyDataSetChanged();

//                teamListAdapter.notifyDataSetChanged();
                updateUI();
            }
        });

    }

    private void updateUI() {
        if (role.equals("3")) {
            ArrayList<UserList> tempList = new ArrayList<>();
            for (UserList userList : userArrayList) {
                if (userList != null) {
                    if (!tempList.contains(userList)) {
                        tempList.add(userList);
                    }
                }
            }
            Collections.sort(tempList);
            Collections.sort(CreateMessage.selectedUserListSearch);
            binding.selectAllUser.setChecked(tempList.equals(CreateMessage.selectedUserListSearch));
        } else if (role.equals("2") || role.equals("5")) {
            //     ArrayList<UserList> tempList = new ArrayList<>();
           /* for (Teamlist tempTeam : teamArrayList) {
                if (tempTeam != null) {
                    for (UserList userList : tempTeam.getUsers()) {
                        if (!tempList.contains(userList)) {
                            tempList.add(userList);
                        }
                    }
                }
            }*/
            // binding.llmessageuser.setVisibility(userArrayList.isEmpty() ? View.GONE : View.VISIBLE);
            binding.selectAllUser.setChecked(CreateMessage.selectedUserListSearch.containsAll(userArrayList));
//            binding.selectAllTeam.setChecked(selectedUserListSearch.containsAll(tempList));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_user, menu);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.done) {
            isSearch = true;
            finish();
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*   @Override
       public boolean onQueryTextSubmit(String query) {
           return false;
       }

       @Override
       public boolean onQueryTextChange(String newText) {

       }
   */
    private void getNameAPI(String teamid) {
        userArrayList.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("name", "");
            object.put("team_id", teamid);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {

                if (result.isStatus()) {
                    userArrayList.clear();
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject objuser = obj.getJSONObject(i);
                        UserList users = new UserList();
                        users.setFirstname(objuser.getString("first_name"));
                        users.setLastname(objuser.getString("last_name"));
                        users.setEmail(objuser.getString("email"));
                        users.setId(objuser.getString("id"));
                        users.setUsername(objuser.getString("username"));
                        users.setProfile_image(objuser.getString("profile_image"));
                        users.setRole(objuser.getString("role"));
                        users.setTeam_name(objuser.getString("team_name"));
                        users.setTeam_id(objuser.getString("team_id"));
                        userArrayList.add(users);
                    }
                  /*  if (binding.selectAllUser.isChecked()) {
                        for (UserList list : userArrayList) {
                            if (!selectedUserListSearch.contains(list)) {
                                selectedUserListSearch.add(list);
//                                updateSelectedUserText();
                            }
                        }
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //  userListAdapter.setLoaded();
                //  binding.llmessageuser.setVisibility(View.VISIBLE);
                userListAdapter.notifyDataSetChanged();
                binding.searchList.invalidate();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "search_userByname");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        userListAdapter.getFilter().filter(newText);
        return true;
    }

    private class UserListAdapter extends RecyclerView.Adapter implements Filterable {
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        ArrayList<UserList> filtedlist;

        private UserListAdapter(ArrayList<UserList> filtedlist) {
            this.filtedlist = filtedlist;


          /*  if (binding.searchList.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.searchList.getLayoutManager();
                binding.searchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (loadMoreForUser) {
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    }
                });
            }*/
        }

        @Override
        public Filter getFilter() {
            if (friendFilters == null) {
                friendFilters = new UserListAdapter.FriendFilters();
            }

            return friendFilters;
        }

        class FriendFilters extends Filter {

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
                userArrayList = (ArrayList<UserList>) results.values;
                if (userArrayList == null) {
                    userArrayList = new ArrayList<>();
                }

                notifyDataSetChanged();

            }
        }

        @Override
        public int getItemViewType(int position) {
            return userArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new UserListAdapter.UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participate_user, parent, false));
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserListAdapter.UserViewHolder) {
                ((UserListAdapter.UserViewHolder) holder).setData(userArrayList.get(position));
            } else {
                ((ProgressViewHolder) holder).setData();
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return userArrayList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        private class UserViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView iv_selected;
            private AvatarView img;
            private TextView txtview, tv_teamName,tv_teamName1;

            UserViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);
                tv_teamName = view.findViewById(R.id.team_name);
                tv_teamName1 = view.findViewById(R.id.tv_name1);
                img = view.findViewById(R.id.iv_profile);
                iv_selected = view.findViewById(R.id.iv_selected);
            }

            void setData(UserList data) {
                tv_teamName.setVisibility(View.GONE);
                if (role.equals("2") || role.equals("5")) {

                    tv_teamName.setVisibility(View.VISIBLE);

                        tv_teamName.setText(data.getRole());


                }
                if (data.getFirstname().length() > 0) {
                    txtview.setText(data.getFirstname().trim());
                } else {
                    txtview.setText(data.getUsername().trim());

                }if (data.getTeam_name().length() > 0) {
                    tv_teamName1.setVisibility(View.VISIBLE);

                    tv_teamName1.setText("( "+data.getTeam_name()+" )");
                } else {
                    tv_teamName1.setVisibility(View.GONE);

                }
                iv_selected.setVisibility(CreateMessage.selectedUserListSearch.contains(data) ? View.VISIBLE : View.GONE);

                if (data.getFirstname().length() > 0) {
                    String name = String.valueOf(data.getFirstname().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(itemView).load(data.getProfile_image()).fitCenter().placeholder(drawable).into(img);
                }
                itemView.setOnClickListener(view -> {
                    searchView.setQuery("", false);
                    cmn.hideKeyboard((Activity) context);
                    if (CreateMessage.selectedUserListSearch.contains(data)) {
                        CreateMessage.selectedUserListSearch.remove(data);
                        user.notifyDataSetChanged();

                    } else {
                        //  binding.selectAll.setChecked(false);
                        CreateMessage.selectedUserListSearch.add(data);
                        user.notifyDataSetChanged();

                    }
//                    updateSelectedUserText();
                    if (role.equals("2") || role.equals("5")) {
                        // teamListAdapter.notifyDataSetChanged();
                    }
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> userListAdapter.notifyItemChanged(userArrayList.indexOf(data)), 100);
                    userListAdapter.notifyItemChanged(userArrayList.indexOf(data));
                    updateUI();
                });


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
        public UserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_user, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final UserAdapter.MyViewHolder holder, final int position) {
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
                CreateMessage.selectedUserListSearch.remove(position);
                userListAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
                updateUI();
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