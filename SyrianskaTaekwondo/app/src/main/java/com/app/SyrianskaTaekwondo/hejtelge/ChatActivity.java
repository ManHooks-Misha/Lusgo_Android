package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lusgo");
        web = findViewById(R.id.webview);

        web.setVerticalScrollBarEnabled(false);

        web.getSettings().setJavaScriptEnabled(true);
//        web.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        web.loadUrl("http://lusgo.se/");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                web.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");

                super.onPageFinished(view, url);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something
                startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                finish();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
