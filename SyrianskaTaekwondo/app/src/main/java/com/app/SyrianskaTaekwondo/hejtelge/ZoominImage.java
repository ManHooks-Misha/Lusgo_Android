package com.app.SyrianskaTaekwondo.hejtelge;

import android.os.Bundle;
import android.view.ScaleGestureDetector;

import com.app.SyrianskaTaekwondo.hejtelge.customClass.Zoom;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityZoominImageBinding;

import vk.help.MasterActivity;

public class ZoominImage extends MasterActivity {
    ActivityZoominImageBinding binding;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityZoominImageBinding.inflate(getLayoutInflater());
        setContentView(new Zoom(this));
//        String imagepath = getIntent().getStringExtra("image");
//        Glide.with(context)
//                .load(imagepath)
//                .fitCenter()
//                .into(binding.imgZoom);
//        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    }

}
