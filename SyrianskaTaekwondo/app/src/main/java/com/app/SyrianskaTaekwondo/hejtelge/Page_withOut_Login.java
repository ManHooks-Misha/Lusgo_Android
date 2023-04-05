package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.SyrianskaTaekwondo.hejtelge.fragments.Calendar_Fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.NewsFragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.info_fragment;
import com.app.SyrianskaTaekwondo.hejtelge.model.ExpandableTextView;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utility.VersionChecker;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;


import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import vk.help.Common;
import vk.help.MasterActivity;

public class Page_withOut_Login extends MasterActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static final String App_Ads = "App_Ads";
    CommonMethods cmn = new CommonMethods();
    String email, deviceID;
    ViewPager viewPager;
    private AppCompatTextView name_txtview;
    private LinearLayout log;
  //  private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private List<Object> mRecyclerViewItems = new ArrayList();
    private String name;
    AppCompatImageView sponser;
    private int[] tabIcons = {
            R.drawable.home_bb,
            R.drawable.ic_calendar,
            R.drawable.ic_inf
    };
    AlertDialog alertDialog;

    public List<Object> getRecyclerViewItems() {
        return mRecyclerViewItems;
    }

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
            Common.INSTANCE.saveString("is_team", "false");
            Common.INSTANCE.saveString("is_profile", "false");
            Common.INSTANCE.saveString("is_reset", "false");
            Common.INSTANCE.saveString("login_sub", "false");

            Intent in = new Intent(this, LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            this.startActivity(in);
            alertDialog.dismiss();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }
    public void Check_version(){
        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(adsshow, new IntentFilter(App_Ads));
        Check_version();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(adsshow);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        email = cmn.getPrefsData(Page_withOut_Login.this, "username", "");
        String usertype = cmn.getPrefsData(Page_withOut_Login.this, "usertype", "");
        String is_loggedIn = cmn.getPrefsData(Page_withOut_Login.this, "is_loggedIn", "");
        String login = Common.INSTANCE.getString("login_sub");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String logo = preferences.getString("logo", "");
        String uname = preferences.getString("uname", "");
        String link = preferences.getString("link", "");

        try{
            // AdShows(getIntent().getStringExtra("name"), getIntent().getStringExtra("logo"), getIntent().getStringExtra("link"));
           if (!uname.equals(""))
            AdShows(uname,logo,link);

        }catch (Exception e){
            e.printStackTrace();
        }
        int PERMISSION_ALL = 1;
       /* String[] PERMISSIONS = {
                Manifest.permission.CAMERA
              *//* android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,*//*
        };*/

      /*  if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }*/

//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
//        }
        if (email.length() > 0) {
            if (is_loggedIn.equals("null")) {
                if (usertype.equals(ConsURL.coach)) {
                    if (!Common.INSTANCE.getString("is_team").equals("true")) {
                        startActivity(new Intent(Page_withOut_Login.this, AddTeam.class).putExtra("flag", "1"));
                        finish();
                    } else if (!Common.INSTANCE.getString("is_profile").equals("true")) {


                        startActivity(new Intent(Page_withOut_Login.this, EditProfile.class).putExtra("flag", "1"));
                        finish();
                    } else if (!Common.INSTANCE.getString("is_reset").equals("true")) {

                        startActivity(new Intent(Page_withOut_Login.this, ResetPassword.class).putExtra("flag", "1"));
                        finish();
                    } else {
                        startActivity(new Intent(Page_withOut_Login.this, HomePage.class));
                        finish();
                    }
                } else {
                    if (!Common.INSTANCE.getString("is_profile").equals("true")) {

                        startActivity(new Intent(Page_withOut_Login.this, EditProfile.class).putExtra("flag", "1"));
                        finish();
                    } else if (!Common.INSTANCE.getString("is_reset").equals("true")) {

                        startActivity(new Intent(Page_withOut_Login.this, ResetPassword.class).putExtra("flag", "1"));
                        finish();
                    } else {
                        startActivity(new Intent(Page_withOut_Login.this, HomePage.class));
                        finish();
                    }

                }

            } else {

                if (login.equals("true")) {
                    logout();
                } else {
                    if (!(usertype.equals(ConsURL.admin))) {
                        if (usertype.equals(ConsURL.coach)) {
                            String val = Common.INSTANCE.getString("is_team");
                            String is_reset = Common.INSTANCE.getString("is_reset");
                            if (Common.INSTANCE.getString("is_team") != null && Common.INSTANCE.getString("is_team").length() > 0) {
                                if (!val.equals("true")) {
                                    startActivity(new Intent(Page_withOut_Login.this, AddTeam.class).putExtra("flag", "blocked"));
                                    finish();
                                } else if (Common.INSTANCE.getString("is_reset") != null && Common.INSTANCE.getString("is_reset").length() > 0) {
                                    if (!is_reset.equals("true")) {

                                        startActivity(new Intent(Page_withOut_Login.this, ResetPassword.class).putExtra("flag", "1"));
                                        finish();
                                    } else {
                                        startActivity(new Intent(Page_withOut_Login.this, HomePage.class));
                                        finish();
                                    }
                                } else {
                                    startActivity(new Intent(Page_withOut_Login.this, HomePage.class));
                                    finish();
                                }
                            } else {
                                startActivity(new Intent(Page_withOut_Login.this, HomePage.class));
                                finish();
                            }
                        } else {
                            if (Common.INSTANCE.getString("is_reset") != null && Common.INSTANCE.getString("is_reset").length() > 0) {
                                String val = Common.INSTANCE.getString("is_reset");

                                if (!Common.INSTANCE.getString("is_reset").equals("true")) {

                                    startActivity(new Intent(Page_withOut_Login.this, ResetPassword.class).putExtra("flag", "1"));
                                    finish();


                                } else {
                                    startActivity(new Intent(Page_withOut_Login.this, HomePage.class));
                                    finish();
                                }
                            } else {
                                startActivity(new Intent(Page_withOut_Login.this, HomePage.class));
                                finish();
                            }
                        }
                    } else {
                        startActivity(new Intent(Page_withOut_Login.this, HomePage.class));

                        finish();
                    }
                }
            }
            getToken();

        }
        else {
            setContentView(R.layout.activity_page_with_out__login);
            FirebaseApp.initializeApp(this);

//
            //MobileAds.initialize(this, getString(R.string.admob_app_id));

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle("");
            viewPager = findViewById(R.id.viewpager);
            name_txtview = findViewById(R.id.name);
            log = findViewById(R.id.log);
            sponser = findViewById(R.id.sponser);
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                //  Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            deviceID = task.getResult();
                           getToken();
                            // Log and toast
                           String msg = getString(R.string.msg_token_fmt, deviceID);
                          //  Toast.makeText(Page_withOut_Login.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });


/*
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("LoginActivity ", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        deviceID = Objects.requireNonNull(task.getResult()).getToken();
                        cmn.setPrefsData(Page_withOut_Login.this, "token", deviceID);
                        getToken();
                        Common.INSTANCE.saveString("deviceID", deviceID);
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, deviceID);
                        Log.d("LoginActivity ", msg);
                        //        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    });
*/
            getUserAPI();
            setupViewPager(viewPager);

            TabLayout tabLayout = findViewById(R.id.tabs);

            tabLayout.setupWithViewPager(viewPager);
            if (getIntent() != null) {
                String flag = getIntent().getStringExtra("flag");
                if (flag != null) {
                    tabLayout.selectTab(tabLayout.getTabAt(1));

                }

            }
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
            Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);
            //setupTabIcons();
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.home_bb);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //do stuff here
                    if (tab.getPosition() == 0) {
                        tab.setIcon(R.drawable.home_bb);
                    }
                    if (tab.getPosition() == 1) {
                        tab.setIcon(R.drawable.ic_cal_name2);
                    }
                    if (tab.getPosition() == 2) {
                        tab.setIcon(R.drawable.information_detail);
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        tab.setIcon(R.drawable.ic_home);
                       // startActivity(new Intent(Page_withOut_Login.this,Page_withOut_Login.class));
                    }
                    if (tab.getPosition() == 1) {
                        tab.setIcon(R.drawable.ic_calendar);
                    }
                    if (tab.getPosition() == 2) {
                        tab.setIcon(R.drawable.ic_inf);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
//            if (cmn.isOnline(context)) {
//                createNewsAPI();
//            }


            sponser.setOnClickListener(view ->{ startActivity(new Intent(Page_withOut_Login.this, SponserListActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            });

            log.setOnClickListener(view -> {
                startActivity(new Intent(Page_withOut_Login.this, LoginActivity.class));
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                //  finish();
            });
        }
    }


    private void getUserAPI() {
        //arr.clear();
        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject object = new JSONObject(result.getData());

                    name = object.getString("name");
                    String image = object.getString("image");
                    String enableads = object.getString("enableads");
                    String t_link = object.getString("t_link");
                    new CommonMethods().setPrefsData(context, "app_name", name);
                    new CommonMethods().setPrefsData(context, "app_img", image);
                    new CommonMethods().setPrefsData(context, "t_link", t_link);
                    new CommonMethods().setPrefsData(context, "enableads", enableads);
                    name_txtview.setText(name);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST + "data_information");

    }

    private void getToken() {
        //arr.clear();
        String requestData;
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


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "public_tokens");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle b = new Bundle();
        b.putString("flag", "1");
        NewsFragment mapFragment = new NewsFragment();
        mapFragment.setArguments(b);
        adapter.addFrag(mapFragment, "nyheter");
        adapter.addFrag(new Calendar_Fragment(), "Kalender");
        adapter.addFrag(new info_fragment(), "Information");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull @NotNull Object object) {
            return POSITION_NONE;
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
            return mFragmentTitleList.get(position);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Nekad", Toast.LENGTH_LONG).show();
            }
        }
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
}
