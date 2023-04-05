package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.SyrianskaTaekwondo.hejtelge.customClass.CompressImg;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ProfilePojo;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import agency.tango.android.avatarview.views.AvatarView;
import id.zelory.compressor.Compressor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;
import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;
import vk.help.views.TextDrawable;

public class EditProfile extends MasterActivity {
    public static final int REQUEST_IMAGE = 100;
    public final int RequestPermissionCode = 1;
    private final int CAMERA_PICK = 1;
    AvatarView profile;
    int profileindex = 0;
    String email_verified = "true";
    String mob_verified = "true";
    String t_link;
    AppCompatEditText one, two, three, four, oneeml, twoeml, threeeml, foureml;
    private String role;
    private String userid;
    private String mob;
    private String email;
    private String imagepath = "null";
    private String banner="";
    private AppCompatTextView title, datapolicy;
    private AppCompatEditText txt_name, txt_role, txt_eml, txt_tel, txt_user;
    private CheckBox isprivacy;
    private LinearLayout ll_privacy;
    private AppCompatTextView link, txt_change, txt_delete,txtVerifiera,txtEmailVerifiera;
    //    private FrameLayout update;
    private AppCompatImageView background, backgroundselection, email_verify, mobile_verify,sampleImage;
    private String status;
    private String id;
    private String msg;
    private String is_loggedIn, stringtxt;
    private String profileBase64 = "";
    private String backgroundBase64 = "";
    private CommonMethods cmn;
    private int flag = 0;
    private long mLastClickTime = 0;
    private String deviceID, add, city, firstname, lastname, state, telephone, usertype;
    private AlertDialog alertDialog;
    private boolean isChangeProfile = false;
    private static String path;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    Uri image_uri;
    String imagepathPref,bannerPref;
    private static final int IMAGE_PICK_CAMERA_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        cmn = new CommonMethods();
        loadID();
        t_link = new CommonMethods().getPrefsData(EditProfile.this, "t_link", "");

        if (getIntent() != null) {
            stringtxt = getIntent().getStringExtra("flag");
        }
        setSupportActionBar(findViewById(R.id.toolbar));
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        String arr = cmn.getPrefsData(context, "team_arr", "");


        datapolicy.setOnClickListener(view -> {
            startActivity(new Intent(EditProfile.this, DataPolicy.class)
                    .putExtra("t_link",t_link)
                    .putExtra("data_policy","1"));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
           /* if (t_link != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(t_link));
                startActivity(i);
            }*/
            //   finish();
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        is_loggedIn = cmn.getPrefsData(EditProfile.this, "is_loggedIn", "");
        userid = cmn.getPrefsData(EditProfile.this, "id", "");
        role = cmn.getPrefsData(EditProfile.this, "role", "");
        usertype = cmn.getPrefsData(EditProfile.this, "usertype", "");
        String fname = cmn.getPrefsData(EditProfile.this, "firstname", "");
        String username = cmn.getPrefsData(EditProfile.this, "username", "");
        String lname = cmn.getPrefsData(EditProfile.this, "lastname", "");
        email = cmn.getPrefsData(EditProfile.this, "email", "");
        mob = cmn.getPrefsData(EditProfile.this, "telephone", "");
        imagepath = cmn.getPrefsData(EditProfile.this, "imagepath", "");
        bannerPref = cmn.getPrefsData(EditProfile.this, "banner", "");
        if (stringtxt.equals("1")) {
            title.setText("Fyll in dina uppgifter");
            txt_change.setVisibility(View.GONE);
            txt_delete.setVisibility(View.GONE);
            ll_privacy.setVisibility(View.VISIBLE);

            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(EditProfile.this, DataPolicy.class)
                            .putExtra("data_policy","0"));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            //Objects.requireNonNull(getSupportActionBar()).setTitle("Fyll in dina uppgifter");

        } else {

            title.setText("Redigera profil");
            txt_change.setVisibility(View.VISIBLE);
            if (usertype.equals(ConsURL.admin)) {
//              LinearLayout.LayoutParams parameter = new LinearLayout.LayoutParams(
//                      LinearLayout.LayoutParams.MATCH_PARENT,
//                      LinearLayout.LayoutParams.WRAP_CONTENT);
//                parameter.setMargins(75, 10, 75, 0); // left, top, right, bottom
//                txt_change.setLayoutParams(parameter);
                txt_delete.setVisibility(View.GONE);

            } else {
                txt_delete.setVisibility(View.VISIBLE);
            }
            ll_privacy.setVisibility(View.GONE);

            //Objects.requireNonNull(getSupportActionBar()).setTitle("Redigera profil");

        }
        try {
        String name = String.valueOf((fname.trim().charAt(0)));
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));

        Glide.with(this)
                .load(imagepath)
                .centerCrop()
                .placeholder(drawable)
                .into(profile);

        }catch (Exception e){
            e.printStackTrace();
        }
//        Glide.with(this)
//                .load(imagepath)
//                .fitCenter()
//                .placeholder(R.drawable.user_profile)
//                .into(profile);

        Glide.with(this)
                .load(bannerPref)
                .centerCrop()
                .placeholder(R.drawable.bnr_img)
                .into(background);


        txt_name.setText(fname + " " + lname);
        if (mob != null && !mob.equals("null")) {
            txt_tel.setText(mob);
        }
        txt_role.setText(role);
        txt_user.setText(username);
        txt_eml.setText(email);
        profile.setOnClickListener(view -> {
            flag = 1;

            if (checkAndRequestPermissions(EditProfile.this)) {

                chooseImage(EditProfile.this);
            }


        });
        backgroundselection.setOnClickListener(view -> {
            flag = 2;

            if (checkAndRequestPermissions(EditProfile.this)) {

                chooseImage(EditProfile.this);
            }

        });
        background.setOnClickListener(view -> {
            flag = 2;
            if (checkAndRequestPermissions(EditProfile.this)) {

                chooseImage(EditProfile.this);
            }

        });
        txt_change.setOnClickListener(view -> {
            Intent i = new Intent(context, ResetPassword.class);
            i.putExtra("flag", "2");
            context.startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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

            object.put("user_id", userid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(EditProfile.this, result -> {
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
                  //  cmn.setPrefsData(context, "imagepath", "");
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
                    cmn.showAlert(result.getMessage(), this);
                    // showCustomDialog(result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_account");
    }

    public void loadID() {
        txt_name = findViewById(R.id.txt_name);
        txt_eml = findViewById(R.id.txt_eml);
        txt_user = findViewById(R.id.txt_username);
        txt_role = findViewById(R.id.txt_role);
        email_verify = findViewById(R.id.email_verify);
        mobile_verify = findViewById(R.id.mobile_verify);
        profile = findViewById(R.id.pp_img);
        sampleImage = findViewById(R.id.sampleImage);
        txt_tel = findViewById(R.id.txt_tel);
        background = findViewById(R.id.pp);
        txt_change = findViewById(R.id.txt_change);
        txt_delete = findViewById(R.id.txt_delete);
        txtVerifiera = findViewById(R.id.txtVerifiera);
        txtEmailVerifiera = findViewById(R.id.txtEmailVerifiera);
        backgroundselection = findViewById(R.id.iv_selected1);
        //update = findViewById(R.id.ff_login);
        title = findViewById(R.id.txt);
        link = findViewById(R.id.link);
        isprivacy = findViewById(R.id.isprivacy);
        ll_privacy = findViewById(R.id.ll_privacy);
        datapolicy = findViewById(R.id.datapolicy);

        txt_eml.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              // Log.d("cehckfjaflksdj",s.toString());
                email_verify.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (!email.equals(s.toString())) {
                        email_verified = "false";
                        if (txtEmailVerifiera.getVisibility()==View.VISIBLE){
                            email_verify.setVisibility(View.GONE);
                        }
                       // email_verify.setVisibility(View.VISIBLE);
                      //  email_verify.setImageResource(R.drawable.invarifed);
                        txtEmailVerifiera.setVisibility(View.VISIBLE);
                        txtEmailVerifiera.setOnClickListener(v ->
                                getOtpAPI(s.toString()));

                    } else {
                        email_verified = "true";
                        email_verify.setVisibility(View.VISIBLE);
                        txtEmailVerifiera.setVisibility(View.GONE);
                        email_verify.setImageResource(R.drawable.varifed);


                    }
                } else {
                    email_verify.setVisibility(View.GONE);
                    email_verified = "true";

                }
            }
        });

        txt_tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mobile_verify.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (!mob.equals(s.toString())) {
                        mob_verified = "false";
                        if (txtVerifiera.getVisibility()==View.VISIBLE){
                            mobile_verify.setVisibility(View.GONE);
                        }
                      //  mobile_verify.setVisibility(View.VISIBLE);
                        txtVerifiera.setVisibility(View.VISIBLE);
                      //  mobile_verify.setImageResource(R.drawable.invarifed);
                        txtVerifiera.setOnClickListener(v ->
                                getMobileOtpAPI(email, s.toString()));


                    } else {
                        mobile_verify.setVisibility(View.VISIBLE);
                        txtVerifiera.setVisibility(View.GONE);
                        mob_verified = "true";
                        mobile_verify.setImageResource(R.drawable.varifed);
                    }
                } else {
                    mobile_verify.setVisibility(View.GONE);
                    mob_verified = "true";

                }
            }
        });

    }


    private void Profile(String first_name, String email1, String mob) {
        ProgressDialog mprogdialog = ProgressDialog.show(EditProfile.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);
        if (imagepath.length() > 0) {
            File file = new File(imagepath);
            int length1 = (int) file.length();
            byte[] bytes = new byte[length1];
            try {
                //fileInputStream.read(b);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    fileInputStream.read(bytes);
                }
                profileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                // upload.setText(namefile);
            } catch (Exception ignored) {

            }
        }
        if (banner.length() > 0) {
            File file1 = new File(banner);
            int length = (int) file1.length();
            byte[] bytes1 = new byte[length];
            try {
                FileInputStream fileInputStream = new FileInputStream(file1);
                try {
                    fileInputStream.read(bytes1);
                } finally {
                    fileInputStream.close();
                }
                backgroundBase64 = Base64.encodeToString(bytes1, Base64.DEFAULT);
            } catch (Exception e) {
                Log.e("", Objects.requireNonNull(e.getMessage()));
            }
        }
        Gson gson = new Gson();
        ProfilePojo asgn = new ProfilePojo();
        asgn.email = email1;
        asgn.access_key = ConsURL.accessKey;
        asgn.first_name = first_name;
        asgn.last_name = "";
        asgn.banner = backgroundBase64;
        asgn.profile_image = profileBase64;
       /* if (valueCam.equals("1")){
            asgn.profile_image = "";
            asgn.banner = backgroundBase64;
        }else {
                asgn.banner = backgroundBase64;
                asgn.profile_image = profileBase64;
        }*/
        asgn.telephone = mob;
        asgn.city = "";
        asgn.state = "";
        asgn.address = "";
        asgn.user_id = userid;
        asgn.is_checked = "1";


        String tset = gson.toJson(asgn);
        String url = ConsURL.BASE_URL_TEST + "update_profile";

        Call call = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                mprogdialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res;
                    if (response.body() != null) {
                        res = Objects.requireNonNull(response.body()).string();
                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");
                        msg = objvalue.optString("message");

                        if (status.equals("true")) {
                            JSONObject object = objvalue.getJSONObject("data");
                            id = object.getString("id");
                            email = object.getString("email");
                            firstname = object.getString("firstname");
                            imagepath = object.getString("profile_image");
                            lastname = object.getString("lastname");
                            state = object.getString("state");
                            city = object.getString("city");
                            add = object.getString("address");
                            telephone = object.getString("telephone");
                            usertype = object.getString("usertype");
                            banner = object.getString("banner_image");
                            role = object.getString("role");


                        } else {
                            cmn.showAlert(msg, EditProfile.this);
                        }

                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {
                            isChangeProfile = false;
                            Toast.makeText(EditProfile.this, "Profilen har uppdaterats", Toast.LENGTH_SHORT).show();
                            cmn.setPrefsData(EditProfile.this, "id", id);
                            cmn.setPrefsData(EditProfile.this, "city", city);
                            cmn.setPrefsData(EditProfile.this, "email", email);
                            cmn.setPrefsData(EditProfile.this, "firstname", firstname);
                            cmn.setPrefsData(EditProfile.this, "imagepath", imagepath);
                            cmn.setPrefsData(EditProfile.this, "lastname", lastname);
                            cmn.setPrefsData(EditProfile.this, "state", state);
                            cmn.setPrefsData(EditProfile.this, "city", city);
                            cmn.setPrefsData(EditProfile.this, "telephone", telephone);
                            cmn.setPrefsData(EditProfile.this, "usertype", usertype);
                            cmn.setPrefsData(EditProfile.this, "role", role);
                            cmn.setPrefsData(EditProfile.this, "banner", banner);

                            if (is_loggedIn.equals("null")) {
                                Common.INSTANCE.saveString("is_profile", "true");

                                startActivity(new Intent(EditProfile.this, ResetPassword.class).putExtra("flag", "1"));
                                finish();
                            } else {
                                startActivity(new Intent(EditProfile.this, ProfileActivity.class).putExtra("flag", "own").putExtra("id", userid).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                                finish();
                            }


                        } else {
                            mprogdialog.dismiss();

                            cmn.showAlert(msg, EditProfile.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
//            if (isChangeProfile) {
//
//                showErrorToast("Du har ändrat din bild men sparade inte din profil! Klicka på knappen \"Uppdatera profil\" för att fortsätta.");
//            } else {
//
//            }
            return true;
        }
        if (item.getItemId() == R.id.save) {

            String name = Objects.requireNonNull(txt_name.getText()).toString();
            email = Objects.requireNonNull(txt_eml.getText()).toString();
            mob = Objects.requireNonNull(txt_tel.getText()).toString();

            if (name.trim().length() > 0) {
                if (stringtxt.equals("1")) {
                    if (isprivacy.isChecked()) {
                        if (profileindex == 0 && imagepath.equals("null")) {
                            profileindex++;

                            showAlertSkip("Det vore trevligt med någon profilbild", context, name);
                            return true;
                        }

                        if (profileindex == 1) {
                            if (stringtxt.equals("1")) {

                                if (email_verified.equals("true") && mob_verified.equals("false")) {
                                    cmn.showAlert("Verifiera mobilnummer", EditProfile.this);
                                    return true;
                                } else if (email_verified.equals("false") && mob_verified.equals("true")) {
                                    cmn.showAlert("Verifiera e-postadressen", EditProfile.this);
                                    return true;
                                } else if (email_verified.equals("false") && mob_verified.equals("false")) {
                                    cmn.showAlert("Verifiera mobilnummer och e-postadress", EditProfile.this);
                                    return true;
                                } else if (email_verified.equals("true") && mob_verified.equals("true")) {
                                    Profile(name, email, mob);
                                }


                                // Profile(name, email, mob);

                            }
                            profileindex = 0;
                        }

                        if (!imagepath.equals("null")) {
                            if (stringtxt.equals("1")) {


                                if (email_verified.equals("true") && mob_verified.equals("false")) {
                                    cmn.showAlert("Verifiera mobilnummer", EditProfile.this);
                                    return true;
                                } else if (email_verified.equals("false") && mob_verified.equals("true")) {
                                    cmn.showAlert("Verifiera e-postadressen", EditProfile.this);
                                    return true;
                                } else if (email_verified.equals("false") && mob_verified.equals("false")) {
                                    cmn.showAlert("Verifiera mobilnummer och e-postadress", EditProfile.this);
                                    return true;
                                } else if (email_verified.equals("true") && mob_verified.equals("true")) {
                                    Profile(name, email, mob);

                                }


                            }
                        }
                    } else {
                        cmn.showAlert("Godkänn användarvillkor samt datapolicy", EditProfile.this);

                        // Toast.makeText(EditProfile.this, "Godkänn sekretesspolicyn", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    if (profileindex == 0 && imagepath.equals("null")) {
                        profileindex++;

                        showAlertSkip("Det vore trevligt med någon profilbild", context, name);
                        return true;
                    } else {

                        if (email_verified.equals("true") && mob_verified.equals("false")) {
                            cmn.showAlert("Verifiera mobilnummer", EditProfile.this);
                            return true;
                        } else if (email_verified.equals("false") && mob_verified.equals("true")) {
                            cmn.showAlert("Verifiera e-postadressen", EditProfile.this);
                            return true;
                        } else if (email_verified.equals("false") && mob_verified.equals("false")) {
                            cmn.showAlert("Verifiera mobilnummer och e-postadress", EditProfile.this);
                            return true;
                        } else if (email_verified.equals("true") && mob_verified.equals("true")) {
                            Profile(name, email, mob);

                        }


                    }

                }

            } else {
                cmn.showAlert("Vänligen ange namn",this);
               // Toast.makeText(EditProfile.this, "Vänligen ange namn", Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save);
        if (stringtxt.equals("1")) {// extract the menu item here
            mColorFullMenuBtn.setTitle("Nästa");
        }
        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }
        return true;
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(EditProfile.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
/*                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(EditProfile.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(EditProfile.this);
                }
                break;
        }
    }

    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Ta en bild", "Välj från galleriet"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Ta en bild")) {

                    // Open the camera and get the photo
                    /*Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_IMAGE);*/
                    pickCamera();
                } else if (optionsMenu[i].equals("Välj från galleriet")) {
                    // choose from  external storage
                    ImagePicker.create(EditProfile.this).theme(R.style.AppTheme_No).single().toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

                }
            }
        });
        builder.show();
    }
    private void pickCamera() {

        //intent to take image from camera, it will also be save to storage to get high quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPick"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text"); //title of the picture
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            if (flag == 1) {
                if (images.size() > 0) {
                    Image dataimg = images.get(0);
                    String path = dataimg.getPath();
                  //  Glide.with(context).load(path).into(profile);
                    File compressedImageFile = Compressor.getDefault(this).compressToFile(new File(path));
                    UCrop.Options option = new UCrop.Options();
                    option.setToolbarColor(Color.parseColor("#ffffff"));
                    option.setToolbarTitle("          Beskär bild");
                    option.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    option.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    option.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white));
                    option.setCropGridColumnCount(0);
                    option.setCropGridRowCount(0);
                    option.setShowCropFrame(false);
                    option.setCircleDimmedLayer(true);
                    Uri destinationUri = Uri.fromFile(new File(getFilesDir().getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                    UCrop.of(Uri.fromFile(compressedImageFile), destinationUri).withAspectRatio(2f, 1.5f).withOptions(option).start(this);


                }
            } else {
                if (images.size() > 0) {
                    Image dataimg = images.get(0);
                    String path = dataimg.getPath();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
                    //      bitmap = Bitmap.createScaledBitmap(bitmap,1000,1000,true);

                    try {
                        ExifInterface exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        Log.d("EXIF", "Exif: " + orientation);
                        Matrix matrix = new Matrix();
                        if (orientation == 6) {
                            matrix.postRotate(90);
                        } else if (orientation == 3) {
                            matrix.postRotate(180);
                        } else if (orientation == 8) {
                            matrix.postRotate(270);
                        }
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
                    } catch (Exception e) {

                    }
                    bitmap = cmn.getResizedBitmap(bitmap, 1000);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    // bitmap.compress(Bitmap.CompressFormat.PNG, 0, os) ;// YOU can also save it in JPEG
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    Uri uri = cmn.getImageUri(this, bitmap);
                    UCrop.Options option = new UCrop.Options();
                    option.setToolbarTitle("          Beskär bild");
                    option.setToolbarColor(ContextCompat.getColor(EditProfile.this, R.color.colorPrimaryDark));
                    option.setStatusBarColor(ContextCompat.getColor(EditProfile.this, R.color.colorPrimaryDark));
                    option.setToolbarWidgetColor(ContextCompat.getColor(EditProfile.this, R.color.white));
                    Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir(null).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                    UCrop.of(uri, destinationUri).withAspectRatio(3f, 1.5f).withOptions(option).start(this);
                }
            }
        }
        if (requestCode == REQUEST_IMAGE ) {
            if (resultCode == Activity.RESULT_OK && flag==1) {
                sampleImage.setImageURI(image_uri);
                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) sampleImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                File finalFile = new File(getRealPathFromURI(getImageUri(this, bitmap)));
                imagepath = String.valueOf(finalFile);
                File compressedImageFile = CompressImg.getDefault(this).compressToFile(new File(imagepath));
                //CompressFile compressFile = new CompressFile();
                assert imagepath != null;
                // File path = compressFile.getCompressedImageFile(new File(imagepath1), EditProfile.this);
                    imagepath = compressedImageFile.getAbsolutePath();

                    Glide.with(context).load(imagepath).into(profile);
                    // imagepath = resultUri.getPath();
                    flag = 0;
                    isChangeProfile = true;
            }else if (resultCode == Activity.RESULT_OK && flag==2){
                sampleImage.setImageURI(image_uri);
                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) sampleImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                File finalFile = new File(getRealPathFromURI(getImageUri(this, bitmap)));
                banner = String.valueOf(finalFile);
                File compressedImageFile = CompressImg.getDefault(this).compressToFile(new File(banner));
                banner = compressedImageFile.getAbsolutePath();
                    Glide.with(context).load(banner).centerCrop().into(background);
                    flag = 0;
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            String imagepath1 = resultUri.getPath();
            assert imagepath1 != null;


            if (flag == 1) {
                imagepath = imagepath1;
                Glide.with(context).load(imagepath).into(profile);
                flag = 0;
                isChangeProfile = true;

            }
            if (flag == 2) {
                banner = imagepath1;
                Glide.with(context).load(banner).into(background);
                flag = 0;

            }


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
            showErrorToast(cropError.getMessage());
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
       // Log.e("path", path);

        return Uri.parse(path);
    }

    public void showAlertSkip(String message, Context context, String name) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(Html.fromHtml(message)).setCancelable(false).setNegativeButton("Hoppa över", (dialog, which) -> {
                Profile(name, email, mob);

            })
                    .setPositiveButton("Ja", (dialog, id) -> {


                    });
            try {
                builder.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void showCustomDialog(String email) {
        AppCompatImageView close;
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_otp_verify, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        oneeml = dialogView.findViewById(R.id.one);
        twoeml = dialogView.findViewById(R.id.two);
        threeeml = dialogView.findViewById(R.id.three);
        foureml = dialogView.findViewById(R.id.four);
        close = dialogView.findViewById(R.id.close);
        close.setVisibility(View.VISIBLE);
        oneeml.addTextChangedListener(new GenericTextWatcher(oneeml));
        twoeml.addTextChangedListener(new GenericTextWatcher(twoeml));
        threeeml.addTextChangedListener(new GenericTextWatcher(threeeml));
        foureml.addTextChangedListener(new GenericTextWatcher(foureml));
        AppCompatTextView ok = dialogView.findViewById(R.id.verify);
        AppCompatTextView resend = dialogView.findViewById(R.id.resend);
        resend.setVisibility(View.GONE);
        close.setVisibility(View.VISIBLE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ok.setOnClickListener(view -> {
            String otp = Objects.requireNonNull(oneeml.getText()).toString() + Objects.requireNonNull(twoeml.getText()).toString() + Objects.requireNonNull(threeeml.getText()).toString() + foureml.getText().toString();

            getVerifyEmailAPI(email, mob, otp);
           /* startActivity(new Intent(EditProfile.this, HomePage.class));
            finish();*/
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void showCustomDialogMOB(String mob) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.otp_mob, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        one = dialogView.findViewById(R.id.one);
        two = dialogView.findViewById(R.id.two);
        three = dialogView.findViewById(R.id.three);
        four = dialogView.findViewById(R.id.four);
        AppCompatImageView close = dialogView.findViewById(R.id.close);
        one.addTextChangedListener(new GenericTextWatchermob(one));
        two.addTextChangedListener(new GenericTextWatchermob(two));
        three.addTextChangedListener(new GenericTextWatchermob(three));
        four.addTextChangedListener(new GenericTextWatchermob(four));
        AppCompatTextView ok = dialogView.findViewById(R.id.verify);
        AppCompatTextView resend = dialogView.findViewById(R.id.resend);
        resend.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ok.setOnClickListener(view -> {
            String otp = Objects.requireNonNull(one.getText()).toString() + Objects.requireNonNull(two.getText()).toString() + Objects.requireNonNull(three.getText()).toString() + Objects.requireNonNull(four.getText()).toString();

            getVerifyMobileAPI(email, mob, otp);

        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void getOtpAPI(String email) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");

            object.put("user_id", userid);

            object.put("email", email);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    showCustomDialog(email);

                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "VerifyEmailAddress");
    }

    private void getVerifyEmailAPI(String email, String mob, String otp) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");

            object.put("user_id", userid);

            object.put("email", email);
            object.put("otp", otp);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    //showCustomDialog(email);
                    alertDialog.dismiss();
                    Profile(Objects.requireNonNull(txt_name.getText()).toString(), email, mob);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "ConfirmEmailAddress");
    }

    private void getMobileOtpAPI(String email, String mob) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("phone", mob);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    showCustomDialogMOB(mob);

                }else {
                    cmn.showAlert(result.getMessage(),this);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "VerifyPhoneNumber");
    }

    private void getVerifyMobileAPI(String email, String mob, String otp) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("phone", mob);
            object.put("otp", otp);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    //showCustomDialog(email);
                    alertDialog.dismiss();
                    Profile(Objects.requireNonNull(txt_name.getText()).toString(), email, mob);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "ConfirmPhoneNumber");
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.one:
                    if (text.length() == 1)
                        twoeml.requestFocus();
                    break;
                case R.id.two:
                    if (text.length() == 1)
                        threeeml.requestFocus();
                    else if (text.length() == 0)
                        oneeml.requestFocus();
                    break;
                case R.id.three:
                    if (text.length() == 1)
                        foureml.requestFocus();
                    else if (text.length() == 0)
                        twoeml.requestFocus();
                    break;
                case R.id.four:
                    if (text.length() == 0)
                        threeeml.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }

    public class GenericTextWatchermob implements TextWatcher {
        private View view;

        private GenericTextWatchermob(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.one:
                    if (text.length() == 1)
                        two.requestFocus();
                    break;
                case R.id.two:
                    if (text.length() == 1)
                        three.requestFocus();
                    else if (text.length() == 0)
                        one.requestFocus();
                    break;
                case R.id.three:
                    if (text.length() == 1)
                        four.requestFocus();
                    else if (text.length() == 0)
                        two.requestFocus();
                    break;
                case R.id.four:
                    if (text.length() == 0)
                        three.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }


}
