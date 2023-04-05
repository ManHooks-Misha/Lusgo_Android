package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.SponserListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class UserTeamList_member extends RecyclerView.Adapter<UserTeamList_member.MyViewHolder> {


    private List<HashMap<String, String>> horizontalList;
    ArrayList<Integer> list_pos = new ArrayList<>();
    private Activity context;
    private String userid;

    public UserTeamList_member(List<HashMap<String, String>> horizontalList, Activity context) {
        this.horizontalList = horizontalList;
        CommonMethods cmn = new CommonMethods();
        this.context = context;
        userid = cmn.getPrefsData(context, "id", "");

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_menu;
        TextView txtview, link;
        AppCompatTextView time_txt;
        AvatarView img;
        private CheckBox check;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            img = view.findViewById(R.id.pos);
            check = view.findViewById(R.id.check);
        }
    }


    @NotNull
    @Override
    public UserTeamList_member.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_userteam, parent, false);

        return new MyViewHolder(itemView);
    }

    public List<HashMap<String, String>> getList() {
        return horizontalList;
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {

        String name = horizontalList.get(position).get("name");
        String image = horizontalList.get(position).get("logo");
        String check = horizontalList.get(position).get("checked");
        assert check != null;

        if (check.equals("true")) {
            holder.check.setChecked(true);
        } else {
            holder.check.setChecked(false);

        }
     /*   String created_at = horizontalList.get(position).get("created_at");
        String[] time_arr = Objects.requireNonNull(created_at).split(" ");
        created_at = time_arr[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long now = System.currentTimeMillis() - 1000;

        long result = TimeUnit.DAYS.convert((now - date.getTime()), TimeUnit.MILLISECONDS);

        updateLabel(holder.time_txt, result);*/
        holder.txtview.setText(name);

        if (horizontalList.get(position).get("name").length() > 0) {
            String namea = String.valueOf(horizontalList.get(position).get("name").trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(context)
                    .load(image)
                    .fitCenter()
                    .placeholder(drawable)
                    .into(holder.img);
        }


        holder.check.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {

                HashMap i = horizontalList.get(position);
                i.put("checked", "true");
                //DataAdapter.img.add(i);
            } else {
                HashMap i = horizontalList.get(position);
                i.put("checked", "false");
               /* String map1 = horizontalList.get(position).get("id");
                for (int i = 0; i < AddCampaign.img.size(); i++) {
                    String id = AddCampaign.img.get(i).get("id");
                    if (map1.equals(id)) {
                        list_pos.add(i);
                    }

                }
                for (int i : list_pos) {
                    AddCampaign.img.remove(i);
                    list_pos.clear();
                }*/
            }

        });
    }

    private void updateLabel(AppCompatTextView txt, long result) {


        // int result = now.compareTo(dd);
        if (result == 0) {
            txt.setText("Idag");

        } else if (result == 1) {
            txt.setText("Igår");

        } else {

            txt.setText(result + " dagar sedan");

        }

    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public void showAlert(String message, Context context, String sid) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage(Html.fromHtml(message)).setCancelable(false)
                    .setPositiveButton("Ja", (dialog, id) -> {

                        getDeleteAPI(sid);
                    });
            builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            try {
                builder.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void getDeleteAPI(String id) {
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

                    context.startActivity(new Intent(context, SponserListActivity.class));
                    context.finish();
//                    JSONArray obj = new JSONArray(result.getData());
//                    for (int i = 0; i < obj.length(); i++) {
                    //arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    //   }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                mAdapter.setLoaded();
//                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_Sponser");
    }
}

