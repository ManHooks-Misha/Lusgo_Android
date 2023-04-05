package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

public class RegistrationActivity extends Activity {
    FrameLayout school, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loadID();
        school.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, SignupActivity.class));
            finish();
        });
    }

    public void loadID() {
        school = findViewById(R.id.frm_school);
        user = findViewById(R.id.frm_user);


    }
}
