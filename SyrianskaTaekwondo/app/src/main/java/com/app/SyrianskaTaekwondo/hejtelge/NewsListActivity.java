package com.app.SyrianskaTaekwondo.hejtelge;/*
package com.mishainfotech.lusgo.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mishainfotech.lusgo.R;
import com.mishainfotech.lusgo.activity.adapters.NewsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewsListActivity extends AppCompatActivity {
    RecyclerView list;
    private List<HashMap<String, String>> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nyhetslista");
        loadID();
        NewsListAdapter libraryHotAdapter = new NewsListAdapter(
                arr, NewsListActivity.this);
        list.setLayoutManager(new GridLayoutManager(NewsListActivity.this, 1));
        list.setAdapter(libraryHotAdapter);

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
    public void loadID() {
        list = findViewById(R.id.list_news);


    }



}
*/
