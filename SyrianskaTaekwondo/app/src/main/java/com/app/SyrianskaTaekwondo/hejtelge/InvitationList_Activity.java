package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.InvitationList_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InvitationList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import vk.help.MasterActivity;

public class InvitationList_Activity extends MasterActivity implements SearchView.OnQueryTextListener {
    private RecyclerView list;
    private  ArrayList<InvitationList> arr = new ArrayList<>();
    private  ArrayList<InvitationList> list1 = new ArrayList<>();
    private String userid, teamid;
    private AppCompatTextView user_record;
    InvitationList_Adapter mAdapter;
    private CommonMethods cmn = new CommonMethods();

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
        setContentView(R.layout.activity_invitation_list_);
        Locale loc = new Locale("sv","SE");
        Locale.setDefault(loc);

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Inbjudningslista");
        loadID();
        userid = new CommonMethods().getPrefsData(InvitationList_Activity.this, "id", "");
      //  teamid = new CommonMethods().getPrefsData(InvitationList_Activity.this, "team_id", "");

        if (cmn.isOnline(InvitationList_Activity.this)) {
            getUserAPI();
        } else {
            showToast(getResources().getString(R.string.internet));
        }

    }

    public void loadID() {
        list = findViewById(R.id.list_user);
        user_record = findViewById(R.id.user_record);

        mAdapter = new InvitationList_Adapter(arr, list,InvitationList_Activity.this);
        list.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            mAdapter.notifyItemInserted(arr.size() - 1);
            list.postDelayed(() -> {
                int end = arr.size() + 20;
                getRecursionAPI(String.valueOf(end));
            }, 1000);
        });
    }

    private void getUserAPI() {
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );

        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "200");
            object.put("offset", "0");
            object.put("user_id", userid);
            object.put("team_id", "");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(InvitationList_Activity.this, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject oo=obj.getJSONObject(i);
                        if(oo.getString("a_status").equals("Inte accepterad"))
                        arr.add((InvitationList) (getObject(obj.getString(i), InvitationList.class)));
                    }
                    if (obj.length() > 0) {
                        mAdapter.setLoaded();

                    }
                    if (arr.size() == 0) {
                        user_record.setVisibility(View.VISIBLE);
                    } else {
                        user_record.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "invitation_list");
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
                        arr.add((InvitationList) (getObject(obj.getString(i), InvitationList.class)));
                    }
                    if (obj.length() > 0) {
                        mAdapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "invitation_list");
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);

        return true;
    }
}
