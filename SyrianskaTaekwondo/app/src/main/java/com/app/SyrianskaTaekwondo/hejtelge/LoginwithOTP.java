package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.database.NotificationTable;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityLoginwithOtpBinding;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import vk.help.Common;

public class LoginwithOTP extends AppCompatActivity {
    ActivityLoginwithOtpBinding binding;
    CommonMethods cmn = new CommonMethods();
    AlertDialog alertDialog;
    String deviceID;
    AppCompatImageView close, cancelBtn;
    AppCompatEditText one, two, three, four;
    private String appname, appimg, Is_Coach, t_link, status, user, news, message, event, role, blockflag, is_loggedIn, id, add, city, msg, email, firstname, imagepath, lastname, state, telephone, banner, usertype;
    private JSONArray arr_team;
    ArrayList<HashMap<String, String>> arrteams = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(LoginwithOTP.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginwithOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.infoIcon.setOnClickListener(v -> {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(LoginwithOTP.this);
            //  alertDialog.setTitle("Confirm Logout.");
            alertDialog.setMessage("Här kan du logga in utan lösenord. Skriv in ditt telefonnummer eller e-post för att få en engångs kod skickad till dig som du sedan kan logga in med.");
            alertDialog.setPositiveButton("Ja",
                    (dialog, which) -> {
                        dialog.cancel();

                    });


            alertDialog.show();
        });

        binding.loginPass.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.editTextEmail.getText()).toString().isEmpty()) {
                binding.editTextEmail.requestFocus();
                binding.editTextEmail.setError("Vänligen ange din e-postadress");
                return;
            }
            if (binding.editTextEmail.getText().toString().length() > 0) {
                getOtpAPI(binding.editTextEmail.getText().toString().trim());
            }
        });
        binding.forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
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
                       // Toast.makeText(LoginwithOTP.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

/*
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("LoginwithOTP ", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    deviceID = Objects.requireNonNull(task.getResult()).getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, deviceID);
                    Log.d("LoginwithOTP ", msg);
                    //        Toast.makeText(LoginwithOTP.this, msg, Toast.LENGTH_SHORT).show();
                });
*/
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

        new NetworkCall(this, result -> {
            try {
                if (result.isStatus()) {
                    showCustomDialog(email);


                } else {
                    cmn.showAlert(result.getMessage(), LoginwithOTP.this);
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
        close = dialogView.findViewById(R.id.close);
        four = dialogView.findViewById(R.id.four);

        AppCompatTextView ok = dialogView.findViewById(R.id.verify);
        AppCompatTextView resend = dialogView.findViewById(R.id.resend);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        one.addTextChangedListener(new LoginwithOTP.GenericTextWatcher(one));
        two.addTextChangedListener(new LoginwithOTP.GenericTextWatcher(two));
        three.addTextChangedListener(new LoginwithOTP.GenericTextWatcher(three));
        four.addTextChangedListener(new LoginwithOTP.GenericTextWatcher(four));
        resend.setVisibility(View.GONE);
        ok.setOnClickListener(view -> {
            String otp = one.getText().toString() + two.getText().toString() + three.getText().toString() + four.getText().toString();

            getVerifyEmailAPI(email, otp);
            /* */
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
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

        new NetworkCall(this, result -> {
            try {
                if (result.isStatus()) {
                    //showCustomDialog(email);
                    alertDialog.dismiss();
                    JSONObject object = new JSONObject(result.getData());
                    // status = objvalue.optString("status");

                    //  msg = objvalue.optString("message");
                    blockflag = object.optString("block");

                    Common.INSTANCE.saveString("Device_id", deviceID);
                    NotificationTable notificationTable = new NotificationTable(LoginwithOTP.this).getInstance(LoginwithOTP.this);
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

                    cmn.setPrefsData(LoginwithOTP.this, "id", id);
                    cmn.setPrefsData(LoginwithOTP.this, "city", city);
                    cmn.setPrefsData(LoginwithOTP.this, "email", email);
                    cmn.setPrefsData(LoginwithOTP.this, "firstname", firstname);
                    cmn.setPrefsData(LoginwithOTP.this, "imagepath", imagepath);
                    cmn.setPrefsData(LoginwithOTP.this, "lastname", lastname);
                    cmn.setPrefsData(LoginwithOTP.this, "state", state);
                    cmn.setPrefsData(LoginwithOTP.this, "Is_Coach", Is_Coach);
                    cmn.setPrefsData(LoginwithOTP.this, "city", city);
                    cmn.setPrefsData(LoginwithOTP.this, "telephone", telephone);
                    cmn.setPrefsData(LoginwithOTP.this, "usertype", usertype);
                    cmn.setPrefsData(LoginwithOTP.this, "username", user);
                    cmn.setPrefsData(LoginwithOTP.this, "role", role);
                    cmn.setPrefsData(LoginwithOTP.this, "imagepath", imagepath);
                    cmn.setPrefsData(LoginwithOTP.this, "banner", banner);
                    cmn.setPrefsData(LoginwithOTP.this, "is_loggedIn", is_loggedIn);
                    cmn.setPrefsData(LoginwithOTP.this, "blockflag", blockflag);
                    cmn.setPrefsData(LoginwithOTP.this, "is_team", "false");
                    cmn.setPrefsData(LoginwithOTP.this, "is_profile", "false");
                    cmn.setPrefsData(LoginwithOTP.this, "is_reset", "false");
                    //Common.INSTANCE.saveString("pass", pass);
                    if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                        cmn.setPrefsData(LoginwithOTP.this, "team_id", "");
                    }
                          /*  if (usertype.equals(ConsURL.members)) {

                                cmn.setPrefsData(LoginwithOTP.this, "news_per", news);
                                cmn.setPrefsData(LoginwithOTP.this, "msg_per", message);
                                cmn.setPrefsData(LoginwithOTP.this, "event_per", event);

                            }*/
                    if (!is_loggedIn.equals("null")) {
                        if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                            startActivity(new Intent(LoginwithOTP.this, HomePage.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


                            //    finish();
                        } else {

                            if (usertype.equals(ConsURL.coach)) {
                                if (blockflag.equals("true")) {
                                    Common.INSTANCE.saveString("is_team", "false");

                                    startActivity(new Intent(LoginwithOTP.this, AddTeam.class).putExtra("flag", "blocked").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();
                                } else {
                                    startActivity(new Intent(LoginwithOTP.this, HomePage.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();
                               /*     if (arrteams.size() > 0) {
                                        if (arrteams.size() > 1) {
                                            //showCustomDialogTeam(arr_team, id);
                                        } else {
                                            String value = arrteams.get(0).get("name");
                                            String team_id = arrteams.get(0).get("team_id");
                                            cmn.setPrefsData(LoginwithOTP.this, "team_id", team_id);
                                            cmn.setPrefsData(LoginwithOTP.this, "team_name", value);
                                            getuserRoleAPI(id, team_id, null);

                                            // startActivity(new Intent(LoginwithOTP.this, HomePage.class));
                                            //  finish();
                                        }
                                    }*/
                                }


                            } else {
                                if (arrteams.size() > 0) {

                                    startActivity(new Intent(LoginwithOTP.this, HomePage.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    finish();

                                }else {
                                    if (usertype.equals(ConsURL.members)) {
                                        startActivity(new Intent(LoginwithOTP.this, HomePage.class));
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    }
                                }
                            }
                        }
                    } else {
                        if (usertype.equals(ConsURL.members)) {

                        } else if (usertype.equals(ConsURL.coach)) {
                            startActivity(new Intent(LoginwithOTP.this, AddTeam.class).putExtra("flag", "1"));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            finish();
                        } else {
                            startActivity(new Intent(LoginwithOTP.this, EditProfile.class).putExtra("flag", "1"));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            finish();
                        }
                    }


                } else {
                    cmn.showAlert(result.getMessage(), LoginwithOTP.this);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
              // Log.d("dsajflkjlf","dasjlfk");

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "Login_by_otp");
    }


    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.forgot_password, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatEditText email = dialogView.findViewById(R.id.email_text);

        ok.setOnClickListener(view -> {
            if (cmn.isOnline(LoginwithOTP.this)) {
                String email_text = Objects.requireNonNull(email.getText()).toString();
                if (email_text.length() > 0) {
                    if (cmn.isEmailValid(email_text) || cmn.isValidMobile(email_text)) {
                        getforgotAPI(email_text);
                    } else {
                        cmn.showAlert("Ange rätt e-postadress eller mobilnummer", LoginwithOTP.this);
                    }
                } else {
                    Toast.makeText(this, "Skriv in ditt användarnamn", Toast.LENGTH_SHORT).show();
                }
             /*   if (cmn.isEmailValid(email_text)) {

                } else {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                }*/

            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }


/*
    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.forgot_password, viewGroup, false);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated


        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatEditText email = dialogView.findViewById(R.id.email_text);
        ok.setOnClickListener(view -> {
            if (cmn.isOnline(LoginwithOTP.this)) {
                String email_text = Objects.requireNonNull(email.getText()).toString();
                if (email_text.length() > 0) {
                    if (cmn.isEmailValid(email_text) || cmn.isValidMobile(email_text)) {
                        getforgotAPI(email_text);
                    } else {
                        cmn.showAlert("Ange rätt e-postadress eller mobilnummer",LoginwithOTP.this);
                    }
                } else {
                    Toast.makeText(this, "Skriv in ditt användarnamn", Toast.LENGTH_SHORT).show();
                }
             *//*   if (cmn.isEmailValid(email_text)) {

                } else {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                }*//*

            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }*/

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

        new NetworkCall(LoginwithOTP.this, result -> {
            try {
                if (result.isStatus()) {
                    //  cmn.showAlert("En kod har skickats till din e-postadress eller sms till dig. Följ instruktionerna för att byta ditt lösenord", context);
                    alertDialog.dismiss();
                    showCustomDialogForgot(email);

                } else {
                    cmn.showAlert(result.getMessage(), LoginwithOTP.this);
                   // Common.INSTANCE.showToast(LoginwithOTP.this, result.getMessage());
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

            startActivity(new Intent(LoginwithOTP.this, OtpVerify.class).putExtra("email", email));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}