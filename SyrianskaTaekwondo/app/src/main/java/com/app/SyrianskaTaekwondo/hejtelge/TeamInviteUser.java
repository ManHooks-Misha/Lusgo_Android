package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityTeamInviteUserBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Invite;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.MasterActivity;

public class TeamInviteUser extends MasterActivity {
    ActivityTeamInviteUserBinding binding;
    CommonMethods cmn = new CommonMethods();
    String status, email_txt, phone, msg;
    List<String> arr_email = new ArrayList<>();
    List<String> arr_phone = new ArrayList<>();
    String teamid = "", userid;
    AlertDialog alertDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeamInviteUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        userid = cmn.getPrefsData(TeamInviteUser.this, "id", "");

        if (getIntent() != null) {
            teamid = getIntent().getStringExtra("id");
        }

        binding.ffinvite.setOnClickListener(v -> {
            arr_phone.clear();
            arr_email.clear();
            email_txt = Objects.requireNonNull(binding.email.getText()).toString().replace(" ", "");
            arr_email.add(email_txt);

            phone = Objects.requireNonNull(binding.mob.getText()).toString();
            arr_phone.add(phone);

            if (email_txt.length() > 0 && Objects.requireNonNull(phone.length() > 0)) {
                Toast.makeText(TeamInviteUser.this, "Välj ett alternativ antingen e-post eller nummer", Toast.LENGTH_SHORT).show();
            } else if (email_txt.length() > 0) {
                if (cmn.isEmailValid(email_txt)) {
                    InviteRequest(userid, teamid);
                   // alert("Denna användare är redan registrerad i applikationen. Vill du fortfarande skicka inbjudan?");
                }
            } else if (Objects.requireNonNull(binding.mob.getText()).length() > 0) {
                InviteRequest(userid, teamid);

            }
        });
    }


    private void alert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
       // builder.setMessage(msg);
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {

                InviteRequest(userid, teamid);
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void InviteRequest(String userid, String teamid) {
        String tset;

        ProgressDialog mprogdialog = ProgressDialog.show(TeamInviteUser.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);
        //  email_txt = Objects.requireNonNull(binding.email.getText()).toString();
        if (email_txt.length() > 0) {
            Gson gson = new Gson();
            Invite asgn = new Invite();
            asgn.email = arr_email;
            asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            asgn.user_id = userid;
            asgn.team_id = teamid;
            asgn.role = "4";
            tset = gson.toJson(asgn);
            Log.d("dasfjlk",tset);

        } else {
            Gson gson = new Gson();
            Invite asgn = new Invite();
            asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            asgn.user_id = userid;
            asgn.phone = arr_phone;
            asgn.team_id = teamid;
            asgn.role = "4";
            tset = gson.toJson(asgn);
        }


        String url = ConsURL.BASE_URL_TEST + "invite_users_team";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(TeamInviteUser.this, e.getMessage(), Toast.LENGTH_SHORT).show());

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
                            if (email_txt.length() > 0) {

                                showCustomDialog("Inbjudan har skickats. Om du valt att skicka inbjudan via e-post, be användaren titta i skräpposten.");
                            } else {
                                showCustomDialog("Inbjudan har skickats.");
                            }

                        } else {
                            cmn.showAlert(msg, TeamInviteUser.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


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
        AppCompatTextView create = dialogView.findViewById(R.id.create);
        create.setText(msg);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
            finish();

        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

}