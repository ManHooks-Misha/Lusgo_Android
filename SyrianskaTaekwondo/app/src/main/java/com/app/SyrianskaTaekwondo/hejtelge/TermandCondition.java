package com.app.SyrianskaTaekwondo.hejtelge;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityTermandConditionBinding;

public class TermandCondition extends AppCompatActivity {
    ActivityTermandConditionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermandConditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}