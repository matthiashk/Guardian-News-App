package com.matthiasko.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matthiasko on 8/15/16.
 */
public class NewsItem implements Parcelable {

    String sectionName;
    String title;
    String webUrl;
    String thumbnail;
    String byline;
    String trailText;

    public String getTrailText() {
        return trailText;
    }

    public void setTrailText(String trailText) {
        this.trailText = trailText;
    }

    public NewsItem(){}

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sectionName);
        dest.writeString(title);
        dest.writeString(webUrl);
        dest.writeString(thumbnail);
        dest.writeString(byline);
        dest.writeString(trailText);
    }

    // some parcelable code from https://dzone.com/articles/using-android-parcel
    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        public NewsItem createFromParcel(Parcel pc) {
            return new NewsItem(pc);
        }
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    /**Ctor from Parcel, reads back fields IN THE ORDER they were written */
    public NewsItem(Parcel pc){
        sectionName = pc.readString();
        title = pc.readString();
        webUrl = pc.readString();
        thumbnail = pc.readString();
        byline = pc.readString();
        trailText = pc.readString();
    }
}
