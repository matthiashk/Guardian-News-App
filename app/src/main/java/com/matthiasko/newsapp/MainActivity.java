package com.matthiasko.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

        // TODO: automatically fetch posts on app start?
        // use database?

        // setup initial empty list + empty view
        results = new NewsItem[0];
        newsAdapter = new NewsAdapter(this, -1, results);
        listView.setAdapter(newsAdapter);
        listView.setEmptyView(findViewById(R.id.empty_textview));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem item = (NewsItem) listView.getItemAtPosition(position);

                //System.out.println("item.getWebUrl() = " + item.getWebUrl());

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getWebUrl()));
                startActivity(browserIntent);
            }
        });

        new FetchNewsAsyncTask(this, MainActivity.this).execute();
    }

    @Override
    public void sendData(NewsItem[] itemsArray) {

        results = itemsArray;
        newsAdapter = new NewsAdapter(this, -1, results);
        listView.setAdapter(newsAdapter);


        //System.out.println("FROM ASYNC TASK -> string = " + string);

    }
}
