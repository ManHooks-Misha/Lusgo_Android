package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityAcceptInviteBinding;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.HashMap;

import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class AcceptInvite extends MasterActivity {
    ActivityAcceptInviteBinding binding;
    CommonMethods cmn = new CommonMethods();
    String userid = "", team_id = "", coach_id = "", uStatus = "";
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
        binding = ActivityAcceptInviteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userid = cmn.getPrefsData(this, "id", "");
        if (getIntent() != null) {
            team_id = getIntent().getStringExtra("team_id");
            coach_id = getIntent().getStringExtra("coach_id");
            team_id = getIntent().getStringExtra("team_id");
            uStatus = getIntent().getStringExtra("status");
        }
        getTeamAPI();

        binding.txtAccept.setOnClickListener(v -> alert("1","Är du säker på att acceptera den här inbjudan?"));
        binding.txtReject.setOnClickListener(v -> alert("2","Är du säker på att avvisa den här inbjudan?"));

    }

    private void alert(String status,String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {

                getAcceptAPI(status);
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


    private void getTeamAPI() {
        //     AddCampaign.img.clear();
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("team_id", team_id);
            object.put("coach_id", coach_id);
            object.put("uStatus", uStatus);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject obj = new JSONObject(result.getData());
                    String teamname = obj.getString("name");
                    String coach_name = obj.getString("inviteBy");
                    String logo = obj.getString("logo");
                    String status = obj.getString("status");
                    String username = obj.getString("username");

                    String name1 = String.valueOf(teamname.trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name1.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(logo)
                            .centerCrop()
                            .placeholder(drawable)
                            .into(binding.ppImg);
                    binding.txtName.setText(teamname);
                 //   binding.coachName.setText("Inbjudan skickad av : " + coach_name);
                    binding.coachName.setText("Inbjudan från : " + coach_name);
                    binding.username.setText("(" + username + ")");
                    if (status.equals("0")) {
                        binding.btnInvite.setVisibility(View.VISIBLE);
                    } else {
                        binding.btnInvite.setVisibility(View.GONE);
                        if (status.equals("1")) {
                            binding.status.setText("Du har accepterat inbjudan");
                        } else {
                            binding.status.setText("Avvisa");

                        }
                        binding.status.setVisibility(View.VISIBLE);
                    }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "Invite_details");
    }

    private void getAcceptAPI(String status) {
        //     AddCampaign.img.clear();
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("team_id", team_id);
            object.put("coach_id", coach_id);
            object.put("uStatus", status);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {

                    binding.btnInvite.setVisibility(View.GONE);
                    if (status.equals("1")) {
                        binding.status.setText("Du har accepterat inbjudan");
                    } else {
                        binding.status.setText("Avvisa");

                    }
                    binding.status.setVisibility(View.VISIBLE);
                    // JSONObject obj = new JSONObject(result.getData());


                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "accept_invitation");
    }

    private void showCustomDialog() {
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
        ok.setOnClickListener(view -> {

            startActivity(new Intent(AcceptInvite.this, HomePage.class));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}