package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityAddNewLinkBinding;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import vk.help.MasterActivity;

public class Add_newLink extends MasterActivity {
    ActivityAddNewLinkBinding binding;
    String id, name, url, order, flag;
    CommonMethods commonMethods=new CommonMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewLinkBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            url = getIntent().getStringExtra("linkurl");
            order = getIntent().getStringExtra("order");
            flag = getIntent().getStringExtra("flag");
            binding.linkName.setText(name);
            binding.linkUrl.setText(url);

        }
    }


    private void addLinkAPI(String linkname, String linkurl, String order, String id) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("LinkName", linkname);
            object.put("LinkUrl", linkurl);
            object.put("Id", Integer.parseInt(id));
            object.put("LinkOrder", order);

            object.put("user_id", Integer.parseInt(new CommonMethods().getPrefsData(Add_newLink.this, "id", "")));

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {

                if (result.isStatus()) {

                    startActivity(new Intent(this, Update_info.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("Flag","link"));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "linkInsertupdate_information");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            String linkname = Objects.requireNonNull(binding.linkName.getText()).toString();
            String linkurl = Objects.requireNonNull(binding.linkUrl.getText()).toString();
            if (new CommonMethods().isOnline(this)) {
                if (flag.equals("edit")) {
                    if (linkname.length() > 0 && linkurl.length() > 0) {

                        addLinkAPI(linkname, linkurl, order, id);
                    } else {
                        commonMethods.showAlert("Ange länk",this);
                      //  Toast.makeText(context, "Ange länk", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    if (linkname.length() > 0 && linkurl.length() > 0) {

                        addLinkAPI(linkname, linkurl, "0", "0");
                    } else {
                        commonMethods.showAlert("Ange länk",this);
                      //  Toast.makeText(context, "Ange länk", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_link, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }

        return true;
    }
}