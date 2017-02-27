package com.matthiasko.newsapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements SendToActivity {

    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //ListView listView;
    NewsAdapter newsAdapter;
    NewsItem[] results;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.news_listview);


        //listView = (ListView) findViewById(R.id.news_listview);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);





        if (savedInstanceState != null) {

            Parcelable[] ps = savedInstanceState.getParcelableArray("NEWSITEMS_ARRAY");
            NewsItem[] newsItems = new NewsItem[ps.length];
            System.arraycopy(ps, 0, newsItems, 0, ps.length);
            results = newsItems;
            newsAdapter = new NewsAdapter(this, newsItems);
            mRecyclerView.setAdapter(newsAdapter);            //listView.setEmptyView(findViewById(R.id.empty_textview));
            newsAdapter.notifyDataSetChanged();

        } else {
            // setup initial empty list + empty view
            results = new NewsItem[0];
            newsAdapter = new NewsAdapter(this, results);
            mRecyclerView.setAdapter(newsAdapter);            //listView.setEmptyView(findViewById(R.id.empty_textview));
        }

        /*

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
                // set title and byline here, sent by handler after
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

                Intent intent = new Intent(MainActivity.this, ArticleViewActivity.class);
                intent.putExtra(EXTRA_MESSAGE, value);
                intent.putExtra(EXTRA_TITLE, title);
                intent.putExtra(EXTRA_BYLINE, byline);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                startActivity(intent, options.toBundle());
            }
        });
        */


        new FetchNewsAsyncTask(this, MainActivity.this).execute();


        /*
        // setup transition animations
        Transition exitTrans = new Slide();
        getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Slide();
        getWindow().setReenterTransition(reenterTrans);
        */
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (results != null) {
            outState.putParcelableArray("NEWSITEMS_ARRAY", results);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sections) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sections")
                    .setItems(R.array.sections, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            String sectionName = getResources().getStringArray(R.array.sections)[which];

                            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                            // launch asynctask that fetches specific section
                            new FetchNewsAsyncTask(getApplicationContext(), MainActivity.this).execute(sectionName);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

            // set the width
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = 800;
            dialog.getWindow().setAttributes(lp);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData(NewsItem[] itemsArray) {
        results = itemsArray;
        newsAdapter = new NewsAdapter(this, results);
        mRecyclerView.setAdapter(newsAdapter);    }

    @Override
    public void stopLoadingPanel(Boolean isFinishedLoading) {
        if (isFinishedLoading) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
}
