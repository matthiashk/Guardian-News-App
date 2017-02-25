package com.matthiasko.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String title = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        String byline = intent.getStringExtra(MainActivity.EXTRA_BYLINE);



        TextView textView = (TextView) findViewById(R.id.articleTextView);
        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView bylineTextView = (TextView) findViewById(R.id.bylineTextView);

        titleTextView.setText(title);
        bylineTextView.setText(byline);

        // parse html
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

        /*
        for (Element link : links) {
            //System.out.println("src = " + src);
        }

        for (Element aside : asides) {
            //System.out.println("aside = " + aside);
        }*/


        textView.setText(Html.fromHtml(doc.toString()));

        //textView.setText(Html.fromHtml(message));
    }
}
