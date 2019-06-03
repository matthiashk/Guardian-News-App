package com.matthiasko.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by matthiasko on 2/23/17.
 */

public class ArticleViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articleview);

        // Postpone the shared element enter transition.
        postponeEnterTransition();

        /*
        Transition enterTrans = new Slide();
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Slide();
        getWindow().setReturnTransition(returnTrans);
        */

    }

    // use this to make the up button trigger the correct animation
    @Override
    protected void onPause() {
        super.onPause();
        //overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String message = intent.getStringExtra(NewsAdapter.EXTRA_MESSAGE);
        String title = intent.getStringExtra(NewsAdapter.EXTRA_TITLE);
        String byline = intent.getStringExtra(NewsAdapter.EXTRA_BYLINE);
        String thumbnail = intent.getStringExtra(NewsAdapter.EXTRA_THUMBNAIL);

        TextView textView = (TextView) findViewById(R.id.article_textview);
        TextView titleTextView = (TextView) findViewById(R.id.title_textview);
        TextView bylineTextView = (TextView) findViewById(R.id.byline_textview);

        final ImageView thumbnailImageView = (ImageView) findViewById(R.id.thumbnail_imageview);

        titleTextView.setText(title);
        bylineTextView.setText(byline);

        // parse html using Jsoup
        // remove href
        // remove aside
        Document doc = Jsoup.parseBodyFragment(message);

        Elements links = doc.select("a[href]");
        links.unwrap();

        Elements asides = doc.select("aside");
        asides.remove();

        Elements figures = doc.select("figure");
        figures.remove();

        Elements figcaptions = doc.select("figcaption");
        figcaptions.remove();

        textView.setText(Html.fromHtml(doc.toString()));

        if (thumbnail.isEmpty()) {

            Picasso.with(getApplicationContext())
                    .load(R.drawable.gu_logo_fallback_resized)
                    .into(thumbnailImageView);

        } else {

            Picasso.with(getApplicationContext())
                    .load(thumbnail)
                    .into(thumbnailImageView);
        }



        startPostponedEnterTransition();
        thumbnailImageView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        thumbnailImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                }
        );


        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }
}
