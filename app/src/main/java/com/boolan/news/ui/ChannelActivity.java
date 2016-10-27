package com.boolan.news.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.boolan.news.R;
import com.boolan.news.ui.adapter.ChannelListAdapter;
import com.boolan.news.utils.NewsDb;

public class ChannelActivity extends AppCompatActivity {

    private NewsDb newsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("订阅频道");
        }

        newsDb = new NewsDb(this);
        ChannelListAdapter viewAdapter = new ChannelListAdapter(newsDb.getAllChannelList());
        viewAdapter.setOnCheckedChangeListener(new ChannelListAdapter.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(String channelId, boolean checked) {
                newsDb.setChannelSubscribed(channelId, checked);
                setResult(MainActivity.CODE_DATA_CHANGED);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(viewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}
