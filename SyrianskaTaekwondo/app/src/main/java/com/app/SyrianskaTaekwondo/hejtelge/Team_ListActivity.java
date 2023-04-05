package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.TeamListAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import vk.help.MasterActivity;

public class Team_ListActivity extends MasterActivity implements SearchView.OnQueryTextListener {
    RecyclerView list;
    private AppCompatTextView ll_norecord;
    String userid, usertype, coachid;
    public ArrayList<Teamlist> arr = new ArrayList<>();
    private FloatingActionButton btn_add;
    private TeamListAdapter mAdapter;
    private String searchvalue = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(Team_ListActivity.this, HomePage.class).putExtra("flag", "menu"));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team__list);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("Laglista");
        CommonMethods cmn = new CommonMethods();
        userid = cmn.getPrefsData(Team_ListActivity.this, "id", "");
        usertype = cmn.getPrefsData(Team_ListActivity.this, "usertype", "");

        loadID();
        if (getIntent() != null) {
            coachid = getIntent().getStringExtra("coachid");
        }

        if (usertype.equals("2") || usertype.equals("5")) {
            btn_add.setVisibility(View.GONE);
            if (cmn.isOnline(Team_ListActivity.this)) {
                getUserAPI("");
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

            mAdapter = new TeamListAdapter(list, ll_norecord, userid, Team_ListActivity.this);
            list.setAdapter(mAdapter);
            mAdapter.setOnLoadMoreListener(() -> {
                mAdapter.addItem();

                list.postDelayed(() -> {
                    int end = mAdapter.getItemCount() + 1;
                    getRecursionAPI(String.valueOf(end), "");
                }, 2000);
            });

        } else {
            if (cmn.isOnline(Team_ListActivity.this)) {
                getUserAPI(userid);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
            btn_add.setVisibility(View.VISIBLE);
            mAdapter = new TeamListAdapter(list, ll_norecord, userid, Team_ListActivity.this);
            list.setAdapter(mAdapter);
            mAdapter.setOnLoadMoreListener(() -> {
                mAdapter.addItem();
                list.postDelayed(() -> {
                    int end = mAdapter.getItemCount() + 1;
                    getRecursionAPI(String.valueOf(end), userid);
                }, 2000);
            });

        }
        btn_add.setOnClickListener(view -> {
            Intent i = new Intent(Team_ListActivity.this, GroupActivity.class);
            i.putExtra("flag", "2");
            startActivity(i);
        });
    }

    public void loadID() {
        list = findViewById(R.id.list_group);
        ll_norecord = findViewById(R.id.ll_norecord);
        btn_add = findViewById(R.id.btn_add);
       /* mAdapter = new TeamListAdapter(arr, list, ll_norecord, userid, Team_ListActivity.this);
        list.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            mAdapter.notifyItemInserted(arr.size() - 1);
            list.postDelayed(() -> {
                int end = arr.size() + 20;
                getRecursionAPI(String.valueOf(end));
            }, 2000);
        });*/

    }

    private void getRecursionAPI(String offset, String coachid) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", Integer.parseInt(offset));
            object.put("user_id", userid);
            object.put("coach_id", coachid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {

                mAdapter.removeLastData();
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((Teamlist) (getObject(obj.getString(i), Teamlist.class)));
                    }

                    if (obj.length() > 0) {
                        mAdapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //mAdapter.setLoaded();
                mAdapter.setUpdatedData(arr, searchvalue);
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "teamList");
    }

    private void getUserAPI(String coach) {
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
            object.put("coach_id", coach);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(Team_ListActivity.this, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((Teamlist) (getObject(obj.getString(i), Teamlist.class)));
                    }
                    if (obj.length() > 0) {
                        mAdapter.setLoaded();

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (arr.size() > 0) {

                    mAdapter.setUpdatedData(arr);
                    ll_norecord.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                } else {
                    ll_norecord.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);

                }
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "teamList");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchvalue = newText;
        mAdapter.getFilter().filter(newText.trim());
        return false;
    }
}
