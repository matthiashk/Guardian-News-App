package com.matthiasko.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by matthiasko on 8/15/16.
 */
public class FetchNewsAsyncTask extends AsyncTask<String, Void, NewsItem[]> {

    private final String LOG_TAG = FetchNewsAsyncTask.class.getSimpleName();
    private final Context mContext;

    SendToActivity dataSendToActivity;
    Date date;

    public FetchNewsAsyncTask(Context context, Activity activity) {
        mContext = context;
        dataSendToActivity = (SendToActivity) activity;
    }

    NewsItem[] values;

    URL url;

    Boolean isIOException = false;

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

                    String webDate = jArray.getJSONObject(i).getString("webPublicationDate");

                    String thumbnail = "";

                    String trailText = "";

                    if (fieldsJson.has("thumbnail")) {
                        thumbnail = fieldsJson.getString("thumbnail");
                    }

                    if (fieldsJson.has("trailText")) {
                        trailText = fieldsJson.getString("trailText");
                    }

                    // using optString here bc sometimes byline does not exist
                    String byline = fieldsJson.optString("byline");

                    // convert string date to Date object
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        date = format.parse(webDate);
                        //System.out.println(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    NewsItem newsItem = new NewsItem();
                    newsItem.setTitle(title);
                    newsItem.setThumbnail(thumbnail);
                    newsItem.setSectionName(sectionName);
                    newsItem.setWebUrl(webUrl);
                    newsItem.setByline(byline);
                    newsItem.setTrailText(trailText);
                    newsItem.setWebDate(date);

                    values[i] = newsItem;

                    //System.out.println("values.length = " + values.length);
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
    protected NewsItem[] doInBackground(String... params) {
        // doInBackground main code based on my previous project Popular Movies 2

        // will contain the raw JSON response as a string.
        String booksJsonResponseString;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;



        // example query
        // http://content.guardianapis.com/search?from-date=2016-08-15&api-key=myApiKey

        try {
            final String BOOKS_BASE_URL = "http://content.guardianapis.com/search?";
            final String DATE_PARAM = "from-date";
            final String FIELDS_PARAM = "show-fields";
            final String PAGESIZE_PARAM = "page-size";

            final String SECTION_PARAM = "section";

            final String API_KEY_PARAM = "api-key";

            final String THUMBNAIL = "thumbnail";
            final String BYLINE = ",byline";
            final String TRAILTEXT = ",trailText";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String datetime = dateFormat.format(date);
            String pagesize = "20";
            //System.out.println("Current Date Time : " + datetime);

            if (params.length == 1) {
                String sectionName = params[0];
                String idName = "";
                // convert sectionName to idName
                switch (sectionName) {
                    case "Art and Design":
                        idName = "artanddesign";
                        break;
                    case "Books":
                        idName = "books";
                        break;
                    case "Business":
                        idName = "business";
                        break;
                    case "Opinion":
                        idName = "commentisfree";
                        break;
                    case "Culture":
                        idName = "culture";
                        break;
                    case "Education":
                        idName = "education";
                        break;
                    case "Environment":
                        idName = "environment";
                        break;
                    case "Film":
                        idName = "film";
                        break;
                    case "Music":
                        idName = "music";
                        break;
                    case "News":
                        idName = "news";
                        break;
                    case "Politics":
                        idName = "politics";
                        break;
                    case "Science":
                        idName = "science";
                        break;
                    case "Society":
                        idName = "society";
                        break;
                    case "Technology":
                        idName = "technology";
                        break;
                    case "Travel":
                        idName = "travel";
                        break;
                    case "UK News":
                        idName = "uk-news";
                        break;
                    case "US News":
                        idName = "us-news";
                        break;
                    case "World News":
                        idName = "world";
                        break;
                }
                // build uri with sectionName
                Uri builtUri = Uri.parse(BOOKS_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(DATE_PARAM, datetime)
                        .appendQueryParameter(PAGESIZE_PARAM, pagesize)
                        .appendQueryParameter(SECTION_PARAM, idName)
                        .appendQueryParameter(FIELDS_PARAM, THUMBNAIL + BYLINE + TRAILTEXT)
                        .appendQueryParameter(API_KEY_PARAM, mContext.getResources().getString(R.string.api_key))
                        .build();
                url = new URL(builtUri.toString());
            } else {
                Uri builtUri = Uri.parse(BOOKS_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(DATE_PARAM, datetime)
                        .appendQueryParameter(PAGESIZE_PARAM, pagesize)
                        .appendQueryParameter(FIELDS_PARAM, THUMBNAIL + BYLINE + TRAILTEXT)
                        .appendQueryParameter(API_KEY_PARAM, mContext.getResources().getString(R.string.api_key))
                        .build();
                url = new URL(builtUri.toString());
            }
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

            NewsItem[] values = new NewsItem[100];

            values = parseBooksJson(booksJsonResponseString);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();

            isIOException = true;


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

        if (isIOException) {



            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("There was an error connecting to the server. Please try again later.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            AlertDialog dialog = builder.create();
            dialog.show();

            /*
            // set the width
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = 800;
            dialog.getWindow().setAttributes(lp);
            */

        }

        if (values != null) {
            dataSendToActivity.sendData(values);

        }

        dataSendToActivity.stopLoadingPanel(true);
    }
}
