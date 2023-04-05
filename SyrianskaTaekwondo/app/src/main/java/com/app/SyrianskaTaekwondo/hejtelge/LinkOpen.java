package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityLinkOpenBinding;

import java.util.Objects;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LinkOpen extends AppCompatActivity {
    ActivityLinkOpenBinding binding;
    ProgressDialog dialog;

    private boolean isSSLErrorDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLinkOpenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Laddar");
        dialog.show();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String url = getIntent().getStringExtra("url");
        String link = getIntent().getStringExtra("link");

    //    link = link.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");

    //    binding.txt.setText(link);
        binding.webLink.getSettings().setJavaScriptEnabled(true);
        binding.webLink.getSettings().setAllowFileAccess(true);
        binding.webLink.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        binding.webLink.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        binding.webLink.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        binding.webLink.getSettings().setAppCacheEnabled(false);
        binding.webLink.getSettings().setDomStorageEnabled(true);
        binding.webLink.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webLink.getSettings().setAllowFileAccess(true);
        // binding.webLink.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");

        String newUA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.137";
        binding.webLink.getSettings().setUserAgentString(newUA);
        binding.webLink.loadUrl(url);
        binding.webLink.setWebViewClient(new WebViewClient());
        String finalUrl = url;
        binding.webLink.setHorizontalScrollBarEnabled(false);

        binding.webLink.getSettings().setLoadWithOverviewMode(true);
        binding.webLink.getSettings().setUseWideViewPort(true);
        AsyncTask.execute(() -> {
            boolean isLoadUrlDirect = true;
            try {
                Response response = new OkHttpClient().newCall(new Request.Builder().url(finalUrl).build()).execute();
                Headers headers = response.headers();
                isLoadUrlDirect = headers.get("x-frame-options").equals("SAMEORIGIN");
                response.body().close();
            } catch (Exception e) {
                isLoadUrlDirect = false;
            } finally {
                boolean finalIsLoadUrlDirect = isLoadUrlDirect;
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (finalIsLoadUrlDirect) {
                        binding.webLink.loadUrl(finalUrl);

                    } else {
                        binding.webLink.loadUrl(finalUrl);

                        //  mWebView.loadData(vid, "text/html", "UTF-8");
                    }
                });
            }
        });
        binding.webLink.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                binding.txt.setText(view.getTitle());

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                binding.txt.setText(view.getTitle());
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialog.dismiss();

                binding.txt.setText(view.getTitle());

                super.onPageStarted(view, url, favicon);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}