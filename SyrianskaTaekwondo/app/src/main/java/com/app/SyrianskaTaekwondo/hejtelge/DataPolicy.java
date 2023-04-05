package com.app.SyrianskaTaekwondo.hejtelge;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.HashMap;

public class DataPolicy extends AppCompatActivity {
    AppCompatTextView policy,toolbar_title;
    private String policy_content;
    private ImageView imgBack;
    WebView wv;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_policy);
        getSupportActionBar().hide();
       //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        wv = (WebView) findViewById(R.id.web_view);
        policy = findViewById(R.id.policy_txt);
        toolbar_title = findViewById(R.id.toolbar_title);
        imgBack = findViewById(R.id.imgBack);
        String t_link=getIntent().getStringExtra("t_link");
        String dataPolicy=getIntent().getStringExtra("data_policy");
      //  html2text(t_link);
       // Toolbar toolbar = findViewById(R.id.anim_toolbar);
     //   CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
      //  setSupportActionBar(toolbar);
      //  collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.black));
        imgBack.setOnClickListener(view -> {
            finish();
        });

        if (dataPolicy.equals("1")){
         //   getSupportActionBar().setTitle("Data Policy");
            toolbar_title.setText("Data Policy");
          //  collapsingToolbar.setTitle("Data Policy");z
          //  policy.setText(Html.fromHtml(t_link));
            //policy.setText(Html.fromHtml(t_link, Html.FROM_HTML_MODE_COMPACT));
           // policy.setText(Html.fromHtml(Html.fromHtml(t_link).toString()));

            wv.loadDataWithBaseURL("", t_link, mimeType , encoding, "");
        }else {
            getUserAPI();
            //getSupportActionBar().setTitle(getResources().getString(R.string.info));
            toolbar_title.setText(getResources().getString(R.string.info));
           // collapsingToolbar.setTitle("Användarvillkor");
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
    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    private void getUserAPI() {
        //arr.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
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

        new NetworkCall(DataPolicy.this, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject obj = new JSONObject(result.getData());
                    policy_content = obj.getString("policy_content");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(policy_content!=null) {
                   // policy.setText(Html.fromHtml(policy_content));
                    wv.loadDataWithBaseURL("", policy_content, mimeType, encoding, "");
                }
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "privacy_policy");
    }


}
