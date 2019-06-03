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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    NewsAdapter newsAdapter;
    NewsItem[] results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.news_listview);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            Parcelable[] ps = savedInstanceState.getParcelableArray("NEWSITEMS_ARRAY");
            NewsItem[] newsItems = new NewsItem[ps.length];
            System.arraycopy(ps, 0, newsItems, 0, ps.length);
            results = newsItems;
            newsAdapter = new NewsAdapter(this, newsItems);
            recyclerView.setAdapter(newsAdapter);
            newsAdapter.notifyDataSetChanged();

        } else {
            // setup initial empty list + empty view
            results = new NewsItem[0];
            newsAdapter = new NewsAdapter(this, results);
            recyclerView.setAdapter(newsAdapter);
        }
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
        recyclerView.setAdapter(newsAdapter);    }

    @Override
    public void stopLoadingPanel(Boolean isFinishedLoading) {
        if (isFinishedLoading) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
}
