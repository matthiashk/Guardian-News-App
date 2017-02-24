package com.matthiasko.newsapp;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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

/**
 * Created by matthiasko on 2/23/17.
 */

public class FetchArticleAsyncTask extends AsyncTask<URL, Void, String> {

    private final String LOG_TAG = FetchArticleAsyncTask.class.getSimpleName();

    Handler handler = null;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    public FetchArticleAsyncTask() {

    }

    String values;

    private String parseBooksJson(String responseString)
            throws JSONException {

        try {
            JSONObject jsonObject = new JSONObject(responseString);

            if (jsonObject.has("response")) {

                JSONObject resultsJson = jsonObject.getJSONObject("response");
                JSONObject contentJson = resultsJson.getJSONObject("content");
                JSONObject blocksJson = contentJson.getJSONObject("blocks");
                JSONArray bodyArray = blocksJson.getJSONArray("body");

                for(int i = 0; i < bodyArray.length(); i++) {
                    values = bodyArray.getJSONObject(i).getString("bodyHtml");
                }

            } else {

                // clear data here since there are no results found
                values = "";
            }
        }

        catch(JSONException e) {
            e.printStackTrace();
        }



        return values;
    }

    @Override
    protected String doInBackground(URL... params) {

        // will contain the raw JSON response as a string.
        String booksJsonResponseString;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String values = "";

        try {

            URL url = params[0];

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

            // send to parser here
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
    protected void onPostExecute(String values) {
        super.onPostExecute(values);

        if (handler != null) {
            Message message = new Message();
            message.obj = values;
            handler.sendMessage(message);
        }

    }
}
