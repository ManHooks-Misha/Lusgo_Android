package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.app.SyrianskaTaekwondo.hejtelge.database.NotificationTable;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.login;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

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
import vk.help.Common;
import vk.help.MasterActivity;

public class LoginActivity extends MasterActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    AppCompatImageView img_logo, eye, eyehide;
    String team_id;
    View v_email;
    List<String> arr_roleteam = new ArrayList<>();
    LinearLayout pwd;
    ArrayList<HashMap<String, String>> arrteams = new ArrayList<>();
    CommonMethods cmn = new CommonMethods();
    AppCompatEditText one, two, three, four;
    private AppCompatTextView info, info1, login_pass;
    private AppCompatTextView forgot, sign_with_otp;
    private AppCompatTextView ff_login;
    private long mLastClickTime = 0;
    private JSONArray arr_team;
    private AppCompatEditText username, password;
    private String deviceID, appname, appimg, Is_Coach, t_link = "", status, user, news, message, event, role, role_id, blockflag, is_loggedIn, id, add, city, msg, email, firstname, imagepath, lastname, state, telephone, banner, usertype;
    private AlertDialog alertDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(LoginActivity.this, Page_withOut_Login.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        loadID();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //  Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        deviceID = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, deviceID);
                        // Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        //  Objects.requireNonNull(getSupportActionBar()).setTitle("Logga in");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        appname = new CommonMethods().getPrefsData(LoginActivity.this, "app_name", "");
        t_link = new CommonMethods().getPrefsData(LoginActivity.this, "t_link", "");
        appimg = new CommonMethods().getPrefsData(LoginActivity.this, "app_img", "");
        //     ll_logo.setText(appname);
        Glide.with(this)
                .load(appimg)

                .placeholder(R.drawable.bnr_img)
                .centerCrop()
                .into(img_logo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        info.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, DataPolicy.class)
                    .putExtra("data_policy", "0"));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            //   finish();
        });
        password.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                if (isValidate()) {

                    String user = Objects.requireNonNull(username.getText()).toString();
                    String pass = Objects.requireNonNull(password.getText()).toString();

                    if (cmn.isOnline(LoginActivity.this)) {

                        LoginRequest(user, pass, deviceID);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            return false;
        });

        info1.setOnClickListener(view -> {
            try {
                startActivity(new Intent(LoginActivity.this, DataPolicy.class)
                        .putExtra("t_link", t_link)
                        .putExtra("data_policy", "1"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } catch (Exception e) {
                e.printStackTrace();
                startActivity(new Intent(LoginActivity.this, DataPolicy.class)
                        .putExtra("t_link", "")
                        .putExtra("data_policy", "1"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }


        });

        eyehide.setOnClickListener(view -> {
            if (Objects.requireNonNull(password.getText()).length() > 0) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eye.setVisibility(View.VISIBLE);
                eyehide.setVisibility(View.GONE);
                password.setSelection(password.length());
            }

        });
        eye.setOnClickListener(view -> {
            if (Objects.requireNonNull(password.getText()).length() > 0) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eye.setVisibility(View.GONE);
                eyehide.setVisibility(View.VISIBLE);
                password.setSelection(password.length());
            }
        });

        ff_login.setOnClickListener(view -> {
            if (isValidate()) {

                String user = Objects.requireNonNull(username.getText()).toString();
                String pass = Objects.requireNonNull(password.getText()).toString();

                if (cmn.isOnline(LoginActivity.this)) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    LoginRequest(user, pass, deviceID);
                } else {
                    Toast.makeText(LoginActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                }
            }
        });
        forgot.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, LoginwithOTP.class));

            //    showCustomDialog();


        });
    }

    public void loadID() {
        info = findViewById(R.id.info);
        info1 = findViewById(R.id.info1);
        login_pass = findViewById(R.id.login_pass);
        eye = findViewById(R.id.eye);
        v_email = findViewById(R.id.v_email);
        eyehide = findViewById(R.id.eyehide);
        username = findViewById(R.id.edit_text_email);
        sign_with_otp = findViewById(R.id.sign_with_otp);
        password = findViewById(R.id.edit_text_pass);
        //      ll_logo = findViewById(R.id.ll_logo);
        img_logo = findViewById(R.id.img_logo);
        pwd = findViewById(R.id.ll_pwd);
        AppCompatTextView details_info = findViewById(R.id.details_info);
        ff_login = findViewById(R.id.login);
        forgot = findViewById(R.id.forgot);

        login_pass.setOnClickListener(v -> {
            if (Objects.requireNonNull(username.getText()).toString().isEmpty()) {
                username.requestFocus();
                username.setError("Vänligen ange din e-postadress");
                return;
            }
            if (username.getText().toString().length() > 0) {
                pwd.setVisibility(View.VISIBLE);
                pwd.requestFocus();
                username.setVisibility(View.GONE);
                ff_login.setVisibility(View.VISIBLE);
                v_email.setVisibility(View.GONE);
                login_pass.setVisibility(View.GONE);
            }
        });

        sign_with_otp.setOnClickListener(v -> {
            if (Objects.requireNonNull(username.getText()).toString().isEmpty()) {
                username.requestFocus();
                username.setError("Vänligen ange din e-postadress");
                return;
            }
            if (username.getText().toString().length() > 0) {
                getOtpAPI(username.getText().toString());
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

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, deviceID);
                    Log.d("LoginActivity ", msg);
                    //        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                });
*/
        details_info.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, Page_withOut_Login.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            finish();
        });
    }

    private boolean isValidate() {
        if (Objects.requireNonNull(username.getText()).toString().isEmpty()) {
            username.requestFocus();
            username.setError("Vänligen ange din e-postadress");
            return false;
        }
        if (Objects.requireNonNull(password.getText()).toString().isEmpty()) {
            password.requestFocus();

            password.setError("Vänligen fyll i ditt lösenord");

            return false;
        }
        return true;
    }


    private void LoginRequest(String username, String pass, String devicetoken) {
        ProgressDialog mprogdialog = ProgressDialog.show(LoginActivity.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);

        Gson gson = new Gson();
        login asgn = new login();
        asgn.username = username;
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.password = pass;
        asgn.device_token = devicetoken;
        asgn.device_type = "A";

        String tset = gson.toJson(asgn);
//       String url = "https://mapzapp.swadhasoftwares.com/api/Authenticate/Login";
        String url = ConsURL.BASE_URL_TEST + "login";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.code() == 200) {
                        res = Objects.requireNonNull(response.body()).string();

                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");

                        msg = objvalue.optString("message");
                        blockflag = objvalue.optString("block");

                        if (status.equals("true")) {
                            Common.INSTANCE.saveString("Device_id", deviceID);
                            NotificationTable notificationTable = new NotificationTable(LoginActivity.this).getInstance(LoginActivity.this);
                            notificationTable.clearTabledata();
                            // notificationTable.clearTable();
                            JSONObject object = objvalue.getJSONObject("data");
                            id = object.getString("id");
                            user = object.getString("username");
                            email = object.getString("email");
                            firstname = object.getString("first_name");
                            imagepath = object.getString("profile_image");
                            lastname = object.getString("last_name");
                            state = object.getString("state");
                            city = object.getString("city");
                            add = object.getString("address");
                            telephone = object.getString("telephone");
                            usertype = object.getString("usertype");
                            banner = object.getString("banner");
                            role = object.getString("role");
                          String  adminRolId = object.getString("role_id");
                            cmn.setPrefsData(context, "adminRolId", adminRolId);
                            Is_Coach = object.getString("is_Coach");

                            if (usertype.equals("2") || usertype.equals("5")) {
                                Is_Coach = "true";
                            }
                            is_loggedIn = object.getString("is_loggedIn");
                            if (usertype.equals("3") || usertype.equals("4") || usertype.equals("6")) {
                                arr_team = object.getJSONArray("teams");
                                JSONArray jsonObject = object.getJSONArray("teams");
                                if (arr_team.length() > 0) {
                                    // for (int i = 0; i < jsonObject.length(); i++) {

                                    JSONObject jsonObject1 = jsonObject.getJSONObject(0);
                                    String team_id = jsonObject1.getString("team_id");
                                    String team_name = jsonObject1.getString("name");
                                    String role_id = jsonObject1.getString("role_id");
                                    Log.e("TeamName", team_id);
                                    cmn.setPrefsData(context, "team_id_invite", team_id);
                                    cmn.setPrefsData(context, "team_name_invite", team_name);
                                    cmn.setPrefsData(context, "role_id", role_id);


                                    // }
                                }
                            }
                            if (usertype.equals("4")) {
                                JSONObject obj = object.getJSONObject("permissions");
                                news = obj.getString("news");
                                message = obj.getString("message");
                                event = obj.getString("event");
                            }
                            // responseText = "Visit saved";
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();
                        if (status.equals("true")) {
                            cmn.setPrefsData(LoginActivity.this, "id", id);
                            cmn.setPrefsData(LoginActivity.this, "city", city);
                            cmn.setPrefsData(LoginActivity.this, "email", email);
                            cmn.setPrefsData(LoginActivity.this, "firstname", firstname);
                            cmn.setPrefsData(LoginActivity.this, "imagepath", imagepath);
                            cmn.setPrefsData(LoginActivity.this, "lastname", lastname);
                            cmn.setPrefsData(LoginActivity.this, "state", state);
                            cmn.setPrefsData(LoginActivity.this, "city", city);
                            cmn.setPrefsData(LoginActivity.this, "telephone", telephone);
                            cmn.setPrefsData(LoginActivity.this, "usertype", usertype);
                            cmn.setPrefsData(LoginActivity.this, "username", user);
                            cmn.setPrefsData(LoginActivity.this, "role", role);
                            cmn.setPrefsData(LoginActivity.this, "imagepath", imagepath);
                            cmn.setPrefsData(LoginActivity.this, "banner", banner);
                            cmn.setPrefsData(LoginActivity.this, "is_loggedIn", is_loggedIn);
                            cmn.setPrefsData(LoginActivity.this, "blockflag", blockflag);
                            cmn.setPrefsData(LoginActivity.this, "is_team", "false");
                            cmn.setPrefsData(LoginActivity.this, "is_profile", "false");
                            cmn.setPrefsData(LoginActivity.this, "is_reset", "false");
                            cmn.setPrefsData(LoginActivity.this, "Is_Coach", Is_Coach);
                            Common.INSTANCE.saveString("pass", pass);

                            if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                                cmn.setPrefsData(LoginActivity.this, "team_id", "");

                            }
                            if (!is_loggedIn.equals("null")) {
                                if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                                    startActivity(new Intent(LoginActivity.this, HomePage.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    //    finish();
                                }
                                if (usertype.equals(ConsURL.coach)) {
                                    if (blockflag.equals("true")) {
                                        Common.INSTANCE.saveString("is_team", "false");

                                        startActivity(new Intent(LoginActivity.this, AddTeam.class).putExtra("flag", "blocked").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                        finish();
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, HomePage.class));
                                        finish();
                                    }


                                } else {
                                    if (blockflag.equals("true")) {
                                        startActivity(new Intent(LoginActivity.this, ResetPassword.class).putExtra("flag", "1").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();

                                    } else {
                                        startActivity(new Intent(LoginActivity.this, HomePage.class));
                                        finish();
                                    }
                                }
                            } else {
                                if (usertype.equals(ConsURL.coach)) {
                                    startActivity(new Intent(LoginActivity.this, AddTeam.class).putExtra("flag", "1"));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, EditProfile.class).putExtra("flag", "1"));
                                    finish();
                                }

                            }
                        } else {
                            cmn.showAlert(msg, LoginActivity.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private void getforgotAPI(String email) {
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("email", email);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(LoginActivity.this, result -> {
            try {
                if (result.isStatus()) {
                    //  cmn.showAlert("En kod har skickats till din e-postadress eller sms till dig. Följ instruktionerna för att byta ditt lösenord", context);
                    alertDialog.dismiss();
                    showCustomDialogForgot(email);

                } else {

                    Common.INSTANCE.showToast(LoginActivity.this, result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "forgot_request");
    }


    private void showCustomDialogForgot(String email) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.news_alert, viewGroup, false);

        AppCompatTextView txt = dialogView.findViewById(R.id.create);
        txt.setText("En kod har skickats till din e-postadress eller sms till dig. Följ instruktionerna för att byta ditt lösenord");
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {

            startActivity(new Intent(LoginActivity.this, OtpVerify.class).putExtra("email", email));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void getuserRoleAPI(String userid, String teamid, AlertDialog dialog) {
        //arr.clear();
        String requestData;
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

        new NetworkCall(LoginActivity.this, result -> {
            try {
                if (result.isStatus()) {

                    //  cmn.showAlert("En kod har skickats till din e-postadress eller sms till dig. Följ instruktionerna för att byta ditt lösenord", context);
                    JSONObject object = new JSONObject(result.getData());
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    cmn.setPrefsData(LoginActivity.this, "usertype", object.getString("role_id"));
                    cmn.setPrefsData(LoginActivity.this, "role", object.getString("role"));
                    if (is_loggedIn.equals("null")) {
                        startActivity(new Intent(LoginActivity.this, EditProfile.class).putExtra("flag", "1"));
                        finish();
                    } else {
                        if (blockflag.equals("true")) {
                            startActivity(new Intent(LoginActivity.this, ResetPassword.class).putExtra("flag", "1").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();

                        } else {
                            startActivity(new Intent(LoginActivity.this, HomePage.class));
                            finish();
                        }

                    }
                } else {
                    Common.INSTANCE.showToast(LoginActivity.this, result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "userRole");
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
            object.put("username", email);
            object.put("device_token", deviceID);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    showCustomDialog(email);


                } else {
                    cmn.showAlert(result.getMessage(), LoginActivity.this);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "Login_Otp_request");
    }


    private void showCustomDialog(String email) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_otp_verify, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        one = dialogView.findViewById(R.id.one);
        two = dialogView.findViewById(R.id.two);
        three = dialogView.findViewById(R.id.three);
        four = dialogView.findViewById(R.id.four);

        AppCompatTextView ok = dialogView.findViewById(R.id.verify);
        AppCompatTextView resend = dialogView.findViewById(R.id.resend);
        AppCompatImageView close = dialogView.findViewById(R.id.close);
        close.setVisibility(View.GONE);
        one.addTextChangedListener(new GenericTextWatcher(one));
        two.addTextChangedListener(new GenericTextWatcher(two));
        three.addTextChangedListener(new GenericTextWatcher(three));
        four.addTextChangedListener(new GenericTextWatcher(four));
        resend.setVisibility(View.GONE);

        ok.setOnClickListener(view -> {
            String otp = one.getText().toString() + two.getText().toString() + three.getText().toString() + four.getText().toString();

            getVerifyEmailAPI(email, otp);
            /* */
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getVerifyEmailAPI(String email1, String otp) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");

            object.put("device_token", deviceID);

            object.put("username", email1);
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
                    JSONObject object = new JSONObject(result.getData());
                    // status = objvalue.optString("status");

                    //  msg = objvalue.optString("message");
                    blockflag = object.optString("block");

                    Common.INSTANCE.saveString("Device_id", deviceID);
                    NotificationTable notificationTable = new NotificationTable(LoginActivity.this).getInstance(LoginActivity.this);
                    notificationTable.clearTabledata();
                    // JSONObject object = objvalue.getJSONObject("data");
                    id = object.getString("id");
                    user = object.getString("username");
                    email = object.getString("email");
                    firstname = object.getString("first_name");
                    imagepath = object.getString("profile_image");
                    lastname = object.getString("last_name");
                    state = object.getString("state");
                    city = object.getString("city");
                    add = object.getString("address");
                    telephone = object.getString("telephone");
                    usertype = object.getString("usertype");
                    banner = object.getString("banner");
                    role = object.getString("role");
                    Is_Coach = object.getString("is_Coach");
                    if (usertype.equals("2") || usertype.equals("5")) {
                        Is_Coach = "true";
                    }
                    is_loggedIn = object.getString("is_loggedIn");
                    if (usertype.equals("3") || usertype.equals("4") || usertype.equals("6")) {
                        arr_team = object.getJSONArray("teams");
                    }
                    if (usertype.equals("4")) {
                        JSONObject obj = object.getJSONObject("permissions");
                        news = obj.getString("news");
                        message = obj.getString("message");
                        event = obj.getString("event");
                    }
                    // responseText = "Visit saved";

                    cmn.setPrefsData(LoginActivity.this, "id", id);
                    cmn.setPrefsData(LoginActivity.this, "city", city);
                    cmn.setPrefsData(LoginActivity.this, "email", email);
                    cmn.setPrefsData(LoginActivity.this, "firstname", firstname);
                    cmn.setPrefsData(LoginActivity.this, "imagepath", imagepath);
                    cmn.setPrefsData(LoginActivity.this, "lastname", lastname);
                    cmn.setPrefsData(LoginActivity.this, "state", state);
                    cmn.setPrefsData(LoginActivity.this, "Is_Coach", Is_Coach);
                    cmn.setPrefsData(LoginActivity.this, "city", city);
                    cmn.setPrefsData(LoginActivity.this, "telephone", telephone);
                    cmn.setPrefsData(LoginActivity.this, "usertype", usertype);
                    cmn.setPrefsData(LoginActivity.this, "username", user);
                    cmn.setPrefsData(LoginActivity.this, "role", role);
                    cmn.setPrefsData(LoginActivity.this, "imagepath", imagepath);
                    cmn.setPrefsData(LoginActivity.this, "banner", banner);
                    cmn.setPrefsData(LoginActivity.this, "is_loggedIn", is_loggedIn);
                    cmn.setPrefsData(LoginActivity.this, "blockflag", blockflag);
                    cmn.setPrefsData(LoginActivity.this, "is_team", "false");
                    cmn.setPrefsData(LoginActivity.this, "is_profile", "false");
                    cmn.setPrefsData(LoginActivity.this, "is_reset", "false");
                    //Common.INSTANCE.saveString("pass", pass);
                    if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                        cmn.setPrefsData(LoginActivity.this, "team_id", "");
                    }
                          /*  if (usertype.equals(ConsURL.members)) {

                                cmn.setPrefsData(LoginActivity.this, "news_per", news);
                                cmn.setPrefsData(LoginActivity.this, "msg_per", message);
                                cmn.setPrefsData(LoginActivity.this, "event_per", event);

                            }*/
                    if (!is_loggedIn.equals("null")) {
                        if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                            startActivity(new Intent(LoginActivity.this, HomePage.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            //    finish();
                        } else {
                            //showCustomDialogTeam(arr_team);
                        /*    if (arr_team != null) {
                                for (int i = 0; i < arr_team.length(); i++) {
                                    try {
                                        JSONObject obj = arr_team.getJSONObject(i);
                                        HashMap map = new HashMap();
                                        map.put("name", obj.getString("name"));
                                        map.put("team_id", obj.getString("team_id"));
                                        map.put("role_id", obj.getString("role_id"));
                                        if (usertype.equals(ConsURL.members)) {
                                            map.put("coach_name", obj.getString("coach_name"));

                                        }
                                        arrteams.add(map);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }*/
                            if (usertype.equals(ConsURL.coach)) {

                                     /*   for (HashMap val : arrteams) {
                                            String role = val.get("role_id").toString();
                                            if (role.equals(ConsURL.coach)) {
                                                arr_roleteam.add(val.get("team_id").toString());
                                            }
                                        }*/
                                if (blockflag.equals("true")) {
                                    Common.INSTANCE.saveString("is_team", "false");

                                    startActivity(new Intent(LoginActivity.this, AddTeam.class).putExtra("flag", "blocked").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, HomePage.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();
                               /*     if (arrteams.size() > 0) {
                                        if (arrteams.size() > 1) {
                                            //showCustomDialogTeam(arr_team, id);
                                        } else {
                                            String value = arrteams.get(0).get("name");
                                            String team_id = arrteams.get(0).get("team_id");
                                            cmn.setPrefsData(LoginActivity.this, "team_id", team_id);
                                            cmn.setPrefsData(LoginActivity.this, "team_name", value);
                                            getuserRoleAPI(id, team_id, null);

                                            // startActivity(new Intent(LoginActivity.this, HomePage.class));
                                            //  finish();
                                        }
                                    }*/
                                }

                            } else {
                                if (arrteams.size() > 0) {
                                   /* if (arrteams.size() > 1) {
                                        //showCustomDialogTeam(arr_team, id);
                                    } else {*/
                                      /*  String value = arrteams.get(0).get("name");
                                        String team_id = arrteams.get(0).get("team_id");
                                        cmn.setPrefsData(LoginActivity.this, "team_id", team_id);
                                        cmn.setPrefsData(LoginActivity.this, "team_name", value);
                                        getuserRoleAPI(id, team_id, null);
*/
                                    startActivity(new Intent(LoginActivity.this, HomePage.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();
                                    // startActivity(new Intent(LoginActivity.this, HomePage.class));
                                    //  finish();
                                    //  }
                                }
                            }
                        }
                    } else {
                        if (usertype.equals(ConsURL.members)) {

                        /*    for (int i = 0; i < arr_team.length(); i++) {
                                try {
                                    JSONObject obj = arr_team.getJSONObject(i);
                                    HashMap map = new HashMap();
                                    map.put("name", obj.getString("name"));
                                    map.put("team_id", obj.getString("team_id"));
                                    if (usertype.equals(ConsURL.members)) {
                                        map.put("coach_name", obj.getString("coach_name"));

                                    }
                                    arrteams.add(map);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }*/
                         /*   if (arrteams.size() > 0) {
                                if (arrteams.size() > 1) {
                                    showCustomDialogTeam(arr_team, id);
                                } else {

                                    String value = arrteams.get(0).get("name");
                                    String team_id = arrteams.get(0).get("team_id");
                                    cmn.setPrefsData(LoginActivity.this, "team_id", team_id);
                                    cmn.setPrefsData(LoginActivity.this, "team_name", value);
                                    getuserRoleAPI(id, team_id, null);

                                }
                            }*/
                        } else if (usertype.equals(ConsURL.coach)) {
                            startActivity(new Intent(LoginActivity.this, AddTeam.class).putExtra("flag", "1"));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            finish();
                        } else {
                            startActivity(new Intent(LoginActivity.this, EditProfile.class).putExtra("flag", "1"));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            finish();
                        }
                    }


                } else {
                    cmn.showAlert(result.getMessage(), LoginActivity.this);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "Login_by_otp");
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
