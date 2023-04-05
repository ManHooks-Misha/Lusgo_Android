package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.InvitationList_Activity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InvitationList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Invite;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import agency.tango.android.avatarview.views.AvatarView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;
import vk.help.views.TextDrawable;


public class InvitationList_Adapter extends RecyclerView.Adapter implements Filterable {
    private final int VIEW_ITEM = 1;
    private String timediff = "", status, msg;

    private ArrayList<InvitationList> horizontalList;
    private ArrayList<InvitationList> filtedlist;
    private FriendFilters friendFilter;

    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Activity con;
    private AlertDialog alertDialog;
    String userid, team_id, roll, username;
    ArrayList<String> arr_email = new ArrayList<>();
    ArrayList<String> arr_phone = new ArrayList<>();

    public InvitationList_Adapter(ArrayList<InvitationList> students, RecyclerView recyclerView, Activity con) {
        horizontalList = students;
        filtedlist = students;
        userid = new CommonMethods().getPrefsData(con, "id", "");
        //  team_id = new CommonMethods().getPrefsData(con, "team_id", "");

        this.con = con;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.invitation_list, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            ((StudentViewHolder) holder).setData(horizontalList.get(position));
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView edit;
        private AvatarView img;
        private TextView txtview, role, email, mob, days, resend, delete;

        StudentViewHolder(View view) {
            super(view);


            txtview = view.findViewById(R.id.txt_name);
            role = view.findViewById(R.id.txt_role);
            days = view.findViewById(R.id.txt_days);
            mob = view.findViewById(R.id.txt_mob);
            img = view.findViewById(R.id.pos);
            resend = view.findViewById(R.id.resend);
            delete = view.findViewById(R.id.delete);

            edit = view.findViewById(R.id.img_edit);
        }

        private void setData(InvitationList data) {
            Locale loc = new Locale("sv","SE");
            Locale.setDefault(loc);

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                Date past = format.parse(data.getCreated());
                Date now = new Date();
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
                /*if (days < 9 && days > 1) {
                    timediff = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " Dagar sedan";
                } else if (days == 0) {
                    timediff = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " Timmar sedan";

                    long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                    if (hours == 0) {
                        long mins = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                        timediff = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minuter sedan";

                        if (mins == 0) {
                            timediff = "Precis nu";

                        }
                    }*/

              //  } else {
                timediff= new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(past);

//                    timediff = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
             //   }
                resend.setOnClickListener(view -> {
                    team_id = data.getTeam_id();
                    roll = (data.getRole_id());
                    username = data.getUsername();
                    getExitingUserAPI(team_id, username);
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uid = data.getInv_id();
                        AlertDelete("Vill du ta bort denna inbjudan", uid);
                    }
                });

            } catch (Exception j) {
                j.printStackTrace();
            }

            days.setText(timediff);
            txtview.setText(data.getUsername());
            if (data.getA_status().equals("Accepterad")) {
                role.setTextColor(Color.GREEN);
                role.setText(data.getA_status());
            } else {
                role.setText(data.getA_status());
                role.setTextColor(Color.RED);

            }
            mob.setText(data.getTelephone());

            if (data.getUsername().length() > 0) {
                String name =String.valueOf(data.getUsername().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(itemView)
                        .load(data.getProfile_image())
                        .placeholder(drawable)
                        .fitCenter()
                        .into(img);
            }

        }

    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilters();
        }

        return friendFilter;
    }

    private class FriendFilters extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<InvitationList> tempList = new ArrayList<>();

                // search content in friend list
                for (InvitationList user : filtedlist) {
                    if (user.getUsername().toLowerCase().contains(constraint.toString().toLowerCase()) || user.getA_status().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = filtedlist.size();
                filterResults.values = filtedlist;
            }

            return filterResults;
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            horizontalList = (ArrayList<InvitationList>) results.values;
            if (horizontalList == null) {
                horizontalList = new ArrayList<>();
            }
            notifyDataSetChanged();
        }
    }


    private void getExitingUserAPI(String teamid, String username) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("username", username);
            object.put("team_id", teamid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(con, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject obj = new JSONObject(result.getData());
                    String login_count = obj.getString("login_count");
                    String invite = obj.getString("exists");

                    if (login_count.equals("0")) {
                        switch (invite) {
                            case "0":
                                InviteRequest(userid, team_id, roll, username, arr_email, arr_phone);
                                break;
                            case "1":
                                Alert("Du har redan skickat inbjudan till detta nummer / e-post. Vill du skicka det igen? ");
                                break;
                            case "2":
                                Alert("Du har redan skickat inbjudan till detta nummer / e-post. Vill du skicka det igen? ");
                                break;
                            default:
                                new CommonMethods().showAlert("Du kan inte skicka inbjudan på detta nummer / e-post eftersom du har överskridit gränsen", con);
                                break;
                        }
                    } else {
                        Common.INSTANCE.showToast(con, "Den här användaren finns redan");
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "checkUser_Team");
    }

    private void deleteApi(String userid, String uid) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("u_id", uid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(con, result -> {
            try {
                if (result.isStatus()) {
                    Common.INSTANCE.showToast(con, "Inbjudan har tagits bort");
                    con.startActivity(new Intent(con, InvitationList_Activity.class));
                    con.finish();


                } else {
                    Common.INSTANCE.showToast(con, result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_invitation");
    }

    private void Alert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(con);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (new CommonMethods().isEmailValid(username)) {
                arr_email.add(username);
                InviteRequest(userid, team_id, roll, username, arr_email, arr_phone);

            } else {
                arr_phone.add(username);
                InviteRequest(userid, team_id, roll, username, arr_email, arr_phone);

            }
        });
        builder.setNegativeButton("NEJ", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }

    private void AlertDelete(String msg, String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(con);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            arr_email.add(username);
            deleteApi(userid, uid);


        });
        builder.setNegativeButton("NEJ", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }

    private void InviteRequest(String userid, String teamid, String role, String username, ArrayList<String> arr_email, ArrayList<String> arr_phone) {
        String tset;

        ProgressDialog mprogdialog = ProgressDialog.show(con, "", "Vänta", true);
        mprogdialog.setCancelable(false);
        //  email_txt = Objects.requireNonNull(binding.email.getText()).toString();
        if (new CommonMethods().isEmailValid(username)) {
            Gson gson = new Gson();
            Invite asgn = new Invite();
            asgn.email = arr_email;
            asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            asgn.user_id = userid;
            asgn.team_id = teamid;
            asgn.role = role;
            tset = gson.toJson(asgn);

        } else {
            Gson gson = new Gson();
            Invite asgn = new Invite();
            asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            asgn.user_id = userid;
            asgn.phone = arr_phone;
            asgn.team_id = teamid;
            asgn.role = role;
            tset = gson.toJson(asgn);
        }


        String url = ConsURL.BASE_URL_TEST + "invite_users";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(con, e.getMessage(), Toast.LENGTH_SHORT).show());

                mprogdialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    if (response.body() != null) {
                        String res = Objects.requireNonNull(response.body()).string();


                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");

                        msg = objvalue.optString("message");
                        //  JSONObject object = objvalue.getJSONObject("data");


                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {
                            new CommonMethods().customDialogMsg(con,"Inbjudan har skickats");
                        //    Common.INSTANCE.showToast(con, "Inbjudan har skickats");
                            //  isInvite = false;
                            //  Toast.makeText(InviteActivity.this, msg, Toast.LENGTH_SHORT).show();
                            //finish();

                        } else {
                            new CommonMethods().showAlert(msg, con);
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





