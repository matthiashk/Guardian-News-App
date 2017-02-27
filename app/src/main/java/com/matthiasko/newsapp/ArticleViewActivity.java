package com.matthiasko.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.widget.TextView;

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

        Transition enterTrans = new Slide();
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Slide();
        getWindow().setReturnTransition(returnTrans);

        //TODO: add onnewintent methods here? or create new method with code in onnewintent?

        //System.out.println("ONCREATE");
    }

    // use this to make the up button trigger the correct animation
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_up);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        //System.out.println("ONNEWINTENT");

        //intent = getIntent();
        String message = intent.getStringExtra(NewsAdapter.EXTRA_MESSAGE);
        String title = intent.getStringExtra(NewsAdapter.EXTRA_TITLE);
        String byline = intent.getStringExtra(NewsAdapter.EXTRA_BYLINE);

        TextView textView = (TextView) findViewById(R.id.articleTextView);
        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView bylineTextView = (TextView) findViewById(R.id.bylineTextView);

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

        Elements strong = doc.select("strong");
        strong.remove();


        textView.setText(Html.fromHtml(doc.toString()));

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }
}
