package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.AddCampaign;
import com.app.SyrianskaTaekwondo.hejtelge.CreateSponsers;
import com.app.SyrianskaTaekwondo.hejtelge.DataPolicy;
import com.app.SyrianskaTaekwondo.hejtelge.EditCampaign;
import com.app.SyrianskaTaekwondo.hejtelge.GroupListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.HomePage;
import com.app.SyrianskaTaekwondo.hejtelge.InvitationList_Activity;
import com.app.SyrianskaTaekwondo.hejtelge.InviteActivity;
import com.app.SyrianskaTaekwondo.hejtelge.LoginActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.Report_ListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.Team_ListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.Update_info;
import com.app.SyrianskaTaekwondo.hejtelge.UserListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CampaignPojo;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Menu_model;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;

public class Menu_adapter extends RecyclerView.Adapter<Menu_adapter.MyViewHolder> {


    private List<Menu_model> horizontalList;
    private ArrayList<HashMap<String, String>> arr = new ArrayList<>();
    private ArrayList<Teamlist> arr_team = new ArrayList<>();
    private ArrayList<CampaignPojo> arr_campaign = new ArrayList<>();
    private String usertype, userid, coachid, status, deviceID;
    private Activity context;
    private CommonMethods cmn;
    private AlertDialog alertDialog;
    private Show_Team_Adapter adapter;
    private Team_list_alertbox adapter_team;

    public Menu_adapter(List<Menu_model> horizontalList, Activity context, String usertype) {
        this.horizontalList = horizontalList;
        this.context = context;
        cmn = new CommonMethods();
        deviceID = Common.INSTANCE.getString("Device_id");
        this.usertype = usertype;
        userid = cmn.getPrefsData(context, "id", "");
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_menu;
        ImageView imageView;
        TextView txtview;

        MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            txtview = view.findViewById(R.id.tv);
            ll_menu = view.findViewById(R.id.ll_menu);

        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.imageView.setImageResource(horizontalList.get(position).imageId);
        holder.txtview.setText(horizontalList.get(position).txt);
        holder.ll_menu.setOnClickListener(v -> {

            if (usertype.equals(ConsURL.admin)) {
                if (position == 0) {
                    Intent i = new Intent(context, Update_info.class);
                    i.putExtra("Flag", "about");
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 1) {
                    Intent i = new Intent(context, InviteActivity.class);
                    i.putExtra("Teamid", "");

                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 2) {

                    Intent i = new Intent(context, Team_ListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    // showCustomDialog(v);
                /*    Intent i = new Intent(context, Team_ListActivity.class);
                    context.startActivity(i);*/
                    //  showCustomDialog(v);

                }
                if (position == 3) {
                    Intent i = new Intent(context, UserListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 4) {
                    Intent i = new Intent(context, CreateSponsers.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } /*  if(position==4){
                Intent i = new Intent(context, SponserListActivity.class);
                context.startActivity(i);
            }*/
                if (position == 5) {
                    Intent i = new Intent(context, GroupListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
             /*   if (position == 6) {
                    Intent i = new Intent(context, GroupChatList.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }*/
                if (position == 6) {
                    Intent i = new Intent(context, InvitationList_Activity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                if (position == 7) {
                    getCampaignAPI();

                }
                if (position == 8) {
                    Intent i = new Intent(context, Report_ListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                /*if (position == 8) {
                    Intent i = new Intent(context, CampaignListActivity.class);
                    context.startActivity(i);
                }*/
              /*  if (position == 8) {
                    Intent i = new Intent(context, ResetPassword.class);
                    context.startActivity(i);
                }*/
            /*}if(position==9){
                Intent i = new Intent(context, GroupActivity.class);
                context.startActivity(i);
            }*/
                if (position == 9) {
                    Intent i = new Intent(context, DataPolicy.class);
                    i.putExtra("data_policy","0");
                    context.startActivity(i);
                }
                if (position == 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Är du säker på att du vill logga ut?");
                    builder.setPositiveButton("Ja", (dialogInterface, i) -> {

                        logout(userid);

                        alertDialog.dismiss();

                    });
                    builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
                    alertDialog = builder.create();
                    alertDialog.show();

                }
            }
            if (usertype.equals(ConsURL.sub_admin)) {
                if (position == 0) {
                    Intent i = new Intent(context, Update_info.class);
                    i.putExtra("Flag", "about");
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//                    Intent i = new Intent(context, Add_info_NEw.class);
//
//                    context.startActivity(i);
//                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 1) {
                    Intent i = new Intent(context, InviteActivity.class);
                    i.putExtra("Teamid", "");

                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 2) {
                    Intent i = new Intent(context, Team_ListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    // showCustomDialog(v);
                /*    Intent i = new Intent(context, Team_ListActivity.class);
                    context.startActivity(i);*/
                    //  showCustomDialog(v);

                }
                if (position == 3) {
                    Intent i = new Intent(context, UserListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 4) {
                    Intent i = new Intent(context, CreateSponsers.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } /*  if(position==4){
                Intent i = new Intent(context, SponserListActivity.class);
                context.startActivity(i);
            }*/
                if (position == 5) {
                    Intent i = new Intent(context, GroupListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
//                }if (position == 6) {
//                    Intent i = new Intent(context, GroupChatList.class);
//                    context.startActivity(i);
//                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//
//                }
                if (position == 6) {
                    Intent i = new Intent(context, InvitationList_Activity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 7) {

                    getCampaignAPI();

                }

                /*if (position == 8) {
                    Intent i = new Intent(context, CampaignListActivity.class);
                    context.startActivity(i);
                }*/
              /*  if (position == 8) {
                    Intent i = new Intent(context, ResetPassword.class);
                    context.startActivity(i);
                }*/
            /*}if(position==9){
                Intent i = new Intent(context, GroupActivity.class);
                context.startActivity(i);
            }*/
                if (position == 8) {
                    Intent i = new Intent(context, Report_ListActivity.class);
                    context.startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                if (position == 9) {
                    Intent i = new Intent(context, DataPolicy.class);
                    i.putExtra("data_policy","0");
                    context.startActivity(i);
                }
                if (position == 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Är du säker på att du vill logga ut?");
                    builder.setPositiveButton("Ja", (dialogInterface, i) -> {

                        logout(userid);

                        alertDialog.dismiss();

                    });
                    builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
                    alertDialog = builder.create();
                    alertDialog.show();

                }
            }
            if (usertype.equals("4")) {
              /*  if (position == 0) {
                    Intent i = new Intent(context, GroupListActivity.class);
                    context.startActivity(i);
                }*/
//               if (position == 0) {
//                    Intent i = new Intent(context, ResetPassword.class);
//                    context.startActivity(i);
//                }
               /* if (position == 0) {
                    showCustomDialogTeam(v, "2");
                }*/

                if (position == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Är du säker på att du vill logga ut?");
                    builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                      /*  cmn.setPrefsData(context, "id", "");
                        cmn.setPrefsData(context, "city", "");
                        cmn.setPrefsData(context, "email", "");
                        cmn.setPrefsData(context, "firstname", "");
                        cmn.setPrefsData(context, "imagepath", "");
                        cmn.setPrefsData(context, "username", "");
                        cmn.setPrefsData(context, "lastname", "");
                        cmn.setPrefsData(context, "state", "");
                        cmn.setPrefsData(context, "city", "");
                        cmn.setPrefsData(context, "telephone", "");

                        cmn.setPrefsData(context, "usertype", "");
                        cmn.setPrefsData(context, "coach_name", "");
                        cmn.setPrefsData(context, "role", "");
                        cmn.setPrefsData(context, "imagepath", "");
                        cmn.setPrefsData(context, "banner", "");
                        cmn.setPrefsData(context, "is_loggedIn", "");
                        cmn.setPrefsData(context, "team_name", "");
                        cmn.setPrefsData(context, "team_id", "");
                        cmn.setPrefsData(context, "is_team", "false");
                        Common.INSTANCE.saveString("is_team", "false");
                        Common.INSTANCE.saveString("is_profile", "false");
                        Common.INSTANCE.saveString("is_reset", "false");

                        Intent in = new Intent(context, LoginActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        context.startActivity(in);
                        context.finish();*/
                        logout(userid);


                        alertDialog.dismiss();

                    });
                    builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
                    alertDialog = builder.create();
                    alertDialog.show();

                }
            }
            if (usertype.equals("3")) {

                if (position == 0) {
                    showCustomDialogTeamPick(v, "1");

                }
                if (position == 1) {
                    Intent i = new Intent(context, Team_ListActivity.class);
                    context.startActivity(i);
                }

                if (position == 2) {
                    Intent i = new Intent(context, InvitationList_Activity.class);
                    context.startActivity(i);
                }

                if (position == 3) {
                    Intent i = new Intent(context, GroupListActivity.class);
                    context.startActivity(i);
                }
//                } if (position == 4) {
//                    Intent i = new Intent(context, GroupChatList.class);
//                    context.startActivity(i);
//                }
                if (position == 4) {
                    Intent i = new Intent(context, UserListActivity.class);
                    context.startActivity(i);
                }

               /* if (position == 5) {
                    Intent i = new Intent(context, ResetPassword.class);
                    context.startActivity(i);
                }*/
              /*  if (position == 5) {

                    showCustomDialogTeam(v, "2");

                }*/

                if (position == 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Är du säker på att du vill logga ut?");
                    builder.setPositiveButton("Ja", (dialogInterface, i) -> {

                        logout(userid);


                    });
                    builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
                    alertDialog = builder.create();
                    alertDialog.show();

                }
            }
            if (usertype.equals(ConsURL.sub_coach)) {

              /*  if (position == 0) {
                    showCustomDialogTeam(v, "1");

                }
                if (position == 1) {
                    Intent i = new Intent(context, Team_ListActivity.class);
                    context.startActivity(i);
                }

                if (position == 2) {
                    Intent i = new Intent(context, InvitationList_Activity.class);
                    context.startActivity(i);
                }
*/
                if (position == 0) {
                    Intent i = new Intent(context, GroupListActivity.class);
                    context.startActivity(i);
                }
//                } if (position == 1) {
//                    Intent i = new Intent(context, GroupChatList.class);
//                    context.startActivity(i);
//                }


                if (position == 1) {
                    Intent i = new Intent(context, UserListActivity.class);
                    context.startActivity(i);
                }
               /* if (position == 2) {
                    showCustomDialogTeam(v, "2");
                }*/
               /* if (position == 5) {
                    Intent i = new Intent(context, ResetPassword.class);
                    context.startActivity(i);
                }*/
           /*     if (position == 5) {
                    showCustomDialogTeam(v, "2");

                }
*/
                if (position == 2) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Är du säker på att du vill logga ut?");
                    builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    /*    cmn.setPrefsData(context, "id", "");
                        cmn.setPrefsData(context, "city", "");
                        cmn.setPrefsData(context, "email", "");
                        cmn.setPrefsData(context, "firstname", "");
                        cmn.setPrefsData(context, "imagepath", "");
                        cmn.setPrefsData(context, "username", "");
                        cmn.setPrefsData(context, "lastname", "");
                        cmn.setPrefsData(context, "state", "");
                        cmn.setPrefsData(context, "city", "");
                        cmn.setPrefsData(context, "telephone", "");
                        cmn.setPrefsData(context, "usertype", "");
                        cmn.setPrefsData(context, "role", "");
                        cmn.setPrefsData(context, "coach_name", "");
                        cmn.setPrefsData(context, "imagepath", "");
                        cmn.setPrefsData(context, "banner", "");
                        cmn.setPrefsData(context, "is_loggedIn", "");
                        cmn.setPrefsData(context, "team_name", "");
                        cmn.setPrefsData(context, "team_id", "");
                        cmn.setPrefsData(context, "team_id_invite", "");
                        cmn.setPrefsData(context, "team_name_invite", "");
                        Common.INSTANCE.saveString("is_team", "false");
                        Common.INSTANCE.saveString("is_profile", "false");
                        Common.INSTANCE.saveString("is_reset", "false");

                        Intent in = new Intent(context, LoginActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        context.startActivity(in);
                        context.finish();*/

                        logout(userid);

                        alertDialog.dismiss();

                    });
                    builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
                    alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }


    private void showCustomDialogTeam(View item, String flag) {
        ViewGroup viewGroup = item.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.team_invite_show, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView txt = dialogView.findViewById(R.id.txt);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatEditText email_text = dialogView.findViewById(R.id.email_text);
        RecyclerView coach = dialogView.findViewById(R.id.list_coach);
        if (flag.equals("1")) {
            txt.setText("Välj till vilket team användaren ska bjudas till");
        } else {
            txt.setText("Välj det team du vill administrera");
        }
        email_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter_team.getFilter().filter(s);

            }
        });
        coach.setVisibility(View.VISIBLE);
        getTeamAPI(userid, email_text, coach);

        email_text.setOnClickListener(view -> {
            coach.setVisibility(View.VISIBLE);
        });
        adapter_team = new Team_list_alertbox(coach, userid, context, email_text, cmn.getPrefsData(context, "team_id_invite", ""), "2");
        coach.setAdapter(adapter_team);
        adapter_team.setOnLoadMoreListener(() -> {
            adapter_team.addItem();

            coach.postDelayed(() -> {
                int end = adapter_team.getItemCount() + 1;
                getRecursionTeamAPI((end), userid);
            }, 2000);
        });


        ok.setOnClickListener(view -> {
            String id = adapter_team.getTeamID();
            String team_name = adapter_team.getTeamName();
            if (flag.equals("1")) {
                String id_invite = cmn.getPrefsData(context, "team_id_invite", "");
                ;
                String name_invite = cmn.getPrefsData(context, "team_name_invite", "");

                if (!email_text.getText().toString().trim().equals(name_invite.trim())) {
                    cmn.setPrefsData(context, "team_id_invite", id);
                    cmn.setPrefsData(context, "team_name_invite", team_name);

                }
                id_invite = cmn.getPrefsData(context, "team_id_invite", "");

                name_invite = cmn.getPrefsData(context, "team_name_invite", "");

                if (id_invite.length() > 0) {
                    context.startActivity(new Intent(context, InviteActivity.class).putExtra("Teamid", id_invite).putExtra("name", name_invite));
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, "Välj team", Toast.LENGTH_SHORT).show();
                }
            } else {

                String id_invite = cmn.getPrefsData(context, "team_id_invite", "");
                ;
                String name_invite = cmn.getPrefsData(context, "team_name_invite", "");
                if (arr_team.size() > 1) {
                    if (id != null) {
                        getuserRoleAPI(userid, id, team_name, alertDialog);
                    } else {
                        Toast.makeText(context, "Välj team", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    getuserRoleAPI(userid, id_invite, name_invite, alertDialog);

                }
            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialogTeamPick(View item, String flag) {
        ViewGroup viewGroup = item.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.team_picker, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView txt = dialogView.findViewById(R.id.txt);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        RecyclerView coach = dialogView.findViewById(R.id.list_coach);
        txt.setText("Välj till vilket team användaren ska bjudas till");
        /*email_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter_team.getFilter().filter(s);

            }
        });*/
        coach.setVisibility(View.VISIBLE);
        getTeamAPIPick(userid, coach);
        adapter_team = new Team_list_alertbox(coach, userid, context, null, cmn.getPrefsData(context, "team_id_invite", ""), "1");
        coach.setAdapter(adapter_team);
        adapter_team.setOnLoadMoreListener(() -> {
            adapter_team.addItem();

            coach.postDelayed(() -> {
                int end = adapter_team.getItemCount() + 1;
                getRecursionTeamAPI((end), userid);
            }, 2000);
        });
        ok.setOnClickListener(view -> {
            String id = adapter_team.getTeamID();
            String team_name = adapter_team.getTeamName();
            if (id!=null) {
                if (flag.equals("1")) {
                    cmn.setPrefsData(context, "team_id_invite", id);
                    cmn.setPrefsData(context, "team_name_invite", team_name);
                    if (id.length() > 0) {
                        context.startActivity(new Intent(context, InviteActivity.class).putExtra("Teamid", id).putExtra("name", team_name));
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(context, "Välj team", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String id_invite = cmn.getPrefsData(context, "team_id_invite", "");
                    String name_invite = cmn.getPrefsData(context, "team_name_invite", "");

                        if (arr_team.size() > 1) {
                            if (id != null) {
                                getuserRoleAPI(userid, id, team_name, alertDialog);
                            } else {
                                Toast.makeText(context, "Välj team", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            getuserRoleAPI(userid, id_invite, name_invite, alertDialog);
                        }

                }
            }
            else {
                cmn.showAlert("Ingen information hittad",context);
                //Toast.makeText(context, "Ingen information hittad", Toast.LENGTH_SHORT).show();

            }
        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getRecursionAPI(String offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
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
                adapter.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject object = obj.getJSONObject(i);
                        HashMap map = new HashMap();
                        map.put("name", object.getString("firstname"));
                        map.put("role", object.getString("role"));
                        map.put("id", object.getString("id"));
                        map.put("email", object.getString("email"));
                        map.put("username", object.getString("username"));
                        map.put("profile", object.getString("profile_image"));

                        arr.add(map);
                    }

                    if (obj.length() > 0) {
                        adapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //  mAdapter.setLoaded();
                adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "coachList");
    }


    private void getUserAPI() {
        arr.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", "0");
            object.put("user_id", userid);
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
                        JSONObject object = obj.getJSONObject(i);
                        HashMap map = new HashMap();
                        map.put("name", object.getString("firstname"));
                        map.put("role", object.getString("role"));
                        map.put("id", object.getString("id"));
                        map.put("email", object.getString("email"));
                        map.put("username", object.getString("username"));
                        map.put("profile", object.getString("profile_image"));
                        arr.add(map);
                    }
                    if (obj.length() > 0) {
                        adapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "coachList");
    }


    public class Show_Team_Adapter extends RecyclerView.Adapter {
        private final int VIEW_ITEM = 1;

        private ArrayList<HashMap<String, String>> horizontalList;

        // The minimum amount of items to have below your current scroll position
// before loading more.
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        AppCompatTextView textView_item;
        private OnLoadMoreListener onLoadMoreListener;
        private RecyclerView recyclerView;

        public Show_Team_Adapter(ArrayList<HashMap<String, String>> students, RecyclerView recyclerView, AppCompatTextView textView) {
            horizontalList = students;
            this.textView_item = textView;
            this.recyclerView = recyclerView;
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
                return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.team_show_adapter, parent, false));
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
            private CircleImageView img;
            private TextView txtview, role, email, mob;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.team);

            }

            void setData(HashMap data) {
                if (Objects.requireNonNull(data.get("name")).toString().length() > 0) {
                    txtview.setText(Objects.requireNonNull(data.get("name")).toString().trim());
                } else {
                    txtview.setText(data.get("username").toString());
                }
                itemView.setOnClickListener(view -> {

                    coachid = data.get("id").toString();
                    textView_item.setText(data.get("name").toString());
                    recyclerView.setVisibility(View.GONE);
                });
            }

        }

        public class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }
    }

    private void getCampaignAPI() {
        arr_campaign.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        AddCampaign.img.clear();
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", "0");

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
                        arr_campaign.add((CampaignPojo) (Common.INSTANCE.getObject(obj.getString(i), CampaignPojo.class)));
                    }
                    if (arr_campaign.size() == 0) {
                        Intent i = new Intent(context, AddCampaign.class);
                        context.startActivity(i);
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else {
                        context.startActivity(new Intent(context, EditCampaign.class).putExtra("DATA", Common.INSTANCE.getBytes(arr_campaign)));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//                        Intent i = new Intent(context, EditCampaign.class);
//                        context.startActivity(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "AllCampaigns");
    }

    private void getTeamAPI(String coach, AppCompatEditText textView, RecyclerView coachlist) {
        arr_team.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", "0");
            object.put("user_id", userid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {


                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr_team.add((Teamlist) (Common.INSTANCE.getObject(obj.getString(i), Teamlist.class)));
                    }
                    if (obj.length() > 0) {
                        if (obj.length() == 1) {
                            textView.setText(arr_team.get(0).getName());
                            cmn.setPrefsData(context, "team_id_invite", arr_team.get(0).getId());
                            cmn.setPrefsData(context, "team_name_invite", arr_team.get(0).getName());
                            coachlist.setVisibility(View.GONE);
                        } else {
                            adapter_team.setLoaded();
                            for (Teamlist list : arr_team) {
                                String id = cmn.getPrefsData(context, "team_id_invite", "");
                                if (id.equals(list.getId())) {
                                    textView.setText(cmn.getPrefsData(context, "team_name_invite", ""));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                adapter_team.setUpdatedData(arr_team);


            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "u_teamList");
    }

    private void getTeamAPIPick(String coach, RecyclerView coachlist) {
        arr_team.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
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
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr_team.add((Teamlist) (Common.INSTANCE.getObject(obj.getString(i), Teamlist.class)));
                    }
                    if (obj.length() > 0) {
                        if (obj.length() == 1) {
                            cmn.setPrefsData(context, "team_id_invite", arr_team.get(0).getId());
                            cmn.setPrefsData(context, "team_name_invite", arr_team.get(0).getName());
                            //         coachlist.setVisibility(View.GONE);
                        } else {
                            adapter_team.setLoaded();
                            adapter_team.notifyDataSetChanged();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                adapter_team.setUpdatedData(arr_team);
                adapter_team.notifyDataSetChanged();


            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "u_teamList");
    }


    private void getRecursionTeamAPI(int offset, String coachid) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", offset);
            object.put("user_id", userid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                adapter_team.removeLastData();

                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr_team.add((Teamlist) (Common.INSTANCE.getObject(obj.getString(i), Teamlist.class)));
                    }

                    if (obj.length() > 0) {
                        adapter_team.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                adapter_team.setUpdatedData(arr_team);
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "u_teamList");
    }


    private void getuserRoleAPI(String userid, String teamid, String team_name, AlertDialog dialog) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("team_id", teamid);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {

                    //  cmn.showAlert("En kod har skickats till din e-postadress eller sms till dig. Följ instruktionerna för att byta ditt lösenord", context);
                    JSONObject object = new JSONObject(result.getData());
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    cmn.setPrefsData(context, "usertype", object.getString("role_id"));
                    cmn.setPrefsData(context, "role", object.getString("role"));
                    cmn.setPrefsData(context, "team_id", teamid);

                    cmn.setPrefsData(context, "team_name", team_name);

                    context.startActivity(new Intent(context, HomePage.class));
                    context.finish();
                    alertDialog.dismiss();

                } else {
                    Common.INSTANCE.showToast(context, result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userRole");
    }


    private void logout(String userid) {
        ProgressDialog mprogdialog = ProgressDialog.show(context, "", "Vänta", true);
        mprogdialog.setCancelable(false);
        String requestData = "";
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");

            object.put("id", userid);
            object.put("device_type", "a");
            object.put("device_token", deviceID);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }
        String url = ConsURL.BASE_URL_TEST + "logout";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), requestData)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());


            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.body() != null) {
                        res = Objects.requireNonNull(response.body()).string();
                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");
                        // JSONObject object = objvalue.getJSONObject("data");


                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();
                        if (status.equals("true")) {
                            getToken();
                        } else {
                            cmn.showAlert("Kan inte logga in", context);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }


    private void getToken() {
        //arr.clear();
        String requestData;
        // String deviceID = Common.INSTANCE.getString("deviceID");
        try {

            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("token", deviceID);
            object.put("device_type", "A");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    cmn.setPrefsData(context, "id", "");
                    cmn.setPrefsData(context, "city", "");
                    cmn.setPrefsData(context, "email", "");
                    cmn.setPrefsData(context, "firstname", "");
                    cmn.setPrefsData(context, "imagepath", "");
                    cmn.setPrefsData(context, "username", "");
                    cmn.setPrefsData(context, "lastname", "");
                    cmn.setPrefsData(context, "state", "");
                    cmn.setPrefsData(context, "city", "");
                    cmn.setPrefsData(context, "telephone", "");
                    cmn.setPrefsData(context, "usertype", "");
                    cmn.setPrefsData(context, "role", "");
                    cmn.setPrefsData(context, "coach_name", "");
                    cmn.setPrefsData(context, "imagepath", "");
                    cmn.setPrefsData(context, "banner", "");
                    cmn.setPrefsData(context, "is_loggedIn", "");
                    cmn.setPrefsData(context, "team_name", "");
                    cmn.setPrefsData(context, "team_id", "");
                    cmn.setPrefsData(context, "team_id_invite", "");
                    cmn.setPrefsData(context, "team_name_invite", "");
                    Common.INSTANCE.saveString("is_team", "");
                    Common.INSTANCE.saveString("is_profile", "");
                    Common.INSTANCE.saveString("deviceID", "");
                    Common.INSTANCE.saveString("Device_id", "");
                    Common.INSTANCE.saveString("is_reset", "");
                    Intent in = new Intent(context, LoginActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    context.startActivity(in);
                    context.finish();
                    alertDialog.dismiss();
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


                }else {
                   cmn.showAlert(result.getMessage(),context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "public_tokens");

    }

}
