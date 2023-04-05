package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.DataAdapter;
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

public class UserListActivity extends MasterActivity implements SearchView.OnQueryTextListener {
    private RecyclerView list;
    private AppCompatTextView user_record,users,publicuser;
    private ArrayList<UserList> arr = new ArrayList<>();
    private String userid, teamid,role;
    private DataAdapter mAdapter;
    LinearLayout llpublic;
    CommonMethods cmn = new CommonMethods();
    HashMap<String,String> header=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Användare");
        loadID();
        userid = cmn.getPrefsData(context, "id", "");
        role = cmn.getPrefsData(context, "usertype", "");
        teamid = cmn.getPrefsData(context, "team_id", "");

        if (cmn.isOnline(context)) {
            getUserAPI();
        } else {
            showToast(getResources().getString(R.string.internet));
        }
    }


    public void loadID() {
        list = findViewById(R.id.list_user);
        user_record = findViewById(R.id.user_record);
        users = findViewById(R.id.users);
        publicuser = findViewById(R.id.publics);
        llpublic = findViewById(R.id.ll_public);
        mAdapter = new DataAdapter(arr, list, this);
        list.setAdapter(mAdapter);
//        if (mAdapter.getItemCount() > 0){
//            list.setAdapter(mAdapter);
//        }
        mAdapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            mAdapter.notifyItemInserted(arr.size() - 1);
            list.postDelayed(() -> {
                int end = arr.size();
                getRecursionAPI(end);
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
        searchView.setOnQueryTextListener(this);

        return true;
    }


    private void getUserAPI() {
        arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 500);
            object.put("offset", 0);
            object.put("user_id", userid);
            object.put("block_users", "1");
            object.put("team_id", "");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                    if (result.isStatus()) {
                        arr.clear();
                    JSONObject objarr = new JSONObject(result.getData());
                    if (objarr.length() > 0) {
                            String count = objarr.getString("un_registered_count");
                            String totalcount = objarr.getString("total_count");
                            JSONArray obj = objarr.getJSONArray("userList");
                            for (int i = 0; i < obj.length(); i++) {
                                arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                            }
                            if (obj.length() > 0) {
                                mAdapter.setLoaded();
                            }
                        if (arr.size() == 0) {
                            user_record.setVisibility(View.VISIBLE);
                        } else {
                            //Objects.requireNonNull(getSupportActionBar()).setTitle(arr.size()+" Användare");
                            users.setText(totalcount + " Användare");
                            if (role.equals(ConsURL.admin) || role.equals(ConsURL.sub_admin)) {
                                publicuser.setText(count + " Offentliga användare");
                                publicuser.setVisibility(View.VISIBLE);
                                users.setVisibility(View.VISIBLE);
                                llpublic.setVisibility(View.VISIBLE);
                            } else {
                                llpublic.setVisibility(View.GONE);

                                publicuser.setVisibility(View.GONE);
                                users.setVisibility(View.GONE);
                                Objects.requireNonNull(getSupportActionBar()).setTitle(totalcount + " Användare");
                            }
                            user_record.setVisibility(View.GONE);
                        }
                    }else{
                        user_record.setVisibility(View.VISIBLE);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getRecursionAPI(int offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", offset+10);
            object.put("offset", offset);
            object.put("user_id", userid);
            object.put("team_id", teamid);
            object.put("block_users", "1");

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
                    String count=objarr.getString("un-registered_count");
                    JSONArray obj=objarr.getJSONArray("userList");
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }
                  //Objects.requireNonNull(getSupportActionBar()).setTitle(arr.size()+" Användare");

                    if (obj.length() > 0) {
                        mAdapter.setLoaded();

                    }
                    if(role.equals(ConsURL.admin)||role.equals(ConsURL.sub_admin)) {
                        publicuser.setText(count + " Offentliga användare");
                        publicuser.setVisibility(View.VISIBLE);
                        users.setVisibility(View.VISIBLE);

                    }else{
                        publicuser.setVisibility(View.GONE);
                        users.setVisibility(View.GONE);
                        Objects.requireNonNull(getSupportActionBar()).setTitle(arr.size()+" Användare");
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText.trim());

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
