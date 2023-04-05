package com.app.SyrianskaTaekwondo.hejtelge;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.Gson;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Reset;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;
import vk.help.MasterActivity;

public class ResetPassword extends MasterActivity {
    private AppCompatEditText txt_old, txt_newpass, txt_confirmpass, txt_email;
    private String email, userid, status, msg, id, is_loggedIn, pass;
    private AppCompatButton reset;
    AppCompatTextView strenth, strenthconfirm, right_pass;
    private AppCompatImageView eyeshow, eyeshow1, eyehide, eyehide1,arrowBack;
    private CommonMethods cmn;
    private long mLastClickTime = 0;
    String flag = "";
    AlertDialog alertDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
         //   startActivity(new Intent(this,EditProfile.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();
       // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
       // getSupportActionBar().setTitle("Uppdatera lösenordet");

        pass = Common.INSTANCE.getString("pass");

        loadID();
        if (getIntent() != null) {
            flag = getIntent().getStringExtra("flag");
        }

        cmn = new CommonMethods();
        userid = cmn.getPrefsData(ResetPassword.this, "id", "");
        email = cmn.getPrefsData(ResetPassword.this, "username", "");
        is_loggedIn = cmn.getPrefsData(ResetPassword.this, "is_loggedIn", "");
        if (flag.equals("1")) {
            txt_old.setText(pass);
        }else {
          arrowBack.setVisibility(View.VISIBLE);
        }


        pass = Common.INSTANCE.getString("pass");
        txt_email.setText(email);
        arrowBack.setOnClickListener(view -> {
            onBackPressed();
        });
        eyehide.setOnClickListener(view -> {
            if (txt_newpass.getText().length() > 0) {
                txt_newpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeshow.setVisibility(View.VISIBLE);
                eyehide.setVisibility(View.GONE);
                txt_newpass.setSelection(txt_newpass.length());
            }

        });
        eyeshow.setOnClickListener(view -> {
            if (Objects.requireNonNull(txt_newpass.getText()).length() > 0) {
                txt_newpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeshow.setVisibility(View.GONE);
                eyehide.setVisibility(View.VISIBLE);
                txt_newpass.setSelection(txt_newpass.length());
            }
        });

        eyehide1.setOnClickListener(view -> {
            if (Objects.requireNonNull(txt_confirmpass.getText()).length() > 0) {
                txt_confirmpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeshow1.setVisibility(View.VISIBLE);
                eyehide1.setVisibility(View.GONE);
                txt_confirmpass.setSelection(txt_confirmpass.length());
            }

        });
        eyeshow1.setOnClickListener(view -> {
            if (Objects.requireNonNull(txt_confirmpass.getText()).length() > 0) {
                txt_confirmpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeshow1.setVisibility(View.GONE);
                eyehide1.setVisibility(View.VISIBLE);
                txt_confirmpass.setSelection(txt_confirmpass.length());
            }
        });
        reset.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            String oldname = Objects.requireNonNull(txt_old.getText()).toString();
            String newpass = Objects.requireNonNull(txt_newpass.getText()).toString();
            if (oldname.length() > 0) {
                if (newpass.length() > 0) {
                    if (txt_confirmpass.length() > 0) {
                        if (txt_newpass.getText().toString().equals(Objects.requireNonNull(txt_confirmpass.getText()).toString())) {
                            ResetPass(newpass, oldname);
                        } else {
                            cmn.showAlert("Lösenorden matchar inte", ResetPassword.this);

                        }
                    } else {
                        cmn.showAlert("Vänligen bekräfta lösenord", ResetPassword.this);

                    }
                } else {
                    cmn.showAlert("Skriv in nytt lösenord", ResetPassword.this);


                }
            } else {
                cmn.showAlert("Ange gammalt lösenord", ResetPassword.this);

            }
        });

    }

    public void loadID() {
        txt_old = findViewById(R.id.txt_oldpass);
        arrowBack = findViewById(R.id.back);
        right_pass = findViewById(R.id.right_pass);
        txt_newpass = findViewById(R.id.txt_newpass);
        txt_confirmpass = findViewById(R.id.txt_confirmpass);
        txt_email = findViewById(R.id.txt_email);
        strenth = findViewById(R.id.strenth);
        strenthconfirm = findViewById(R.id.strenthconfirm);
        eyeshow = findViewById(R.id.eye);
        eyeshow1 = findViewById(R.id.eye1);
        eyehide = findViewById(R.id.eyehide);
        eyehide1 = findViewById(R.id.eye1hide);

        reset = findViewById(R.id.resetPassword);

        txt_newpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Calculate password strength
                calculateStrength(editable.toString(), strenth);
            }
        });
        txt_old.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Calculate password strength
                if (pass.equals(editable.toString())) {
                    right_pass.setVisibility(View.VISIBLE);
                } else {
                    right_pass.setVisibility(View.GONE);

                }
            }
        });
        txt_confirmpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Calculate password strength
                calculateStrength(editable.toString(), strenthconfirm);
            }
        });
    }

    private void ResetPass(String newpass, String oldpass) {
        ProgressDialog mprogdialog = ProgressDialog.show(ResetPassword.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);

        Gson gson = new Gson();
        Reset asgn = new Reset();
        asgn.email = email;
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.new_password = newpass;
        asgn.old_password = oldpass;
        asgn.device_type = "a";

        String tset = gson.toJson(asgn);
        String url = ConsURL.BASE_URL_TEST + "reset_password";
        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show());


            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.body() != null) {
                        res = Objects.requireNonNull(response.body()).string();
                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");
                        msg = objvalue.optString("message");
                        // JSONObject object = objvalue.getJSONObject("data");


                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();
                        if (status.equals("true")) {
                            if (!is_loggedIn.equals("null")) {
                                dialogeChangePass(msg,newpass);
                               // cmn.showAlert(msg, ResetPassword.this);

                               /* Intent i = new Intent(context, HomePage.class);
                                // set the new task and clear flags
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
                                cmn.setPrefsData(context, "coach_name", "");
                                cmn.setPrefsData(context, "role", "");
                                cmn.setPrefsData(context, "imagepath", "");
                                cmn.setPrefsData(context, "banner", "");
                                cmn.setPrefsData(context, "is_loggedIn", "");
                                cmn.setPrefsData(context, "team_name", "");
                                cmn.setPrefsData(context, "team_id", "");
                                Common.INSTANCE.saveString("is_team", "");
                                Common.INSTANCE.saveString("is_profile", "");
                                Common.INSTANCE.saveString("deviceID", "");
                                Common.INSTANCE.saveString("Device_id", "");
                                Common.INSTANCE.saveString("is_reset", "");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();*/
                            } else {
                             //   Toast.makeText(ResetPassword.this, msg, Toast.LENGTH_SHORT).show();

                                Common.INSTANCE.saveString("pass", newpass);
                                Common.INSTANCE.saveString("is_reset", "true");

                                cmn.setPrefsData(ResetPassword.this, "blockflag", "false");

                                Intent i = new Intent(context, HomePage.class);
                                startActivity(i);
                                finish();

                            }


                        } else {
                            cmn.showAlert(msg, ResetPassword.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }

    public void dialogeChangePass(String message,String newpass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            alertDialog.dismiss();
            Common.INSTANCE.saveString("pass", newpass);
            Common.INSTANCE.saveString("is_reset", "true");

            cmn.setPrefsData(ResetPassword.this, "blockflag", "false");

            Intent intent = new Intent(context, HomePage.class);
            startActivity(intent);
            finish();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
            alertDialog.show();
    }


    private void calculateStrength(String passwordText, AppCompatTextView strenth) {
        int upperChars = 0, lowerChars = 0, numbers = 0,
                specialChars = 0, otherChars = 0, strengthPoints = 0;
        char c;

        int passwordLength = passwordText.length();

        if (passwordLength == 0) {
            strenth.setText("Fel lösenord");
            strenth.setTextColor(Color.RED);
            return;
        }

        //If password length is <= 5 set strengthPoints=1
        if (passwordLength <= 5) {
            strengthPoints = 1;
        }
        //If password length is >5 and <= 10 set strengthPoints=2
        else if (passwordLength <= 10) {
            strengthPoints = 2;
        }
        //If password length is >10 set strengthPoints=3
        else
            strengthPoints = 3;
        // Loop through the characters of the password
        for (int i = 0; i < passwordLength; i++) {
            c = passwordText.charAt(i);
            // If password contains lowercase letters
            // then increase strengthPoints by 1
            if (c >= 'a' && c <= 'z') {
                if (lowerChars == 0) strengthPoints++;
                lowerChars = 1;
            }
            // If password contains uppercase letters
            // then increase strengthPoints by 1
            else if (c >= 'A' && c <= 'Z') {
                if (upperChars == 0) strengthPoints++;
                upperChars = 1;
            }
            // If password contains numbers
            // then increase strengthPoints by 1
            else if (c >= '0' && c <= '9') {
                if (numbers == 0) strengthPoints++;
                numbers = 1;
            }
            // If password contains _ or @
            // then increase strengthPoints by 1
            else if (c == '_' || c == '@') {
                if (specialChars == 0) strengthPoints += 1;
                specialChars = 1;
            }
            // If password contains any other special chars
            // then increase strengthPoints by 1
            else {
                if (otherChars == 0) strengthPoints += 2;
                otherChars = 1;
            }
        }

        if (strengthPoints <= 3) {
            strenth.setText("Låg");
            strenth.setTextColor(Color.RED);
        } else if (strengthPoints <= 6) {
            strenth.setText("Medium");
            strenth.setTextColor(Color.parseColor("#CFBD1F"));
        } else if (strengthPoints <= 9) {
            strenth.setText("Hög");
            strenth.setTextColor(Color.GREEN);
        }
    }

}

