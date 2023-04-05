package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.GroupListAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import vk.help.MasterActivity;

public class GroupListActivity extends MasterActivity implements SearchView.OnQueryTextListener {
    RecyclerView list;
    private AppCompatTextView ll_norecord;
    String userid, teamid;
    public ArrayList<GroupList> arr = new ArrayList<>();
    private FloatingActionButton btn_add;
    private GroupListAdapter mAdapter;
    ProgressBar progress_dialog;
    private CommonMethods cmn;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list2);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle("Grupplista");
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(GroupListActivity.this, "id", "");
        teamid = cmn.getPrefsData(GroupListActivity.this, "team_id", "");

        loadID();
        getUserAPI();

        btn_add.setOnClickListener(view -> {
            Intent i = new Intent(GroupListActivity.this, GroupActivity.class);
            i.putExtra("flag", "1");
            startActivity(i);
            finish();
        });
    }

    public void loadID() {
        list = findViewById(R.id.list_group);
        ll_norecord = findViewById(R.id.ll_norecord);
        progress_dialog = findViewById(R.id.progress_dialog);

        btn_add = findViewById(R.id.btn_add);

        mAdapter = new GroupListAdapter(arr, list, ll_norecord, userid, GroupListActivity.this);
        list.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            mAdapter.notifyItemInserted(arr.size() - 1);
            list.postDelayed(() -> {
                int end = arr.size() + 20;
                getRecursionAPI((end));
            }, 2000);
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
      //  searchView.setQueryHint("Sök");
        searchView.setOnQueryTextListener(this);

        return true;
    }


    private void getUserAPI() {
        progress_dialog.setVisibility(View.VISIBLE);
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
            object.put("team_id", teamid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    progress_dialog.setVisibility(View.GONE);
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((GroupList) (getObject(obj.getString(i), GroupList.class)));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                progress_dialog.setVisibility(View.GONE);
            } finally {
                if (arr.size() > 0) {
                    mAdapter.setLoaded();
                    mAdapter.notifyDataSetChanged();
                    ll_norecord.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                } else {
                    ll_norecord.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);

                }
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupList");
    }

    private void getRecursionAPI(int offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 1000);
            object.put("offset", offset);
            object.put("team_id", teamid);

            object.put("user_id", userid);
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
                JSONArray obj = new JSONArray(result.getData());
                for (int i = 0; i < obj.length(); i++) {
                    arr.add((GroupList) (getObject(obj.getString(i), UserList.class)));
                }

                if (obj.length() > 0) {
                    mAdapter.setLoaded();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //mAdapter.setLoaded();
            mAdapter.notifyDataSetChanged();
        }
        return null;
    }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupList");
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
}
