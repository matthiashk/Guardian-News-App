package com.matthiasko.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements SendToActivity {

    ListView listView;
    NewsAdapter newsAdapter;
    NewsItem[] results;
    String title;
    String byline;

    public final static String EXTRA_MESSAGE = "com.matthiasko.newsapp.MESSAGE";
    public final static String EXTRA_TITLE = "com.matthiasko.newsapp.TITLE";
    public final static String EXTRA_BYLINE = "com.matthiasko.newsapp.BYLINE";

    Handler handler = new Handler() { // used to get article html from fetcharticleaynctask onpostexecute

        @Override
        public void handleMessage(Message msg) {
            String value = (String) msg.obj;

            // send to articleviewactivity

            Intent intent = new Intent(getApplicationContext(), ArticleViewActivity.class);
            intent.putExtra(EXTRA_MESSAGE, value);
            intent.putExtra(EXTRA_TITLE, title);
            intent.putExtra(EXTRA_BYLINE, byline);

            startActivity(intent);

            //System.out.println("MAINACTIVITY value = " + value);
        }
    };

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

                // instead of launching url in web browser
                // make api request for selected article
                // get full text and load in textview/new activity

                NewsItem item = (NewsItem) listView.getItemAtPosition(position);
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getWebUrl()));

                //System.out.println("item.getWebUrl() = " + item.getWebUrl());

                // change webUrl to get article text
                // example url: https://www.theguardian.com/artanddesign/2017/feb/24/andy-warhols-mad-men-era-he-found-new-york-at-this-incredible-moment
                // change https://www.theguardian.com to https://content.guardianapis.com
                // add api-key
                // add &show-blocks=all to end of url


                // send title and byline to articleviewactivity
                title = item.getTitle();
                byline = item.getByline();


                String webUrl = item.getWebUrl();

                try {
                    URL aURL = new URL(webUrl);

                    String changedHost = "content.guardianapis.com";

                    URL changedURL = new URL(aURL.getProtocol(), changedHost, aURL.getPort(), aURL.getFile());

                    final String SHOW_BLOCKS_PARAM = "show-blocks";
                    final String API_KEY_PARAM = "api-key";

                    String showBlocksAll = "all";

                    Uri builtUri = Uri.parse(changedURL.toString())
                            .buildUpon()
                            .appendQueryParameter(SHOW_BLOCKS_PARAM, showBlocksAll)
                            //.appendQueryParameter(API_KEY_PARAM, mContext.getResources().getString(R.string.api_key))
                            .appendQueryParameter(API_KEY_PARAM, getResources().getString(R.string.api_key))
                            .build();

                    URL url = new URL(builtUri.toString());

                    //System.out.println("url.toString() = " + url.toString());

                    FetchArticleAsyncTask fetchTask = new FetchArticleAsyncTask();

                    fetchTask.setHandler(handler);
                    fetchTask.execute(url);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // disabled
                //startActivity(browserIntent);
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
