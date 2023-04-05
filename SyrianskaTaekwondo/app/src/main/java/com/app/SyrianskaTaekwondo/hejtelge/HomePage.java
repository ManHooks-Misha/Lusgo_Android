package com.app.SyrianskaTaekwondo.hejtelge;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.SyrianskaTaekwondo.hejtelge.activity.broadcastreciever.NotificationBadge;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.ApiClient;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.ApiInterface;
import com.app.SyrianskaTaekwondo.hejtelge.database.Chatfloatmessage;
import com.app.SyrianskaTaekwondo.hejtelge.database.MuteNotify;
import com.app.SyrianskaTaekwondo.hejtelge.database.NotificationTable;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.Calendar_Fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.Inbox_Fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.Menu_fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.NewsFragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.Notification_Fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.info_fragment;
import com.app.SyrianskaTaekwondo.hejtelge.model.ExpandableTextView;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Notification;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ReadAllNotificationModel;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utility.VersionChecker;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.Common;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class HomePage extends MasterActivity {
    private TabLayout tabLayout;
    private String[] permissionsRequired = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    ViewPagerAdapter adapter;
    private String userid, role;
    TextView text_count;
    AvatarView log;
    View view;
    MuteNotify muteTable;
    public static boolean OpenActivityAd = false;
 //   AppCompatImageView ProfileActivity_ivLine;
    private NotificationTable notificationTable;
    public static final String NOTIFY_BELL = "NOTIFY_BELL";
    public static final String CHAT_FLOAT_NOTIFY_BELL = "CHAT_NOTIFY_BELL";
    public static final String NOTIFY_Permission = "NOTIFY_Permission";
    public static final String BLOCK = "BLOCK";
    public static final String APP_BLOCK = "APP_BLOCK";
    public static final String SEEN_NOTIFICATION = "seen_notification";
    public static final String APP_FREEZE = "APP_FREEZE";
    public static final String App_Ads = "App_Ads";
    public static final String Changerole = "change";
    public static final String Report = "report";
    ArrayList<Notification> arr = new ArrayList<>();

 //   ArrayList<ReadAllNotificationModel.Datum> notificationList = new ArrayList<>();
    private CommonMethods cmn;
    public Chatfloatmessage chatTable;

    ArrayList<HashMap<String, String>> arr_badge = new ArrayList<>();
    FloatingActionButton floatChatBtn;
    AlertDialog alertDialog;


    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            arr.clear();
            // sendBroadcast(new Intent("VK.NEW.NOTIFICATION"));
            if (tabLayout.getSelectedTabPosition() != 4) {
                arr = notificationTable.getDataNotification();

                View view = Objects.requireNonNull(tabLayout.getTabAt(4)).view;
                if (view.findViewById(R.id.count) != null) {
                    view.findViewById(R.id.count).setVisibility(View.VISIBLE);
                    ((NotificationBadge) view.findViewById(R.id.count)).setNumber(arr.size());
                    ImageView imageView=view.findViewById(R.id.bell_img);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_notification));
                 //   Glide.with(context).load(R.drawable.ic_notification).into((ImageView) view.findViewById(R.id.bell_img));
                } else {
                    view = LayoutInflater.from(context).inflate(R.layout.notification_bell, null);
                    ((NotificationBadge) view.findViewById(R.id.count)).setNumber(arr.size());
                    Objects.requireNonNull(tabLayout.getTabAt(4)).setCustomView(view);
                }
            }


        }
    };
    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // sendBroadcast(new Intent("VK.NEW.NOTIFICATION"));
            arr_badge.clear();
            arr_badge = chatTable.getDataNotificationData();
            if (arr_badge.size() > 0) {
                text_count.setVisibility(View.VISIBLE);
                text_count.setText("" + arr_badge.size());
            } else {
                text_count.setVisibility(View.GONE);
            }

        }
    };
    private BroadcastReceiver permission = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (role.equals(ConsURL.members) || role.equals(ConsURL.sub_coach) || role.equals(ConsURL.coach)) {
                logout();



            }
        }
    };
    private BroadcastReceiver block = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //logoutblock("Du har blockerats av admin. Du kan inte använda funktionen i den här appen");

            // showToast("Tillstånd har gett dig");
            // check_permission();
            BlockFun();

        }
    };
    private BroadcastReceiver adsshow = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String link = intent.getExtras().getString("link");
            String text = intent.getExtras().getString("txt");
            String path = intent.getExtras().getString("path");
            //https://web.lusgo.se/api/StaticFiles/GeneralPost/Monday-27-June-2022_09-34-09_images9.jpg
            // logoutblock("Du har blockerats av admin. Du kan inte använda funktionen i den här appen");
            AdShows(text, path, link);
            // showToast("Tillstånd har gett dig");
            // check_permission();


        }
    };
    private BroadcastReceiver app_block = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
           // logoutblock("Ditt konto har blockerats. kontakta administratören.");
         //   logoutblock(msg);
            // create funtion of aplicatioin block
            BlockFun();

            // showToast("Tillstånd har gett dig");
            // check_permission();


        }
    };
    private BroadcastReceiver seen_notification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            view = Objects.requireNonNull(tabLayout.getTabAt(4)).view;
            notificationTable.clearTabledata();
            notificationTable.clearTable();
            view.findViewById(R.id.count).setVisibility(View.GONE);
        }
    };
    private BroadcastReceiver app_freeze = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

           // logoutblock("Ditt konto har frusits. kontakta administratören.");
            // create funtion of user block
            BlockFun();
            // showToast("Tillstånd har gett dig");
            // check_permission();


        }
    };
    private BroadcastReceiver change = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            String type = intent.getStringExtra("type");
            if (type.equals("add_team")){
                dialogeAddTeam(msg);
            }else {
                logoutchange(msg);
            }


            // showToast("Tillstånd har gett dig");
            // check_permission();


        }
    };
    private BroadcastReceiver report = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            String reportid = intent.getStringExtra("reportid");
            report(reportid, msg);


            // showToast("Tillstånd har gett dig");
            // check_permission();


        }
    };

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Dina privilegier har ändrats. vänligen logga in igen.");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            cmn.setPrefsData(this, "id", "");
            cmn.setPrefsData(this, "city", "");
            cmn.setPrefsData(this, "email", "");
            cmn.setPrefsData(this, "firstname", "");
            cmn.setPrefsData(this, "imagepath", "");
            cmn.setPrefsData(this, "username", "");
            cmn.setPrefsData(this, "lastname", "");
            cmn.setPrefsData(this, "state", "");
            cmn.setPrefsData(this, "city", "");
            cmn.setPrefsData(this, "telephone", "");
            cmn.setPrefsData(this, "usertype", "");
            cmn.setPrefsData(this, "role", "");
            cmn.setPrefsData(this, "coach_name", "");
            cmn.setPrefsData(this, "imagepath", "");
            cmn.setPrefsData(this, "banner", "");
            cmn.setPrefsData(this, "is_loggedIn", "");
            cmn.setPrefsData(this, "team_name", "");
            cmn.setPrefsData(this, "team_id", "");
            cmn.setPrefsData(this, "team_id_invite", "");
            cmn.setPrefsData(this, "team_name_invite", "");
            Common.INSTANCE.saveString("is_team", "");
            Common.INSTANCE.saveString("is_profile", "");
            Common.INSTANCE.saveString("is_reset", "");
            Common.INSTANCE.saveString("login_sub", "false");

            Intent in = new Intent(this, LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            this.startActivity(in);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            alertDialog.dismiss();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();

    }

    public void report(String reportid, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            Intent in = new Intent(this, Report_detailsActivity.class);
            in.putExtra("report_id", reportid);
            in.putExtra("type", "home");
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            this.startActivity(in);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            alertDialog.dismiss();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }

    public void Check_version() {
        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.execute();
    }
    public void BlockFun(){
        cmn.setPrefsData(this, "id", "");
        cmn.setPrefsData(this, "city", "");
        cmn.setPrefsData(this, "email", "");
        cmn.setPrefsData(this, "firstname", "");
        cmn.setPrefsData(this, "imagepath", "");
        cmn.setPrefsData(this, "username", "");
        cmn.setPrefsData(this, "lastname", "");
        cmn.setPrefsData(this, "state", "");
        cmn.setPrefsData(this, "city", "");
        cmn.setPrefsData(this, "telephone", "");
        cmn.setPrefsData(this, "usertype", "");
        cmn.setPrefsData(this, "role", "");
        cmn.setPrefsData(this, "coach_name", "");
        cmn.setPrefsData(this, "imagepath", "");
        cmn.setPrefsData(this, "banner", "");
        cmn.setPrefsData(this, "is_loggedIn", "");
        cmn.setPrefsData(this, "team_name", "");
        cmn.setPrefsData(this, "team_id", "");
        cmn.setPrefsData(this, "team_id_invite", "");
        cmn.setPrefsData(this, "team_name_invite", "");
        Common.INSTANCE.saveString("is_team", "");
        Common.INSTANCE.saveString("is_profile", "");
        Common.INSTANCE.saveString("is_reset", "");
        Common.INSTANCE.saveString("login_sub", "false");

        Intent in = new Intent(this, LoginActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        this.startActivity(in);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void logoutblock(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(msg);
//        builder.setMessage("Du har blockerats av admin. Du kan inte använda funktionen i den här appen");
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            cmn.setPrefsData(this, "id", "");
            cmn.setPrefsData(this, "city", "");
            cmn.setPrefsData(this, "email", "");
            cmn.setPrefsData(this, "firstname", "");
            cmn.setPrefsData(this, "imagepath", "");
            cmn.setPrefsData(this, "username", "");
            cmn.setPrefsData(this, "lastname", "");
            cmn.setPrefsData(this, "state", "");
            cmn.setPrefsData(this, "city", "");
            cmn.setPrefsData(this, "telephone", "");
            cmn.setPrefsData(this, "usertype", "");
            cmn.setPrefsData(this, "role", "");
            cmn.setPrefsData(this, "coach_name", "");
            cmn.setPrefsData(this, "imagepath", "");
            cmn.setPrefsData(this, "banner", "");
            cmn.setPrefsData(this, "is_loggedIn", "");
            cmn.setPrefsData(this, "team_name", "");
            cmn.setPrefsData(this, "team_id", "");
            cmn.setPrefsData(this, "team_id_invite", "");
            cmn.setPrefsData(this, "team_name_invite", "");
            Common.INSTANCE.saveString("is_team", "");
            Common.INSTANCE.saveString("is_profile", "");
            Common.INSTANCE.saveString("is_reset", "");
            Common.INSTANCE.saveString("login_sub", "false");

            Intent in = new Intent(this, LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            this.startActivity(in);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            alertDialog.dismiss();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }

    public void logoutchange(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            alertDialog.dismiss();
            BlockFun();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        if(!((Activity) context).isFinishing()){
            alertDialog.show();
        }
    }
    public void dialogeAddTeam(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            alertDialog.dismiss();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        if(!((Activity) context).isFinishing()){
            alertDialog.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        cmn = new CommonMethods();

        OpenActivityAd=true;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String logo = preferences.getString("logo", "");
        String uname = preferences.getString("uname", "");
        String link = preferences.getString("link", "");
//        try {
//            if (preferences.getString("countDelete", "").equals("1")){
//                chatTable.clearTable();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        try{
            // AdShows(getIntent().getStringExtra("name"), getIntent().getStringExtra("logo"), getIntent().getStringExtra("link"));
             if (!uname.equals(""))
             AdShows(uname,logo,link);

         }catch (Exception e){
             e.printStackTrace();
         }
        AppCompatImageView sponser = findViewById(R.id.sponser);
   //     sponser.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce));

        //  MobileAds.initialize(this, getString(R.string.admob_app_id));
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        floatChatBtn = findViewById(R.id.group_cht);
      //  ProfileActivity_ivLine = findViewById(R.id.ProfileActivity_ivLine);

        Toolbar toolbar = findViewById(R.id.toolbar);
        text_count = findViewById(R.id.text_count);

        setSupportActionBar(toolbar);
        chatTable = new Chatfloatmessage(this).getInstance(this);

        arr_badge = chatTable.getDataNotificationData();
        if (arr_badge.size() > 0) {
            text_count.setText("" + arr_badge.size());

        } else {
            text_count.setVisibility(View.GONE);
        }
        notificationTable = new NotificationTable(this).getInstance(this);

        ViewPager viewPager = findViewById(R.id.viewpager);
        AppCompatTextView team = findViewById(R.id.teamname);
        setupViewPager(viewPager);

        String teamname = cmn.getPrefsData(context, "team_name", "");
        userid = cmn.getPrefsData(context, "id", "");
        Log.d("checkuserid",userid);
        role = cmn.getPrefsData(context, "usertype", "");
        String teamid = cmn.getPrefsData(context, "team_id", "");
        String imagepath = cmn.getPrefsData(context, "imagepath", "");
        String app = cmn.getPrefsData(context, "app_name", "");
        String firstname = cmn.getPrefsData(context, "firstname", "");
        String email = cmn.getPrefsData(context, "email", "");
        Log.d("checkuserid",userid);
        if (role.equals(ConsURL.admin) || role.equals(ConsURL.sub_admin)) {
            team.setText(app);

        } else {
            team.setText(app);
        }

        tabLayout = findViewById(R.id.tabs);
        log = findViewById(R.id.log);

        if (firstname.length() > 0) {
            String namea = String.valueOf(firstname.trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(context)
                    .load(imagepath)
                    .placeholder(drawable)
                    .centerCrop()
                    .into(log);
        }

       // Glide.with(context).load(imagepath).placeholder(R.drawable.user_profile).centerCrop().into(log);

        tabLayout.setupWithViewPager(viewPager);
        floatChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatTable.clearTable();
                Intent i = new Intent(context, GroupChatList.class);
                context.startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (getIntent() != null) {
            String flag = getIntent().getStringExtra("flag");
            if (flag != null) {
                if (flag.length() > 0) {

                    if (flag.equals("cal")) {
                        setupTabIcons2();
                        tabLayout.selectTab(tabLayout.getTabAt(1));
                    } else if (flag.equals("1")) {
                        setupTabIcons1();
                        tabLayout.selectTab(tabLayout.getTabAt(2));

                    } else if (flag.equals("menu")) {
                        setupTabIcons3();
                        tabLayout.selectTab(tabLayout.getTabAt(5));

                    }
                } else {
                    setupTabIcons();

                }
            } else {
                setupTabIcons();

            }

        }
       // ProfileActivity_ivLine.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_360_rotation));
        log.setOnClickListener(view -> {

            startActivity(new Intent(HomePage.this, ProfileActivity.class).putExtra("flag", "own").putExtra("id", userid));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });
        sponser.setOnClickListener(view -> {
            startActivity(new Intent(HomePage.this, SponserListActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                if (tab.getPosition() == 0) {
                    tab.setIcon(R.drawable.ic_homebb);
                }
                if (tab.getPosition() == 1) {
                    tab.setIcon(R.drawable.ic_cal_name2);
                }
                if (tab.getPosition() == 2) {
                    tab.setIcon(R.drawable.ic_inbox_name2);
                }
                if (tab.getPosition() == 3) {
                    tab.setIcon(R.drawable.ic_info_name2);
                }
                if (tab.getPosition() == 4) {
                    readAllNotificationApi();
                 //   notificationTable.clearTabledata();
                 //   notificationTable.clearTable();
                     view = Objects.requireNonNull(tabLayout.getTabAt(4)).view;
                    if (view.findViewById(R.id.count) != null) {
                        view.findViewById(R.id.count).setVisibility(View.GONE);
                       // ImageView imageView=view.findViewById(R.id.bell_img);
                        //imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_noti_name2));
                        Glide.with(context).load(R.drawable.ic_noti_name2).into((ImageView) view.findViewById(R.id.bell_img));

                    } else {
                        tab.setIcon(R.drawable.ic_noti_name2);
                    }
//                    Objects.requireNonNull(tabLayout.getTabAt(4)).setCustomView(view);
//                    tab.setIcon(R.drawable.notification_b);
                }
                if (tab.getPosition() == 5) {
                    tab.setIcon(R.drawable.ic_menub);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tab.setIcon(R.drawable.ic_home);
                }
                if (tab.getPosition() == 1) {
                    tab.setIcon(R.drawable.ic_calendar);
                }
                if (tab.getPosition() == 2) {
                    tab.setIcon(R.drawable.ic_inbox);
                }
                if (tab.getPosition() == 3) {
                    tab.setIcon(R.drawable.ic_info);
                }
                if (tab.getPosition() == 4) {
                 //   notificationTable.clearTabledata();
                    //notificationTable.clearTable();
                    readAllNotificationApi();
                    View view = Objects.requireNonNull(tabLayout.getTabAt(4)).view;
                    if (view.findViewById(R.id.count) != null) {
                        view.findViewById(R.id.count).setVisibility(View.GONE);
                        ImageView imageView=view.findViewById(R.id.bell_img);
                        imageView.setImageResource(R.drawable.ic_notification);
                      //  Glide.with(context).load(R.drawable.ic_notification).into((imageView));
                    } else {
                        tab.setIcon(R.drawable.ic_notification);
                    }
                }
                if (tab.getPosition() == 5) {
                    tab.setIcon(R.drawable.ic_menu);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setupTabIcons1() {
        int[] tabIcons = {
                R.drawable.ic_home,
                R.drawable.ic_calendar,
                R.drawable.ic_inbox_name2,
                R.drawable.ic_info,
                R.drawable.ic_notification,
                R.drawable.ic_menu

        };


        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(tabIcons[3]);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(tabIcons[4]);


        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(tabIcons[5]);
    }

    private void setupTabIcons3() {
        int[] tabIcons = {
                R.drawable.ic_home,
                R.drawable.ic_calendar,
                R.drawable.ic_inbox,
                R.drawable.ic_info,
                R.drawable.ic_notification,
                R.drawable.ic_menub

        };


        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(tabIcons[3]);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(tabIcons[4]);


        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(tabIcons[5]);
    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_homebb,
                R.drawable.ic_calendar,
                R.drawable.ic_inbox,
                R.drawable.ic_info,
                R.drawable.ic_notification,
                R.drawable.ic_menu

        };


        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(tabIcons[3]);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(tabIcons[4]);


        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(tabIcons[5]);
    }

    private void setupTabIcons2() {
        int[] tabIcons = {
                R.drawable.ic_home,
                R.drawable.ic_cal_name2,
                R.drawable.ic_inbox,
                R.drawable.ic_info,
                R.drawable.ic_notification,
                R.drawable.ic_menu

        };


        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(tabIcons[3]);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(tabIcons[4]);


        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(tabIcons[5]);
    }


    private void setupViewPager(ViewPager viewPager) {
        Bundle b = new Bundle();
        b.putString("flag", "2");
        NewsFragment mapFragment = new NewsFragment();
        mapFragment.setArguments(b);
        adapter.addFrag(mapFragment, "News");
        adapter.addFrag(new Calendar_Fragment(), "Calendar");
        adapter.addFrag(new Inbox_Fragment(), "Inbox");
        adapter.addFrag(new info_fragment(), "Information");
        adapter.addFrag(new Notification_Fragment(), "Notification");
        adapter.addFrag(new Menu_fragment(), "Menus");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return null;
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        arr.clear();
        arr_badge.clear();
        arr_badge = chatTable.getDataNotificationData();
        if (arr_badge.size() > 0) {
            text_count.setText("" + arr_badge.size());
        } else {
            text_count.setVisibility(View.GONE);
        }
        //  Check_version();
        String imagepath = cmn.getPrefsData(context, "imagepath", "");
        String firstname = cmn.getPrefsData(context, "firstname", "");
        if (firstname.length() > 0) {
            String namea = String.valueOf(firstname.trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(context)
                    .load(imagepath)
                    .placeholder(drawable)
                    .centerCrop()
                    .into(log);
        }

      //  Glide.with(context).load(imagepath).placeholder(R.drawable.user_profile).centerCrop().into(log);

        arr = notificationTable.getDataNotification();
        if (tabLayout.getSelectedTabPosition() != 4) {
            View view = Objects.requireNonNull(tabLayout.getTabAt(4)).view;
            if (view.findViewById(R.id.count) != null) {
                view.findViewById(R.id.count).setVisibility(View.VISIBLE);
                ((NotificationBadge) view.findViewById(R.id.count)).setNumber(arr.size());
//                Glide.with(context).load(R.drawable.ic_notification).into((AppCompatImageView) view.findViewById(R.id.bell_img));
//                view.findViewById(R.id.count).setVisibility(View.GONE);
//                Glide.with(context).load(R.drawable.ic_notification).into((AppCompatImageView) view.findViewById(R.id.bell_img));
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.notification_bell, null);
                ((NotificationBadge) view.findViewById(R.id.count)).setNumber(arr.size());
                Objects.requireNonNull(tabLayout.getTabAt(4)).setCustomView(view);
            }
        }
        registerReceiver(notificationReceiver, new IntentFilter(NOTIFY_BELL));
        registerReceiver(permission, new IntentFilter(NOTIFY_Permission));
        registerReceiver(block, new IntentFilter(BLOCK));
        registerReceiver(app_block, new IntentFilter(APP_BLOCK));
        registerReceiver(seen_notification, new IntentFilter(SEEN_NOTIFICATION));
        registerReceiver(app_freeze, new IntentFilter(APP_FREEZE));
        registerReceiver(change, new IntentFilter(Changerole));
        registerReceiver(report, new IntentFilter(Report));
        registerReceiver(adsshow, new IntentFilter(App_Ads));
        registerReceiver(chatReceiver, new IntentFilter(CHAT_FLOAT_NOTIFY_BELL));

       /* if (role.equals(ConsURL.members)||role.equals(ConsURL.sub_coach)) {
            sendBroadcast(new Intent(HomePage.NOTIFY_Permission));

        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationReceiver);
        unregisterReceiver(permission);
        unregisterReceiver(chatReceiver);
      //  unregisterReceiver(block);
        unregisterReceiver(adsshow);
       // unregisterReceiver(change);
        unregisterReceiver(report);
        unregisterReceiver(seen_notification);
      //  unregisterReceiver(app_block);
      //  unregisterReceiver(app_freeze);
    }

    @Override
    public void onBackPressed() {
        if (tabLayout.getSelectedTabPosition() != 0) {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        } else {
            finishAffinity();

            //finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenActivityAd = false;

    }

    private void AdShows(String txt, String path, String linkstr) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        // ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        View dialogView = LayoutInflater.from(this).inflate(R.layout.ad_shows, null, false);

        //https://admin.lusgo.se/StaticFiles/GeneralPost/Monday-27-June-2022_09-34-09_images9.jpg
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        ExpandableTextView show = dialogView.findViewById(R.id.text_show);
        AppCompatTextView link = dialogView.findViewById(R.id.link_ads);
        AppCompatImageView picture = dialogView.findViewById(R.id.ad_picture);
        AppCompatImageView close = dialogView.findViewById(R.id.close);

        if (path.length() > 0) {
            Glide.with(getApplicationContext()).load("https://admin.lusgo.se"+path).placeholder(R.drawable.loader).into(picture);
            picture.setVisibility(View.VISIBLE);

        } else {
            picture.setVisibility(View.GONE);
        }
        show.setText(Html.fromHtml(txt));
        show.initViews();
        show.setVisibility(View.VISIBLE);


        if (!linkstr.equals("null")) {
            link.setText(linkstr);
            link.setVisibility(View.VISIBLE);


        } else {
            link.setVisibility(View.GONE);

        }
        link.setOnClickListener(view -> {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("logo","");
            editor.putString("uname","");
            editor.putString("link","");
            editor.apply();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkstr));
            startActivity(browserIntent);
            alertDialog.dismiss();
        });
        close.setOnClickListener(view -> {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("logo","");
            editor.putString("uname","");
            editor.putString("link","");
            editor.apply();
            alertDialog.dismiss();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLinkURLAPI(String link) {
        String requestData;

        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("url", link);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    alertDialog.dismiss();
                    String object = result.getData();
                    context.startActivity(new Intent(context, LinkOpen.class).putExtra("url", object).putExtra("link", link));

                }
            } catch (Exception e) {
                alertDialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST1 + "geturl");

    }

    private void readAllNotificationApi() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        ReadAllNotificationModel model = new ReadAllNotificationModel("f76646abb2bb5408ecc6d8e36b64c9d8", "100", "0", userid);
        retrofit2.Call<ReadAllNotificationModel> call = apiService.readAllNotification(model);
        call.enqueue(new retrofit2.Callback<ReadAllNotificationModel>() {
            @Override
            public void onResponse(retrofit2.Call<ReadAllNotificationModel> call, retrofit2.Response<ReadAllNotificationModel> response) {
                ReadAllNotificationModel res = response.body();
                if (response.isSuccessful()) {
                    if (res.status.equals("true")) {
                        notificationTable.clearTabledata();
                        notificationTable.clearTable();

                    } else {
                        Toast.makeText(HomePage.this, "" + res.message, Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(HomePage.this, "Serverfel", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(retrofit2.Call<ReadAllNotificationModel> call, Throwable t) {
            }
        });
    }



}