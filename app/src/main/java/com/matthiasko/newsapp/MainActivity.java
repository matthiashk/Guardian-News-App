package com.matthiasko.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    NewsAdapter newsAdapter;
    NewsItem[] results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.news_listview);

        // TODO: automatically fetch posts on app start?
        // use database?

        // setup initial empty list + empty view
        results = new NewsItem[0];
        newsAdapter = new NewsAdapter(this, -1, results);
        listView.setAdapter(newsAdapter);
        listView.setEmptyView(findViewById(R.id.empty_textview));

        new FetchNewsAsyncTask(this).execute();
    }
}
