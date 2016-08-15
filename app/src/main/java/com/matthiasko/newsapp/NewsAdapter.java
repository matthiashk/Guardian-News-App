package com.matthiasko.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

            holder.titleTextView = (TextView) convertView.findViewById(R.id.title_textview);





            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        NewsItem currentNewsItem = objects[position];


        holder.titleTextView.setText(currentNewsItem.getTitle());





        return convertView;
    }

    static class ViewHolder {

        TextView titleTextView;
    }
}
