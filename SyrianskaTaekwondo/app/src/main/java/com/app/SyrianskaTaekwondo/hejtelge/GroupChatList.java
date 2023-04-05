package com.app.SyrianskaTaekwondo.hejtelge;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.GroupChatListAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.Progressbar;
import com.app.SyrianskaTaekwondo.hejtelge.database.ChatPush;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import vk.help.MasterActivity;

public class GroupChatList extends MasterActivity implements SearchView.OnQueryTextListener {
    RecyclerView list;
    private AppCompatTextView ll_norecord;
    ProgressBar progresBar;
    String userid, teamid, usertype;
    public ArrayList<GroupList> arr = new ArrayList<>();
    private GroupChatListAdapter mAdapter;
    private CommonMethods cmn;
    public ChatPush chatTable;
    Progressbar progressbar;
    ArrayList<HashMap<String, String>> arr_badge = new ArrayList<>();
    public static final String CHAT_NOTIFY_BELL = "CHAT_NOTIFY_BELL";
    //public static final String CHAT_FLOAT_NOTIFY_BELL = "CHAT_FLOAT_NOTIFY_BELL";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
           // startActivity(new Intent(this,HomePage.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver notificationReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // sendBroadcast(new Intent("VK.NEW.NOTIFICATION"));
            arr_badge.clear();
            arr_badge = chatTable.getDataNotificationData();

//            mAdapter = new GroupChatListAdapter(arr, list, ll_norecord, userid, GroupChatList.this, usertype, arr_badge);
//            list.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
            mAdapter = new GroupChatListAdapter(arr, list, ll_norecord, userid, GroupChatList.this, usertype, arr_badge);
            list.setAdapter(mAdapter);
            //mAdapter.setLoaded();
            mAdapter.notifyDataSetChanged();
            getUserAPI();

            mAdapter.setOnLoadMoreListener(() -> {
                list.postDelayed(() -> {
                  //Toast.makeText(context, "call1", Toast.LENGTH_SHORT).show();
                    int end = arr.size();
                    int limit = arr.size() + 10;
//                    arr.add(null);
//                    mAdapter.notifyItemInserted(arr.size() - 1);
                    getRecursionAPI(end,limit);
                }, 2000);
            });
        }
    };

    @Override
    protected void onResume() {

        super.onResume();
//        arr_badge.clear();
//        arr_badge = chatTable.getDataNotificationData();
//        mAdapter = new GroupChatListAdapter(arr, list, ll_norecord, userid, GroupChatList.this, usertype, arr_badge);
//        list.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
        registerReceiver(notificationReceiver1, new IntentFilter(CHAT_NOTIFY_BELL));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationReceiver1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_list);
        chatTable = new ChatPush(this).getInstance(this);
        progressbar=new Progressbar();

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("Grupplista");
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(GroupChatList.this, "id", "");
        usertype = cmn.getPrefsData(GroupChatList.this, "usertype", "");
        teamid = cmn.getPrefsData(GroupChatList.this, "team_id", "");
        arr_badge = chatTable.getDataNotificationData();
        loadID();
        mAdapter = new GroupChatListAdapter(arr, list, ll_norecord, userid, GroupChatList.this, usertype, arr_badge);
        list.setAdapter(mAdapter);
        //mAdapter.setLoaded();
        mAdapter.notifyDataSetChanged();
        getUserAPI();
        mAdapter.setOnLoadMoreListener(() -> {

            list.postDelayed(() -> {
                int end = arr.size();
                int limit = arr.size() + 10;
//                arr.add(null);
//                mAdapter.notifyItemInserted(arr.size() - 1);
                getRecursionAPI(end,limit);
            }, 2000);
        });
/*
        btn_add.setOnClickListener(view -> {
            Intent i = new Intent(GroupChatList.this, GroupActivity.class);
            i.putExtra("flag", "1");
            startActivity(i);
            finish();
        });*/
    }

    public void loadID() {
        list = findViewById(R.id.list_group);
        ll_norecord = findViewById(R.id.ll_norecord);
        progresBar = findViewById(R.id.progresBar);


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

//    public int numDifferentiation(int val) {
//        if(val >= 10000000) val = (val/10000000).Math.round(2) + " Cr";
//        else if(val >= 100000) val = (val/100000).toFixed(2) + " Lac";
//        else if(val >= 1000) val = (val/1000).toFixed(2) + " K";
//        return val;
//    }
    private void getUserAPI() {
       // ProgressDialog mprogdialog = ProgressDialog.show(this, "", "Vänta", true);
      //  mprogdialog.setCancelable(false);
        //arr.clear();
        progresBar.setVisibility(View.VISIBLE);
        String requestData;
        arr.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 1000);
            object.put("offset", 0);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    progresBar.setVisibility(View.GONE);
                    arr.clear();
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((GroupList) (getObject(obj.getString(i), GroupList.class)));
                    }
                    if (obj.length() > 0) {

                        //mAdapter.setLoaded();
                        mAdapter.notifyDataSetChanged();
                        ll_norecord.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);

                    } else {
                        ll_norecord.setVisibility(View.VISIBLE);
                        list.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupListMember");
    }

    private void getRecursionAPI(int offset,int limit) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", limit);
            object.put("offset", offset);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
//                int position = arr.size() - 1;
//                arr.remove(position);
//                mAdapter.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((GroupList) (getObject(obj.getString(i), GroupList.class)));
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
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupListMember");
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
