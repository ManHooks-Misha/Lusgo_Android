package com.app.SyrianskaTaekwondo.hejtelge;


import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewGroupInfo extends Activity {
    private List<HashMap<String, String>> arr = new ArrayList<>();

    RecyclerView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_info);
        loadID();

        /*GroupAdapter libraryHotAdapter = new GroupAdapter(
                arr, ViewGroupInfo.this);
        list.setLayoutManager(new GridLayoutManager(ViewGroupInfo.this, 1));
        list.setAdapter(libraryHotAdapter);*/
    }
    public void loadID() {

        list = findViewById(R.id.list_view);

    }

}
