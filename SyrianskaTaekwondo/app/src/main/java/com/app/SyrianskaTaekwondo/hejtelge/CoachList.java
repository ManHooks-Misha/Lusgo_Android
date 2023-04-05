package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityCoachListBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;

import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import vk.help.MasterActivity;

public class CoachList extends MasterActivity implements SearchView.OnQueryTextListener{
    ActivityCoachListBinding binding;
    public  static ArrayList<UserList> arr_user_participate = new ArrayList<>();
    public  ArrayList<UserList> arr_user1 = new ArrayList<>();
    private User_event_Adapter.FriendFilters friendFilter;
    private ArrayList<UserList> filtedlist;
    private CommonMethods cmn;
    private String userid;
    private Handler handler;
    public boolean isCheckFromParentUser = true;

    private User_event_Adapter mAdapter;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.next) {


              //  startActivity(new Intent(context, AddTeam.class).putExtra("flag", ""));
                finish();


                // ll_group.setVisibility(View.VISIBLE);


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityCoachListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handler = new Handler();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tränare");
        cmn=new CommonMethods();
        userid = cmn.getPrefsData(context, "id", "");
        getUserAPI();
        mAdapter = new User_event_Adapter(arr_user1, binding.coachList, CoachList.this);
        binding.coachList.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(() -> {
            arr_user1.add(null);
            mAdapter.notifyItemInserted(arr_user1.size() - 1);
            handler.postDelayed(() -> {
                int end = arr_user1.size() + 20;
                getRecursionAPI(String.valueOf(end));
            }, 2000);
        });
    }

    private void getRecursionAPI(String offset) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", offset);
            object.put("user_id", userid);
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
                    JSONArray obj = new JSONArray(result.getData());
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
        }, requestData).execute(ConsURL.BASE_URL_TEST + "coachList");
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
            object.put("limit", "100");
            object.put("offset", "0");
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
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
                       // user_record.setVisibility(View.VISIBLE);
                      //  usergroup.setVisibility(View.GONE);
                        binding.coachList.setVisibility(View.GONE);

                    } else {
                        //user_record.setVisibility(View.GONE);
                        // usergroup.setVisibility(View.VISIBLE);
                        binding.coachList.setVisibility(View.VISIBLE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                mAdapter.notifyDataSetChanged();
                //  mAdapter.setLoaded();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "coachList");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_list, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.next); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        mColorFullMenuBtn.setTitle(s);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(this);

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

    private class User_event_Adapter extends RecyclerView.Adapter implements Filterable {
        private final int VIEW_ITEM = 1;
        private ArrayList<UserList> horizontalList;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        public CoachList activity;

        private User_event_Adapter(ArrayList<UserList> students, RecyclerView recyclerView, CoachList activity) {
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
                friendFilter = new User_event_Adapter.FriendFilters();
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
            private CircleImageView img;
            private TextView txtview;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);

                img = view.findViewById(R.id.iv_profile);
                iv_selected = view.findViewById(R.id.iv_selected);

            }

            void setData(UserList data) {
                txtview.setText(data.getFirstname());
                iv_selected.setVisibility(arr_user_participate.contains(data) ? View.VISIBLE : View.GONE);

                itemView.setOnClickListener(view -> {


                    // UserList item = arr_user1.get(position);

                        /*if (data.getTeam_id().length() == 0) {
                            if (!arr_user_participate.contains(data)) {
                                usergroup.setVisibility(View.VISIBLE);

                                arr_user_participate.add(data);
                                user.notifyDataSetChanged();
                            } else {
                                arr_user_participate.remove(data);
                                user.notifyDataSetChanged();

                                //  showToast("Användaren finns redan");
                            }


                        } else {
                            Toast.makeText(context, "Användare har redan lagts till i teamet", Toast.LENGTH_SHORT).show();
                        }*/

                        if (!arr_user_participate.contains(data)) {
                           // usergroup.setVisibility(View.VISIBLE);

                            arr_user_participate.add(data);
                       //     user.notifyDataSetChanged();
                        } else {
                            arr_user_participate.remove(data);
                         //   user.notifyDataSetChanged();

                            // showToast("Användaren finns redan");
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

                   /* if (arr_userlist.contains(data)) {
                        arr_userlist.remove(data);
                    } else {
                        arr_userlist.add(data);
                    }*/
                    notifyItemChanged(getAdapterPosition());
                    updateUI();
                });
                Glide.with(itemView)
                        .load(data.getProfile_image())
                        .centerCrop()
                        .placeholder(R.drawable.user_diff)
                        .into(img);
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
}
