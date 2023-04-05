package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.GridLayoutManager;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.AssignTeam_Coach;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityTeamshowArrayBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateCoach;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import vk.help.Common;
import vk.help.MasterActivity;

public class Rolechange_assign extends MasterActivity {
    ActivityTeamshowArrayBinding binding;
    String userid, p_coach, roleid, teamid;
    List<Teamlist> arrteams = new ArrayList<>();
    CommonMethods cmn=new CommonMethods();
    AssignTeam_Coach teamadapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", p_coach).putExtra("flag", "user"));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            ArrayList<HashMap<String, String>> arr_outputs = teamadapter.getList();
            if (arrteams.size() == arr_outputs.size()) {

                teamid = arrteams.get(0).getId();
                getUpdateAPI(arr_outputs, p_coach, teamid);
            } else {
                cmn.showAlert("Välj alla lagtränare",context);
                //Toast.makeText(context, "Välj alla lagtränare", Toast.LENGTH_SHORT).show();

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeamshowArrayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userid = new CommonMethods().getPrefsData(context, "id", "");

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Välj teamleader");
        if (getIntent() != null) {
            Intent intent = getIntent();
            arrteams = (List<Teamlist>) Common.INSTANCE.getObject(intent.getByteArrayExtra("data"));
            p_coach = getIntent().getStringExtra("coach");
            roleid = getIntent().getStringExtra("roleid");
        }

        teamadapter = new AssignTeam_Coach(
                arrteams, Rolechange_assign.this);
        binding.assignTeam.setLayoutManager(new GridLayoutManager(context, 1));
        binding.assignTeam.setAdapter(teamadapter);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save);

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }
        return true;
    }

    private void getUpdateAPI(ArrayList<HashMap<String, String>> arr, String p_coach, String teamid) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            Gson gson = new Gson();

            UpdateCoach object1 = new UpdateCoach();
            object1.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            object1.assign = arr;
            object1.user_id = userid;
            requestData = gson.toJson(object1);

        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    getchangeRoleAPI(p_coach, teamid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_CoachTeam");
    }

    private void getchangeRoleAPI(String member, String teamid) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("u_id", member);
            object.put("role", roleid);
            object.put("user_id", userid);
            object.put("team_id", teamid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", member).putExtra("flag", "user"));
                    finish();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "updateRole");
    }


}
