package com.matthiasko.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by matthiasko on 8/15/16.
 */
public class NewsAdapter extends ArrayAdapter<NewsItem> {

    private final Context mContext;
    private final NewsItem[] objects;

    public NewsAdapter(Context context, int resource, NewsItem[] objects){
        super(context, resource, objects);
        this.mContext = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);

            holder = new ViewHolder();

            holder.sectionNameTextView = (TextView) convertView.findViewById(R.id.sectionname_textview);

            holder.titleTextView = (TextView) convertView.findViewById(R.id.title_textview);

            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.thumbnail_imageview);

            holder.bylineTextView = (TextView) convertView.findViewById(R.id.byline_textview);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        NewsItem currentNewsItem = objects[position];

        holder.sectionNameTextView.setText(currentNewsItem.getSectionName());

        holder.titleTextView.setText(currentNewsItem.getTitle());

        holder.bylineTextView.setText(currentNewsItem.getByline());

        Picasso.with(mContext)
                .load(currentNewsItem.getThumbnail())
                .resize(100, 100)
                .centerCrop()
                .into(holder.thumbnailImageView);

        return convertView;
    }

    static class ViewHolder {

        TextView sectionNameTextView;
        TextView titleTextView;
        TextView bylineTextView;
        ImageView thumbnailImageView;

    }
}
