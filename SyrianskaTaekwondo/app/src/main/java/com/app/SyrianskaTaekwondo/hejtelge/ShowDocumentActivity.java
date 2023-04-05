package com.app.SyrianskaTaekwondo.hejtelge;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;


import vk.help.MasterActivity;

public class ShowDocumentActivity extends MasterActivity {

    private String url;
    ProgressBar progressBar;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_document);
        Toolbar toobar=findViewById(R.id.toolbar);
        setSupportActionBar(toobar);
        WebView urlWebView = findViewById(R.id.docview);

        progressBar = findViewById(R.id.progress_bar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

/*
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Dokument");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
   //     abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
*/

        if (getIntent() != null) {
            url = getIntent().getStringExtra("Url");
        }
        showPdf(urlWebView,url);

    }
    private void showPdf(WebView pdfView, String imageString) {
        pdfView.invalidate();
        pdfView.getSettings().setJavaScriptEnabled(true);
        pdfView.getSettings().setSupportZoom(true);
        pdfView.getSettings().setBuiltInZoomControls(true);
        pdfView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + imageString);
       // pdfView.setVerticalScrollBarEnabled(false);
       // pdfView.setHorizontalScrollBarEnabled(true);
        pdfView.setWebViewClient(new WebViewClient() {
            boolean checkhasOnPageStarted = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                checkhasOnPageStarted = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (checkhasOnPageStarted ) {
                    progressBar.setVisibility(View.GONE);
                    pdfView.loadUrl(imageString);
                } else {
                    showPdf(pdfView,imageString);
                }
            }
        });
    }
}






