package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.ChooseSponserAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ListNews;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ChooseSponser extends AppCompatActivity {
    RecyclerView list;
    private AppCompatImageView ll_norecord;
    private String email, status, msg;
    private CommonMethods cmn = new CommonMethods();
    private List<HashMap<String, String>> arr = new ArrayList<>();
    private FloatingActionButton btn_add;
    private LinearLayout ll_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sponser);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        email = cmn.getPrefsData(ChooseSponser.this, "email", "");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle("Välj sponsorer");
        loadID();
        if (cmn.isOnline(ChooseSponser.this)) {
            getSponserAPI();
        } else {
            cmn.showAlert(getResources().getString(R.string.internet), ChooseSponser.this);
        }
        btn_add.setOnClickListener(view -> {
            if (AddCampaign.img.size() == 0) {
                Toast.makeText(this, "Välj någon sponsor", Toast.LENGTH_SHORT).show();
            } else {
                finish();

            }
        });
    }

    public void loadID() {
        list = findViewById(R.id.list_sponsers);
        ll_norecord = findViewById(R.id.ll_norecord);
        btn_add = findViewById(R.id.btn_add);
        ll_add = findViewById(R.id.ll_add);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private void getSponserAPI() {
        arr.clear();

        ProgressDialog mprogdialog = ProgressDialog.show(ChooseSponser.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);


        Gson gson = new Gson();
        ListNews asgn = new ListNews();
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.limit = "100";
        asgn.offset = "0";


        String tset = gson.toJson(asgn);
        System.out.println("Sent Data" + tset);
        String url = ConsURL.BASE_URL_TEST + "Sponsers";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(ChooseSponser.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                mprogdialog.dismiss();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.body() != null) {
                        res = Objects.requireNonNull(response.body()).string();


                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");
                        msg = objvalue.optString("message");
                        JSONArray obj = objvalue.getJSONArray("data");
                        for (int i = 0; i < obj.length(); i++) {

                            JSONObject object = obj.getJSONObject(i);
                            HashMap map = new HashMap();
                            String id = object.getString("id");
                            String name = object.getString("name");
                            String image = object.getString("image");
                            String link = object.getString("link");
                            String created_by = object.getString("created_by");
                            String created_at = object.getString("created_at");
                            map.put("id", id);
                            map.put("name", name);
                            map.put("image", image);
                            map.put("created_by", created_by);
                            map.put("created_at", created_at);
                            map.put("link", link);
                            map.put("checked", "false");
                            arr.add(map);
                            // responseText = "Visit saved";
                        }

                    } else {
                        mprogdialog.dismiss();
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true") && arr.size() > 0) {
                            if (AddCampaign.img.size() > 0) {
                                for (HashMap map2 : AddCampaign.img) {
                                    String id = Objects.requireNonNull(map2.get("id")).toString();

                                    for (int i = 0; i < arr.size(); i++) {
                                        String ids = Objects.requireNonNull(arr.get(i).get("id")).toString();
                                        HashMap map = arr.get(i);
                                        if (ids.equals(id)) {
                                            arr.remove(i);
                                            map.put("checked", "true");
                                            arr.add(i, map);
                                        }/*else{
                                            arr.remove(i);
                                            map.put("checked", "false");
                                            arr.add(i,map);

                                        }*/
                                    }

                                }

                            }
                            mprogdialog.dismiss();
                            ll_norecord.setVisibility(View.GONE);


                            ChooseSponserAdapter libraryHotAdapter = new ChooseSponserAdapter(
                                    arr, ChooseSponser.this, email, ll_add);
                            list.setLayoutManager(new GridLayoutManager(ChooseSponser.this, 1));
                            list.setAdapter(libraryHotAdapter);

                        } else {

                            mprogdialog.dismiss();
                            if (arr.size() == 0) {
                                ll_norecord.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }


}
