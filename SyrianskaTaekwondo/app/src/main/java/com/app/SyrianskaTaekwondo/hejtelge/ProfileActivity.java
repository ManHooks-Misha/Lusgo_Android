package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Change_teamAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.Profile_Add_team_Member;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.Show_coachInProfileAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.TeamListAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.Common;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class ProfileActivity extends MasterActivity {
    CommonMethods cmn = new CommonMethods();
    public String imagepath;
    private TeamListAdapter mAdapter;
    List<HashMap<String, String>> arr_teamrole = new ArrayList<>();
    List<HashMap<String, String>> arr_teamcoach = new ArrayList<>();
    private String searchValue = "", usertype;

    private String banner, coachname, coachid, role_id, userid, profileuser, screen, uid, role_, statusvalue, messagestr;
    AvatarView profile;
    private LinearLayout llcoach, llteam, llaction, ll_unblock;
    Change_teamAdapter adapter_team;
    Show_coachInProfileAdapter adapter_coach;
    RecyclerView teamlist, coachteam;
    private List<Teamlist> arr_teams = new ArrayList<>();
    private View v1, v2, v_call, v_add, v_unblock;
    private LinearLayout ll_call, ll_message, llemail, telePhoneView, ll_email, ll_addmember, ll_block;
    private AppCompatTextView roll, opt, block, edit_profile, add_member, txt_name, txt_role, txt_eml, txt_coach, txt_tel, txt_user, txt_change, txt_delete, showblock;
    private AppCompatImageView background, mobile_verified, email_verified, email_chat;
    private String deviceID, status, user, news, message, event, role, is_loggedIn, id, add, city, coach, email, firstname, lastname, state, telephone;
    private AlertDialog alertDialog;
    private Show_Team_Adapter adapter;
    private Profile_Add_team_Member mAdapter_add;
    public ArrayList<Teamlist> arr_team = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setTitle("Profil");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loadID();

        //   userid = cmn.getPrefsData(ProfileActivity.this, "id", "");
        if (getIntent() != null) {
            profileuser = getIntent().getStringExtra("flag");
            screen = getIntent().getStringExtra("flag_screen");
            userid = getIntent().getStringExtra("id");
        }
      //  imagepath = cmn.getPrefsData(ProfileActivity.this, "imagepath", "");
        banner = cmn.getPrefsData(ProfileActivity.this, "banner", "");
        coachname = cmn.getPrefsData(ProfileActivity.this, "coach_name", "");
        uid = cmn.getPrefsData(ProfileActivity.this, "id", "");
        role_ = cmn.getPrefsData(ProfileActivity.this, "usertype", "");

        getprofile();

        if (role_.equals(ConsURL.admin)) {
            opt.setVisibility(View.VISIBLE);
        } else {
            roll.setGravity(Gravity.END);
            opt.setVisibility(View.GONE);
        }
        txt_change.setOnClickListener(view -> {
            Intent i = new Intent(context, ResetPassword.class);
            i.putExtra("flag", "2");
            context.startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        if (!role_.equals(ConsURL.admin) && (profileuser.equals("own"))) {

            txt_delete.setVisibility(View.GONE);
            txt_delete.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage("Är du säker på att du raderar ditt konto?");
                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    getDeleteAPI();
                    alertDialog.dismiss();
                });
                builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
                alertDialog = builder.create();
                alertDialog.show();
            });
        } else {
            txt_delete.setVisibility(View.GONE);
        }
        edit_profile.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, EditProfile.class).putExtra("flag", "2"));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            //   finish();
        });

        add_member.setOnClickListener(v ->
                showCustomDialogadd("add"));


        block.setOnClickListener(view -> {
            if (status.equals("2")) {
                statusvalue = "0";
                messagestr = "Är du säker på att du vill avblockera den här användaren?";
            } else {
                messagestr = "Är du säker på att du vill blockera den här användaren?";
                statusvalue = "1";
            }
            showAlert(context, statusvalue, messagestr);

        });
        ll_unblock.setOnClickListener(view -> {
            if (status.equals("2")) {
                statusvalue = "0";

                messagestr = "Är du säker på att du vill avblockera den här användaren?";
            } else {
                messagestr = "Är du säker på att du vill blockera den här användaren?";
                statusvalue = "1";

            }
            showAlert(context, statusvalue, messagestr);
        });

        profile.setOnClickListener(view -> {
            ArrayList<String> arr_images = new ArrayList<>();
            if (!imagepath.equals("")) {
                arr_images.add(imagepath);
                startActivity(new Intent(ProfileActivity.this, ImageShowDetails.class).putExtra("image", arr_images).putExtra("flag", "profile").putExtra("position", "0"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            } else {

            }
        });
    }

    private void getprofile() {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                JSONObject object = new JSONObject(result.getData());
                id = object.getString("id");
                user = object.getString("username");
                email = object.getString("email");
                firstname = object.getString("first_name");
                imagepath = object.getString("profile_image");
                lastname = object.getString("last_name");
                state = object.getString("state");
                city = object.getString("city");
                status = object.getString("status");
                if (result.isStatus()) {
                    add = object.getString("address");
                    telephone = object.getString("telephone");
                    usertype = object.getString("usertype");
                    banner = object.getString("banner");
                    role = object.getString("role");
                    role_id = object.getString("role_id");
                    if (telephone.equals("") || telephone.equals("null")){
                        mobile_verified.setVisibility(View.GONE);
                        txt_tel.setVisibility(View.GONE);
                    }
                    if (email.equals("") || email.equals("null")){
                        email_verified.setVisibility(View.GONE);
                    }
                    if (role_id.equals(ConsURL.members) || role_id.equals(ConsURL.coach) || role_id.equals(ConsURL.sub_coach)) {
                        JSONArray teams_ = object.getJSONArray("teams");
                        for (int j = 0; j < teams_.length(); j++) {
                            JSONObject object1 = teams_.getJSONObject(j);
                            HashMap map = new HashMap();
                            map.put("team_name", object1.getString("name"));
                            map.put("teamid", object1.getString("team_id"));
                            map.put("role", object1.getString("role"));
                            if (role_id.equals(ConsURL.members)) {
                                coach = object1.getString("coach_name");
                                map.put("coach_name", coach);
                                map.put("coachid", object1.getString("coach_id"));
                            }
                            map.put("roleid", object1.getString("role_id"));

                            arr_teamrole.add(map);
                        }
                        for (int j = 0; j < teams_.length(); j++) {
                            JSONObject object1 = teams_.getJSONObject(j);
                            String roleid = object1.getString("role_id");
                            if (roleid.equals(ConsURL.members) || roleid.equals(ConsURL.sub_coach)) {
                                HashMap map = new HashMap();
                                map.put("team_name", object1.getString("name"));
                                map.put("teamid", object1.getString("team_id"));
                                map.put("role", object1.getString("role"));
                                if (roleid.equals(ConsURL.members) || roleid.equals(ConsURL.sub_coach)) {
                                    coach = object1.getString("coach_name");
                                    map.put("coach_name", coach.trim());
                                    map.put("coachid", object1.getString("coach_id"));
                                }
                                map.put("roleid", object1.getString("role_id"));
                                arr_teamcoach.add(map);
                            }
                        }

                        adapter_team = new Change_teamAdapter(arr_teamrole, ProfileActivity.this, userid, role_id, role_);
                        teamlist.setLayoutManager(new GridLayoutManager(context, 1));
                        teamlist.setAdapter(adapter_team);
                        adapter_coach = new Show_coachInProfileAdapter(arr_teamcoach, ProfileActivity.this);
                        coachteam.setLayoutManager(new GridLayoutManager(context, 1));
                        coachteam.setAdapter(adapter_coach);

                        if (arr_teamcoach.size() == 0) {
                            llcoach.setVisibility(View.GONE);
                        } else {
                            llcoach.setVisibility(View.VISIBLE);
                        }


                    }
                    is_loggedIn = object.getString("is_loggedIn");
                    if (role_id.equals(ConsURL.sub_admin) || role_id.equals(ConsURL.admin)) {
                        llcoach.setVisibility(View.GONE);
                        llteam.setVisibility(View.GONE);
                    }
                    if (profileuser.equals("own")) {
                        v1.setVisibility(View.GONE);
                        v2.setVisibility(View.GONE);
                        v_call.setVisibility(View.GONE);
                        llteam.setVisibility(View.GONE);
                        teamlist.setVisibility(View.GONE);
                        add_member.setVisibility(View.GONE);
                        cmn.setPrefsData(context, "id", id);
                        cmn.setPrefsData(context, "city", city);
                        cmn.setPrefsData(context, "email", email);
                        cmn.setPrefsData(context, "firstname", firstname);
                        cmn.setPrefsData(context, "imagepath", imagepath);
                        cmn.setPrefsData(context, "lastname", lastname);
                        cmn.setPrefsData(context, "state", state);
                        cmn.setPrefsData(context, "city", city);
                        cmn.setPrefsData(context, "telephone", telephone);
                        // cmn.setPrefsData(context, "usertype", usertype);
                        cmn.setPrefsData(context, "username", user);
                        //   cmn.setPrefsData(context, "role", role);
                        cmn.setPrefsData(context, "banner", banner);
                        edit_profile.setVisibility(View.VISIBLE);
                        llaction.setVisibility(View.GONE);
                        txt_change.setVisibility(View.GONE);
                        if (!role_.equals(ConsURL.admin) || profileuser.equals("Own")) {
                            txt_delete.setVisibility(View.GONE);
                        }
                    } else {
                        if (status.equals("2")) {
                            llaction.setVisibility(View.GONE);
                            ll_unblock.setVisibility(View.GONE);
                        } else {
                            llaction.setVisibility(View.VISIBLE);

                        }
                        if (email.isEmpty()) {
                            ll_email.setVisibility(View.GONE);
                        }
                        if (telephone.isEmpty()) {
                            v_call.setVisibility(View.GONE);
                            ll_call.setVisibility(View.GONE);

                        }
                    }
                    ll_email.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivityForResult(Intent.createChooser(intent, "Email via..."), 800);
                    });
                    ll_message.setOnClickListener(view -> {
                        startActivity(new Intent(ProfileActivity.this, MessageForProfile.class).putExtra("u_id", userid).putExtra("u_name", firstname).putExtra("img", imagepath));
                    });

                    ll_call.setOnClickListener(arg0 -> {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + telephone));//change the number
                            context.startActivity(callIntent);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 101);
                            }
                        }
                    });
                    if (profileuser != null) {
                        if (role_.equals(ConsURL.admin)) {
                            if (profileuser.equals("user")) {
                                teamlist.setVisibility(View.VISIBLE);

                                edit_profile.setVisibility(View.GONE);
                                txt_change.setVisibility(View.GONE);
                                if (usertype.equals(ConsURL.sub_admin)) {
                                    ll_block.setVisibility(View.GONE);

                                } else {
                                    ll_block.setVisibility(View.VISIBLE);
                                }
                                //  llaction.setVisibility(View.VISIBLE);
                            } else {
                                block.setVisibility(View.GONE);
                                llteam.setVisibility(View.GONE);
                            }
                            if (profileuser.equals("user")) {
                                if (usertype.equals(ConsURL.members) || usertype.equals(ConsURL.coach) || usertype.equals(ConsURL.sub_coach)) {
                                    if (!status.equals("2")) {
                                        ll_unblock.setVisibility(View.GONE);
                                        add_member.setVisibility(View.VISIBLE);
                                    } else {
                                        ll_addmember.setVisibility(View.GONE);

                                        add_member.setVisibility(View.GONE);
                                        ll_unblock.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    add_member.setVisibility(View.GONE);
                                    ll_addmember.setVisibility(View.GONE);
                                    v_add.setVisibility(View.GONE);
                                    v_unblock.setVisibility(View.GONE);

                                }
                                edit_profile.setVisibility(View.GONE);
                                // llaction.setVisibility(View.VISIBLE);

                            }
                        } else if (role_.equals(ConsURL.sub_admin)) {
                            if (profileuser.equals("user")) {
                                edit_profile.setVisibility(View.GONE);
                                txt_change.setVisibility(View.GONE);
                                ll_block.setVisibility(View.GONE);

                                //  llaction.setVisibility(View.VISIBLE);
                            } else {
                                block.setVisibility(View.GONE);
                                llteam.setVisibility(View.GONE);
                            }
                            if (profileuser.equals("user")) {
                                if (usertype.equals(ConsURL.members) || usertype.equals(ConsURL.coach) || usertype.equals(ConsURL.sub_coach)) {
                                    if (!status.equals("2")) {
                                        ll_unblock.setVisibility(View.GONE);
                                        add_member.setVisibility(View.VISIBLE);
                                    } else {
                                        add_member.setVisibility(View.GONE);
                                        ll_unblock.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    add_member.setVisibility(View.GONE);
                                    ll_addmember.setVisibility(View.GONE);
                                    v_add.setVisibility(View.GONE);
                                    v_unblock.setVisibility(View.GONE);

                                }
                                edit_profile.setVisibility(View.GONE);
                                // llaction.setVisibility(View.VISIBLE);

                            }
                        }
                        else if (profileuser.equals("user")) {

                            if (status.equals("2")) {
                                showblock.setVisibility(View.VISIBLE);
                                add_member.setVisibility(View.GONE);
                                ll_addmember.setVisibility(View.GONE);
                                showblock.setText("Blockerad");
                                llaction.setVisibility(View.GONE);
                                ll_unblock.setVisibility(View.VISIBLE);

                            }
                            if (role_.equals(ConsURL.admin) || role_.equals(ConsURL.coach) || role_.equals(ConsURL.sub_coach)) {
                                if (profileuser.equals("user")) {
                                    txt_change.setVisibility(View.GONE);
                                    llaction.setVisibility(View.VISIBLE);
                                    teamlist.setVisibility(View.VISIBLE);
                                    edit_profile.setVisibility(View.GONE);
                                    //              ll_block.setVisibility(View.GONE);
                                    ll_block.setVisibility(View.GONE);
                                    ll_addmember.setVisibility(View.GONE);
                                } else {
                                    txt_change.setVisibility(View.GONE);
                                    llaction.setVisibility(View.GONE);
                                    llteam.setVisibility(View.GONE);
                                    teamlist.setVisibility(View.GONE);
                                    edit_profile.setVisibility(View.VISIBLE);

                                }

                            } else {
                                txt_change.setVisibility(View.GONE);
                                llaction.setVisibility(View.GONE);
                                teamlist.setVisibility(View.GONE);
                                edit_profile.setVisibility(View.GONE);
                                //              ll_block.setVisibility(View.GONE);
                                ll_block.setVisibility(View.GONE);
                                ll_addmember.setVisibility(View.GONE);
                                txt_delete.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        llaction.setVisibility(View.GONE);

                        edit_profile.setVisibility(View.VISIBLE);
                    }
                    txt_name.setText(firstname.trim() + " " + lastname.trim());


                    if (role.equals("Admin") && (!profileuser.equals("own"))) {
                        telePhoneView.setVisibility(View.GONE);
                        llemail.setVisibility(View.GONE);
                        llaction.setVisibility(View.GONE);

                    } else {
                        telePhoneView.setVisibility(View.VISIBLE);
                        llemail.setVisibility(View.VISIBLE);
                        txt_eml.setText(email);
                        txt_tel.setText(telephone);


                    }

                    txt_user.setText(user);

                    if (role_.equals(ConsURL.admin)) {
                        if (status.equals("2")) {
                            block.setText("Avblockera användare");
                            ll_unblock.setVisibility(View.VISIBLE);
                            llaction.setVisibility(View.GONE);
                        }
                    } else {
                        if (status.equals("2")) {
                            block.setText("Blockerad");
                        }
                    }
                    String name = String.valueOf((firstname.trim().charAt(0)));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));

                    Glide.with(this)
                            .load(imagepath)
                            .centerCrop()
                            .placeholder(drawable)
                            .into(profile);

                    Glide.with(this)
                            .load(banner)
                          //  .centerCrop()
                            .placeholder(R.drawable.bnr_img)
                            .into(background);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "user_profile");
    }


    private void getBlockprofile(String status1, String p_coach) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("uStatus", status1);
            object.put("user_id", uid);
            object.put("u_id", userid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {

                if (result.isStatus()) {
                    if (role_.equals(ConsURL.admin)) {
                        if (result.getData().equals("2")) {
                            status = result.getData();
                            //block.setText("Avblockera användare");
                            llaction.setVisibility(View.GONE);
                            ll_unblock.setVisibility(View.VISIBLE);
                            if (screen != null) {
                                if (screen.equals("report")) {
                                    context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen", "report").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                } else {
                                    startActivity(new Intent(this, ProfileActivity.class).putExtra("id", userid).putExtra("flag", "user").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            } else {
                                startActivity(new Intent(this, ProfileActivity.class).putExtra("id", userid).putExtra("flag", "user").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                            }
                        } else {
                            status = result.getData();
                            add_member.setVisibility(View.VISIBLE);
                            block.setText("Blockera användare");
                            ll_unblock.setVisibility(View.GONE);
                            ll_block.setVisibility(View.VISIBLE);
                            llaction.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    arr_team.clear();
                    //  JSONObject object=new JSONObject(result.getData());
                    JSONArray arr = new JSONArray(result.getData());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Teamlist list = new Teamlist();
                        list.setName(obj.getString("team_name"));
                        list.setCoach_id(obj.getString("coach_id"));
                        list.setId(obj.getString("team_id"));
                        list.setRole_id(obj.getString("role_id"));
                        arr_teams.add(list);
                    }
                    showCustomDialog(arr_teams, p_coach);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "block_user");
    }

    public void loadID() {
        txt_name = findViewById(R.id.txt_name);
        txt_eml = findViewById(R.id.txt_eml);
        txt_user = findViewById(R.id.txt_user);
        txt_role = findViewById(R.id.txt_role);
        profile = findViewById(R.id.pp_img);
        txt_tel = findViewById(R.id.txt_tel);
        mobile_verified = findViewById(R.id.mobile_verified);
        email_verified = findViewById(R.id.email_verified);
        txt_coach = findViewById(R.id.txt_coach);
        background = findViewById(R.id.pp);
        llcoach = findViewById(R.id.llcoach);
        txt_change = findViewById(R.id.txt_change);
        txt_delete = findViewById(R.id.txt_delete);
        showblock = findViewById(R.id.showblock);
        v2 = findViewById(R.id.v2);
        v_call = findViewById(R.id.v_call);
        v_unblock = findViewById(R.id.v_unblock);
        v_add = findViewById(R.id.v_add);
        v1 = findViewById(R.id.v1);
        edit_profile = findViewById(R.id.edit_profile);
        add_member = findViewById(R.id.add_member);
        block = findViewById(R.id.block);
        opt = findViewById(R.id.opt);
        roll = findViewById(R.id.roll);
        teamlist = findViewById(R.id.team);
        coachteam = findViewById(R.id.coachteam);
        llteam = findViewById(R.id.team_opt);
        llaction = findViewById(R.id.ll_action);
        //  call = findViewById(R.id.call);
        ll_call = findViewById(R.id.ll_call);
        ll_message = findViewById(R.id.ll_message);
        ll_email = findViewById(R.id.ll_emails);
        llemail = findViewById(R.id.ll_email);
        telePhoneView = findViewById(R.id.telePhoneView);
        ll_addmember = findViewById(R.id.ll_add_member);
        ll_block = findViewById(R.id.ll_block);
        ll_unblock = findViewById(R.id.ll_unblock);
        //  message_img = findViewById(R.id.message);
        email_chat = findViewById(R.id.email);
    }


    private void showCustomDialog(List<Teamlist> arr_teams, String p_coach) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.team_assign, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        ok.setOnClickListener(view -> {
            if (cmn.isOnline(ProfileActivity.this)) {
                startActivity(new Intent(context, Teamshow_array.class).putExtra("data", getBytes(arr_teams)).putExtra("coach", p_coach));


            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    //To do  if admin wants to block the coach then admin make a coach to another coach or same team member

    public void showAlert(Context context, String statusvalue, String Message) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(Html.fromHtml(Message)).setCancelable(false)
                    .setPositiveButton("Ja", (dialog, id) -> {

                        if (statusvalue.equals("0")) {
                            if (usertype.equals(ConsURL.coach)) {

                                getBlockprofile(statusvalue, userid);            //   finish();
                            } else {
                                showCustomDialogadd("block");
                            }
                        } else {
                            getBlockprofile(statusvalue, userid);            //   finish();

                        }

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


    private void getUserAPI() {
        arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 50);
            object.put("offset", 0);
            object.put("user_id", uid);
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
                        if (!userid.equals(object.getString("id"))) {
                            HashMap map = new HashMap();
                            map.put("name", object.getString("firstname"));
                            map.put("role", object.getString("role"));
                            map.put("id", object.getString("id"));
                            map.put("email", object.getString("email"));
                            map.put("username", object.getString("username"));
                            map.put("profile", object.getString("profile_image"));
                            arr.add(map);
                        }
                    }
                    if (obj.length() > 0) {
                        adapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                adapter.setUpdatedData(arr);

            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "coachList");
    }

    private void getRecursionAPI(int offset) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 50);
            object.put("offset", offset);
            object.put("user_id", uid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                adapter.removeLastData();
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
                adapter.setUpdatedData(arr, "");
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "coachList");
    }

    private void showCustomDialogadd(String flag) {
        getUserAPI();
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.team_show, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView txt = dialogView.findViewById(R.id.txt);
        //txt.setText("Lägg till team för den här användaren");
        //Sk chages
        txt.setText("Placera användaren under någon teamleader");
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatEditText email_text = dialogView.findViewById(R.id.email_text);
        RecyclerView coach = dialogView.findViewById(R.id.list_coach);
        coach.setVisibility(View.VISIBLE);
        // email_text.setOnClickListener(view -> coach.setVisibility(View.VISIBLE));
        adapter = new Show_Team_Adapter(coach, email_text,flag);
        coach.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            adapter.addItem();
            coach.postDelayed(() -> {
                int end = adapter.getItemCount() + 1;
                getRecursionAPI((end));
            }, 2000);
        });


        ok.setOnClickListener(view -> {
            if (email_text.getText().toString().length() > 0) {
                alertDialog.dismiss();
                context.startActivity(new Intent(context, AddTeam_member.class)
                        .putExtra("coachid", coachid)
                        .putExtra("userid", userid)
                        .putExtra("roleid", role_id)
                        .putExtra("flag", flag));

            } else {
                cmn.showAlert("Välj coach",context);
              //  Common.INSTANCE.showToast(context, "Välj coach");
            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    public class Show_Team_Adapter extends RecyclerView.Adapter implements Filterable {
        private final int VIEW_ITEM = 1;

        private ArrayList<HashMap<String, String>> horizontalList = new ArrayList<>();
        private ArrayList<HashMap<String, String>> filtedlist = new ArrayList<>();

        // The minimum amount of items to have below your current scroll position
// before loading more.
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        AppCompatEditText textView_item;
        private OnLoadMoreListener onLoadMoreListener;
        private RecyclerView recyclerView;
        private String  flag;
        private FriendFilters friendFilter;

        public Show_Team_Adapter(RecyclerView recyclerView, AppCompatEditText textView,String  flag) {
            this.textView_item = textView;
            this.recyclerView = recyclerView;
            this.flag = flag;
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
                ((StudentViewHolder) holder).setData(horizontalList.get(position),flag);
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        public void setLoaded() {
            loading = false;
        }

        public void setUpdatedData(ArrayList<HashMap<String, String>> tempList) {
            horizontalList.clear();
            filtedlist.clear();
            for (HashMap<String, String> teamlist : tempList) {
                if (!horizontalList.contains(teamlist)) {
                    horizontalList.add(teamlist);
                }
                if (!filtedlist.contains(teamlist)) {
                    filtedlist.add(teamlist);
                }
            }
            this.notifyDataSetChanged();
        }

        public void setUpdatedData(ArrayList<HashMap<String, String>> tempList, String charSequence) {
            if (charSequence.isEmpty()) {
                setUpdatedData(tempList);
            } else {
                horizontalList.clear();
                filtedlist.clear();


                for (HashMap<String, String> user : tempList) {
                    if (!filtedlist.contains(user)) {
                        filtedlist.add(user);
                    }

                    if (user == null || user.get("name").toLowerCase().trim().contains(charSequence.toLowerCase())) {
                        if (!horizontalList.contains(user)) {
                            horizontalList.add(user);
                        }
                    }
                }
                this.notifyDataSetChanged();
                setLoaded();
            }
        }

        public void addItem() {
            horizontalList.add(null);
            filtedlist.add(null);
            this.notifyItemInserted(horizontalList.size() - 1);
        }

        public void removeLastData() {
            horizontalList.remove(horizontalList.size() - 1);
            filtedlist.remove(filtedlist.size() - 1);
            notifyItemRemoved(horizontalList.size() - 1);
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
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
                    ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
                    // search content in friend list
                    for (HashMap<String, String> user : filtedlist) {
                        if (user == null || user.get("name").toLowerCase().trim().contains(constraint.toString().toLowerCase().trim())) {
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
                //   horizontalList = (ArrayList<Teamlist>) results.values;
                if (results.values != null) {
                    horizontalList.clear();
                    horizontalList.addAll((ArrayList<HashMap<String, String>>) results.values);
                }

                notifyDataSetChanged();
                recyclerView.invalidate();
            }

        }

        //
        public class StudentViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView edit;
            private AvatarView img;
            private TextView txtview, role, email, mob;
            private LinearLayout ll_notification;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.team);
                img = view.findViewById(R.id.profile_img);
                ll_notification = view.findViewById(R.id.ll_notification);

            }

            void setData(HashMap data,String flag) {
                if (Objects.requireNonNull(data.get("name")).toString().length() > 0) {
                    txtview.setText(Objects.requireNonNull(data.get("name")).toString().trim());
                    if (data.get("name").toString().trim().length() > 0) {
                        String name = String.valueOf(data.get("name").toString().trim().charAt(0));
                        String image = (String) data.get("profile");
                     /*   TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                        Glide.with(itemView)
                                .load(data.get("profile"))
                                .fitCenter()
                                .placeholder(drawable)
                                .into(img);*/


                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                        Glide.with(itemView).load(image).centerCrop().placeholder(drawable).into(img);
                    }


                  /*  Glide.with(ProfileActivity.this)
                            .load(data.get("profile"))
                            .fitCenter()
                            .placeholder(R.drawable.user_diff)
                            .into(img);*/
                } else {
                    txtview.setText(data.get("username").toString());

                    if (data.get("username").toString().trim().length() > 0) {
                        String name = String.valueOf(data.get("username").toString().trim().charAt(0));
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                        Glide.with(itemView)
                                .load(data.get("profile"))
                                .centerCrop()
                                .placeholder(drawable)
                                .into(img);
                    }
                   /* Glide.with(ProfileActivity.this)
                            .load(data.get("profile"))
                            .fitCenter()
                            .placeholder(R.drawable.user_diff)
                            .into(img);*/
                }
                itemView.setOnClickListener(view -> {

                    coachid = data.get("id").toString();
                    textView_item.setText(data.get("name").toString());

                    UserDialoge(data.get("name").toString(),flag);

                    //ll_notification.setBackgroundColor(getResources().getColor(R.color.gray));
                    //  recyclerView.setVisibility(View.GONE);
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

    public void UserDialoge(String name,String flag){
        View alertCustomdialog = LayoutInflater.from(this).inflate(R.layout.user_dialog,null);
        //initialize alert builder.
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(alertCustomdialog);
        ImageView cancel = (ImageView)alertCustomdialog.findViewById(R.id.cancel_button);
        Button btnOk = (Button) alertCustomdialog.findViewById(R.id.btn_user);
        TextView txtName = (TextView) alertCustomdialog.findViewById(R.id.txt_user_name);
        final AlertDialog dialog = alert.create();
        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //finally show the dialog box in android all
        dialog.show();
        txtName.setText(name);
        cancel.setOnClickListener(view -> dialog.dismiss());

        btnOk.setOnClickListener(view -> {
            dialog.dismiss();
            context.startActivity(new Intent(context, AddTeam_member.class)
                    .putExtra("coachid", coachid)
                    .putExtra("userid", userid)
                    .putExtra("roleid", role_id)
                    .putExtra("flag", flag));
        });
    }

    private void showCustomDialogUserTeam() {


        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.user_previleges, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        RecyclerView create_news = dialogView.findViewById(R.id.user_team);

        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);

        mAdapter_add = new Profile_Add_team_Member(
                arr_team, context);
        create_news.setLayoutManager(new GridLayoutManager(context, 1));
        create_news.setAdapter(adapter);

        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + telephone));//change the number
            startActivity(callIntent);
        } else {

            // We were not granted permission this time, so don't try to show the contact picker
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 800) {
            //   Toast.makeText(context,"mail sent successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeleteAPI() {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", uid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(ProfileActivity.this, result -> {
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

                    startActivity(in);
                    finish();
                } else {

                    showCustomDialog(result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                showCustomDialog(result.getMessage());
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_account");
    }

    private void showCustomDialog(String msg) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.news_alert, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        AppCompatTextView create = dialogView.findViewById(R.id.create);
        create.setText(msg);
        ok.setOnClickListener(view -> {
            alertDialog.dismiss();

        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}
