package com.matthiasko.newsapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by matthiasko on 8/15/16.
 */
public class FetchNewsAsyncTask extends AsyncTask<Void, Void, NewsItem[]> {

    private final String LOG_TAG = FetchNewsAsyncTask.class.getSimpleName();
    private final Context mContext;

    SendToActivity dataSendToActivity;

    public FetchNewsAsyncTask(Context context, Activity activity) {
        mContext = context;
        dataSendToActivity = (SendToActivity) activity;
    }

    NewsItem[] values;

    // parsing based on http://stackoverflow.com/a/14699406/1079883
    private NewsItem[] parseBooksJson(String responseString)
            throws JSONException {

        try {
            JSONObject jsonObject = new JSONObject(responseString);

            if (jsonObject.has("response")) {

                JSONObject resultsJson = jsonObject.getJSONObject("response");

                JSONArray jArray = resultsJson.getJSONArray("results");

                values = new NewsItem[jArray.length()];

                for(int i = 0; i < jArray.length(); i++) {

                    String title = jArray.getJSONObject(i).getString("webTitle");

                    String sectionName = jArray.getJSONObject(i).getString("sectionName");

                    String webUrl = jArray.getJSONObject(i).getString("webUrl");

                    JSONObject fieldsJson = jArray.getJSONObject(i).getJSONObject("fields");

                    String thumbnail = "";

                    if (fieldsJson.has("thumbnail")) {
                        thumbnail = fieldsJson.getString("thumbnail");
                    }

                    String byline = fieldsJson.getString("byline");

                    NewsItem newsItem = new NewsItem();
                    newsItem.setTitle(title);
                    newsItem.setThumbnail(thumbnail);
                    newsItem.setSectionName(sectionName);
                    newsItem.setWebUrl(webUrl);
                    newsItem.setByline(byline);

                    values[i] = newsItem;
                }
            } else {

                // clear array here since there are no results found
                values = new NewsItem[0];
            }
        }

        catch(JSONException e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    protected NewsItem[] doInBackground(Void... params) {
        // doInBackground main code based on my previous project Popular Movies 2

        // will contain the raw JSON response as a string.
        String booksJsonResponseString;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        NewsItem[] values = new NewsItem[100];

        // example query
        // http://content.guardianapis.com/search?from-date=2016-08-15&api-key=myApiKey

        try {
            final String BOOKS_BASE_URL = "http://content.guardianapis.com/search?";
            final String DATE_PARAM = "from-date";
            final String FIELDS_PARAM = "show-fields";
            final String PAGESIZE_PARAM = "page-size";

            final String API_KEY_PARAM = "api-key";

            final String THUMBNAIL = "thumbnail";
            final String BYLINE = ",byline";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String datetime = dateFormat.format(date);
            String pagesize = "20";
            //System.out.println("Current Date Time : " + datetime);

            Uri builtUri = Uri.parse(BOOKS_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(DATE_PARAM, datetime)
                    .appendQueryParameter(PAGESIZE_PARAM, pagesize)
                    .appendQueryParameter(FIELDS_PARAM, THUMBNAIL + BYLINE)
                    .appendQueryParameter(API_KEY_PARAM, mContext.getResources().getString(R.string.api_key))
                    .build();

            URL url = new URL(builtUri.toString());

            //System.out.println("builtUri = " + builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                return null;
            }

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                return null;
            }

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            booksJsonResponseString = buffer.toString();

            values = parseBooksJson(booksJsonResponseString);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return values;
    }

    @Override
    protected void onPostExecute(NewsItem[] values) {
        super.onPostExecute(values);

        if (values != null) {
            dataSendToActivity.sendData(values);
        }
    }
}
