package com.matthiasko.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.value;

/**
 * Created by matthiasko on 8/15/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    String title;
    String byline;
    String thumbnail;

    ViewHolder viewHolder;

    public final static String EXTRA_MESSAGE = "com.matthiasko.newsapp.MESSAGE";
    public final static String EXTRA_TITLE = "com.matthiasko.newsapp.TITLE";
    public final static String EXTRA_BYLINE = "com.matthiasko.newsapp.BYLINE";
    public final static String EXTRA_THUMBNAIL = "com.matthiasko.newsapp.THUMBNAIL";


    private final NewsItem[] objects;
    Context mContext;

    // used to get article html from fetcharticleaynctask onpostexecute
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String value = (String) msg.obj;

            // send to articleviewactivity
            Intent intent = new Intent(mContext, ArticleViewActivity.class);
            intent.putExtra(EXTRA_MESSAGE, value);
            intent.putExtra(EXTRA_TITLE, title);
            intent.putExtra(EXTRA_BYLINE, byline);
            intent.putExtra(EXTRA_THUMBNAIL, thumbnail);

            /*
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity)mContext, viewHolder.thumbnailImageView, "image");
            mContext.startActivity(intent, options.toBundle());
            */
            mContext.startActivity(intent);
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        TextView sectionNameTextView;
        TextView titleTextView;
        ImageView thumbnailImageView;
        TextView trailTextView;
        TextView webDateTextView;
        //TextView bylineTextView;

        public ViewHolder(View v) {

            super(v);
            view = v;
            sectionNameTextView = (TextView) v.findViewById(R.id.sectionname_textview);
            titleTextView = (TextView) v.findViewById(R.id.title_textview);
            thumbnailImageView = (ImageView) v.findViewById(R.id.thumbnail_imageview);
            trailTextView = (TextView) v.findViewById(R.id.trailtext_textview);
            webDateTextView = (TextView) v.findViewById(R.id.webdate_textview);
        }
    }

    public NewsAdapter(Context context, NewsItem[]objects){
        this.mContext = context;
        this.objects = objects;
    }

    @Override
    public int getItemCount() {
        return objects.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.rowlayout, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        this.viewHolder = viewHolder;

        final NewsItem currentNewsItem = objects[i];
        viewHolder.sectionNameTextView.setText(currentNewsItem.sectionName);
        viewHolder.titleTextView.setText(currentNewsItem.title);

        if (!currentNewsItem.getThumbnail().isEmpty()) {

            Picasso.with(viewHolder.thumbnailImageView.getContext())
                    .load(currentNewsItem.getThumbnail())
                    .into(viewHolder.thumbnailImageView);
        } else {

            Picasso.with(viewHolder.thumbnailImageView.getContext())
                    .load(R.drawable.gu_logo_fallback_resized)
                    .into(viewHolder.thumbnailImageView);

        }

        viewHolder.trailTextView.setText(currentNewsItem.trailText);

        SimpleDateFormat dateformat = new SimpleDateFormat("MMM d");
        Date date = currentNewsItem.getWebDate();
        String dateString = dateformat.format(date);
        viewHolder.webDateTextView.setText(dateString);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send title and byline to articleviewactivity
                // set title and byline here, sent by handler after
                title = currentNewsItem.getTitle();
                byline = currentNewsItem.getByline();
                thumbnail = currentNewsItem.getThumbnail();

                String webUrl = currentNewsItem.getWebUrl();

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
                            .appendQueryParameter(API_KEY_PARAM, mContext.getResources().getString(R.string.api_key))
                            .build();

                    URL url = new URL(builtUri.toString());
                    FetchArticleAsyncTask fetchTask = new FetchArticleAsyncTask();
                    fetchTask.setHandler(handler);
                    fetchTask.execute(url);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(mContext, ArticleViewActivity.class);
                intent.putExtra(EXTRA_MESSAGE, value);
                intent.putExtra(EXTRA_TITLE, title);
                intent.putExtra(EXTRA_BYLINE, byline);
                intent.putExtra(EXTRA_THUMBNAIL, thumbnail);


                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)mContext, viewHolder.thumbnailImageView, "image");
                mContext.startActivity(intent, options.toBundle());
            }
        });
    }
}
