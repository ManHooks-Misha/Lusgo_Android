package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InboxList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.IsReadUserArr;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityReadUserMessageBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class ReadUser_Message extends MasterActivity implements SwipeRefreshLayout.OnRefreshListener {
    List<IsReadUserArr> arr_readusers;
    List<InboxList> arr = new ArrayList<>();
    String id;
    ActivityReadUserMessageBinding binding;
    Read_show_user adapterteam;
    private ImageView imgBack;
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

        binding = ActivityReadUserMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
       // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle("Läst av");

        imgBack = findViewById(R.id.imgBack);
        if (getIntent().hasExtra(DATA)) {
            arr_readusers = (List<IsReadUserArr>) getObject(getIntent().getByteArrayExtra(DATA));
            id = getIntent().getStringExtra("id");
        }
        getUserAPI(id);
     /*   List<IsReadUserArr> arr_readuser = new ArrayList<>();
        arr_readuser.addAll(arr_readusers);
        arr_readusers.clear();

        for (int a = 0; a < arr_readuser.size(); a++) {
            if (arr_readuser.get(a).getIsRead().equals("1")) {
                IsReadUserArr read = arr_readuser.get(a);
                arr_readusers.add(read);
            }
        }
        for (int a = 0; a < arr_readuser.size(); a++) {
            if (arr_readuser.get(a).getIsRead().equals("0")) {
                IsReadUserArr read = arr_readuser.get(a);
                arr_readusers.add(read);
            }
        }*/
//        adapterteam = new Read_show_user(arr_readusers, context);
//        binding.listReadusers.setAdapter(adapterteam);
        //binding.swipe.setOnRefreshListener(this);
        imgBack.setOnClickListener(view -> {
            finish();
        });
        binding.swipe.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getUserAPI(id);
    }

    public class Read_show_user extends BaseAdapter {

        private List<IsReadUserArr> dataSet;
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            private AppCompatTextView txtName, group_line;
            private AvatarView img_user;
            private AppCompatImageView iv_selected;

        }

        public Read_show_user(List<IsReadUserArr> data, Context context) {
            super();
            this.dataSet = data;
            this.mContext = context;

        }


        @Override
        public int getCount() {
            return dataSet.size();
        }

        @Override
        public Object getItem(int i) {
            return dataSet.get(i);
        }

        @Override
        public long getItemId(int i) {
            return dataSet.size();
        }

        @NotNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            // Get the data item for this position
            IsReadUserArr dataModel = dataSet.get(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(mContext);

                convertView = inflater.inflate(R.layout.group_user, parent, false);
                viewHolder.txtName = convertView.findViewById(R.id.group_name);
                viewHolder.group_line = convertView.findViewById(R.id.group_line);
                viewHolder.img_user = convertView.findViewById(R.id.pos);
                viewHolder.iv_selected = convertView.findViewById(R.id.iv_selected);

                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;
            if (dataModel.getIsRead().equals("1")) {
                viewHolder.group_line.setText("Läst");
                viewHolder.group_line.setTextColor(Color.GREEN);
                viewHolder.iv_selected.setImageResource(R.drawable.right);
                //convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                viewHolder.group_line.setText("Inte läst");
                viewHolder.group_line.setTextColor(Color.RED);
                viewHolder.iv_selected.setImageResource(R.drawable.pending);

                //    convertView.setBackgroundColor(Color.parseColor("#e8f6fe"));

            }


            if (dataModel.getFirst_name() != null) {
                if (dataModel.getFirst_name().length() > 0) {
                    viewHolder.txtName.setText(dataModel.getFirst_name());
                }
            } else if (dataModel.getUsername() != null) {

                if (dataModel.getUsername().length() > 0) {
                    {
                        viewHolder.txtName.setText(dataModel.getUsername());

                    }
                }
            }
            if (dataModel.getFirst_name()!=null) {
                if (dataModel.getFirst_name().trim().length() > 0) {
                    String name = String.valueOf((dataModel.getFirst_name().trim().charAt(0)));

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(dataModel.getProfile_image())
                            .fitCenter()
                            .placeholder(drawable)
                            .into(viewHolder.img_user);
                }
            }
            if (dataModel.getFirst_name()!=null) {

                if (dataModel.getFirst_name().trim().length() > 0) {
                    String name = String.valueOf((dataModel.getFirst_name().trim().charAt(0)));

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(dataModel.getProfile_image())
                            .fitCenter()
                            .placeholder(drawable)
                            .into(viewHolder.img_user);
                }
            }
            viewHolder.txtName.setOnClickListener(view -> {
                String id = dataModel.getId();

                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            });
            viewHolder.img_user.setOnClickListener(view -> {
                String id = dataModel.getId();

                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            });
//            Glide.with(context)
//                    .load(dataModel.getProfile_image())
//                    .placeholder(R.drawable.user_diff)
//                    .fitCenter()
//                    .into(viewHolder.img_user);

            // Return the completed view to render on screen
            return convertView;
        }
    }


    private void getUserAPI(String id) {
        arr.clear();
        String requestData;
        arr_readusers.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", new CommonMethods().getPrefsData(context, "id", ""));
            object.put("message_id", id);
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
                        arr.add((InboxList) (getObject(obj.getString(i), InboxList.class)));
                    }
                    runOnUiThread(() -> {
                        if (arr.size() > 0) {
                            binding.swipe.setRefreshing(false);
                            List<IsReadUserArr> arr_readuser = arr.get(0).getRead_users();

                            for (int a = 0; a < arr_readuser.size(); a++) {
                                if (arr_readuser.get(a).getIsRead().equals("1")) {
                                    IsReadUserArr read = arr_readuser.get(a);
                                    arr_readusers.add(read);
                                }
                            }
                            for (int a = 0; a < arr_readuser.size(); a++) {
                                if (arr_readuser.get(a).getIsRead().equals("0")) {
                                    IsReadUserArr read = arr_readuser.get(a);
                                    arr_readusers.add(read);
                                }
                            }
                            adapterteam = new Read_show_user(arr_readusers, context);
                            binding.listReadusers.setAdapter(adapterteam);
                            adapterteam.notifyDataSetChanged();
                           // binding.swipe.setRefreshing(false);
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
             //   binding.swipe.setRefreshing(false);
                // mAdapter.setLoaded();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "messageInfo");
    }
}
