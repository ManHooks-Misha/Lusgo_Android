package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.Addteammember_adapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import vk.help.MasterActivity;

public class AddTeam_member extends MasterActivity {
    RecyclerView list;
    private AppCompatTextView ll_norecord,txt;
    String userid, usertype,u_id, coachid,role_id,flag;
    public ArrayList<Teamlist> arr = new ArrayList<>();
    private FloatingActionButton btn_add;
    private Addteammember_adapter mAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
           // startActivity(new Intent(AddTeam_member.this,HomePage.class).putExtra("flag","menu"));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team__list);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        CommonMethods cmn = new CommonMethods();
        userid = cmn.getPrefsData(AddTeam_member.this, "id", "");
        usertype = cmn.getPrefsData(AddTeam_member.this, "usertype", "");

        loadID();

       // txt.setText("Lägg till medlem i teamet");
        // sk chamges
        // txt.setText("Lägg till medlem i teamet");
        txt.setText("Välj ett lag");
        if (getIntent() != null) {
            coachid = getIntent().getStringExtra("coachid");
            u_id = getIntent().getStringExtra("userid");
            role_id = getIntent().getStringExtra("roleid");
            flag = getIntent().getStringExtra("flag");
        }

        if (usertype.equals("2")) {
            btn_add.setVisibility(View.GONE);
            if (cmn.isOnline(AddTeam_member.this)) {
                getUserAPI(coachid);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

            mAdapter = new Addteammember_adapter(arr, list, ll_norecord, userid, AddTeam_member.this,coachid,u_id,role_id,flag);
            list.setAdapter(mAdapter);
            mAdapter.setOnLoadMoreListener(() -> {
                arr.add(null);
                mAdapter.notifyItemInserted(arr.size() - 1);
                list.postDelayed(() -> {
                    int end = arr.size() + 20;
                    getRecursionAPI((end),coachid);
                }, 2000);
            });

        } else {
            if (cmn.isOnline(AddTeam_member.this)) {
                getUserAPI(userid);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
            btn_add.setVisibility(View.VISIBLE);
            mAdapter = new Addteammember_adapter(arr, list, ll_norecord, userid, AddTeam_member.this,coachid,u_id,role_id,flag);
            list.setAdapter(mAdapter);
            mAdapter.setOnLoadMoreListener(() -> {
                arr.add(null);
                mAdapter.notifyItemInserted(arr.size() - 1);
                list.postDelayed(() -> {
                    int end = arr.size() + 20;
                    getRecursionAPI(end,userid);
                }, 2000);
            });

        }
        btn_add.setOnClickListener(view -> {
            Intent i = new Intent(AddTeam_member.this, GroupActivity.class);
            i.putExtra("flag", "2");
            startActivity(i);
        });
    }

    public void loadID() {
        list = findViewById(R.id.list_group);
        ll_norecord = findViewById(R.id.ll_norecord);
        btn_add = findViewById(R.id.btn_add);
        txt = findViewById(R.id.txt);
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

    private void getRecursionAPI(int offset,String coachid) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", offset);
            object.put("user_id", userid);
            object.put("coach_id", coachid);
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
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "teamList");
    }

    private void getUserAPI(String coach) {
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", 0);
            object.put("user_id", userid);
            object.put("coach_id", coach);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(AddTeam_member.this, result -> {
            try {
                if (result.isStatus()) {

                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((Teamlist) (getObject(obj.getString(i), Teamlist.class)));
                    }
                    if(obj.length()>0){
                        mAdapter.setLoaded();

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (arr.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                    ll_norecord.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                } else {
                    ll_norecord.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);

                }
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "teamList");
    }


}
