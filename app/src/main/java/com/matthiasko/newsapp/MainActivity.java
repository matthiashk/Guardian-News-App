package com.matthiasko.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements SendToActivity {

    ListView listView;
    NewsAdapter newsAdapter;
    NewsItem[] results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.news_listview);

        if (savedInstanceState != null) {

            Parcelable[] ps = savedInstanceState.getParcelableArray("NEWSITEMS_ARRAY");
            NewsItem[] newsItems = new NewsItem[ps.length];
            System.arraycopy(ps, 0, newsItems, 0, ps.length);
            results = newsItems;
            newsAdapter = new NewsAdapter(this, -1, newsItems);
            listView.setAdapter(newsAdapter);
            listView.setEmptyView(findViewById(R.id.empty_textview));
            newsAdapter.notifyDataSetChanged();

        } else {
            // setup initial empty list + empty view
            results = new NewsItem[0];
            newsAdapter = new NewsAdapter(this, -1, results);
            listView.setAdapter(newsAdapter);
            listView.setEmptyView(findViewById(R.id.empty_textview));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem item = (NewsItem) listView.getItemAtPosition(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getWebUrl()));
                startActivity(browserIntent);
            }
        });

        new FetchNewsAsyncTask(this, MainActivity.this).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (results != null) {
            outState.putParcelableArray("NEWSITEMS_ARRAY", results);
        }
    }

    @Override
    public void sendData(NewsItem[] itemsArray) {
        results = itemsArray;
        newsAdapter = new NewsAdapter(this, -1, results);
        listView.setAdapter(newsAdapter);
    }
}
