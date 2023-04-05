package com.app.SyrianskaTaekwondo.hejtelge.utils;

import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;


public class ProgressViewHolder extends RecyclerView.ViewHolder {

    private ProgressBar progressBar;

    public ProgressViewHolder(View v) {
        super(v);
        progressBar = v.findViewById(R.id.progressBar1);
    }

    public void setData() {
        progressBar.setIndeterminate(true);
    }

}