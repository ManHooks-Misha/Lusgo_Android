package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;

public class SplashActivity extends Activity {
    private static final int SPLASH_TIME_OUT = 1000;
    String email;
    CommonMethods cmn = new CommonMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        email = cmn.getPrefsData(SplashActivity.this, "email", "");

        loadScreen();
    }

    private void loadScreen() {
        new Handler().postDelayed(() -> {
            if (email.length() == 0) {
                Intent intent = new Intent(SplashActivity.this, Page_withOut_Login.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, HomePage.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
