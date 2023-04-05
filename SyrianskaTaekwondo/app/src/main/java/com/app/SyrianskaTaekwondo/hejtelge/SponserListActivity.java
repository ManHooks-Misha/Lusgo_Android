package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.grid.BaseDynamicGridAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.grid.DynamicGridView;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ListNews;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.sponserpojo;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.MasterActivity;

public class SponserListActivity extends MasterActivity implements PopupMenu.OnMenuItemClickListener {
    DynamicGridView list;
    private AppCompatTextView ll_norecord, datatext;
    private String email, status, msg;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    private CommonMethods cmn = new CommonMethods();
    private List<HashMap<String, String>> arr = new ArrayList<>();
    private FloatingActionButton btn_add;
    private LinearLayout ll_add;
    String usertype, userid;
    ProgressBar progress_dialog;
    SponserDynamicAdapter libraryHotAdapter;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponser_list);
        email = cmn.getPrefsData(SponserListActivity.this, "email", "");
        usertype = cmn.getPrefsData(SponserListActivity.this, "usertype", "");
        userid = cmn.getPrefsData(SponserListActivity.this, "id", "");
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //    getSupportActionBar().setTitle("Sponsorslista");
        loadID();
        if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
            datatext.setVisibility(View.VISIBLE);
        } else {
            datatext.setVisibility(View.GONE);

        }


     /*   list.setLayoutManager(new GridLayoutManager(getContext(), 2));
        SponserAdapter_drag listAdapter = new SponserAdapter_drag(images_exterior1, R.layout.gallery_adapter, R.id.item_layout, true, getActivity(),1);
        list.setAdapter(listAdapter, true);
        list.setCanDragHorizontally(true);
        list.setCustomDragItem(null);
*/
        if (usertype.equals("2") || usertype.equals("5")) {
           /* list.setOnItemLongClickListener((adapterView, view, i, l) -> {
                registerForContextMenu(list);
                position = i;
                return false;
            });*/

            list.setOnDragListener(new DynamicGridView.OnDragListener() {
                @Override
                public void onDragStarted(int position) {


                }

                @Override
                public void onDragPositionsChanged(int oldPosition, int newPosition) {
                    list.stopEditMode();

                }


            });
            list.setOnItemLongClickListener((parent, view, position, id) -> {
                list.startEditMode(position);
                return true;
            });

        }

        //getSponserAPI();

        btn_add.setOnClickListener(view -> {
            startActivity(new Intent(SponserListActivity.this, CreateSponsers.class));
            finish();
        });


    }

    public void showAlert(String message, Context context, String sid, int pos) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage(Html.fromHtml(message)).setCancelable(false)
                    .setPositiveButton("Ja", (dialog, id) -> {

                        getDeleteAPI(sid, pos);
                    });
            builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {

            });
            try {
                builder.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void getDeleteAPI(String id, int position) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("sponser_id", id);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    Toast.makeText(context, "Sponsor Radera framgångsrikt", Toast.LENGTH_SHORT).show();
                    arr.remove(position);
                    libraryHotAdapter = new SponserDynamicAdapter(SponserListActivity.this,
                            arr, 2);
                    //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                    list.setAdapter(libraryHotAdapter);
                    libraryHotAdapter.notifyDataSetChanged();
//                    JSONArray obj = new JSONArray(result.getData());
//                    for (int i = 0; i < obj.length(); i++) {
                    //arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    //   }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_Sponser");
    }

    public void getAddAPI(ArrayList<HashMap<String, String>> id) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        Gson gson = new Gson();

        sponserpojo pojo = new sponserpojo();
        pojo.access_key = ConsURL.accessKey;
        pojo.sponsers = id;
        pojo.user_id = userid;
        requestData = gson.toJson(pojo);

        Log.e("JSON", requestData);

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "sponser_orders");
    }


   /* @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                // add stuff here
                String id = arr.get(position).get("id");
                showAlert("Vill du ta bort denna sponsor", context, id);
                return true;
            case R.id.edit:
                // edit stuff here
                String id1 = arr.get(position).get("id");
                String name = arr.get(position).get("name");
                String image = arr.get(position).get("image");
                String link = arr.get(position).get("link");

                Intent i = new Intent(SponserListActivity.this, EditSponser.class);
                i.putExtra("image", image);
                i.putExtra("name", name);
                i.putExtra("link", link);
                i.putExtra("id", id1);
                startActivity(i);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(userid.length()>0) {
                startActivity(new Intent(SponserListActivity.this, HomePage.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }else{
                startActivity(new Intent(SponserListActivity.this, Page_withOut_Login.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
            return true;
        }
      /* if(item.getItemId()==R.id.edit){
            String id = arr.get(position).get("id");
            String name = arr.get(position).get("name");
            String image = arr.get(position).get("image");
            String link = arr.get(position).get("link");

            Intent i = new Intent(SponserListActivity.this, EditSponser.class);
            i.putExtra("image", image);
            i.putExtra("name", name);
            i.putExtra("link", link);
            i.putExtra("id", id);
            startActivity(i);
        }
        if(item.getItemId()==R.id.delete){
            String id = arr.get(position).get("id");
            showAlert("Vill du ta bort denna sponsor", context, id);
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(userid.length()>0) {
            startActivity(new Intent(SponserListActivity.this, HomePage.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }else{
            startActivity(new Intent(SponserListActivity.this, Page_withOut_Login.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    public void loadID() {
        list = findViewById(R.id.list_sponsers);
        ll_norecord = findViewById(R.id.ll_norecord);
        btn_add = findViewById(R.id.btn_add);
        ll_add = findViewById(R.id.ll_add);
        progress_dialog = findViewById(R.id.progress_dialog);
        datatext = findViewById(R.id.datatxt);


    }

    @Override
    protected void onResume() {
        super.onResume();
        arr.clear();
        getSponserAPI();

    }

    private void getSponserAPI() {
        arr.clear();

//        ProgressDialog mprogdialog = ProgressDialog.show(SponserListActivity.this, "", "Vänta", true);
//        mprogdialog.setCancelable(false);
//
        progress_dialog.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        ListNews asgn = new ListNews();
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.limit = "500";
        asgn.offset = "0";
        String tset = gson.toJson(asgn);
        System.out.println("Sent Data" + tset);
        String url = ConsURL.BASE_URL_TEST + "Sponsers";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(SponserListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                //mprogdialog.dismiss();
                progress_dialog.setVisibility(View.GONE);

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

                            arr.add(map);
                            // responseText = "Visit saved";
                        }

                    } else {
                        progress_dialog.setVisibility(View.GONE);
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progress_dialog.setVisibility(View.GONE);


                        if (status.equals("true") && arr.size() > 0) {
                            progress_dialog.setVisibility(View.GONE);
                            ll_norecord.setVisibility(View.GONE);
                            ll_add.setVisibility(View.GONE);
                            libraryHotAdapter = new SponserDynamicAdapter(SponserListActivity.this,
                                    arr, 2);
                            //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                            list.setAdapter(libraryHotAdapter);
                            libraryHotAdapter.notifyDataSetChanged();

                        } else {

                            progress_dialog.setVisibility(View.GONE);
                            if (arr.size() == 0) {

                                if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                                    btn_add.setVisibility(View.VISIBLE);
                                    ll_add.setVisibility(View.VISIBLE);

                                } else {
                                    btn_add.setVisibility(View.GONE);
                                    ll_add.setVisibility(View.GONE);

                                }
                                ll_norecord.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (Exception e) {
                    progress_dialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }


            }
        });


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pmnuDelete:
                String id = arr.get(position).get("id");
                showAlert("Vill du ta bort denna sponsor", context, id, position);
                break;
            case R.id.pmnuEdit:

                String id1 = arr.get(position).get("id");
                String name = arr.get(position).get("name");
                String image = arr.get(position).get("image");
                String link = arr.get(position).get("link");

                Intent i = new Intent(SponserListActivity.this, EditSponser.class);
                i.putExtra("image", image);
                i.putExtra("name", name);
                i.putExtra("link", link);
                i.putExtra("id", id1);
                startActivity(i);
                break;

        }

        return false;
    }

    public class SponserDynamicAdapter extends BaseDynamicGridAdapter {

        public SponserDynamicAdapter(SponserListActivity context, List<HashMap<String, String>> items, int columnCount) {
            super(context, items, columnCount);

        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            CheeseViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sponserlist_adapter, null);
                holder = new CheeseViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CheeseViewHolder) convertView.getTag();
            }
            holder.build(getItems().get(pos), pos);
            return convertView;
        }

        private class CheeseViewHolder {
            private ImageView image;
            private ImageView menu;

            private CheeseViewHolder(View view) {
                //titleText = (TextView) view.findViewById(R.id.item_title);
                image = view.findViewById(R.id.pos);
                menu = view.findViewById(R.id.img_menu);
            }

            void build(HashMap title, int pos) {
                // titleText.setText(title);
                String images = Objects.requireNonNull(title.get("image")).toString();
                Glide.with(getContext())
                        .load(images)
                        .centerCrop()
                        .into(image);
                image.setOnClickListener(view -> {
                    if (title.get("link").toString().contains("http")) {
                        Uri uri = Uri.parse(title.get("link").toString()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    } else {
                        Uri uri = Uri.parse("http://" + title.get("link")); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }

                });
                if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                    menu.setVisibility(View.VISIBLE);
                } else {
                    menu.setVisibility(View.GONE);

                }
                menu.setOnClickListener(v -> {
                    showPopup(v);
                    position = pos;
                });


            }
        }
    }


    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(SponserListActivity.this, view);
        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.sponser, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }
}
