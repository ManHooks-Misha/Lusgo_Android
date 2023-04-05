package com.app.SyrianskaTaekwondo.hejtelge;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.CampaignListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CampaignListActivity extends AppCompatActivity {
    private RecyclerView list;
    private List<HashMap<String, String>> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Kampanjlista");
        loadID();
        CampaignListAdapter libraryHotAdapter = new CampaignListAdapter(
                arr, CampaignListActivity.this);
        list.setLayoutManager(new GridLayoutManager(CampaignListActivity.this, 1));
        list.setAdapter(libraryHotAdapter);

    }

    public void loadID() {
        list = findViewById(R.id.list_campaign);


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


}
